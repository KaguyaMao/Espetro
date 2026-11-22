package org.espetro.team;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.DeployScreenSync;
import org.espetro.network.FactionDisplay;
import org.espetro.network.MatchBroadcasts;
import org.espetro.network.PreMatchScreens;
import org.espetro.network.SquadSync;
   import org.espetro.network.TeamSelectStatePacket;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.VehicleEventHandler;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.resupply.ResupplySessionManager;

import net.minecraftforge.common.MinecraftForge;
import org.espetro.api.event.GamePhaseChangedEvent;

import java.util.*;
import java.util.function.Consumer;

/**
 * 游戏状态管理器
 * 管理游戏各阶段流程
 *
 * 阶段流转（按流程图）：
 * WAITING → ATTACK_COMMANDER_VOTE(20s) → DEFEND_COMMANDER_VOTE(20s)
 * → ATTACK_FACTION_SELECT(30s) → DEFEND_FACTION_SELECT(30s) → FACTION_REVEAL(5s)
 * → DEPLOYING(240s, 守方部署防线/攻方屏障内等待) → BATTLE
 */
public class GameStateManager {
    private static GameStateManager INSTANCE;

    // 当前游戏阶段
    private GamePhase currentPhase = GamePhase.LOBBY;

    // 部署阶段计时器
    private int deployTickCounter = 0;

    private int battleTickCounter = 0;

    // 双方编制揭示阶段计时器
    private int factionRevealTickCounter = 0;

    private static final int TICKS_PER_SECOND = 20;

    /** 队伍平衡：双方人数差异上限，达到此值时锁定人多的一方。 */
    static final int TEAM_BALANCE_MAX_DIFF = 3;

    /** 攻方等待屏障（防守部署阶段）。 */
    private final AttackWaitingBarrier barrier = new AttackWaitingBarrier();

    /** Deployment timer expired; wait for the bounded barrier-removal queue before BATTLE. */
    private boolean battleStartPending;

    /**
     * Which team votes factions first this match.
     * AAS is always ATTACK; RAAS is randomized when entering faction select.
     */
    private String firstFactionSelectTeam = "ATTACK";

    // 等待选择队伍的玩家
    private final Set<UUID> waitingForTeam = new HashSet<>();

    // 已选择队伍的玩家
    private final Set<UUID> teamSelectedPlayers = new HashSet<>();

    // 战局中加入的玩家（部署点选择完成前）
    private final Set<UUID> midGameJoiners = new HashSet<>();

    /** 开局自动分配时记录玩家队伍，用于重连还原。 */
    private final Map<UUID, String> assignedTeams = new java.util.HashMap<>();

    // 已在部署阶段选择过职业的玩家（防止重复选择）
    private final Set<UUID> deployClassSelected = new HashSet<>();

    /** 管理员设为观察者的玩家（局内观战）：本局结束（beginCleanup/forceStop/reset）时自动清除恢复。 */
    private final Set<UUID> observers = new HashSet<>();

    /**
     * 主城等待期间被设为观察者的玩家：<b>跨局保持</b>，下一局继续观战，
     * 直到管理员用跳边指令把他编入队伍。与 {@link #observers} 的区别只有生命周期。
     */
    private final Set<UUID> persistentObservers = new HashSet<>();

    /** 投票阶段给观战者刷新"请等待"提示的节流计数。 */
    private int votingNoticeTicker = 0;

    private int teamSelectTickCounter = 0;

    private int roundEndTickCounter = 0;

    private String pendingRoundWinner = null;

    private ActiveMapConfig pendingMap = null;

    private boolean forceStopInProgress = false;

    /** 主城状态广播：上次推送的在线人数与 tick，用于降频 */
    private int lastHubBroadcastPlayerCount = -1;

    private int lastHubBroadcastTick = Integer.MIN_VALUE;

    private GameStateManager() {
        INSTANCE = this;
    }

    public static GameStateManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameStateManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new GameStateManager();
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    /** Map folder selected for this round, including the loading transition. */
    public String getCurrentMapFolder() {
        ActiveMapConfig map = pendingMap != null ? pendingMap : BattlefieldContext.getOrNull();
        return map == null || map.mapFolder == null ? "" : map.mapFolder;
    }

    public void setPhase(GamePhase phase) {
        if (phase != null && !phase.isLobbyLike()
            && !BattlefieldWorldManager.getInstance().isStartupReady()) {
            Espetro.LOGGER.error("战场启动门禁未 READY，拒绝阶段切换 {} -> {}",
                currentPhase, phase);
            phase = GamePhase.LOBBY;
        }
        GamePhase previous = this.currentPhase;
        this.currentPhase = phase;
        Espetro.LOGGER.info("游戏阶段切换: {}", phase.getDisplayName());
        MatchBroadcasts.broadcastGamePhase(phase);
        if (phase == GamePhase.BATTLE && previous != GamePhase.BATTLE) {
            // 部署阶段不可用技能：冷却从对战阶段开始才起算
            CommanderSkillManager.getInstance().armSkillCooldownsOnBattleStart();
        }
        MinecraftForge.EVENT_BUS.post(new GamePhaseChangedEvent(previous, phase));
    }

    /**
     * Administrator entry point for a new round. There is intentionally no
     * ready-player gate: one online player is enough to begin the global map
     * vote, while later joins are handled by the phase-specific join flow.
     */
    public boolean prestart(MinecraftServer server) {
        if (server == null || !currentPhase.isLobbyLike()) {
            return false;
        }
        if (!BattlefieldWorldManager.getInstance().isStartupReady()) {
            var preparation = BattlefieldWorldManager.getInstance().getStartupPreparation();
            Espetro.broadcastToAll("§c[Espetro] 战场启动重置失败，本次会话地图已禁用："
                + (preparation.error() == null ? preparation.status().name() : preparation.error()));
            setPhase(GamePhase.LOBBY);
            return false;
        }
        if (server.getPlayerCount() < 1) {
            return false;
        }
        if (ExternalConfigBootstrap.getUsableMaps().isEmpty()) {
            Espetro.broadcastToAll("§c[Espetro] 没有通过校验的地图，无法开始。请查看服务端日志。");
            return false;
        }

        clearRoundRuntime(false);
        PlayerMatchStatsManager.getInstance().resetMatch(
            server.getPlayerList().getPlayers());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            TeamManager.removeFromAllTeams(server.getScoreboard(), player.getName().getString());
            ClassEquipment.clearEquipment(player);
        }

        setPhase(GamePhase.MAP_VOTE);
        if (!MapVoteManager.getInstance().start(server)) {
            setPhase(GamePhase.LOBBY);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                PlayerHoldStates.applyHubState(player);
            }
            return false;
        }
        // 地图投票起即进入统一 MatchHold：旁观 + 失明 + 高空禁移（勿 applyHubState）。
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.HUB_HIGH);
        }
        Espetro.broadcastToAll("§6地图投票开始，所有在线玩家均可投票。");
        return true;
    }

    /** Called by the global MapVoteManager on the server thread. */
    public void onMapVoteFinished(ActiveMapConfig winner) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || currentPhase != GamePhase.MAP_VOTE || winner == null || !winner.usable) {
            return;
        }
        pendingMap = winner;
        // 地图投票结束 → 先进 5 秒地图揭晓页（MAP_REVEAL），随后进入装载。
        startMapReveal(winner);
    }

    private void startTeamSelect() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || pendingMap == null || !BattlefieldContext.isActive()) {
            setPhase(GamePhase.LOBBY);
            if (server != null) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    PlayerHoldStates.applyHubState(player);
                }
            }
            return;
        }

        waitingForTeam.clear();
        teamSelectedPlayers.clear();
        midGameJoiners.clear();
        teamSelectTickCounter = 0;
        // 新局选边开始：上一局的观察者全部恢复为正常玩家
        clearAllObservers();
        setPhase(GamePhase.TEAM_SELECT);
        TeamManager.refreshDisplayNames(server);

        List<ServerPlayer> allPlayers = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            clearPlayerRoundAssignment(player);
            allPlayers.add(player);
            PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.BATTLEFIELD_WAIT);
        }

        // 自动分配：按组队信息尽量保持同队，均分人数
        java.util.Map<UUID, String> assignments =
            org.espetro.team.PartyManager.getInstance().computeTeamAssignment(allPlayers);
        assignedTeams.clear();
        assignedTeams.putAll(assignments);

        for (ServerPlayer player : allPlayers) {
            // 观战者（含主城设置的跨局观战）不参与自动分配，继续保持旁观。
            if (isObserver(player.getUUID())) {
                continue;
            }
            String team = assignments.get(player.getUUID());
            if (team == null) team = "ATTACK";
            applyTeamAssignmentToPlayer(player, team);
            teamSelectedPlayers.add(player.getUUID());
        }

        // 组队信息在分配后无需保留
        org.espetro.team.PartyManager.getInstance().clearAll();

        broadcastTeamSelectState(false);
        Espetro.LOGGER.info("自动分配队伍完成: 进攻{}人 防守{}人",
            countPlayersOnTeam("ATTACK"), countPlayersOnTeam("DEFEND"));
        // 分配完成 → 先进 5 秒分配提示页（TEAM_ASSIGN_SHOW），随后进入并行指挥官投票。
        startTeamAssignShow();
    }

    // ========== 队伍分配提示页（5s）→ 并行指挥官投票 ==========

    /** 分配提示页阶段计时（tick）。 */
    private int teamAssignShowTickCounter = 0;

    private void startTeamAssignShow() {
        teamAssignShowTickCounter = 0;
        setPhase(GamePhase.TEAM_ASSIGN_SHOW);
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        // 每个玩家收到自己的分配结果页
        org.espetro.network.PreMatchScreens.broadcastTeamAssignShow(server);
        Espetro.LOGGER.info("队伍分配提示页开始，5秒后进入指挥官投票");
    }

    private void finishTeamAssignShow() {
        if (currentPhase != GamePhase.TEAM_ASSIGN_SHOW) return;
        startBothCommanderVote();
    }

    /** 双方并行指挥官投票。 */
    private void startBothCommanderVote() {
        setPhase(GamePhase.COMMANDER_VOTE);
        VoteManager.getInstance().initPlayers();
        VoteManager.getInstance().startBothVote();
    }

    /** 并行指挥官投票超时/结束 → 进入编制选择。 */
    private void finishBothCommanderVote() {
        if (currentPhase != GamePhase.COMMANDER_VOTE) return;
        VoteManager.getInstance().finishCurrentVote();
        startFirstFactionSelect();
    }

    /** 地图投票结束后：先进 5 秒地图揭晓页（MAP_REVEAL），再进入地图装载。 */
    private int mapRevealTickCounter = 0;

    private void startMapReveal(org.espetro.mapconfig.ActiveMapConfig winner) {
        mapRevealTickCounter = 0;
        setPhase(GamePhase.MAP_REVEAL);
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.AUTO);
        }
        org.espetro.network.PreMatchScreens.broadcastMapRevealScreen(server, winner, 5);
        Espetro.LOGGER.info("地图揭晓页开始，5秒后装载: {}", winner.displayName);
    }

    private void finishMapReveal() {
        if (currentPhase != GamePhase.MAP_REVEAL || pendingMap == null) return;
        beginMapLoading();
    }

    /** 装载胜出地图（原 onMapVoteFinished 的装载主体）。 */
    private void beginMapLoading() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || pendingMap == null || currentPhase != GamePhase.MAP_REVEAL) {
            return;
        }
        ActiveMapConfig winner = pendingMap;
        setPhase(GamePhase.MAP_LOADING);
        // 装载阶段保持 hold；禁止 applyHubState（会摘失明并允许活动）。
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.AUTO);
            player.sendSystemMessage(Component.literal("§e正在装载战场地图：" + winner.displayName));
        }
        BattlefieldWorldManager.getInstance().importAndLoad(server, winner, result -> {
            if (!result.success()) {
                pendingMap = null;
                setPhase(GamePhase.LOBBY);
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    PlayerHoldStates.applyHubState(player);
                }
                Espetro.broadcastToAll("§c地图装载失败：" + result.error());
                broadcastHubStatus();
                return;
            }
            startTeamSelect();
        });
    }

    /** 将一个玩家的队伍分配应用到所有相关系统。 */
    private void applyTeamAssignmentToPlayer(ServerPlayer player, String team) {
        if (!"ATTACK".equals(team) && !"DEFEND".equals(team)) return;
        ClassCountManager.getInstance().setPlayerFaction(player.getUUID(), team);
        ClassCountManager.getInstance().setPlayerTeam(player.getUUID(), team);
        if ("ATTACK".equals(team)) {
            TeamManager.joinAttackTeam(player.server, player.getName().getString());
        } else {
            TeamManager.joinDefendTeam(player.server, player.getName().getString());
        }
        PlayerMatchStatsManager.getInstance().onTeamSelected(player, team);
        player.sendSystemMessage(Component.literal(
            "§a你已被分配到" + TeamDisplayNames.coloredDisplayName(team)));
    }

    // ========== 队伍选择阶段 ==========

    public void onTeamSelected(ServerPlayer player, String factionId) {
        if (currentPhase != GamePhase.TEAM_SELECT) {
            return;
        }
        String resolvedTeam = getTeamFromFactionStatic(factionId);
        if (!"ATTACK".equals(resolvedTeam) && !"DEFEND".equals(resolvedTeam)) {
            return;
        }

        // 队伍平衡检查：计算选择后双方人数差异
        String oldTeam = ClassCountManager.getInstance().getPlayerTeam(player.getUUID());
        int attack = countPlayersOnTeam("ATTACK");
        int defend = countPlayersOnTeam("DEFEND");
        if ("ATTACK".equals(resolvedTeam) && !"ATTACK".equals(oldTeam)) {
            attack++;
            if (oldTeam != null) defend--;
        } else if ("DEFEND".equals(resolvedTeam) && !"DEFEND".equals(oldTeam)) {
            defend++;
            if (oldTeam != null) attack--;
        }
        int diff = Math.abs(attack - defend);
        if (diff > TEAM_BALANCE_MAX_DIFF) {
            player.sendSystemMessage(Component.literal(
                "§c队伍人数差异已达上限，该阵营暂时锁定。请选择另一阵营或等待人数变化。"));
            return;
        }

        waitingForTeam.remove(player.getUUID());
        teamSelectedPlayers.add(player.getUUID());

        // TEAM_SELECT uses ATTACK / DEFEND as the temporary faction id. The
        // winning formation replaces it after both formation votes finish.
        ClassCountManager.getInstance().setPlayerFaction(player.getUUID(), resolvedTeam);
        ClassCountManager.getInstance().setPlayerTeam(player.getUUID(), resolvedTeam);
        if ("ATTACK".equals(resolvedTeam)) {
            TeamManager.joinAttackTeam(player.server, player.getName().getString());
        } else {
            TeamManager.joinDefendTeam(player.server, player.getName().getString());
        }
        PlayerMatchStatsManager.getInstance().onTeamSelected(player, resolvedTeam);

        Espetro.LOGGER.info("玩家 {} 选择了队伍 {}", player.getName().getString(), resolvedTeam);
        broadcastTeamSelectState();
    }

    public void forceStartCommanderVote() {
        if (currentPhase == GamePhase.TEAM_SELECT && teamSelectedPlayers.isEmpty()) {
            Espetro.LOGGER.info("没有玩家选择队伍！");
            return;
        }
        if (currentPhase == GamePhase.TEAM_SELECT) {
            finishTeamSelect();
            return;
        }
        startBothCommanderVote();
    }

    private void finishTeamSelect() {
        if (currentPhase != GamePhase.TEAM_SELECT) {
            return;
        }
        // Players who did not choose during the free-selection window remain
        // in the battlefield waiting area and may still join later as
        // reinforcements through the same server-authoritative side picker.
        midGameJoiners.addAll(waitingForTeam);
        waitingForTeam.clear();
        broadcastTeamSelectState(false);
        // 选边结束 → 分配提示页（5s）→ 并行指挥官投票
        startTeamAssignShow();
    }

    private void broadcastTeamSelectState() {
        broadcastTeamSelectState(true);
    }

    private void broadcastTeamSelectState(boolean active) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        int attack = countPlayersOnTeam("ATTACK");
        int defend = countPlayersOnTeam("DEFEND");
        int remaining = Math.max(0,
            GameConfig.getTeamSelectSeconds() - teamSelectTickCounter / TICKS_PER_SECOND);
        long end = server.overworld().getGameTime() + remaining * 20L;
        // 队伍平衡锁定：差异达到上限时锁定人多的一方
        String lockedTeam = null;
        int diff = Math.abs(attack - defend);
        if (diff >= TEAM_BALANCE_MAX_DIFF) {
            lockedTeam = attack > defend ? "ATTACK" : "DEFEND";
        }
        PreMatchScreens.broadcastTeamSelectState(attack, defend, remaining, end, active, lockedTeam,
            getFinalFactionImageClassStatic("ATTACK"), getFinalFactionImageClassStatic("DEFEND"));
    }

    /** 向所有 midGameJoiner（尚未选边）发送最新的队伍人数和编制图片。 */
    private void broadcastMidGameTeamState() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || midGameJoiners.isEmpty()) return;

        int attack = countPlayersOnTeam("ATTACK");
        int defend = countPlayersOnTeam("DEFEND");
        long end = server.overworld().getGameTime() + 999999L;
        String atkImg = FactionDisplay.getFactionSelectionImageStatic(
            ClassSelectManager.getInstance().getFinalAttackClass());
        String defImg = FactionDisplay.getFactionSelectionImageStatic(
            ClassSelectManager.getInstance().getFinalDefendClass());

        for (UUID uuid : midGameJoiners) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null || ClassCountManager.getInstance().getPlayerTeam(uuid) != null) continue;
            String myTeam = null; // 尚未选边
            NetworkManager.NET.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new TeamSelectStatePacket(attack, defend, 0, end, false, myTeam, null, atkImg, defImg));
        }
    }

    /** 静态辅助：获取 faction 的 selectionImage，避免循环依赖。 */
    private static String getFinalFactionImageClassStatic(String team) {
        String id = "ATTACK".equals(team)
            ? ClassSelectManager.getInstance().getFinalAttackClass()
            : ClassSelectManager.getInstance().getFinalDefendClass();
        return FactionDisplay.getFactionSelectionImageStatic(id);
    }

    private int countPlayersOnTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return 0;
        int count = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String pt = ClassCountManager.getInstance().getPlayerTeam(player.getUUID());
            if (team.equals(pt)) count++;
        }
        return count;
    }

    // ========== 阶段流转 ==========

    /**
     * 开始守方指挥官投票
     */
    private void startDefendCommanderVote() {
        setPhase(GamePhase.DEFEND_COMMANDER_VOTE);
        VoteManager.getInstance().initPlayers();
        VoteManager.getInstance().startDefendVote();
    }

    /**
     * 开始攻方指挥官投票
     */
    private void startAttackCommanderVote() {
        setPhase(GamePhase.ATTACK_COMMANDER_VOTE);
        VoteManager.getInstance().initPlayers();
        VoteManager.getInstance().startAttackVote();
    }

    /**
     * 开始守方编制选择
     */
    private void startDefendFactionSelect() {
        setPhase(GamePhase.DEFEND_FACTION_SELECT);
        ClassSelectManager.getInstance().startDefendSelecting();
    }

    /**
     * 开始攻方编制选择
     */
    private void startAttackFactionSelect() {
        setPhase(GamePhase.ATTACK_FACTION_SELECT);
        ClassSelectManager.getInstance().startAttackSelecting();
    }

    /**
     * AAS: ATTACK then DEFEND. RAAS: random first side, stored on this manager.
     */
    private void startFirstFactionSelect() {
        ClassSelectManager.getInstance().initFactionPool();
        if (TeamDisplayNames.isSymmetricMode()) {
            firstFactionSelectTeam = java.util.concurrent.ThreadLocalRandom.current().nextBoolean()
                ? "ATTACK" : "DEFEND";
            Espetro.LOGGER.info("RAAS 编制选择先手: {}", firstFactionSelectTeam);
        } else {
            firstFactionSelectTeam = "ATTACK";
        }
        startFactionSelectForTeam(firstFactionSelectTeam);
    }

    private void startFactionSelectForTeam(String team) {
        if ("DEFEND".equals(team)) {
            startDefendFactionSelect();
        } else {
            startAttackFactionSelect();
        }
    }

    private void onFactionSelectFinished() {
        ClassSelectManager.getInstance().finishCurrentSelecting();
        String currentTeam = currentPhase.getActiveTeam();
        if (firstFactionSelectTeam != null && firstFactionSelectTeam.equals(currentTeam)) {
            startFactionSelectForTeam("ATTACK".equals(firstFactionSelectTeam) ? "DEFEND" : "ATTACK");
            return;
        }
        startFactionReveal();
    }

    public String getFirstFactionSelectTeam() {
        return firstFactionSelectTeam;
    }

    /**
     * 开始部署阶段
     */
    private void startDeploying() {
        battleStartPending = false;
        setPhase(GamePhase.DEPLOYING);
        deployTickCounter = 0;
        factionRevealTickCounter = 0;
        deployClassSelected.clear();
        BastionManager.getInstance().reset();
        TeamPackManager.getInstance().reset();
        barrier.remove();

        // AAS: 部署阶段防守方可用前哨。RAAS: 双方都不可用。
        if (TeamDisplayNames.isSymmetricMode()) {
            Espetro.LOGGER.info("RAAS 模式跳过前哨基地激活（双方均不可用）");
        } else {
            OutpostManager.getInstance().activate();
        }

        // 编制选择最终处理
        ClassSelectManager.getInstance().finalizeSelection();

        // 布防阶段开始计时；达到各类型首次部署延迟后由系统自动生成首批载具。
        ClassSelectManager selection = ClassSelectManager.getInstance();
        scheduleInitialFactionVehicles(selection);

        // 所有玩家先进入统一等待点，选择部署点后才进入战场。
        teleportAllToSpawnPoints();
        // Bastion.reset() 会清除位置锁；只按未选边记录恢复受影响玩家，
        // 不在服务器 tick 中扫描全部在线玩家。
        restoreRecordedUnassignedHolds();
        if (!TeamDisplayNames.isSymmetricMode()) {
            barrier.place();
        }

        // 广播职业选择界面给所有玩家（部署阶段可选职业）
        broadcastClassSelectionForDeploy();

        if (TeamDisplayNames.isSymmetricMode()) {
            Espetro.LOGGER.info("部署阶段开始，持续{}秒（RAAS：无攻方等待屏障）",
                GameConfig.getDeployTimeoutSeconds());
        } else {
            Espetro.LOGGER.info("防守部署阶段开始，持续{}秒，攻方等待区域边长{}格，高{}格",
                GameConfig.getDeployTimeoutSeconds(), AttackWaitingBarrier.SIDE, AttackWaitingBarrier.HEIGHT);
        }
    }

    /**
     * 开始双方最终编制揭示阶段。
     */
    private void startFactionReveal() {
        setPhase(GamePhase.FACTION_REVEAL);
        factionRevealTickCounter = 0;

        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        PreMatchScreens.broadcastFactionRevealScreen(
            selectManager.getFinalAttackClass(),
            selectManager.getFinalDefendClass(),
            GameConfig.getFactionRevealSeconds()
        );

        Espetro.LOGGER.info("双方编制揭示开始，持续{}秒", GameConfig.getFactionRevealSeconds());
    }

    /**
     * 首发载具以布防阶段开始时刻为统一计时原点。到期前不加载出生区块；到期后
     * VehicleManager 分批异步加载并自动生成，战斗阶段不会被较长延迟阻塞。
     */
    private void scheduleInitialFactionVehicles(ClassSelectManager selection) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        String attackFaction = selection.getFinalAttackClass();
        String defendFaction = selection.getFinalDefendClass();
        VehicleManager vehicleManager = VehicleManager.getInstance();
        vehicleManager.armInitialDeployCooldowns(
            attackFaction, "ATTACK", defendFaction, "DEFEND");

        ServerLevel level = BattlefieldContext.requireBattlefield(server);
        long deploymentStartedAt = System.currentTimeMillis();
        int attackCount = prepareInitialFactionVehiclesForTeam(
            "ATTACK", attackFaction, level, deploymentStartedAt);
        int defendCount = prepareInitialFactionVehiclesForTeam(
            "DEFEND", defendFaction, level, deploymentStartedAt);
        int deployedNow = vehicleManager.activateInitialVehicleDeployment();
        VehicleManager.InitialDeploymentStatus status =
            vehicleManager.getInitialDeploymentStatus();
        Espetro.LOGGER.info(
            "首批载具自动部署已安排: 攻方{}辆，守方{}辆，立即落地{}辆，等待{}辆",
            attackCount, defendCount, deployedNow, status.pending());
    }

    private int prepareInitialFactionVehiclesForTeam(
        String team, String factionId, ServerLevel level, long deploymentStartedAt
    ) {
        if (factionId == null || factionId.isBlank()) return 0;
        return VehicleManager.getInstance().prepareInitialVehicles(
            factionId, team, level, deploymentStartedAt);
    }

    /**
     * 部署阶段开始时广播统一部署界面
     */
    private void broadcastClassSelectionForDeploy() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String team = ClassCountManager.getInstance().getPlayerTeam(player.getUUID());
            if (team == null) continue;

            // 发送统一部署主界面（集成职业选择、复活点选择、载具部署、地图）
            DeployScreenSync.queueUnifiedDeployScreen(
                player, GameConfig.getDeployTimeoutSeconds(), true);
        }
        // 职业选择界面已通过 UnifiedDeployScreen 自动打开，不再发送聊天消息
    }

    /** 将所有已选队玩家放入统一部署等待状态。 */
    private void teleportAllToSpawnPoints() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        ClassCountManager countManager = ClassCountManager.getInstance();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            String team = countManager.getPlayerTeam(uuid);
            if (team == null) {
                team = Espetro.getPlayerTeam(player);
            }
            if (team == null) {
                String factionId = countManager.getPlayerFaction(uuid);
                if (factionId != null) {
                    team = getTeamFromFactionStatic(factionId);
                }
            }
            if (team != null) {
                prepareDeploySelection(player, team);
            }
        }
    }

    private void saveTeamSpawnAsDeployPoint(ServerPlayer player, String team) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        ServerLevel overworld = BattlefieldContext.requireBattlefield(player.server);
        BlockPos deployPos = new BlockPos((int) spawn.x, (int) spawn.y, (int) spawn.z);
        BastionManager.getInstance().savePlayerDeployPoint(player, deployPos, overworld);
    }

    private void prepareDeploySelection(ServerPlayer player, String team) {
        saveTeamSpawnAsDeployPoint(player, team);
        BastionManager.getInstance().activatePlayerBastionSelection(player.getUUID());
        PlayerHoldStates.applyDeploymentWaitingState(player);
        if ("DEFEND".equals(team)) {
            OutpostManager.getInstance().prepareDeployTargets(BattlefieldContext.requireBattlefield(player.server));
        }
    }

    /**
     * 部署阶段Tick处理
     */
    public void onDeployTick() {
        barrier.processWork();
        // 每秒更新一次
        if (deployTickCounter % TICKS_PER_SECOND == 0) {
            broadcastDefenseSetupActionBar(getDeployTimeRemainingSeconds());
        }

        // 部署阶段结束后先分帧移除已放置屏障，再进入 BATTLE。
        if (deployTickCounter >= GameConfig.getDeployTimeoutSeconds() * TICKS_PER_SECOND) {
            startBattle();
        }
    }

    public int getDeployTimeRemainingSeconds() {
        if (currentPhase != GamePhase.DEPLOYING) {
            return 0;
        }
        return Math.max(0, GameConfig.getDeployTimeoutSeconds() - (deployTickCounter / TICKS_PER_SECOND));
    }

    /** Current server-authoritative battle timer; {@code -1} means not in battle. */
    public int getBattleTimeRemainingSeconds() {
        if (currentPhase != GamePhase.BATTLE) {
            return -1;
        }
        int timeoutSeconds = GameConfig.getBattleTimeoutSeconds();
        if (timeoutSeconds <= 0) {
            return -1;
        }
        return Math.max(0, timeoutSeconds - (battleTickCounter / TICKS_PER_SECOND));
    }

    private void broadcastDefenseSetupActionBar(int secondsRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        ClassCountManager countManager = ClassCountManager.getInstance();
        for (UUID uuid : teamSelectedPlayers) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) continue;

            String team = countManager.getPlayerTeam(uuid);
            if (team == null) {
                team = getTeamFromFactionStatic(countManager.getPlayerFaction(uuid));
            }

            String message;
            if (TeamDisplayNames.isSymmetricMode()) {
                message = "§e部署中[" + secondsRemaining + "秒]";
            } else if ("ATTACK".equals(team)) {
                message = "§c等待进攻§e[" + secondsRemaining + "秒]";
            } else {
                message = "§9部署防线§e[" + secondsRemaining + "秒]";
            }
            MatchBroadcasts.sendWaitingStatus(player, message, true);
        }
    }

    /**
     * 恢复本次运行记录的临时屏障，并扫描预期屏障外壳清除崩服/异常退出留下的孤儿屏障。
     * 服务器停机时也会显式调用。
     */
    public int cleanupTemporaryBarriers(MinecraftServer server) {
        return barrier.remove(server);
    }

    /**
     * 开始对战
     */
    private void startBattle() {
        if (!battleStartPending) {
            battleStartPending = true;
            barrier.queueRemoval();
        }
        if (barrier.hasPendingWork()) {
            return;
        }
        battleStartPending = false;
        barrier.releaseLevel();

        // 开战前先销毁虚拟前哨，确保 BATTLE 阶段的任何请求都无法再使用。
        boolean hadActiveOutposts = OutpostManager.getInstance().isAvailable();
        OutpostManager.getInstance().deactivate();
        setPhase(GamePhase.BATTLE);
        battleTickCounter = 0;
        // 有开局刷新延时的首发载具自此刻起才开始计时（部署阶段只登记不计时）。
        VehicleManager.getInstance().onBattleStarted();
        if (hadActiveOutposts) {
            Espetro.broadcastToTeam("DEFEND", "§c⚔ 攻方已开始进攻，前哨基地已销毁！");
        }

        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        // 未选择部署点的玩家即使布防结束也继续留在统一等待点；
        // 只有部署命令成功后才会由 BastionManager.clearWaiting() 解除。
        BastionManager bastionManager = BastionManager.getInstance();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isObserver(player.getUUID())) {
                // 观战者：进入战局即解除失明与锁位（不再走"未分配"的隐藏态）。
                PlayerHoldStates.releaseObserverHold(player);
            } else if (isRecordedUnassigned(player)) {
                // 开战事件不得像普通已部署玩家一样摘除失明。
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.CURRENT_LOCK);
            } else if (bastionManager.isWaitingForBastion(player.getUUID())) {
                PlayerHoldStates.applyDeploymentWaitingState(player);
                DeployScreenSync.queueUnifiedDeployScreen(player, -1);
            } else {
                player.removeEffect(MobEffects.BLINDNESS);
                PlayerHoldStates.applyBattlefieldMiningRestriction(player);
            }
        }

        // 初始化兵力统计
        TroopCountManager.getInstance().initializeTroops();
        CommanderGovernanceManager.getInstance().syncCommandersFromVoteManager();

        // 自动首发与后续部署一致：载具被摧毁时再扣 troopValue，不在开战时预扣。

        org.espetro.network.MatchBroadcasts.broadcastTroopCounts(
            TroopCountManager.getInstance().getAttackTroops(),
            TroopCountManager.getInstance().getDefendTroops()
        );

        // 广播开始消息
        Espetro.broadcastToAll("§6========================================");
        Espetro.broadcastToAll("§a§l★ 对战开始！ ★");
        Espetro.broadcastToAll("§6========================================");

        Espetro.LOGGER.info("===== 对战开始 =====");
    }

    private void onFactionRevealTick() {
        factionRevealTickCounter++;
        if (factionRevealTickCounter >= GameConfig.getFactionRevealSeconds() * TICKS_PER_SECOND) {
            startDeploying();
        }
    }

    /**
     * 投票/揭示阶段：观战者不参与投票，只显示"当前正在投票，请等待"（动作栏，1.5 秒刷新一次）。
     */
    private void notifySpectatorsDuringVoting() {
        if (currentPhase == null) return;
        boolean voting = switch (currentPhase) {
            case MAP_VOTE, MAP_REVEAL, MAP_LOADING, TEAM_SELECT, TEAM_ASSIGN_SHOW,
                 COMMANDER_VOTE, DEFEND_COMMANDER_VOTE, ATTACK_COMMANDER_VOTE,
                 DEFEND_FACTION_SELECT, ATTACK_FACTION_SELECT, FACTION_REVEAL -> true;
            default -> false;
        };
        if (!voting) return;
        if (++votingNoticeTicker % 30 != 0) return;
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isObserver(player.getUUID())) {
                MatchBroadcasts.sendWaitingStatus(player, "§e当前正在投票，请等待", true);
            }
        }
    }

    // ========== 服务器Tick ==========

    public void onServerTick() {
        notifySpectatorsDuringVoting();
        switch (currentPhase) {
            case LOBBY, WAITING_FOR_PLAYERS:
                // 人数变化时立即推；否则最多 5 秒一次，降低主城挂机广播负载
                if (deployTickCounter % TICKS_PER_SECOND == 0) {
                    broadcastHubStatusThrottled();
                }
                break;
            case MAP_VOTE:
                MinecraftServer mapVoteServer = Espetro.getServer();
                if (mapVoteServer != null) {
                    MapVoteManager.getInstance().onServerTick(mapVoteServer);
                }
                break;
            case MAP_REVEAL:
                // 地图揭晓页 5 秒后进入装载
                mapRevealTickCounter++;
                if (mapRevealTickCounter >= 5 * TICKS_PER_SECOND) {
                    finishMapReveal();
                }
                break;
            case MAP_LOADING:
                break;
            case TEAM_SELECT:
                teamSelectTickCounter++;
                if (teamSelectTickCounter % TICKS_PER_SECOND == 0) {
                    broadcastTeamSelectState();
                }
                if (teamSelectTickCounter >= GameConfig.getTeamSelectSeconds() * TICKS_PER_SECOND) {
                    finishTeamSelect();
                }
                break;
            case TEAM_ASSIGN_SHOW:
                // 分配提示页 5 秒后进入并行指挥官投票
                teamAssignShowTickCounter++;
                if (teamAssignShowTickCounter >= 5 * TICKS_PER_SECOND) {
                    finishTeamAssignShow();
                }
                break;
            case COMMANDER_VOTE:
                VoteManager.getInstance().onServerTick();
                if (VoteManager.getInstance().isCurrentVoteTimedOut()) {
                    finishBothCommanderVote();
                }
                break;
            case DEFEND_COMMANDER_VOTE:
                VoteManager.getInstance().onServerTick();
                if (VoteManager.getInstance().isCurrentVoteTimedOut()) {
                    VoteManager.getInstance().finishCurrentVote();
                    startFirstFactionSelect();
                }
                break;
            case ATTACK_COMMANDER_VOTE:
                VoteManager.getInstance().onServerTick();
                if (VoteManager.getInstance().isCurrentVoteTimedOut()) {
                    VoteManager.getInstance().finishCurrentVote();
                    startDefendCommanderVote();
                }
                break;
            case DEFEND_FACTION_SELECT:
                ClassSelectManager.getInstance().onServerTick();
                if (ClassSelectManager.getInstance().isCurrentSelectTimedOut()) {
                    onFactionSelectFinished();
                }
                break;
            case ATTACK_FACTION_SELECT:
                ClassSelectManager.getInstance().onServerTick();
                if (ClassSelectManager.getInstance().isCurrentSelectTimedOut()) {
                    onFactionSelectFinished();
                }
                break;
            case FACTION_REVEAL:
                onFactionRevealTick();
                break;
            case DEPLOYING:
                org.espetro.vehicle.VehicleManager.getInstance().onServerTick();
                // 每隔 5 秒向中途加入但尚未选边的玩家同步最新人数和编制图片
                if (deployTickCounter % (5 * TICKS_PER_SECOND) == 0) {
                    broadcastMidGameTeamState();
                }
                onDeployTick();
                break;
            case BATTLE:
                org.espetro.vehicle.VehicleManager.getInstance().onServerTick();
                // 每隔 5 秒向中途加入但尚未选边的玩家同步最新人数和编制图片
                if (battleTickCounter % (5 * TICKS_PER_SECOND) == 0) {
                    broadcastMidGameTeamState();
                }
                // 开战瞬间可能仍有屏障分帧恢复队列
                barrier.processWork();
                barrier.releaseLevelIfIdle();
                MinecraftServer battleServer = Espetro.getServer();
                if (battleServer != null) {
                    CommanderGovernanceManager.getInstance().onServerTick(battleServer);
                }
                // 战局倒计时
                battleTickCounter++;
                int battleTimeoutSeconds = GameConfig.getBattleTimeoutSeconds();
                if (battleTimeoutSeconds > 0 && battleTickCounter % TICKS_PER_SECOND == 0) {
                    int remaining = battleTimeoutSeconds - (battleTickCounter / TICKS_PER_SECOND);
                    if (remaining <= 0) {
                        Espetro.broadcastToAll("§c" + TeamDisplayNames.displayName("ATTACK")
                            + "未在一小时内占领所有据点，"
                            + TeamDisplayNames.displayName("DEFEND") + "获胜！");
                        endRound("DEFEND", true);
                    }
                    MatchBroadcasts.broadcastBattleTimer(remaining);
                }
                break;
            case ROUND_END:
                roundEndTickCounter++;
                if (roundEndTickCounter >= GameConfig.getRoundEndSeconds() * TICKS_PER_SECOND) {
                    beginCleanup();
                }
                break;
            case CLEANUP:
                break;
            default:
                break;
        }
        deployTickCounter++;
    }

    /**
     * 部署阶段重置 Bastion 记录后，按选边状态集合恢复未选阵营玩家的等待锁。
     * 此方法只在阶段切换事件执行一次，复杂度与实际未选边人数相关。
     */
    private void restoreRecordedUnassignedHolds() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (UUID playerId : waitingForTeam) {
            restoreUnassignedHold(server, playerId);
        }
        for (UUID playerId : midGameJoiners) {
            restoreUnassignedHold(server, playerId);
        }
    }

    private void restoreUnassignedHold(MinecraftServer server, UUID playerId) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerId);
        if (player != null && isRecordedUnassigned(player)) {
            PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.AUTO);
        }
    }

    private boolean isRecordedUnassigned(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (!waitingForTeam.contains(playerId) && !midGameJoiners.contains(playerId)) {
            return false;
        }
        String team = ClassCountManager.getInstance().getPlayerTeam(playerId);
        if (team == null) {
            team = Espetro.getPlayerTeam(player);
        }
        return !"ATTACK".equals(team) && !"DEFEND".equals(team);
    }

    // ========== 工具方法 ==========

    private void teleportToTeamSpawn(ServerPlayer player, String team) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        ServerLevel overworld = BattlefieldContext.requireBattlefield(player.server);
        player.teleportTo(overworld, spawn.x, spawn.y, spawn.z, spawn.yaw, 0f);

        BlockPos deployPos = new BlockPos((int) spawn.x, (int) spawn.y, (int) spawn.z);
        org.espetro.bastion.BastionManager.getInstance().savePlayerDeployPoint(player, deployPos, overworld);
    }

    public static String getTeamFromFactionStatic(String factionId) {
        if (factionId == null) return "DEFEND";

        if ("ATTACK".equalsIgnoreCase(factionId) || "DEFEND".equalsIgnoreCase(factionId)) {
            return factionId.toUpperCase();
        }

        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.FactionData factionData = loader.getFaction(factionId);
        if (factionData != null && factionData.team != null) {
            return factionData.team;
        }

        FactionConfig config = FactionConfigLoader.loadFaction(factionId);
        if (config != null && config.team != null) {
            return config.team;
        }

        String lower = factionId.toLowerCase();
        if (lower.contains("attack") ||
            lower.contains("pla") ||
            lower.contains("russia") ||
            lower.contains("rus") ||
            lower.contains("militia")) {
            return "ATTACK";
        }

        return "DEFEND";
    }

    // ========== 重置 ==========

    public void resetGame() {
        MinecraftServer server = Espetro.getServer();
        forceStopInProgress = false;
        // 仅本局有效：重置对局时关闭装备完全解锁模式
        FreeUnlockManager.onRoundEnded();
        clearRoundRuntime(false);
        pendingRoundWinner = null;
        pendingMap = null;
        currentPhase = GamePhase.LOBBY;
        // 局重置：观察者恢复为正常玩家
        clearAllObservers();

        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                clearPlayerRoundAssignment(player);
                PlayerHoldStates.applyHubState(player);
            }
            if (BattlefieldContext.isActive()) {
                BattlefieldWorldManager.getInstance().cleanupBattlefield(server, null, () -> {
                    setPhase(GamePhase.LOBBY);
                    broadcastHubStatus();
                });
            } else {
                setPhase(GamePhase.LOBBY);
                broadcastHubStatus();
            }
        }
        Espetro.LOGGER.info("游戏状态已重置到主城");
    }

    /**
     * Immediately aborts any phase and returns every online player to the
     * overworld hub. Battlefield cleanup is serialized with an in-flight map
     * activation, so an administrator can safely use this during MAP_LOADING.
     *
     * <p>The battlefield is detached from the server tick list, its storage
     * handles are closed without saving match damage, and the entire save-side
     * dimension copy is deleted. A later round imports a new copy from the
     * read-only EsWorld template.</p>
     *
     * @return {@code false} only when there is no server or another forced stop
     * is already waiting for battlefield cleanup
     */
    public boolean forceStopGame(
        MinecraftServer server,
        Consumer<BattlefieldWorldManager.Result> onComplete
    ) {
        if (server == null || forceStopInProgress) {
            return false;
        }

        forceStopInProgress = true;
        GamePhase stoppedPhase = currentPhase;
        ActiveMapConfig stoppedMap = pendingMap != null ? pendingMap : BattlefieldContext.getOrNull();
        int playerCount = server.getPlayerCount();

        setPhase(GamePhase.CLEANUP);
        // 强制结束：观察者恢复为正常玩家
        clearAllObservers();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            clearPlayerRoundAssignment(player);
            player.removeAllEffects();
            PlayerHoldStates.applyHubState(player);
        }

        // 仅本局有效：强制结束时关闭装备完全解锁模式
        FreeUnlockManager.onRoundEnded();
        clearRoundRuntime(true);
        pendingMap = null;
        pendingRoundWinner = null;
        SupplyManager.getInstance().reset();

        Espetro.broadcastToAll("§6[Espetro] 管理员已强制结束本局，正在返回主城。");
        BattlefieldWorldManager.getInstance().cleanupBattlefield(server, stoppedMap, cleanupResult -> {
            forceStopInProgress = false;
            setPhase(GamePhase.LOBBY);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                clearPlayerRoundAssignment(player);
                player.removeAllEffects();
                PlayerHoldStates.applyHubState(player);
                MatchBroadcasts.sendOpenHubScreen(player, server.getPlayerCount(),
                    "对局已结束，等待管理员开始下一局");
            }
            broadcastHubStatus();
            if (cleanupResult.success()) {
                Espetro.broadcastToAll(
                    "§a[Espetro] 所有玩家已返回主城，战场存档副本已删除。");
            } else {
                Espetro.broadcastToAll(
                    "§c[Espetro] 玩家已返回主城，但战场存档副本删除失败："
                        + cleanupResult.error());
            }
            Espetro.LOGGER.info(
                "管理员强制终止战局完成: previousPhase={}, map={}, players={}, terrainReset={}",
                stoppedPhase, stoppedMap == null ? "none" : stoppedMap.dimensionId,
                playerCount, cleanupResult.success());
            if (onComplete != null) {
                try {
                    onComplete.accept(cleanupResult);
                } catch (Exception e) {
                    Espetro.LOGGER.error("强制终止战局完成回调执行失败", e);
                }
            }
        });
        return true;
    }

    private void clearRoundRuntime(boolean clearStats) {
        ResupplySessionManager.clearAll();
        waitingForTeam.clear();
        teamSelectedPlayers.clear();
        midGameJoiners.clear();
        assignedTeams.clear();
        deployClassSelected.clear();
        deployTickCounter = 0;
        battleTickCounter = 0;
        factionRevealTickCounter = 0;
        teamSelectTickCounter = 0;
        roundEndTickCounter = 0;
        firstFactionSelectTeam = "ATTACK";
        barrier.remove();

        MapVoteManager.getInstance().reset();
        VoteManager.getInstance().reset();
        ClassSelectManager.getInstance().reset();
        SquadManager.getInstance().reset();
        TeamPackManager.getInstance().reset();
        OutpostManager.getInstance().reset();
        CommanderSkillManager.getInstance().reset();
        CommanderGovernanceManager.getInstance().reset();
        ClassCountManager.getInstance().resetAll();
        VehicleManager.getInstance().reset();
        org.espetro.bastion.FortificationManager.getInstance().reset();
        org.espetro.bastion.FobSupplyTracker.clearAll();
        TroopCountManager.getInstance().resetTroops();
        // 回城 / 回合清理：摧毁全部兵站（不扣兵力），在卸载战场维度前清掉运行时结构
        // （内部会取消 HAB 读条并临时加载卸载区核心后清理）
        BastionManager.getInstance().destroyAllBastionsForMatchEnd();
        org.espetro.runtime.ServerRuntimeMaintenance.getInstance().reset();
        if (clearStats) {
            PlayerMatchStatsManager.getInstance().resetMatch();
        }
    }

    public void onPlayerJoin(ServerPlayer player) {
        PlayerMatchStatsManager.getInstance().onPlayerJoin(player);
        // 入服时主动推送指挥官技能数据，确保客户端轮盘可立即显示技能入口
        MatchBroadcasts.sendCommanderSkillSync(player);

        // 观察者重连：本局内保持旁观，不重新编队、不强制回城/等待点。
        if (isObserver(player.getUUID())) {
            player.removeAllEffects();
            player.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
            player.setHealth(player.getMaxHealth());
            player.sendSystemMessage(Component.literal(
                "§e你仍是观察者，本局结束后自动恢复为正常玩家。"));
            return;
        }

        // 入服即同步当前游戏阶段，防止客户端阶段停留在默认主城导致
        // 误判主城（跳过上车读条、放行原版交互）而无法在战局内上车。
        org.espetro.network.NetworkManager.NET.send(
            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
            new org.espetro.network.GamePhaseSyncPacket(
                currentPhase,
                getCurrentMapFolder(),
                BattlefieldContext.getObjectiveMode()));

        switch (currentPhase) {
            case LOBBY, WAITING_FOR_PLAYERS -> {
                PlayerHoldStates.forcePlayerToHub(player);
                MatchBroadcasts.sendOpenHubScreen(player, player.server.getPlayerCount(),
                    "等待管理员开始下一局");
            }
            case MAP_VOTE -> {
                // 投票中途加入：直接 MatchHold，勿 hub（会摘失明）。
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.HUB_HIGH);
                MapVoteManager.getInstance().syncToPlayer(player);
            }
            case MAP_REVEAL -> {
                // 地图揭晓页中途加入：MatchHold + 同步揭晓页
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.AUTO);
                if (pendingMap != null) {
                    PreMatchScreens.broadcastMapRevealScreenTo(player, pendingMap,
                        Math.max(1, 5 - mapRevealTickCounter / TICKS_PER_SECOND));
                }
            }
            case MAP_LOADING -> {
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.AUTO);
                player.sendSystemMessage(Component.literal("§e战场正在装载，请稍候。"));
            }
            case TEAM_SELECT -> {
                String existing = assignedTeams.get(player.getUUID());
                if (existing != null) {
                    // 重连玩家直接还原原队伍
                    clearPlayerRoundAssignment(player);
                    applyTeamAssignmentToPlayer(player, existing);
                    PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.BATTLEFIELD_WAIT);
                    teamSelectedPlayers.add(player.getUUID());
                    broadcastTeamSelectState();
                } else {
                    // 新玩家在自动分配阶段加入：纳入下次分配或走 midGameJoin
                    waitingForTeam.add(player.getUUID());
                    clearPlayerRoundAssignment(player);
                    PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.BATTLEFIELD_WAIT);
                    PreMatchScreens.sendOpenFactionScreen(player);
                    broadcastTeamSelectState();
                }
            }
            case TEAM_ASSIGN_SHOW -> {
                // 分配展示页中途加入：还原/分配队伍后重发提示页
                String existing = assignedTeams.get(player.getUUID());
                if (existing != null) {
                    clearPlayerRoundAssignment(player);
                    applyTeamAssignmentToPlayer(player, existing);
                } else {
                    clearPlayerRoundAssignment(player);
                    applyTeamAssignmentToPlayer(player, "ATTACK");
                }
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.BATTLEFIELD_WAIT);
                teamSelectedPlayers.add(player.getUUID());
                PreMatchScreens.sendTeamAssignShowTo(player,
                    ClassCountManager.getInstance().getPlayerTeam(player.getUUID()),
                    Math.max(1, 5 - teamAssignShowTickCounter / TICKS_PER_SECOND));
            }
            case COMMANDER_VOTE -> {
                // 指挥官投票阶段中途加入：还原队伍后直接打开投票界面
                String existing = assignedTeams.get(player.getUUID());
                if (existing != null) {
                    clearPlayerRoundAssignment(player);
                    applyTeamAssignmentToPlayer(player, existing);
                    PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.BATTLEFIELD_WAIT);
                    teamSelectedPlayers.add(player.getUUID());
                } else {
                    clearPlayerRoundAssignment(player);
                    applyTeamAssignmentToPlayer(player, "ATTACK");
                    PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.BATTLEFIELD_WAIT);
                    teamSelectedPlayers.add(player.getUUID());
                }
                org.espetro.team.VoteManager voteManager = VoteManager.getInstance();
                String myTeam = ClassCountManager.getInstance().getPlayerTeam(player.getUUID());
                if (myTeam != null) {
                    // 与中途加入流程一致：纳入投票名单，否则编制结算后的
                    // 阵营回填（finalizeSelection→updatePlayerFactions）会漏掉该玩家。
                    if ("ATTACK".equals(myTeam)) {
                        voteManager.addAttackPlayer(player.getUUID());
                    } else if ("DEFEND".equals(myTeam)) {
                        voteManager.addDefendPlayer(player.getUUID());
                    }
                    if (voteManager.isVotingActive()) {
                        String[] candidateNames = voteManager.getPlayerNamesForTeam(myTeam);
                        PreMatchScreens.sendCommanderVoteScreenToPlayer(
                            player, myTeam, candidateNames, voteManager.getRemainingSeconds());
                    }
                }
            }
            case ROUND_END, CLEANUP -> {
                PlayerHoldStates.forcePlayerToHub(player);
                player.sendSystemMessage(Component.literal("§e本回合正在结算，请等待下一局。"));
            }
            default -> onMidGameJoin(player);
        }
    }

    /**
     * 退服前强制写回主城位置/重生点（主城维度持久不重置）。
     * 应在清空装备并保存 playerdata 之前调用。
     */
    public void onPlayerLeave(ServerPlayer player) {
        if (player != null) {
            PlayerHoldStates.forcePlayerToHub(player);
            onPlayerLeave(player.getUUID());
        }
    }

    public void onPlayerLeave(UUID uuid) {
        waitingForTeam.remove(uuid);
        teamSelectedPlayers.remove(uuid);
        midGameJoiners.remove(uuid);
        deployClassSelected.remove(uuid);
        MapVoteManager.getInstance().onPlayerLeave(uuid);
        VoteManager.getInstance().removePlayer(uuid);
        ClassSelectManager.getInstance().removePlayerVote(uuid);
        PlayerMatchStatsManager.getInstance().onPlayerLeave(uuid);
        BastionManager.getInstance().unlockPlayerPosition(uuid);
        BastionManager.getInstance().clearWaiting(uuid);
    }

    // ========== 战局中加入 ==========

    public boolean isMidGameJoiner(UUID uuid) {
        return midGameJoiners.contains(uuid);
    }

    public void removeMidGameJoiner(UUID uuid) {
        midGameJoiners.remove(uuid);
    }

    public void onMidGameJoin(ServerPlayer player) {
        // 检查是否有之前的队伍分配（掉线重连）
        String assigned = assignedTeams.get(player.getUUID());
        if (assigned != null) {
            midGameJoiners.add(player.getUUID());
            clearPlayerRoundAssignment(player);
            applyTeamAssignmentToPlayer(player, assigned);
            // applyTeamAssignmentToPlayer 只设置了 ATTACK/DEFEND 作为临时 factionId；
            // 重连后选职业需要实际的编制 ID（如 "us_army"）。
            assignFinalFactionToPlayer(player, assigned);
            // 重连直接进入部署阶段
            if (currentPhase == GamePhase.DEPLOYING || currentPhase == GamePhase.BATTLE) {
                prepareDeploySelection(player, assigned);
                DeployScreenSync.queueUnifiedDeployScreen(player, getDeployTimeRemainingSeconds());
            } else {
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.HUB_HIGH);
            }
            player.sendSystemMessage(Component.literal(
                "§a已还原你的队伍分配: " + TeamDisplayNames.coloredDisplayName(assigned)));
            return;
        }

        midGameJoiners.add(player.getUUID());
        clearPlayerRoundAssignment(player);
        // 中途加入先在主城高空等待点选边。未选边之前必须保持旁观、失明和位置锁，
        // 不能通过关闭界面或长时间不选择来恢复移动。
        PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.HUB_HIGH);

        // 只给该玩家同步阶段信息，避免全局广播
        org.espetro.network.NetworkManager.NET.send(
            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
            new org.espetro.network.GamePhaseSyncPacket(
                currentPhase,
                getCurrentMapFolder(),
                BattlefieldContext.getObjectiveMode()));
        PreMatchScreens.sendOpenFactionScreen(player);

        player.sendSystemMessage(Component.literal("§6========================================"));
        player.sendSystemMessage(Component.literal("§e⚡ 战场上需要增援！请选择你的阵营"));
        player.sendSystemMessage(Component.literal("§e按上方按钮选择 "
            + TeamDisplayNames.coloredDisplayName("ATTACK")
            + " §e或 " + TeamDisplayNames.coloredDisplayName("DEFEND")));
        player.sendSystemMessage(Component.literal("§6========================================"));

    }

    public void onMidGameTeamSelected(ServerPlayer player, String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        String factionId = "ATTACK".equals(team)
            ? selectManager.getFinalAttackClass()
            : selectManager.getFinalDefendClass();

        if (factionId == null) {
            factionId = team;
            Espetro.LOGGER.warn("战局中加入: {} 方编制未设置，使用默认值", team);
        }

        if ("ATTACK".equals(team)) {
            TeamManager.joinAttackTeam(server, player.getName().getString());
        } else {
            TeamManager.joinDefendTeam(server, player.getName().getString());
        }

        ClassCountManager.getInstance().setPlayerFaction(player.getUUID(), factionId);
        ClassCountManager.getInstance().setPlayerTeam(player.getUUID(), team);
        PlayerMatchStatsManager.getInstance().onTeamSelected(player, team);

        VoteManager voteManager = VoteManager.getInstance();
        if ("ATTACK".equals(team)) {
            voteManager.addAttackPlayer(player.getUUID());
        } else {
            voteManager.addDefendPlayer(player.getUUID());
        }

        teamSelectedPlayers.add(player.getUUID());

        // ===== 根据当前游戏阶段分别处理中途加入者的状态同步 =====
        switch (currentPhase) {

            case DEFEND_COMMANDER_VOTE, ATTACK_COMMANDER_VOTE, COMMANDER_VOTE -> {
                int voteRemaining = voteManager.getRemainingSeconds();
                if (voteRemaining > 0) {
                    PreMatchScreens.sendCommanderVoteScreenToPlayer(
                        player, team, voteManager.getPlayerNamesForTeam(team), voteRemaining);
                }
            }

            case DEFEND_FACTION_SELECT, ATTACK_FACTION_SELECT -> {
                String selectingTeam = currentPhase.getActiveTeam();
                int selectRemaining = selectManager.getRemainingSeconds();

                UUID cmdUuid = "ATTACK".equals(team) ? voteManager.getAttackCommander()
                    : voteManager.getDefendCommander();
                boolean isCmd = cmdUuid != null && cmdUuid.equals(player.getUUID());

                if (team.equals(selectingTeam)) {
                    PreMatchScreens.sendClassSelectScreen(player, team, isCmd, selectRemaining);
                } else {
                    PreMatchScreens.sendClassSelectScreen(player, team, false, 0);
                }
            }

            case DEPLOYING -> {
                // 部署阶段增援与开局玩家一致：先进入统一等待点，再选择部署点。
                player.removeAllEffects();
                prepareDeploySelection(player, team);

                // 发送统一部署主界面
                int remaining = getDeployTimeRemainingSeconds();
                DeployScreenSync.sendUnifiedDeployScreen(player, remaining);
                DeployScreenSync.sendDeployPointSync(player);
                MatchBroadcasts.sendWaitingStatus(player, TeamDisplayNames.isSymmetricMode()
                    ? "§e部署中[" + remaining + "秒]"
                    : ("ATTACK".equals(team)
                        ? "§c等待进攻§e[" + remaining + "秒]"
                        : "§9部署防线§e[" + remaining + "秒]"), true);
                player.sendSystemMessage(Component.literal(
                    "§a✅ 增援到达部署阶段！请在左侧面板选择职业和部署点"));

                Espetro.broadcastToTeam(team, "§e⚡ 增援到达！" + player.getName().getString()
                    + " 加入了 " + TeamDisplayNames.coloredDisplayName(team)
                    + " §7(部署中)");
            }

            case BATTLE -> {
                // 对战阶段增援也必须先选择部署点。
                player.removeAllEffects();

                BastionManager bastionManager = BastionManager.getInstance();
                SpawnPointConfig.SpawnPoint spawnPoint = SpawnPointConfig.getSpawnPoint(team);
                ServerLevel overworld = BattlefieldContext.requireBattlefield(server);
                bastionManager.savePlayerDeployPoint(player,
                    new BlockPos((int) spawnPoint.x, (int) spawnPoint.y, (int) spawnPoint.z),
                    overworld);
                bastionManager.activatePlayerBastionSelection(player.getUUID());
                PlayerHoldStates.applyDeploymentWaitingState(player);

                TroopCountManager troopMgr = TroopCountManager.getInstance();
                MatchBroadcasts.broadcastTroopCounts(
                    troopMgr.getAttackTroops(), troopMgr.getDefendTroops());

                String commanderName = "无";
                UUID commanderUuid = "ATTACK".equals(team)
                    ? voteManager.getAttackCommander()
                    : voteManager.getDefendCommander();
                if (commanderUuid != null) {
                    ServerPlayer commander = server.getPlayerList().getPlayer(commanderUuid);
                    if (commander != null) {
                        commanderName = commander.getName().getString();
                    }
                }

                player.sendSystemMessage(Component.literal("§a════════════════════════════════"));
                player.sendSystemMessage(Component.literal("§a你已作为增援加入"
                    + TeamDisplayNames.coloredDisplayName(team) + "§a！"));
                player.sendSystemMessage(Component.literal("§e编制: §f" + factionId));
                player.sendSystemMessage(Component.literal("§e指挥官: §f" + commanderName));
                player.sendSystemMessage(Component.literal("§a════════════════════════════════"));
                player.sendSystemMessage(Component.literal("§e⚠ 请先在战术面板选择部署点，再选择职业！"));

                DeployScreenSync.sendUnifiedDeployScreen(player, -1);
                DeployScreenSync.sendDeployPointSync(player);

                Espetro.broadcastToTeam(team, "§e⚡ 增援到达！" + player.getName().getString()
                    + " 加入了 " + TeamDisplayNames.coloredDisplayName(team));
            }

            default -> {
                // 其他阶段保持旁观者状态
            }
        }
    }

    public void onMidGameDeployComplete(ServerPlayer player) {
        boolean wasMidGameJoiner = midGameJoiners.remove(player.getUUID());
        PlayerHoldStates.applyBattlefieldMiningRestriction(player);

        // 每次部署完成都核对一次载具：重发全部活跃载具/补给站（spawn + teleport + 数据），
        // 确保客户端与服务端载具状态一致（部署瞬间客户端区块可能尚未就绪）。
        org.espetro.vehicle.VehicleManager.getInstance().resyncVehiclesForPlayer(player);

        // 落地后若已选职却未发装（等待点选职失败 / 背包被清），补发一次
        ClassEquipment.ensureEquippedIfNeeded(player);

        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.getUUID());

        if (factionId != null) {
            // 不再发送旧的 ClassSelectionScreen，由 UnifiedDeployScreen 统一处理
            if (wasMidGameJoiner) {
                player.sendSystemMessage(Component.literal("§a✅ 部署完成！"));
            } else {
                player.sendSystemMessage(Component.literal("§a✅ 已复活！"));
            }
        }
    }

    /**
     * End a battle from troop depletion, command, API or KubeJS. Winner is one
     * of ATTACK / DEFEND / DRAW. The actual world detach happens after the
     * configured result-screen delay.
     */
    public boolean endRound(String winner) {
        return endRound(winner, false);
    }

    /**
     * 结束当前战局。
     * @param attackerTimedOut 攻击方超时 → attack 票数归零
     */
    public boolean endRound(String winner, boolean attackerTimedOut) {
        if (currentPhase != GamePhase.BATTLE) {
            Espetro.LOGGER.warn("[endRound] 拒绝：当前阶段={} (非 BATTLE)", currentPhase);
            return false;
        }
        String normalized = winner == null ? "" : winner.trim().toUpperCase(Locale.ROOT);
        if (!"ATTACK".equals(normalized) && !"DEFEND".equals(normalized)
            && !"DRAW".equals(normalized)) {
            Espetro.LOGGER.warn("[endRound] 拒绝：非法赢家={}", normalized);
            return false;
        }
        pendingRoundWinner = normalized;
        BattlefieldContext.setLastRoundWinner(normalized);
        roundEndTickCounter = 0;
        barrier.remove();
        setPhase(GamePhase.ROUND_END);

        // —— 收集结算数据 ——
        TroopCountManager tcm = TroopCountManager.getInstance();
        int atkRaw = tcm.getAttackTroops();
        int defRaw = tcm.getDefendTroops();
        if (attackerTimedOut) atkRaw = 0;

        // 获取双方 faction show_name / name
        String atkFactionId = ClassSelectManager.getInstance().getFinalAttackClass();
        String defFactionId = ClassSelectManager.getInstance().getFinalDefendClass();
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        String atkShow = getFactionShowName(loader, atkFactionId, TeamDisplayNames.displayName("ATTACK"));
        String defShow = getFactionShowName(loader, defFactionId, TeamDisplayNames.displayName("DEFEND"));

        // 计算结果等级
        int diff, level;
        String winShow, loseShow;
        if ("DRAW".equals(normalized)) {
            diff = 0; level = 0;
            winShow = null; loseShow = null;
        } else if ("ATTACK".equals(normalized)) {
            diff = atkRaw - defRaw;
            level = calcResultLevel(diff);
            winShow = atkShow; loseShow = defShow;
        } else { // DEFEND
            diff = defRaw - atkRaw;
            level = calcResultLevel(diff);
            winShow = defShow; loseShow = atkShow;
        }

        int displaySeconds = GameConfig.getRoundEndSeconds();
        MatchBroadcasts.broadcastRoundEnd(normalized, displaySeconds,
            winShow, loseShow, atkRaw, defRaw, level, attackerTimedOut);
        org.espetro.audio.FactionAudioCoordinator.broadcastRoundResult(normalized);

        String result = switch (normalized) {
            case "ATTACK" -> TeamDisplayNames.coloredDisplayName("ATTACK") + "胜利";
            case "DEFEND" -> TeamDisplayNames.coloredDisplayName("DEFEND") + "胜利";
            default -> "§e平局";
        };
        Espetro.broadcastToAll("§6===== " + result + " §6=====");
        Espetro.LOGGER.info("[endRound] winner={} atkTickets={} defTickets={} level={} timedOut={}",
            normalized, atkRaw, defRaw, level, attackerTimedOut);
        return true;
    }

    /** 根据票数差异计算结果等级（从赢方视角）。 */
    private static int calcResultLevel(int diff) {
        if (diff > 200) return 5;  // 完胜
        if (diff > 100) return 4;  // 重大胜利
        if (diff > 50)  return 3;  // 决定性胜利
        if (diff > 25)  return 2;  // 险胜
        if (diff > 0)   return 1;  // 惨烈胜利
        return 0;                  // 平局
    }

    private static String getFactionShowName(FactionDataLoader loader, String factionId, String fallback) {
        if (loader == null || factionId == null) return fallback;
        FactionDataLoader.FactionData fd = loader.getFaction(factionId);
        if (fd == null) return fallback;
        if (fd.showName != null && !fd.showName.isEmpty()) return fd.showName;
        return fd.name != null && !fd.name.isEmpty() ? fd.name : fallback;
    }

    private void beginCleanup() {
        if (currentPhase != GamePhase.ROUND_END) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        setPhase(GamePhase.CLEANUP);
        // 本局结束：观察者恢复为正常玩家
        clearAllObservers();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerHoldStates.applyHubState(player);
            ClassEquipment.clearEquipment(player);
            TeamManager.removeFromAllTeams(
                server.getScoreboard(), player.getName().getString());
        }

        ActiveMapConfig completedMap = pendingMap != null ? pendingMap : BattlefieldContext.getOrNull();
        // 仅本局有效：本局结束（回城流程）关闭装备完全解锁模式
        FreeUnlockManager.onRoundEnded();
        clearRoundRuntime(false); // per-round scoreboard survives until next prestart
        BattlefieldWorldManager.getInstance().cleanupBattlefield(server, completedMap, cleanupResult -> {
            pendingMap = null;
            pendingRoundWinner = null;
            setPhase(GamePhase.LOBBY);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                PlayerHoldStates.applyHubState(player);
                MatchBroadcasts.sendOpenHubScreen(player, server.getPlayerCount(),
                    "等待管理员开始下一局");
            }
            broadcastHubStatus();
            if (!cleanupResult.success()) {
                Espetro.broadcastToAll(
                    "§c[Espetro] 战场存档副本删除失败，下次加载将重试："
                        + cleanupResult.error());
            }
        });
    }

    private void clearPlayerRoundAssignment(ServerPlayer player) {
        if (player == null) return;
        // 跳边 / 观战 / 重连 / 对局重置：先强制下车。
        // 玩家若正坐在敌方载具里（换队瞬间载具归属未变），仅靠座位校验拦不住，
        // 必须先脱离载具，之后每次上车都会重新走阵营校验。
        if (player.getVehicle() != null) {
            player.stopRiding();
        }
        // 该玩家在旧队伍的待处理认领申请一并作废，避免队长收到已失效的
        // [通过]/[否决] 按钮并把载具归属给旧编制。
        VehicleEventHandler.clearClaimsFor(player.getUUID());
        String squadTeam = SquadManager.getInstance().removePlayer(player.getUUID());
        ClassCountManager.getInstance().removePlayer(player);
        ClassEquipment.clearEquipment(player);
        TeamManager.removeFromAllTeams(player.server.getScoreboard(), player.getName().getString());
        waitingForTeam.remove(player.getUUID());
        teamSelectedPlayers.remove(player.getUUID());
        deployClassSelected.remove(player.getUUID());
        if (squadTeam != null) {
            SquadSync.syncSquadsToTeam(squadTeam);
        }
    }

    // ========== 管理员：跳边 / 观战 ==========

    public boolean isObserver(UUID playerId) {
        return playerId != null
            && (observers.contains(playerId) || persistentObservers.contains(playerId));
    }

    /**
     * 用本局队伍最终编制替换 {@code applyTeamAssignmentToPlayer} 写入的临时
     * factionId（ATTACK/DEFEND）。部署/选职业界面按队伍最终编制下发职业列表，
     * 而选职业校验要求 kit.factionId == playerFaction，因此跳边、重连、观战编入
     * 之后必须回填真实编制，否则选职业会被判定"该职业不属于当前选择的编制"。
     */
    private static void assignFinalFactionToPlayer(ServerPlayer player, String team) {
        if (player == null) return;
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        String factionId = "ATTACK".equals(team)
            ? selectManager.getFinalAttackClass()
            : selectManager.getFinalDefendClass();
        if (factionId == null) factionId = team;
        ClassCountManager.getInstance().setPlayerFaction(player.getUUID(), factionId);
    }

    /**
     * 管理员将玩家切换为观察者：离开任何队伍，旁观模式自由观战。
     * 本局结束（beginCleanup/forceStop/resetGame）后自动恢复为正常玩家。
     */
    public boolean adminSetObserver(ServerPlayer player) {
        return setObserver(player, false);
    }

    /** 玩家自己通过面板按钮进入观战（局内有效，本局结束自动恢复）。 */
    public boolean selfSetObserver(ServerPlayer player) {
        return setObserver(player, true);
    }

    private boolean setObserver(ServerPlayer player, boolean selfRequest) {
        if (player == null) return false;
        UUID id = player.getUUID();
        // 主城等待期间设置 → 跨局保持（下一局继续观战）；局内设置 → 本局结束自动恢复。
        boolean lobbyLike = currentPhase == null || currentPhase.isLobbyLike();
        if (lobbyLike) {
            persistentObservers.add(id);
            observers.remove(id);
        } else {
            observers.add(id);
            persistentObservers.remove(id);
        }
        clearPlayerRoundAssignment(player);
        // 服务端已清空队伍/小队/职业记录：让客户端立即优雅关闭残留的部署/投票/
        // 选职等对局界面，避免残留到后续阶段（J 屏等不会自行感知被移出对局）。
        MatchBroadcasts.sendCloseModScreens(player);
        BastionManager.getInstance().clearWaiting(id);
        BastionManager.getInstance().unlockPlayerPosition(id);
        player.removeAllEffects();
        player.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
        player.setHealth(player.getMaxHealth());
        if (selfRequest) {
            player.sendSystemMessage(Component.literal("§e你已进入观战模式。"));
        } else if (lobbyLike) {
            player.sendSystemMessage(Component.literal(
                "§e你已被设为观察者：本局与下一局都将保持观战，直到被跳边编入队伍。"));
        } else {
            player.sendSystemMessage(Component.literal(
                "§e你已被管理员设为观察者，本局结束后将自动恢复为正常玩家。"));
        }
        return true;
    }

    /**
     * 管理员跳边：
     * <ul>
     *   <li>观察者玩家：随机编入一方并正常进入部署流程。</li>
     *   <li>普通玩家：立即击杀并将队伍更换到对面。</li>
     * </ul>
     * @return 目标队伍（ATTACK/DEFEND），失败返回 null
     */
    @javax.annotation.Nullable
    public String adminChangeTeam(ServerPlayer player) {
        if (player == null) return null;
        UUID id = player.getUUID();
        if (observers.remove(id) | persistentObservers.remove(id)) {
            String team = java.util.concurrent.ThreadLocalRandom.current().nextBoolean()
                ? "ATTACK" : "DEFEND";
            clearPlayerRoundAssignment(player);
            applyTeamAssignmentToPlayer(player, team);
            // applyTeamAssignmentToPlayer 只设置了 ATTACK/DEFEND 作为临时 factionId；
            // 补上实际编制 ID，与中途加入流程一致。
            assignFinalFactionToPlayer(player, team);
            player.removeAllEffects();
            // 观战玩家编入后直接进入部署流程（无需击杀，人已在战场）。
            if (currentPhase == GamePhase.DEPLOYING || currentPhase == GamePhase.BATTLE) {
                prepareDeploySelection(player, team);
                DeployScreenSync.queueUnifiedDeployScreen(player, getDeployTimeRemainingSeconds());
            } else {
                PlayerHoldStates.applyMatchHoldState(player, PlayerHoldStates.HoldAnchor.AUTO);
            }
            player.sendSystemMessage(Component.literal(
                "§a你已被管理员编入" + TeamDisplayNames.coloredDisplayName(team) + "，请选择部署点。"));
            Espetro.broadcastToAll("§e[管理] " + player.getName().getString()
                + " 已加入" + TeamDisplayNames.coloredDisplayName(team));
            return team;
        }

        String current = ClassCountManager.getInstance().getPlayerTeam(id);
        if (current == null) {
            current = getTeamFromFactionStatic(ClassCountManager.getInstance().getPlayerFaction(id));
        }
        String target = "ATTACK".equals(current) ? "DEFEND" : "ATTACK";
        clearPlayerRoundAssignment(player);
        applyTeamAssignmentToPlayer(player, target);
        // applyTeamAssignmentToPlayer 只写入了 ATTACK/DEFEND 临时 factionId；
        // 必须先回填新队伍的真实编制（死亡流程与重生部署不会设置），否则选职业会因
        // kit.factionId != playerFaction 被拒（"该职业不属于你当前选择的编制"）。
        assignFinalFactionToPlayer(player, target);
        // 先换队再击杀：死亡流程会按新队伍记录部署点并进入部署选择。
        player.kill();
        player.sendSystemMessage(Component.literal(
            "§e你已被管理员跳边至" + TeamDisplayNames.coloredDisplayName(target) + "，即将重生。"));
        Espetro.broadcastToAll("§e[管理] " + player.getName().getString()
            + " 已跳边至" + TeamDisplayNames.coloredDisplayName(target));
        return target;
    }

    /** 本局结束：观察者恢复为正常玩家（保留 spectators 本身由回城流程处理）。 */
    public void clearAllObservers() {
        // 只清"局内观战"；主城设置的跨局观战者保持旁观，下一局继续观战。
        observers.clear();
    }

    private void broadcastHubStatus() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        int count = server.getPlayerCount();
        lastHubBroadcastPlayerCount = count;
        lastHubBroadcastTick = deployTickCounter;
        String text = "§6主城 §7| §e在线人数: §f" + count
            + " §7| §e等待管理员开始下一局";
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.serverLevel() == server.overworld()) {
                MatchBroadcasts.sendWaitingStatus(player, text, true);
            }
        }
    }

    /** 人数变化立即推；否则至少间隔 5 秒。 */
    private void broadcastHubStatusThrottled() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        int count = server.getPlayerCount();
        boolean countChanged = count != lastHubBroadcastPlayerCount;
        boolean intervalElapsed = deployTickCounter - lastHubBroadcastTick >= 5 * TICKS_PER_SECOND;
        if (countChanged || intervalElapsed || lastHubBroadcastTick == Integer.MIN_VALUE) {
            broadcastHubStatus();
        }
    }

    /**
     * 标记玩家在部署阶段已选择职业
     */
    public void markDeployClassSelected(UUID uuid) {
        deployClassSelected.add(uuid);
    }

    public boolean isDeployClassSelected(UUID uuid) {
        return deployClassSelected.contains(uuid);
    }

    // ========== 兼容方法 ==========

    public int getTeamSelectedCount() {
        return teamSelectedPlayers.size();
    }

    public int getWaitingForTeamCount() {
        return waitingForTeam.size();
    }

    public boolean isGameStarted() {
        return currentPhase == GamePhase.BATTLE;
    }

    public void forceStartGame() {
        MinecraftServer server = Espetro.getServer();
        if (server != null && currentPhase.isLobbyLike()) {
            prestart(server);
        }
    }

    public Map<String, SpawnPointConfig.SpawnPoint> getAllSpawnPoints() {
        return SpawnPointConfig.getAllSpawnPoints();
    }

    public void setTeamSpawnPoint(String team, double x, double y, double z, float yaw) {
        SpawnPointConfig.setSpawnPoint(team, x, y, z, yaw);

        BlockPos newPos = new BlockPos((int) x, (int) y, (int) z);
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            ServerLevel overworld = BattlefieldContext.requireBattlefield(server);
            org.espetro.bastion.BastionManager bastionMgr = org.espetro.bastion.BastionManager.getInstance();

            for (UUID uuid : teamSelectedPlayers) {
                String playerTeam = ClassCountManager.getInstance().getPlayerTeam(uuid);
                if (playerTeam == null) {
                    playerTeam = getTeamFromFactionStatic(ClassCountManager.getInstance().getPlayerFaction(uuid));
                }
                if (team.equals(playerTeam)) {
                    ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                    if (player != null) {
                        bastionMgr.savePlayerDeployPoint(player, newPos, overworld);
                    }
                }
            }
        }
    }

    /** @deprecated Readiness no longer exists; this is the selected-side count. */
    @Deprecated
    public int getReadyCount() {
        return teamSelectedPlayers.size();
    }

    public int getWaitingCount() {
        return waitingForTeam.size();
    }
}
