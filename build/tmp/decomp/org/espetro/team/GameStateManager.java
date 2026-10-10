/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.team;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.api.event.GamePhaseChangedEvent;
import org.espetro.audio.FactionAudioCoordinator;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.bastion.FortificationManager;
import org.espetro.config.GameConfig;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.network.GamePhaseSyncPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.TeamSelectStatePacket;
import org.espetro.runtime.ServerRuntimeMaintenance;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassEquipment;
import org.espetro.team.ClassSelectManager;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.FactionConfig;
import org.espetro.team.FactionConfigLoader;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.MapVoteManager;
import org.espetro.team.OutpostManager;
import org.espetro.team.PartyManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamDisplayNames;
import org.espetro.team.TeamManager;
import org.espetro.team.TeamPackManager;
import org.espetro.team.TroopCountManager;
import org.espetro.team.VoteManager;
import org.espetro.vehicle.VehicleManager;

public class GameStateManager {
    private static GameStateManager INSTANCE;
    private GamePhase currentPhase = GamePhase.LOBBY;
    private int deployTickCounter = 0;
    private int battleTickCounter = 0;
    private int factionRevealTickCounter = 0;
    private static final int TICKS_PER_SECOND = 20;
    static final int TEAM_BALANCE_MAX_DIFF = 3;
    private static final int ATTACK_WAITING_BARRIER_SIDE = 80;
    private static final int ATTACK_WAITING_BARRIER_HEIGHT = 12;
    private static final int BARRIER_BLOCKS_PER_TICK = 400;
    private final Map<BlockPos, BlockState> attackWaitingBarrierBlocks = new HashMap<BlockPos, BlockState>();
    private ResourceKey<Level> attackWaitingBarrierDimension;
    private final ArrayDeque<BlockPos> pendingBarrierPlace = new ArrayDeque();
    private final ArrayDeque<Map.Entry<BlockPos, BlockState>> pendingBarrierRemove = new ArrayDeque();
    private ServerLevel barrierWorkLevel;
    private boolean battleStartPending;
    private String firstFactionSelectTeam = "ATTACK";
    private final Set<UUID> waitingForTeam = new HashSet<UUID>();
    private final Set<UUID> teamSelectedPlayers = new HashSet<UUID>();
    private final Set<UUID> midGameJoiners = new HashSet<UUID>();
    private final Map<UUID, String> assignedTeams = new HashMap<UUID, String>();
    private final Set<UUID> deployClassSelected = new HashSet<UUID>();
    private int teamSelectTickCounter = 0;
    private int roundEndTickCounter = 0;
    private String pendingRoundWinner = null;
    private ActiveMapConfig pendingMap = null;
    private boolean forceStopInProgress = false;
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
        return this.currentPhase;
    }

    public String getCurrentMapFolder() {
        ActiveMapConfig map = this.pendingMap != null ? this.pendingMap : BattlefieldContext.getOrNull();
        return map == null || map.mapFolder == null ? "" : map.mapFolder;
    }

    public void setPhase(GamePhase phase) {
        if (phase != null && !phase.isLobbyLike() && !BattlefieldWorldManager.getInstance().isStartupReady()) {
            Espetro.LOGGER.error("\u6218\u573a\u542f\u52a8\u95e8\u7981\u672a READY\uff0c\u62d2\u7edd\u9636\u6bb5\u5207\u6362 {} -> {}", (Object)this.currentPhase, (Object)phase);
            phase = GamePhase.LOBBY;
        }
        GamePhase previous = this.currentPhase;
        this.currentPhase = phase;
        Espetro.LOGGER.info("\u6e38\u620f\u9636\u6bb5\u5207\u6362: {}", (Object)phase.getDisplayName());
        NetworkManager.broadcastGamePhase(phase);
        MinecraftForge.EVENT_BUS.post((Event)new GamePhaseChangedEvent(previous, phase));
    }

    public boolean prestart(MinecraftServer server) {
        if (server == null || !this.currentPhase.isLobbyLike()) {
            return false;
        }
        if (!BattlefieldWorldManager.getInstance().isStartupReady()) {
            BattlefieldWorldManager.StartupPreparationResult preparation = BattlefieldWorldManager.getInstance().getStartupPreparation();
            Espetro.broadcastToAll("\u00a7c[Espetro] \u6218\u573a\u542f\u52a8\u91cd\u7f6e\u5931\u8d25\uff0c\u672c\u6b21\u4f1a\u8bdd\u5730\u56fe\u5df2\u7981\u7528\uff1a" + (preparation.error() == null ? preparation.status().name() : preparation.error()));
            this.setPhase(GamePhase.LOBBY);
            return false;
        }
        if (server.m_7416_() < 1) {
            return false;
        }
        if (ExternalConfigBootstrap.getUsableMaps().isEmpty()) {
            Espetro.broadcastToAll("\u00a7c[Espetro] \u6ca1\u6709\u901a\u8fc7\u6821\u9a8c\u7684\u5730\u56fe\uff0c\u65e0\u6cd5\u5f00\u59cb\u3002\u8bf7\u67e5\u770b\u670d\u52a1\u7aef\u65e5\u5fd7\u3002");
            return false;
        }
        this.clearRoundRuntime(false);
        PlayerMatchStatsManager.getInstance().resetMatch(server.m_6846_().m_11314_());
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            TeamManager.removeFromAllTeams(server.m_129896_(), player.m_7755_().getString());
            ClassEquipment.clearEquipment(player);
        }
        this.setPhase(GamePhase.MAP_VOTE);
        if (!MapVoteManager.getInstance().start(server)) {
            this.setPhase(GamePhase.LOBBY);
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                this.applyHubState(player);
            }
            return false;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            this.applyMatchHoldState(player, HoldAnchor.HUB_HIGH);
        }
        Espetro.broadcastToAll("\u00a76\u5730\u56fe\u6295\u7968\u5f00\u59cb\uff0c\u6240\u6709\u5728\u7ebf\u73a9\u5bb6\u5747\u53ef\u6295\u7968\u3002");
        return true;
    }

    public void onMapVoteFinished(ActiveMapConfig winner) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || this.currentPhase != GamePhase.MAP_VOTE || winner == null || !winner.usable) {
            return;
        }
        this.pendingMap = winner;
        this.setPhase(GamePhase.MAP_LOADING);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            this.applyMatchHoldState(player, HoldAnchor.AUTO);
            player.m_213846_(Component.m_237113_("\u00a7e\u6b63\u5728\u88c5\u8f7d\u6218\u573a\u5730\u56fe\uff1a" + winner.displayName));
        }
        BattlefieldWorldManager.getInstance().importAndLoad(server, winner, result -> {
            if (!result.success()) {
                this.pendingMap = null;
                this.setPhase(GamePhase.LOBBY);
                for (ServerPlayer player : server.m_6846_().m_11314_()) {
                    this.applyHubState(player);
                }
                Espetro.broadcastToAll("\u00a7c\u5730\u56fe\u88c5\u8f7d\u5931\u8d25\uff1a" + result.error());
                this.broadcastHubStatus();
                return;
            }
            this.startTeamSelect();
        });
    }

    public void applyMatchHoldState(ServerPlayer player, HoldAnchor anchor) {
        HoldAnchor resolved;
        if (player == null || player.f_8906_ == null) {
            return;
        }
        HoldAnchor holdAnchor = resolved = anchor == null ? HoldAnchor.AUTO : anchor;
        if (resolved == HoldAnchor.AUTO) {
            HoldAnchor holdAnchor2 = resolved = BattlefieldContext.isActive() ? HoldAnchor.BATTLEFIELD_WAIT : HoldAnchor.HUB_HIGH;
        }
        if (resolved == HoldAnchor.CURRENT_LOCK) {
            Vec3 existing = BastionManager.getInstance().getPlayerLockPosition(player.m_20148_());
            if (existing == null) {
                resolved = BattlefieldContext.isActive() ? HoldAnchor.BATTLEFIELD_WAIT : HoldAnchor.HUB_HIGH;
            } else {
                GameStateManager.enforceSpectatorBlindness(player);
                player.m_20334_(0.0, 0.0, 0.0);
                player.f_19789_ = 0.0f;
                if (player.m_20238_(existing) > 0.01) {
                    player.m_8999_(player.m_284548_(), existing.f_82479_, existing.f_82480_, existing.f_82481_, 0.0f, 0.0f);
                }
                return;
            }
        }
        GameStateManager.enforceSpectatorBlindness(player);
        player.m_20334_(0.0, 0.0, 0.0);
        player.f_19789_ = 0.0f;
        if (resolved == HoldAnchor.BATTLEFIELD_WAIT) {
            if (!BattlefieldContext.isActive()) {
                resolved = HoldAnchor.HUB_HIGH;
            } else {
                ServerLevel battlefield = BattlefieldContext.requireBattlefield(player.f_8924_);
                Vec3 waitingPosition = this.getWaitingPosition();
                if (player.m_284548_() != battlefield || player.m_20238_(waitingPosition) > 0.01) {
                    player.m_8999_(battlefield, waitingPosition.f_82479_, waitingPosition.f_82480_, waitingPosition.f_82481_, 0.0f, 0.0f);
                }
                BastionManager.getInstance().lockPlayerPosition(player.m_20148_(), waitingPosition);
                GameStateManager.enforceSpectatorBlindness(player, true);
                return;
            }
        }
        ServerLevel hub = player.f_8924_.m_129783_();
        BlockPos spawn = hub.m_220360_();
        double x = (double)spawn.m_123341_() + 0.5;
        double y = GameConfig.getWaitingY();
        double z = (double)spawn.m_123343_() + 0.5;
        Vec3 hold = new Vec3(x, y, z);
        if (player.m_284548_() != hub || player.m_20238_(hold) > 0.25) {
            player.m_8999_(hub, x, y, z, 0.0f, 0.0f);
        }
        BastionManager.getInstance().lockPlayerPosition(player.m_20148_(), hold);
        GameStateManager.enforceSpectatorBlindness(player, true);
    }

    public static void enforceSpectatorBlindness(ServerPlayer player) {
        GameStateManager.enforceSpectatorBlindness(player, true);
    }

    public static void enforceSpectatorBlindness(ServerPlayer player, boolean forceClientResync) {
        if (player == null) {
            return;
        }
        if (!player.m_5833_()) {
            player.m_143403_(GameType.SPECTATOR);
        }
        if (forceClientResync) {
            player.m_21195_(MobEffects.f_19610_);
            player.m_7292_(new MobEffectInstance(MobEffects.f_19610_, Integer.MAX_VALUE, 0, false, false, false));
        } else if (!player.m_21023_(MobEffects.f_19610_)) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19610_, Integer.MAX_VALUE, 0, false, false, false));
        }
        player.m_20334_(0.0, 0.0, 0.0);
        player.f_19789_ = 0.0f;
    }

    @Deprecated
    public void applyMatchLoadingHoldState(ServerPlayer player) {
        this.applyMatchHoldState(player, HoldAnchor.AUTO);
    }

    private void startTeamSelect() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || this.pendingMap == null || !BattlefieldContext.isActive()) {
            this.setPhase(GamePhase.LOBBY);
            if (server != null) {
                for (ServerPlayer player : server.m_6846_().m_11314_()) {
                    this.applyHubState(player);
                }
            }
            return;
        }
        this.waitingForTeam.clear();
        this.teamSelectedPlayers.clear();
        this.midGameJoiners.clear();
        this.teamSelectTickCounter = 0;
        this.setPhase(GamePhase.TEAM_SELECT);
        TeamManager.refreshDisplayNames(server);
        ArrayList<ServerPlayer> allPlayers = new ArrayList<ServerPlayer>();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            this.clearPlayerRoundAssignment(player);
            allPlayers.add(player);
            this.applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
        }
        Map<UUID, String> assignments = PartyManager.getInstance().computeTeamAssignment(allPlayers);
        this.assignedTeams.clear();
        this.assignedTeams.putAll(assignments);
        for (ServerPlayer player : allPlayers) {
            String team = assignments.get(player.m_20148_());
            if (team == null) {
                team = "ATTACK";
            }
            this.applyTeamAssignmentToPlayer(player, team);
            this.teamSelectedPlayers.add(player.m_20148_());
        }
        PartyManager.getInstance().clearAll();
        this.broadcastTeamSelectState(false);
        Espetro.LOGGER.info("\u81ea\u52a8\u5206\u914d\u961f\u4f0d\u5b8c\u6210: \u8fdb\u653b{}\u4eba \u9632\u5b88{}\u4eba", (Object)this.countPlayersOnTeam("ATTACK"), (Object)this.countPlayersOnTeam("DEFEND"));
        this.startAttackCommanderVote();
    }

    private void applyTeamAssignmentToPlayer(ServerPlayer player, String team) {
        if (!"ATTACK".equals(team) && !"DEFEND".equals(team)) {
            return;
        }
        ClassCountManager.getInstance().setPlayerFaction(player.m_20148_(), team);
        ClassCountManager.getInstance().setPlayerTeam(player.m_20148_(), team);
        if ("ATTACK".equals(team)) {
            TeamManager.joinAttackTeam(player.f_8924_, player.m_7755_().getString());
        } else {
            TeamManager.joinDefendTeam(player.f_8924_, player.m_7755_().getString());
        }
        PlayerMatchStatsManager.getInstance().onTeamSelected(player, team);
        player.m_213846_(Component.m_237113_("\u00a7a\u4f60\u5df2\u88ab\u5206\u914d\u5230" + TeamDisplayNames.coloredDisplayName(team)));
    }

    public void onTeamSelected(ServerPlayer player, String factionId) {
        int diff;
        if (this.currentPhase != GamePhase.TEAM_SELECT) {
            return;
        }
        String resolvedTeam = GameStateManager.getTeamFromFactionStatic(factionId);
        if (!"ATTACK".equals(resolvedTeam) && !"DEFEND".equals(resolvedTeam)) {
            return;
        }
        String oldTeam = ClassCountManager.getInstance().getPlayerTeam(player.m_20148_());
        int attack = this.countPlayersOnTeam("ATTACK");
        int defend = this.countPlayersOnTeam("DEFEND");
        if ("ATTACK".equals(resolvedTeam) && !"ATTACK".equals(oldTeam)) {
            ++attack;
            if (oldTeam != null) {
                --defend;
            }
        } else if ("DEFEND".equals(resolvedTeam) && !"DEFEND".equals(oldTeam)) {
            ++defend;
            if (oldTeam != null) {
                --attack;
            }
        }
        if ((diff = Math.abs(attack - defend)) > 3) {
            player.m_213846_(Component.m_237113_("\u00a7c\u961f\u4f0d\u4eba\u6570\u5dee\u5f02\u5df2\u8fbe\u4e0a\u9650\uff0c\u8be5\u9635\u8425\u6682\u65f6\u9501\u5b9a\u3002\u8bf7\u9009\u62e9\u53e6\u4e00\u9635\u8425\u6216\u7b49\u5f85\u4eba\u6570\u53d8\u5316\u3002"));
            return;
        }
        this.waitingForTeam.remove(player.m_20148_());
        this.teamSelectedPlayers.add(player.m_20148_());
        ClassCountManager.getInstance().setPlayerFaction(player.m_20148_(), resolvedTeam);
        ClassCountManager.getInstance().setPlayerTeam(player.m_20148_(), resolvedTeam);
        if ("ATTACK".equals(resolvedTeam)) {
            TeamManager.joinAttackTeam(player.f_8924_, player.m_7755_().getString());
        } else {
            TeamManager.joinDefendTeam(player.f_8924_, player.m_7755_().getString());
        }
        PlayerMatchStatsManager.getInstance().onTeamSelected(player, resolvedTeam);
        Espetro.LOGGER.info("\u73a9\u5bb6 {} \u9009\u62e9\u4e86\u961f\u4f0d {}", (Object)player.m_7755_().getString(), (Object)resolvedTeam);
        this.broadcastTeamSelectState();
    }

    public void forceStartCommanderVote() {
        if (this.currentPhase == GamePhase.TEAM_SELECT && this.teamSelectedPlayers.isEmpty()) {
            Espetro.LOGGER.info("\u6ca1\u6709\u73a9\u5bb6\u9009\u62e9\u961f\u4f0d\uff01");
            return;
        }
        if (this.currentPhase == GamePhase.TEAM_SELECT) {
            this.finishTeamSelect();
            return;
        }
        this.startAttackCommanderVote();
    }

    private void finishTeamSelect() {
        if (this.currentPhase != GamePhase.TEAM_SELECT) {
            return;
        }
        this.midGameJoiners.addAll(this.waitingForTeam);
        this.waitingForTeam.clear();
        this.broadcastTeamSelectState(false);
        this.startAttackCommanderVote();
    }

    private void broadcastTeamSelectState() {
        this.broadcastTeamSelectState(true);
    }

    private void broadcastTeamSelectState(boolean active) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        int attack = this.countPlayersOnTeam("ATTACK");
        int defend = this.countPlayersOnTeam("DEFEND");
        int remaining = Math.max(0, GameConfig.getTeamSelectSeconds() - this.teamSelectTickCounter / 20);
        long end = server.m_129783_().m_46467_() + (long)remaining * 20L;
        String lockedTeam = null;
        int diff = Math.abs(attack - defend);
        if (diff >= 3) {
            lockedTeam = attack > defend ? "ATTACK" : "DEFEND";
        }
        NetworkManager.broadcastTeamSelectState(attack, defend, remaining, end, active, lockedTeam, GameStateManager.getFinalFactionImageClassStatic("ATTACK"), GameStateManager.getFinalFactionImageClassStatic("DEFEND"));
    }

    private void broadcastMidGameTeamState() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || this.midGameJoiners.isEmpty()) {
            return;
        }
        int attack = this.countPlayersOnTeam("ATTACK");
        int defend = this.countPlayersOnTeam("DEFEND");
        long end = server.m_129783_().m_46467_() + 999999L;
        String atkImg = NetworkManager.getFactionSelectionImageStatic(ClassSelectManager.getInstance().getFinalAttackClass());
        String defImg = NetworkManager.getFactionSelectionImageStatic(ClassSelectManager.getInstance().getFinalDefendClass());
        for (UUID uuid : this.midGameJoiners) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null || ClassCountManager.getInstance().getPlayerTeam(uuid) != null) continue;
            String myTeam = null;
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new TeamSelectStatePacket(attack, defend, 0, end, false, myTeam, null, atkImg, defImg));
        }
    }

    private static String getFinalFactionImageClassStatic(String team) {
        String id = "ATTACK".equals(team) ? ClassSelectManager.getInstance().getFinalAttackClass() : ClassSelectManager.getInstance().getFinalDefendClass();
        return NetworkManager.getFactionSelectionImageStatic(id);
    }

    private int countPlayersOnTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return 0;
        }
        int count = 0;
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String pt = ClassCountManager.getInstance().getPlayerTeam(player.m_20148_());
            if (!team.equals(pt)) continue;
            ++count;
        }
        return count;
    }

    private void startDefendCommanderVote() {
        this.setPhase(GamePhase.DEFEND_COMMANDER_VOTE);
        VoteManager.getInstance().initPlayers();
        VoteManager.getInstance().startDefendVote();
    }

    private void startAttackCommanderVote() {
        this.setPhase(GamePhase.ATTACK_COMMANDER_VOTE);
        VoteManager.getInstance().initPlayers();
        VoteManager.getInstance().startAttackVote();
    }

    private void startDefendFactionSelect() {
        this.setPhase(GamePhase.DEFEND_FACTION_SELECT);
        ClassSelectManager.getInstance().startDefendSelecting();
    }

    private void startAttackFactionSelect() {
        this.setPhase(GamePhase.ATTACK_FACTION_SELECT);
        ClassSelectManager.getInstance().startAttackSelecting();
    }

    private void startFirstFactionSelect() {
        ClassSelectManager.getInstance().initFactionPool();
        if (TeamDisplayNames.isSymmetricMode()) {
            this.firstFactionSelectTeam = ThreadLocalRandom.current().nextBoolean() ? "ATTACK" : "DEFEND";
            Espetro.LOGGER.info("RAAS \u7f16\u5236\u9009\u62e9\u5148\u624b: {}", (Object)this.firstFactionSelectTeam);
        } else {
            this.firstFactionSelectTeam = "ATTACK";
        }
        this.startFactionSelectForTeam(this.firstFactionSelectTeam);
    }

    private void startFactionSelectForTeam(String team) {
        if ("DEFEND".equals(team)) {
            this.startDefendFactionSelect();
        } else {
            this.startAttackFactionSelect();
        }
    }

    private void onFactionSelectFinished() {
        ClassSelectManager.getInstance().finishCurrentSelecting();
        String currentTeam = this.currentPhase.getActiveTeam();
        if (this.firstFactionSelectTeam != null && this.firstFactionSelectTeam.equals(currentTeam)) {
            this.startFactionSelectForTeam("ATTACK".equals(this.firstFactionSelectTeam) ? "DEFEND" : "ATTACK");
            return;
        }
        this.startFactionReveal();
    }

    public String getFirstFactionSelectTeam() {
        return this.firstFactionSelectTeam;
    }

    private void startDeploying() {
        this.battleStartPending = false;
        this.setPhase(GamePhase.DEPLOYING);
        this.deployTickCounter = 0;
        this.factionRevealTickCounter = 0;
        this.deployClassSelected.clear();
        BastionManager.getInstance().reset();
        TeamPackManager.getInstance().reset();
        this.removeAttackWaitingBarrier();
        if (TeamDisplayNames.isSymmetricMode()) {
            Espetro.LOGGER.info("RAAS \u6a21\u5f0f\u8df3\u8fc7\u524d\u54e8\u57fa\u5730\u6fc0\u6d3b\uff08\u53cc\u65b9\u5747\u4e0d\u53ef\u7528\uff09");
        } else {
            OutpostManager.getInstance().activate();
        }
        ClassSelectManager.getInstance().finalizeSelection();
        ClassSelectManager selection = ClassSelectManager.getInstance();
        this.scheduleInitialFactionVehicles(selection);
        this.teleportAllToSpawnPoints();
        this.restoreRecordedUnassignedHolds();
        if (!TeamDisplayNames.isSymmetricMode()) {
            this.placeAttackWaitingBarrier();
        }
        this.broadcastClassSelectionForDeploy();
        if (TeamDisplayNames.isSymmetricMode()) {
            Espetro.LOGGER.info("\u90e8\u7f72\u9636\u6bb5\u5f00\u59cb\uff0c\u6301\u7eed{}\u79d2\uff08RAAS\uff1a\u65e0\u653b\u65b9\u7b49\u5f85\u5c4f\u969c\uff09", (Object)GameConfig.getDeployTimeoutSeconds());
        } else {
            Espetro.LOGGER.info("\u9632\u5b88\u90e8\u7f72\u9636\u6bb5\u5f00\u59cb\uff0c\u6301\u7eed{}\u79d2\uff0c\u653b\u65b9\u7b49\u5f85\u533a\u57df\u8fb9\u957f{}\u683c\uff0c\u9ad8{}\u683c", new Object[]{GameConfig.getDeployTimeoutSeconds(), 80, 12});
        }
    }

    private void startFactionReveal() {
        this.setPhase(GamePhase.FACTION_REVEAL);
        this.factionRevealTickCounter = 0;
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        NetworkManager.broadcastFactionRevealScreen(selectManager.getFinalAttackClass(), selectManager.getFinalDefendClass(), GameConfig.getFactionRevealSeconds());
        Espetro.LOGGER.info("\u53cc\u65b9\u7f16\u5236\u63ed\u793a\u5f00\u59cb\uff0c\u6301\u7eed{}\u79d2", (Object)GameConfig.getFactionRevealSeconds());
    }

    private void scheduleInitialFactionVehicles(ClassSelectManager selection) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        String attackFaction = selection.getFinalAttackClass();
        String defendFaction = selection.getFinalDefendClass();
        VehicleManager vehicleManager = VehicleManager.getInstance();
        vehicleManager.armInitialDeployCooldowns(attackFaction, "ATTACK", defendFaction, "DEFEND");
        ServerLevel level = BattlefieldContext.requireBattlefield(server);
        long deploymentStartedAt = System.currentTimeMillis();
        int attackCount = this.prepareInitialFactionVehiclesForTeam("ATTACK", attackFaction, level, deploymentStartedAt);
        int defendCount = this.prepareInitialFactionVehiclesForTeam("DEFEND", defendFaction, level, deploymentStartedAt);
        int deployedNow = vehicleManager.activateInitialVehicleDeployment();
        VehicleManager.InitialDeploymentStatus status = vehicleManager.getInitialDeploymentStatus();
        Espetro.LOGGER.info("\u9996\u6279\u8f7d\u5177\u81ea\u52a8\u90e8\u7f72\u5df2\u5b89\u6392: \u653b\u65b9{}\u8f86\uff0c\u5b88\u65b9{}\u8f86\uff0c\u7acb\u5373\u843d\u5730{}\u8f86\uff0c\u7b49\u5f85{}\u8f86", new Object[]{attackCount, defendCount, deployedNow, status.pending()});
    }

    private int prepareInitialFactionVehiclesForTeam(String team, String factionId, ServerLevel level, long deploymentStartedAt) {
        if (factionId == null || factionId.isBlank()) {
            return 0;
        }
        return VehicleManager.getInstance().prepareInitialVehicles(factionId, team, level, deploymentStartedAt);
    }

    private void broadcastClassSelectionForDeploy() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String team = ClassCountManager.getInstance().getPlayerTeam(player.m_20148_());
            if (team == null) continue;
            NetworkManager.queueUnifiedDeployScreen(player, GameConfig.getDeployTimeoutSeconds(), true);
        }
    }

    private void teleportAllToSpawnPoints() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        ClassCountManager countManager = ClassCountManager.getInstance();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String factionId;
            UUID uuid = player.m_20148_();
            String team = countManager.getPlayerTeam(uuid);
            if (team == null) {
                team = Espetro.getPlayerTeam(player);
            }
            if (team == null && (factionId = countManager.getPlayerFaction(uuid)) != null) {
                team = GameStateManager.getTeamFromFactionStatic(factionId);
            }
            if (team == null) continue;
            this.prepareDeploySelection(player, team);
        }
    }

    private void saveTeamSpawnAsDeployPoint(ServerPlayer player, String team) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        ServerLevel overworld = BattlefieldContext.requireBattlefield(player.f_8924_);
        BlockPos deployPos = new BlockPos((int)spawn.x, (int)spawn.y, (int)spawn.z);
        BastionManager.getInstance().savePlayerDeployPoint(player, deployPos, overworld);
    }

    private void prepareDeploySelection(ServerPlayer player, String team) {
        this.saveTeamSpawnAsDeployPoint(player, team);
        BastionManager.getInstance().activatePlayerBastionSelection(player.m_20148_());
        this.applyDeploymentWaitingState(player);
        if ("DEFEND".equals(team)) {
            OutpostManager.getInstance().prepareDeployTargets(BattlefieldContext.requireBattlefield(player.f_8924_));
        }
    }

    public void onDeployTick() {
        this.processBarrierWork();
        if (this.deployTickCounter % 20 == 0) {
            this.broadcastDefenseSetupActionBar(this.getDeployTimeRemainingSeconds());
        }
        if (this.deployTickCounter >= GameConfig.getDeployTimeoutSeconds() * 20) {
            this.startBattle();
        }
    }

    public int getDeployTimeRemainingSeconds() {
        if (this.currentPhase != GamePhase.DEPLOYING) {
            return 0;
        }
        return Math.max(0, GameConfig.getDeployTimeoutSeconds() - this.deployTickCounter / 20);
    }

    public int getBattleTimeRemainingSeconds() {
        if (this.currentPhase != GamePhase.BATTLE) {
            return -1;
        }
        int timeoutSeconds = GameConfig.getBattleTimeoutSeconds();
        if (timeoutSeconds <= 0) {
            return -1;
        }
        return Math.max(0, timeoutSeconds - this.battleTickCounter / 20);
    }

    private void broadcastDefenseSetupActionBar(int secondsRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        ClassCountManager countManager = ClassCountManager.getInstance();
        for (UUID uuid : this.teamSelectedPlayers) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null) continue;
            String team = countManager.getPlayerTeam(uuid);
            if (team == null) {
                team = GameStateManager.getTeamFromFactionStatic(countManager.getPlayerFaction(uuid));
            }
            String message = TeamDisplayNames.isSymmetricMode() ? "\u00a7e\u90e8\u7f72\u4e2d[" + secondsRemaining + "\u79d2]" : ("ATTACK".equals(team) ? "\u00a7c\u7b49\u5f85\u8fdb\u653b\u00a7e[" + secondsRemaining + "\u79d2]" : "\u00a79\u90e8\u7f72\u9632\u7ebf\u00a7e[" + secondsRemaining + "\u79d2]");
            NetworkManager.sendWaitingStatus(player, message, true);
        }
    }

    private void placeAttackWaitingBarrier() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        this.removeAttackWaitingBarrier();
        ServerLevel level = BattlefieldContext.requireBattlefield(server);
        if (!BattlefieldContext.isActiveBattlefield(level)) {
            Espetro.LOGGER.error("\u62d2\u7edd\u5728\u975e\u6d3b\u52a8\u6218\u573a\u7ef4\u5ea6\u521b\u5efa\u90e8\u7f72\u5c4f\u969c: {}", (Object)level.m_46472_().m_135782_());
            return;
        }
        this.attackWaitingBarrierDimension = level.m_46472_();
        this.barrierWorkLevel = level;
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint("ATTACK");
        BlockPos center = new BlockPos((int)Math.floor(spawn.x), (int)Math.floor(spawn.y), (int)Math.floor(spawn.z));
        int half = 40;
        int minX = center.m_123341_() - half;
        int maxX = minX + 80 - 1;
        int minZ = center.m_123343_() - half;
        int maxZ = minZ + 80 - 1;
        int baseY = center.m_123342_();
        this.pendingBarrierPlace.clear();
        for (int y = baseY; y < baseY + 12; ++y) {
            for (int x = minX; x <= maxX; ++x) {
                this.pendingBarrierPlace.add(new BlockPos(x, y, minZ));
                this.pendingBarrierPlace.add(new BlockPos(x, y, maxZ));
            }
            for (int z = minZ + 1; z < maxZ; ++z) {
                this.pendingBarrierPlace.add(new BlockPos(minX, y, z));
                this.pendingBarrierPlace.add(new BlockPos(maxX, y, z));
            }
        }
        int queued = this.pendingBarrierPlace.size();
        this.processBarrierWork();
        Espetro.LOGGER.info("\u5df2\u6392\u961f\u653b\u65b9\u7b49\u5f85\u5c4f\u969c: center={}, side={}, height={}, queued={}, placedNow={}", new Object[]{center, 80, 12, queued, this.attackWaitingBarrierBlocks.size()});
    }

    private void setTemporaryBarrier(ServerLevel level, BlockPos pos) {
        BlockState previous = level.m_8055_(pos);
        if (!previous.m_60795_() && !previous.m_60812_(level, pos).m_83281_()) {
            return;
        }
        if (!this.attackWaitingBarrierBlocks.containsKey(pos)) {
            this.attackWaitingBarrierBlocks.put(pos.m_7949_(), previous);
        }
        level.m_7731_(pos, Blocks.f_50375_.m_49966_(), 18);
    }

    private void processBarrierWork() {
        int budget;
        MinecraftServer server;
        ServerLevel level = this.barrierWorkLevel;
        if (level == null && this.attackWaitingBarrierDimension != null && (server = Espetro.getServer()) != null) {
            this.barrierWorkLevel = level = server.m_129880_(this.attackWaitingBarrierDimension);
        }
        if (level == null) {
            this.pendingBarrierPlace.clear();
            this.pendingBarrierRemove.clear();
            return;
        }
        for (budget = 400; budget > 0 && !this.pendingBarrierPlace.isEmpty(); --budget) {
            this.setTemporaryBarrier(level, this.pendingBarrierPlace.poll());
        }
        while (budget > 0 && !this.pendingBarrierRemove.isEmpty()) {
            Map.Entry<BlockPos, BlockState> entry = this.pendingBarrierRemove.poll();
            BlockPos pos = entry.getKey();
            if (level.m_8055_(pos).m_60713_(Blocks.f_50375_)) {
                level.m_7731_(pos, entry.getValue(), 3);
            }
            --budget;
        }
        if (!this.pendingBarrierPlace.isEmpty() || this.pendingBarrierRemove.isEmpty()) {
            // empty if block
        }
    }

    private int removeAttackWaitingBarrier() {
        return this.removeAttackWaitingBarrier(Espetro.getServer());
    }

    public int cleanupTemporaryBarriers(MinecraftServer server) {
        return this.removeAttackWaitingBarrier(server);
    }

    private int removeAttackWaitingBarrier(MinecraftServer server) {
        ServerLevel level;
        this.pendingBarrierPlace.clear();
        if (server == null) {
            this.attackWaitingBarrierBlocks.clear();
            this.attackWaitingBarrierDimension = null;
            this.pendingBarrierRemove.clear();
            this.barrierWorkLevel = null;
            return 0;
        }
        ResourceKey<Level> dimension = this.attackWaitingBarrierDimension;
        ServerLevel serverLevel = level = dimension == null ? null : server.m_129880_(dimension);
        if (level == null || Level.f_46428_.equals(level.m_46472_())) {
            this.attackWaitingBarrierBlocks.clear();
            this.attackWaitingBarrierDimension = null;
            this.pendingBarrierRemove.clear();
            this.barrierWorkLevel = null;
            return 0;
        }
        this.barrierWorkLevel = level;
        this.pendingBarrierRemove.clear();
        for (Map.Entry<BlockPos, BlockState> entry : this.attackWaitingBarrierBlocks.entrySet()) {
            this.pendingBarrierRemove.add(Map.entry(entry.getKey(), entry.getValue()));
        }
        int queued = this.pendingBarrierRemove.size();
        this.attackWaitingBarrierBlocks.clear();
        this.processBarrierWork();
        if (this.pendingBarrierRemove.isEmpty()) {
            this.attackWaitingBarrierDimension = null;
            this.barrierWorkLevel = null;
        }
        if (queued > 0) {
            Espetro.LOGGER.info("\u5df2\u6e05\u7406\u653b\u65b9\u7b49\u5f85\u5c4f\u969c: \u6392\u961f\u6062\u590d{}\u4e2a, \u672c\u6279\u5904\u7406\u5269\u4f59{}", (Object)queued, (Object)this.pendingBarrierRemove.size());
        }
        return queued;
    }

    private int removeOrphanedAttackWaitingBarriers(ServerLevel level) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint("ATTACK");
        BlockPos center = new BlockPos((int)Math.floor(spawn.x), (int)Math.floor(spawn.y), (int)Math.floor(spawn.z));
        int half = 40;
        int minX = center.m_123341_() - half;
        int maxX = minX + 80 - 1;
        int minZ = center.m_123343_() - half;
        int maxZ = minZ + 80 - 1;
        int baseY = center.m_123342_();
        int removed = 0;
        for (int y = baseY; y < baseY + 12; ++y) {
            for (int x = minX; x <= maxX; ++x) {
                removed += this.removeBarrierAt(level, new BlockPos(x, y, minZ));
                removed += this.removeBarrierAt(level, new BlockPos(x, y, maxZ));
            }
            for (int z = minZ + 1; z < maxZ; ++z) {
                removed += this.removeBarrierAt(level, new BlockPos(minX, y, z));
                removed += this.removeBarrierAt(level, new BlockPos(maxX, y, z));
            }
        }
        return removed;
    }

    private int removeBarrierAt(ServerLevel level, BlockPos pos) {
        if (!level.m_8055_(pos).m_60713_(Blocks.f_50375_)) {
            return 0;
        }
        level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 18);
        return 1;
    }

    private void startBattle() {
        MinecraftServer server;
        if (!this.battleStartPending) {
            this.battleStartPending = true;
            this.queueAttackWaitingBarrierRemoval();
        }
        if (!this.pendingBarrierPlace.isEmpty() || !this.pendingBarrierRemove.isEmpty()) {
            return;
        }
        this.battleStartPending = false;
        this.attackWaitingBarrierDimension = null;
        this.barrierWorkLevel = null;
        boolean hadActiveOutposts = OutpostManager.getInstance().isAvailable();
        OutpostManager.getInstance().deactivate();
        this.setPhase(GamePhase.BATTLE);
        this.battleTickCounter = 0;
        if (hadActiveOutposts) {
            Espetro.broadcastToTeam("DEFEND", "\u00a7c\u2694 \u653b\u65b9\u5df2\u5f00\u59cb\u8fdb\u653b\uff0c\u524d\u54e8\u57fa\u5730\u5df2\u9500\u6bc1\uff01");
        }
        if ((server = Espetro.getServer()) == null) {
            return;
        }
        BastionManager bastionManager = BastionManager.getInstance();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (this.isRecordedUnassigned(player)) {
                this.applyMatchHoldState(player, HoldAnchor.CURRENT_LOCK);
                continue;
            }
            if (bastionManager.isWaitingForBastion(player.m_20148_())) {
                this.applyDeploymentWaitingState(player);
                NetworkManager.queueUnifiedDeployScreen(player, -1);
                continue;
            }
            player.m_21195_(MobEffects.f_19610_);
            this.applyBattlefieldMiningRestriction(player);
        }
        TroopCountManager.getInstance().initializeTroops();
        CommanderGovernanceManager.getInstance().syncCommandersFromVoteManager();
        NetworkManager.broadcastTroopCounts(TroopCountManager.getInstance().getAttackTroops(), TroopCountManager.getInstance().getDefendTroops());
        Espetro.broadcastToAll("\u00a76========================================");
        Espetro.broadcastToAll("\u00a7a\u00a7l\u2605 \u5bf9\u6218\u5f00\u59cb\uff01 \u2605");
        Espetro.broadcastToAll("\u00a76========================================");
        Espetro.LOGGER.info("===== \u5bf9\u6218\u5f00\u59cb =====");
    }

    private void queueAttackWaitingBarrierRemoval() {
        this.pendingBarrierPlace.clear();
        this.pendingBarrierRemove.clear();
        for (Map.Entry<BlockPos, BlockState> entry : this.attackWaitingBarrierBlocks.entrySet()) {
            this.pendingBarrierRemove.add(Map.entry(entry.getKey(), entry.getValue()));
        }
        int queued = this.pendingBarrierRemove.size();
        this.attackWaitingBarrierBlocks.clear();
        Espetro.LOGGER.info("\u90e8\u7f72\u7ed3\u675f\uff1a\u5df2\u5206\u5e27\u6392\u961f\u6062\u590d\u653b\u65b9\u7b49\u5f85\u5c4f\u969c {} \u4e2a", (Object)queued);
    }

    private void onFactionRevealTick() {
        ++this.factionRevealTickCounter;
        if (this.factionRevealTickCounter >= GameConfig.getFactionRevealSeconds() * 20) {
            this.startDeploying();
        }
    }

    public void onServerTick() {
        switch (this.currentPhase) {
            case LOBBY: 
            case WAITING_FOR_PLAYERS: {
                if (this.deployTickCounter % 20 != 0) break;
                this.broadcastHubStatusThrottled();
                break;
            }
            case MAP_VOTE: {
                MinecraftServer mapVoteServer = Espetro.getServer();
                if (mapVoteServer == null) break;
                MapVoteManager.getInstance().onServerTick(mapVoteServer);
                break;
            }
            case MAP_LOADING: {
                break;
            }
            case TEAM_SELECT: {
                ++this.teamSelectTickCounter;
                if (this.teamSelectTickCounter % 20 == 0) {
                    this.broadcastTeamSelectState();
                }
                if (this.teamSelectTickCounter < GameConfig.getTeamSelectSeconds() * 20) break;
                this.finishTeamSelect();
                break;
            }
            case DEFEND_COMMANDER_VOTE: {
                VoteManager.getInstance().onServerTick();
                if (!VoteManager.getInstance().isCurrentVoteTimedOut()) break;
                VoteManager.getInstance().finishCurrentVote();
                this.startFirstFactionSelect();
                break;
            }
            case ATTACK_COMMANDER_VOTE: {
                VoteManager.getInstance().onServerTick();
                if (!VoteManager.getInstance().isCurrentVoteTimedOut()) break;
                VoteManager.getInstance().finishCurrentVote();
                this.startDefendCommanderVote();
                break;
            }
            case DEFEND_FACTION_SELECT: {
                ClassSelectManager.getInstance().onServerTick();
                if (!ClassSelectManager.getInstance().isCurrentSelectTimedOut()) break;
                this.onFactionSelectFinished();
                break;
            }
            case ATTACK_FACTION_SELECT: {
                ClassSelectManager.getInstance().onServerTick();
                if (!ClassSelectManager.getInstance().isCurrentSelectTimedOut()) break;
                this.onFactionSelectFinished();
                break;
            }
            case FACTION_REVEAL: {
                this.onFactionRevealTick();
                break;
            }
            case DEPLOYING: {
                VehicleManager.getInstance().onServerTick();
                if (this.deployTickCounter % 100 == 0) {
                    this.broadcastMidGameTeamState();
                }
                this.onDeployTick();
                break;
            }
            case BATTLE: {
                MinecraftServer battleServer;
                VehicleManager.getInstance().onServerTick();
                if (this.battleTickCounter % 100 == 0) {
                    this.broadcastMidGameTeamState();
                }
                this.processBarrierWork();
                if (this.pendingBarrierRemove.isEmpty() && this.pendingBarrierPlace.isEmpty() && this.attackWaitingBarrierDimension != null && this.barrierWorkLevel != null) {
                    this.attackWaitingBarrierDimension = null;
                    this.barrierWorkLevel = null;
                }
                if ((battleServer = Espetro.getServer()) != null) {
                    CommanderGovernanceManager.getInstance().onServerTick(battleServer);
                }
                ++this.battleTickCounter;
                int battleTimeoutSeconds = GameConfig.getBattleTimeoutSeconds();
                if (battleTimeoutSeconds <= 0 || this.battleTickCounter % 20 != 0) break;
                int remaining = battleTimeoutSeconds - this.battleTickCounter / 20;
                if (remaining <= 0) {
                    Espetro.broadcastToAll("\u00a7c" + TeamDisplayNames.displayName("ATTACK") + "\u672a\u5728\u4e00\u5c0f\u65f6\u5185\u5360\u9886\u6240\u6709\u636e\u70b9\uff0c" + TeamDisplayNames.displayName("DEFEND") + "\u83b7\u80dc\uff01");
                    this.endRound("DEFEND", true);
                }
                NetworkManager.broadcastBattleTimer(remaining);
                break;
            }
            case ROUND_END: {
                ++this.roundEndTickCounter;
                if (this.roundEndTickCounter < GameConfig.getRoundEndSeconds() * 20) break;
                this.beginCleanup();
                break;
            }
            case CLEANUP: {
                break;
            }
        }
        ++this.deployTickCounter;
    }

    private void restoreRecordedUnassignedHolds() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (UUID playerId : this.waitingForTeam) {
            this.restoreUnassignedHold(server, playerId);
        }
        for (UUID playerId : this.midGameJoiners) {
            this.restoreUnassignedHold(server, playerId);
        }
    }

    private void restoreUnassignedHold(MinecraftServer server, UUID playerId) {
        ServerPlayer player = server.m_6846_().m_11259_(playerId);
        if (player != null && this.isRecordedUnassigned(player)) {
            this.applyMatchHoldState(player, HoldAnchor.AUTO);
        }
    }

    private boolean isRecordedUnassigned(ServerPlayer player) {
        UUID playerId = player.m_20148_();
        if (!this.waitingForTeam.contains(playerId) && !this.midGameJoiners.contains(playerId)) {
            return false;
        }
        String team = ClassCountManager.getInstance().getPlayerTeam(playerId);
        if (team == null) {
            team = Espetro.getPlayerTeam(player);
        }
        return !"ATTACK".equals(team) && !"DEFEND".equals(team);
    }

    private void teleportToTeamSpawn(ServerPlayer player, String team) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        ServerLevel overworld = BattlefieldContext.requireBattlefield(player.f_8924_);
        player.m_8999_(overworld, spawn.x, spawn.y, spawn.z, spawn.yaw, 0.0f);
        BlockPos deployPos = new BlockPos((int)spawn.x, (int)spawn.y, (int)spawn.z);
        BastionManager.getInstance().savePlayerDeployPoint(player, deployPos, overworld);
    }

    public static String getTeamFromFactionStatic(String factionId) {
        if (factionId == null) {
            return "DEFEND";
        }
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
        if (lower.contains("attack") || lower.contains("pla") || lower.contains("russia") || lower.contains("rus") || lower.contains("militia")) {
            return "ATTACK";
        }
        return "DEFEND";
    }

    public void resetGame() {
        MinecraftServer server = Espetro.getServer();
        this.forceStopInProgress = false;
        this.clearRoundRuntime(false);
        this.pendingRoundWinner = null;
        this.pendingMap = null;
        this.currentPhase = GamePhase.LOBBY;
        if (server != null) {
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                this.clearPlayerRoundAssignment(player);
                this.applyHubState(player);
            }
            if (BattlefieldContext.isActive()) {
                BattlefieldWorldManager.getInstance().cleanupBattlefield(server, null, () -> {
                    this.setPhase(GamePhase.LOBBY);
                    this.broadcastHubStatus();
                });
            } else {
                this.setPhase(GamePhase.LOBBY);
                this.broadcastHubStatus();
            }
        }
        Espetro.LOGGER.info("\u6e38\u620f\u72b6\u6001\u5df2\u91cd\u7f6e\u5230\u4e3b\u57ce");
    }

    public boolean forceStopGame(MinecraftServer server, Consumer<BattlefieldWorldManager.Result> onComplete) {
        if (server == null || this.forceStopInProgress) {
            return false;
        }
        this.forceStopInProgress = true;
        GamePhase stoppedPhase = this.currentPhase;
        ActiveMapConfig stoppedMap = this.pendingMap != null ? this.pendingMap : BattlefieldContext.getOrNull();
        int playerCount = server.m_7416_();
        this.setPhase(GamePhase.CLEANUP);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            this.clearPlayerRoundAssignment(player);
            player.m_21219_();
            this.applyHubState(player);
        }
        this.clearRoundRuntime(true);
        this.pendingMap = null;
        this.pendingRoundWinner = null;
        SupplyManager.getInstance().reset();
        Espetro.broadcastToAll("\u00a76[Espetro] \u7ba1\u7406\u5458\u5df2\u5f3a\u5236\u7ed3\u675f\u672c\u5c40\uff0c\u6b63\u5728\u8fd4\u56de\u4e3b\u57ce\u3002");
        BattlefieldWorldManager.getInstance().cleanupBattlefield(server, stoppedMap, cleanupResult -> {
            this.forceStopInProgress = false;
            this.setPhase(GamePhase.LOBBY);
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                this.clearPlayerRoundAssignment(player);
                player.m_21219_();
                this.applyHubState(player);
                NetworkManager.sendOpenHubScreen(player, server.m_7416_(), "\u5bf9\u5c40\u5df2\u7ed3\u675f\uff0c\u7b49\u5f85\u7ba1\u7406\u5458\u5f00\u59cb\u4e0b\u4e00\u5c40");
            }
            this.broadcastHubStatus();
            if (cleanupResult.success()) {
                Espetro.broadcastToAll("\u00a7a[Espetro] \u6240\u6709\u73a9\u5bb6\u5df2\u8fd4\u56de\u4e3b\u57ce\uff0c\u6218\u573a\u5b58\u6863\u526f\u672c\u5df2\u5220\u9664\u3002");
            } else {
                Espetro.broadcastToAll("\u00a7c[Espetro] \u73a9\u5bb6\u5df2\u8fd4\u56de\u4e3b\u57ce\uff0c\u4f46\u6218\u573a\u5b58\u6863\u526f\u672c\u5220\u9664\u5931\u8d25\uff1a" + cleanupResult.error());
            }
            Espetro.LOGGER.info("\u7ba1\u7406\u5458\u5f3a\u5236\u7ec8\u6b62\u6218\u5c40\u5b8c\u6210: previousPhase={}, map={}, players={}, terrainReset={}", new Object[]{stoppedPhase, stoppedMap == null ? "none" : stoppedMap.dimensionId, playerCount, cleanupResult.success()});
            if (onComplete != null) {
                try {
                    onComplete.accept((BattlefieldWorldManager.Result)cleanupResult);
                }
                catch (Exception e) {
                    Espetro.LOGGER.error("\u5f3a\u5236\u7ec8\u6b62\u6218\u5c40\u5b8c\u6210\u56de\u8c03\u6267\u884c\u5931\u8d25", (Throwable)e);
                }
            }
        });
        return true;
    }

    private void clearRoundRuntime(boolean clearStats) {
        ResupplySessionManager.clearAll();
        this.waitingForTeam.clear();
        this.teamSelectedPlayers.clear();
        this.midGameJoiners.clear();
        this.assignedTeams.clear();
        this.deployClassSelected.clear();
        this.deployTickCounter = 0;
        this.battleTickCounter = 0;
        this.factionRevealTickCounter = 0;
        this.teamSelectTickCounter = 0;
        this.roundEndTickCounter = 0;
        this.firstFactionSelectTeam = "ATTACK";
        this.removeAttackWaitingBarrier();
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
        FortificationManager.getInstance().reset();
        FobSupplyTracker.clearAll();
        TroopCountManager.getInstance().resetTroops();
        BastionManager.getInstance().destroyAllBastionsForMatchEnd();
        ServerRuntimeMaintenance.getInstance().reset();
        if (clearStats) {
            PlayerMatchStatsManager.getInstance().resetMatch();
        }
    }

    public void onPlayerJoin(ServerPlayer player) {
        PlayerMatchStatsManager.getInstance().onPlayerJoin(player);
        NetworkManager.sendCommanderSkillSync(player);
        switch (this.currentPhase) {
            case LOBBY: 
            case WAITING_FOR_PLAYERS: {
                this.forcePlayerToHub(player);
                NetworkManager.sendOpenHubScreen(player, player.f_8924_.m_7416_(), "\u7b49\u5f85\u7ba1\u7406\u5458\u5f00\u59cb\u4e0b\u4e00\u5c40");
                break;
            }
            case MAP_VOTE: {
                this.applyMatchHoldState(player, HoldAnchor.HUB_HIGH);
                MapVoteManager.getInstance().syncToPlayer(player);
                break;
            }
            case MAP_LOADING: {
                this.applyMatchHoldState(player, HoldAnchor.AUTO);
                player.m_213846_(Component.m_237113_("\u00a7e\u6218\u573a\u6b63\u5728\u88c5\u8f7d\uff0c\u8bf7\u7a0d\u5019\u3002"));
                break;
            }
            case TEAM_SELECT: {
                String existing = this.assignedTeams.get(player.m_20148_());
                if (existing != null) {
                    this.clearPlayerRoundAssignment(player);
                    this.applyTeamAssignmentToPlayer(player, existing);
                    this.applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
                    this.teamSelectedPlayers.add(player.m_20148_());
                    this.broadcastTeamSelectState();
                    break;
                }
                this.waitingForTeam.add(player.m_20148_());
                this.clearPlayerRoundAssignment(player);
                this.applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
                NetworkManager.sendOpenFactionScreen(player);
                this.broadcastTeamSelectState();
                break;
            }
            case ROUND_END: 
            case CLEANUP: {
                this.forcePlayerToHub(player);
                player.m_213846_(Component.m_237113_("\u00a7e\u672c\u56de\u5408\u6b63\u5728\u7ed3\u7b97\uff0c\u8bf7\u7b49\u5f85\u4e0b\u4e00\u5c40\u3002"));
                break;
            }
            default: {
                this.onMidGameJoin(player);
            }
        }
    }

    public void onPlayerLeave(ServerPlayer player) {
        if (player != null) {
            this.forcePlayerToHub(player);
            this.onPlayerLeave(player.m_20148_());
        }
    }

    public void onPlayerLeave(UUID uuid) {
        this.waitingForTeam.remove(uuid);
        this.teamSelectedPlayers.remove(uuid);
        this.midGameJoiners.remove(uuid);
        this.deployClassSelected.remove(uuid);
        MapVoteManager.getInstance().onPlayerLeave(uuid);
        VoteManager.getInstance().removePlayer(uuid);
        ClassSelectManager.getInstance().removePlayerVote(uuid);
        PlayerMatchStatsManager.getInstance().onPlayerLeave(uuid);
        BastionManager.getInstance().unlockPlayerPosition(uuid);
        BastionManager.getInstance().clearWaiting(uuid);
    }

    public Vec3 getWaitingPosition() {
        return new Vec3(0.5, GameConfig.getWaitingY(), 0.5);
    }

    public void applyWaitingState(ServerPlayer player) {
        this.applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
    }

    public void applyBattlefieldWaitingState(ServerPlayer player) {
        this.applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
    }

    public void applyHubState(ServerPlayer player) {
        this.forcePlayerToHub(player);
    }

    public boolean shouldForceHubAdventure(ServerPlayer player) {
        return player != null && player.f_8906_ != null && !player.m_20310_(2) && Level.f_46428_.equals(player.m_284548_().m_46472_());
    }

    public void applyHubAdventureOnEnter(ServerPlayer player) {
        if (player == null || player.f_8906_ == null) {
            return;
        }
        if (!Level.f_46428_.equals(player.m_284548_().m_46472_())) {
            return;
        }
        if (player.f_8941_.m_9290_() != GameType.ADVENTURE) {
            player.m_143403_(GameType.ADVENTURE);
        }
    }

    public void enforceHubAdventure(ServerPlayer player) {
        if (this.shouldForceHubAdventure(player) && player.f_8941_.m_9290_() != GameType.ADVENTURE) {
            player.m_143403_(GameType.ADVENTURE);
        }
    }

    public void applyBattlefieldMiningRestriction(ServerPlayer player) {
        if (player == null || player.f_8906_ == null) {
            return;
        }
        player.m_21195_(MobEffects.f_19599_);
    }

    @Deprecated
    public void applyBattlefieldMiningFatigue(ServerPlayer player) {
        this.applyBattlefieldMiningRestriction(player);
    }

    public boolean shouldRestrictBattlefieldMining(Player player) {
        if (player == null || !player.m_6084_()) {
            return false;
        }
        if (player.m_5833_() || player.m_150110_().f_35937_) {
            return false;
        }
        if (!this.isDeployOrBattlePhaseForMining(player)) {
            return false;
        }
        Level level = player.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            return BattlefieldContext.isActiveBattlefield(serverLevel);
        }
        if (BattlefieldContext.isActive()) {
            return BattlefieldContext.isActiveBattlefield(player.m_9236_().m_46472_());
        }
        return !Level.f_46428_.equals(player.m_9236_().m_46472_());
    }

    private boolean isDeployOrBattlePhaseForMining(Player player) {
        if (!player.m_9236_().m_5776_()) {
            return this.currentPhase == GamePhase.DEPLOYING || this.currentPhase == GamePhase.BATTLE;
        }
        try {
            Class<?> clientState = Class.forName("org.espetro.client.gui.ClientGameState");
            Object phase = clientState.getMethod("getCurrentPhase", new Class[0]).invoke(null, new Object[0]);
            if (phase == null) {
                return false;
            }
            String name = phase.toString();
            return "DEPLOYING".equals(name) || "BATTLE".equals(name);
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public void forcePlayerToHub(ServerPlayer player) {
        boolean farFromSpawn;
        if (player == null || player.f_8906_ == null) {
            return;
        }
        player.m_21195_(MobEffects.f_19610_);
        player.m_21195_(MobEffects.f_19599_);
        BastionManager.getInstance().unlockPlayerPosition(player.m_20148_());
        BastionManager.getInstance().clearWaiting(player.m_20148_());
        if (player.m_6084_()) {
            player.m_21153_(player.m_21233_());
            player.m_20095_();
            player.m_20301_(player.m_6062_());
        }
        ServerLevel hub = player.f_8924_.m_129783_();
        BlockPos spawn = hub.m_220360_();
        double x = (double)spawn.m_123341_() + 0.5;
        double y = spawn.m_123342_();
        double z = (double)spawn.m_123343_() + 0.5;
        player.m_9158_(Level.f_46428_, spawn, 0.0f, true, false);
        boolean wrongDimension = player.m_284548_() != hub;
        boolean bl = farFromSpawn = player.m_20275_(x, y, z) > 4.0;
        if (wrongDimension || farFromSpawn) {
            player.m_8999_(hub, x, y, z, 0.0f, 0.0f);
        }
        this.applyHubAdventureOnEnter(player);
        player.m_20334_(0.0, 0.0, 0.0);
        player.f_19789_ = 0.0f;
    }

    public void applyDeploymentWaitingState(ServerPlayer player) {
        this.applyAdventureDeploymentWaitingState(player);
    }

    public void applyAdventureDeploymentWaitingState(ServerPlayer player) {
        if (player == null || player.f_8906_ == null) {
            return;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null || !BattlefieldContext.isActive()) {
            this.applyMatchHoldState(player, HoldAnchor.BATTLEFIELD_WAIT);
            return;
        }
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        ServerLevel battlefield = BattlefieldContext.requireBattlefield(player.f_8924_);
        BastionManager.DeployPoint original = BastionManager.getInstance().getPlayerDeployPoint(player.m_20148_());
        BlockPos originalPos = original != null && original.pos != null ? original.pos : new BlockPos((int)spawn.x, (int)spawn.y, (int)spawn.z);
        Vec3 hold = new Vec3((double)originalPos.m_123341_() + 0.5, (double)originalPos.m_123342_() + 0.1, (double)originalPos.m_123343_() + 0.5);
        if (player.m_284548_() != battlefield || player.m_20238_(hold) > 0.01) {
            player.m_8999_(battlefield, hold.f_82479_, hold.f_82480_, hold.f_82481_, spawn.yaw, 0.0f);
        }
        BastionManager.getInstance().lockPlayerPosition(player.m_20148_(), hold);
        GameStateManager.enforceAdventureBlindness(player, true);
    }

    public static void enforceAdventureBlindness(ServerPlayer player, boolean forceClientResync) {
        if (player == null) {
            return;
        }
        if (player.f_8941_.m_9290_() != GameType.ADVENTURE) {
            player.m_143403_(GameType.ADVENTURE);
        }
        if (forceClientResync) {
            player.m_21195_(MobEffects.f_19610_);
            player.m_7292_(new MobEffectInstance(MobEffects.f_19610_, Integer.MAX_VALUE, 0, false, false, false));
        } else if (!player.m_21023_(MobEffects.f_19610_)) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19610_, Integer.MAX_VALUE, 0, false, false, false));
        }
        if (!player.m_21023_(MobEffects.f_19606_)) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, Integer.MAX_VALUE, 127, false, false, false));
        }
        player.m_20334_(0.0, 0.0, 0.0);
        player.f_19789_ = 0.0f;
    }

    public boolean isMidGameJoiner(UUID uuid) {
        return this.midGameJoiners.contains(uuid);
    }

    public void removeMidGameJoiner(UUID uuid) {
        this.midGameJoiners.remove(uuid);
    }

    public void onMidGameJoin(ServerPlayer player) {
        String assigned = this.assignedTeams.get(player.m_20148_());
        if (assigned != null) {
            String factionId;
            this.midGameJoiners.add(player.m_20148_());
            this.clearPlayerRoundAssignment(player);
            this.applyTeamAssignmentToPlayer(player, assigned);
            ClassSelectManager selectManager = ClassSelectManager.getInstance();
            String string = factionId = "ATTACK".equals(assigned) ? selectManager.getFinalAttackClass() : selectManager.getFinalDefendClass();
            if (factionId == null) {
                factionId = assigned;
            }
            ClassCountManager.getInstance().setPlayerFaction(player.m_20148_(), factionId);
            if (this.currentPhase == GamePhase.DEPLOYING || this.currentPhase == GamePhase.BATTLE) {
                this.prepareDeploySelection(player, assigned);
                NetworkManager.queueUnifiedDeployScreen(player, this.getDeployTimeRemainingSeconds());
            } else {
                this.applyMatchHoldState(player, HoldAnchor.HUB_HIGH);
            }
            player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u8fd8\u539f\u4f60\u7684\u961f\u4f0d\u5206\u914d: " + TeamDisplayNames.coloredDisplayName(assigned)));
            return;
        }
        this.midGameJoiners.add(player.m_20148_());
        this.clearPlayerRoundAssignment(player);
        this.applyMatchHoldState(player, HoldAnchor.HUB_HIGH);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new GamePhaseSyncPacket(this.currentPhase, this.getCurrentMapFolder(), BattlefieldContext.getObjectiveMode()));
        NetworkManager.sendOpenFactionScreen(player);
        player.m_213846_(Component.m_237113_("\u00a76========================================"));
        player.m_213846_(Component.m_237113_("\u00a7e\u26a1 \u6218\u573a\u4e0a\u9700\u8981\u589e\u63f4\uff01\u8bf7\u9009\u62e9\u4f60\u7684\u9635\u8425"));
        player.m_213846_(Component.m_237113_("\u00a7e\u6309\u4e0a\u65b9\u6309\u94ae\u9009\u62e9 " + TeamDisplayNames.coloredDisplayName("ATTACK") + " \u00a7e\u6216 " + TeamDisplayNames.coloredDisplayName("DEFEND")));
        player.m_213846_(Component.m_237113_("\u00a76========================================"));
    }

    public void onMidGameTeamSelected(ServerPlayer player, String team) {
        String factionId;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        String string = factionId = "ATTACK".equals(team) ? selectManager.getFinalAttackClass() : selectManager.getFinalDefendClass();
        if (factionId == null) {
            factionId = team;
            Espetro.LOGGER.warn("\u6218\u5c40\u4e2d\u52a0\u5165: {} \u65b9\u7f16\u5236\u672a\u8bbe\u7f6e\uff0c\u4f7f\u7528\u9ed8\u8ba4\u503c", (Object)team);
        }
        if ("ATTACK".equals(team)) {
            TeamManager.joinAttackTeam(server, player.m_7755_().getString());
        } else {
            TeamManager.joinDefendTeam(server, player.m_7755_().getString());
        }
        ClassCountManager.getInstance().setPlayerFaction(player.m_20148_(), factionId);
        ClassCountManager.getInstance().setPlayerTeam(player.m_20148_(), team);
        PlayerMatchStatsManager.getInstance().onTeamSelected(player, team);
        VoteManager voteManager = VoteManager.getInstance();
        if ("ATTACK".equals(team)) {
            voteManager.addAttackPlayer(player.m_20148_());
        } else {
            voteManager.addDefendPlayer(player.m_20148_());
        }
        this.teamSelectedPlayers.add(player.m_20148_());
        switch (this.currentPhase) {
            case DEFEND_COMMANDER_VOTE: 
            case ATTACK_COMMANDER_VOTE: {
                String votingTeam = this.currentPhase.getActiveTeam();
                int voteRemaining = voteManager.getRemainingSeconds();
                if (team.equals(votingTeam)) {
                    NetworkManager.sendCommanderVoteScreenToPlayer(player, team, voteRemaining);
                    break;
                }
                NetworkManager.sendCommanderVoteScreenToPlayer(player, team, 0);
                break;
            }
            case DEFEND_FACTION_SELECT: 
            case ATTACK_FACTION_SELECT: {
                boolean isCmd;
                String selectingTeam = this.currentPhase.getActiveTeam();
                int selectRemaining = selectManager.getRemainingSeconds();
                UUID cmdUuid = "ATTACK".equals(team) ? voteManager.getAttackCommander() : voteManager.getDefendCommander();
                boolean bl = isCmd = cmdUuid != null && cmdUuid.equals(player.m_20148_());
                if (team.equals(selectingTeam)) {
                    NetworkManager.sendClassSelectScreen(player, team, isCmd, selectRemaining);
                    break;
                }
                NetworkManager.sendClassSelectScreen(player, team, false, 0);
                break;
            }
            case DEPLOYING: {
                player.m_21219_();
                this.prepareDeploySelection(player, team);
                int remaining = this.getDeployTimeRemainingSeconds();
                NetworkManager.sendUnifiedDeployScreen(player, remaining);
                NetworkManager.sendDeployPointSync(player);
                NetworkManager.sendWaitingStatus(player, TeamDisplayNames.isSymmetricMode() ? "\u00a7e\u90e8\u7f72\u4e2d[" + remaining + "\u79d2]" : ("ATTACK".equals(team) ? "\u00a7c\u7b49\u5f85\u8fdb\u653b\u00a7e[" + remaining + "\u79d2]" : "\u00a79\u90e8\u7f72\u9632\u7ebf\u00a7e[" + remaining + "\u79d2]"), true);
                player.m_213846_(Component.m_237113_("\u00a7a\u2705 \u589e\u63f4\u5230\u8fbe\u90e8\u7f72\u9636\u6bb5\uff01\u8bf7\u5728\u5de6\u4fa7\u9762\u677f\u9009\u62e9\u804c\u4e1a\u548c\u90e8\u7f72\u70b9"));
                Espetro.broadcastToTeam(team, "\u00a7e\u26a1 \u589e\u63f4\u5230\u8fbe\uff01" + player.m_7755_().getString() + " \u52a0\u5165\u4e86 " + TeamDisplayNames.coloredDisplayName(team) + " \u00a77(\u90e8\u7f72\u4e2d)");
                break;
            }
            case BATTLE: {
                ServerPlayer commander;
                UUID commanderUuid;
                player.m_21219_();
                BastionManager bastionManager = BastionManager.getInstance();
                SpawnPointConfig.SpawnPoint spawnPoint = SpawnPointConfig.getSpawnPoint(team);
                ServerLevel overworld = BattlefieldContext.requireBattlefield(server);
                bastionManager.savePlayerDeployPoint(player, new BlockPos((int)spawnPoint.x, (int)spawnPoint.y, (int)spawnPoint.z), overworld);
                bastionManager.activatePlayerBastionSelection(player.m_20148_());
                this.applyDeploymentWaitingState(player);
                TroopCountManager troopMgr = TroopCountManager.getInstance();
                NetworkManager.broadcastTroopCounts(troopMgr.getAttackTroops(), troopMgr.getDefendTroops());
                String commanderName = "\u65e0";
                UUID uUID = commanderUuid = "ATTACK".equals(team) ? voteManager.getAttackCommander() : voteManager.getDefendCommander();
                if (commanderUuid != null && (commander = server.m_6846_().m_11259_(commanderUuid)) != null) {
                    commanderName = commander.m_7755_().getString();
                }
                player.m_213846_(Component.m_237113_("\u00a7a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));
                player.m_213846_(Component.m_237113_("\u00a7a\u4f60\u5df2\u4f5c\u4e3a\u589e\u63f4\u52a0\u5165" + TeamDisplayNames.coloredDisplayName(team) + "\u00a7a\uff01"));
                player.m_213846_(Component.m_237113_("\u00a7e\u7f16\u5236: \u00a7f" + factionId));
                player.m_213846_(Component.m_237113_("\u00a7e\u6307\u6325\u5b98: \u00a7f" + commanderName));
                player.m_213846_(Component.m_237113_("\u00a7a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"));
                player.m_213846_(Component.m_237113_("\u00a7e\u26a0 \u8bf7\u5148\u5728\u90e8\u7f72\u9762\u677f\u9009\u62e9\u90e8\u7f72\u70b9\uff0c\u518d\u9009\u62e9\u804c\u4e1a\uff01"));
                NetworkManager.sendUnifiedDeployScreen(player, -1);
                NetworkManager.sendDeployPointSync(player);
                Espetro.broadcastToTeam(team, "\u00a7e\u26a1 \u589e\u63f4\u5230\u8fbe\uff01" + player.m_7755_().getString() + " \u52a0\u5165\u4e86 " + TeamDisplayNames.coloredDisplayName(team));
                break;
            }
        }
    }

    public void onMidGameDeployComplete(ServerPlayer player) {
        boolean wasMidGameJoiner = this.midGameJoiners.remove(player.m_20148_());
        this.applyBattlefieldMiningRestriction(player);
        ClassEquipment.ensureEquippedIfNeeded(player);
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId != null) {
            if (wasMidGameJoiner) {
                player.m_213846_(Component.m_237113_("\u00a7a\u2705 \u90e8\u7f72\u5b8c\u6210\uff01"));
            } else {
                player.m_213846_(Component.m_237113_("\u00a7a\u2705 \u5df2\u590d\u6d3b\uff01"));
            }
        }
    }

    public boolean endRound(String winner) {
        return this.endRound(winner, false);
    }

    public boolean endRound(String winner, boolean attackerTimedOut) {
        String loseShow;
        String winShow;
        int level;
        String normalized;
        if (this.currentPhase != GamePhase.BATTLE) {
            Espetro.LOGGER.warn("[endRound] \u62d2\u7edd\uff1a\u5f53\u524d\u9636\u6bb5={} (\u975e BATTLE)", (Object)this.currentPhase);
            return false;
        }
        String string = normalized = winner == null ? "" : winner.trim().toUpperCase(Locale.ROOT);
        if (!("ATTACK".equals(normalized) || "DEFEND".equals(normalized) || "DRAW".equals(normalized))) {
            Espetro.LOGGER.warn("[endRound] \u62d2\u7edd\uff1a\u975e\u6cd5\u8d62\u5bb6={}", (Object)normalized);
            return false;
        }
        this.pendingRoundWinner = normalized;
        BattlefieldContext.setLastRoundWinner(normalized);
        this.roundEndTickCounter = 0;
        this.removeAttackWaitingBarrier();
        this.setPhase(GamePhase.ROUND_END);
        TroopCountManager tcm = TroopCountManager.getInstance();
        int atkRaw = tcm.getAttackTroops();
        int defRaw = tcm.getDefendTroops();
        if (attackerTimedOut) {
            atkRaw = 0;
        }
        String atkFactionId = ClassSelectManager.getInstance().getFinalAttackClass();
        String defFactionId = ClassSelectManager.getInstance().getFinalDefendClass();
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        String atkShow = GameStateManager.getFactionShowName(loader, atkFactionId, TeamDisplayNames.displayName("ATTACK"));
        String defShow = GameStateManager.getFactionShowName(loader, defFactionId, TeamDisplayNames.displayName("DEFEND"));
        if ("DRAW".equals(normalized)) {
            boolean diff = false;
            level = 0;
            winShow = null;
            loseShow = null;
        } else if ("ATTACK".equals(normalized)) {
            int diff = atkRaw - defRaw;
            level = GameStateManager.calcResultLevel(diff);
            winShow = atkShow;
            loseShow = defShow;
        } else {
            int diff = defRaw - atkRaw;
            level = GameStateManager.calcResultLevel(diff);
            winShow = defShow;
            loseShow = atkShow;
        }
        int displaySeconds = GameConfig.getRoundEndSeconds();
        NetworkManager.broadcastRoundEnd(normalized, displaySeconds, winShow, loseShow, atkRaw, defRaw, level, attackerTimedOut);
        FactionAudioCoordinator.broadcastRoundResult(normalized);
        String result = switch (normalized) {
            case "ATTACK" -> TeamDisplayNames.coloredDisplayName("ATTACK") + "\u80dc\u5229";
            case "DEFEND" -> TeamDisplayNames.coloredDisplayName("DEFEND") + "\u80dc\u5229";
            default -> "\u00a7e\u5e73\u5c40";
        };
        Espetro.broadcastToAll("\u00a76===== " + result + " \u00a76=====");
        Espetro.LOGGER.info("[endRound] winner={} atkTickets={} defTickets={} level={} timedOut={}", new Object[]{normalized, atkRaw, defRaw, level, attackerTimedOut});
        return true;
    }

    private static int calcResultLevel(int diff) {
        if (diff > 200) {
            return 5;
        }
        if (diff > 100) {
            return 4;
        }
        if (diff > 50) {
            return 3;
        }
        if (diff > 25) {
            return 2;
        }
        if (diff > 0) {
            return 1;
        }
        return 0;
    }

    private static String getFactionShowName(FactionDataLoader loader, String factionId, String fallback) {
        if (loader == null || factionId == null) {
            return fallback;
        }
        FactionDataLoader.FactionData fd = loader.getFaction(factionId);
        if (fd == null) {
            return fallback;
        }
        if (fd.showName != null && !fd.showName.isEmpty()) {
            return fd.showName;
        }
        return fd.name != null && !fd.name.isEmpty() ? fd.name : fallback;
    }

    private void beginCleanup() {
        if (this.currentPhase != GamePhase.ROUND_END) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        this.setPhase(GamePhase.CLEANUP);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            this.applyHubState(player);
            ClassEquipment.clearEquipment(player);
            TeamManager.removeFromAllTeams(server.m_129896_(), player.m_7755_().getString());
        }
        ActiveMapConfig completedMap = this.pendingMap != null ? this.pendingMap : BattlefieldContext.getOrNull();
        this.clearRoundRuntime(false);
        BattlefieldWorldManager.getInstance().cleanupBattlefield(server, completedMap, cleanupResult -> {
            this.pendingMap = null;
            this.pendingRoundWinner = null;
            this.setPhase(GamePhase.LOBBY);
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                this.applyHubState(player);
                NetworkManager.sendOpenHubScreen(player, server.m_7416_(), "\u7b49\u5f85\u7ba1\u7406\u5458\u5f00\u59cb\u4e0b\u4e00\u5c40");
            }
            this.broadcastHubStatus();
            if (!cleanupResult.success()) {
                Espetro.broadcastToAll("\u00a7c[Espetro] \u6218\u573a\u5b58\u6863\u526f\u672c\u5220\u9664\u5931\u8d25\uff0c\u4e0b\u6b21\u52a0\u8f7d\u5c06\u91cd\u8bd5\uff1a" + cleanupResult.error());
            }
        });
    }

    private void clearPlayerRoundAssignment(ServerPlayer player) {
        if (player == null) {
            return;
        }
        String squadTeam = SquadManager.getInstance().removePlayer(player.m_20148_());
        ClassCountManager.getInstance().removePlayer(player);
        ClassEquipment.clearEquipment(player);
        TeamManager.removeFromAllTeams(player.f_8924_.m_129896_(), player.m_7755_().getString());
        this.waitingForTeam.remove(player.m_20148_());
        this.teamSelectedPlayers.remove(player.m_20148_());
        this.deployClassSelected.remove(player.m_20148_());
        if (squadTeam != null) {
            NetworkManager.syncSquadsToTeam(squadTeam);
        }
    }

    private void broadcastHubStatus() {
        int count;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        this.lastHubBroadcastPlayerCount = count = server.m_7416_();
        this.lastHubBroadcastTick = this.deployTickCounter;
        String text = "\u00a76\u4e3b\u57ce \u00a77| \u00a7e\u5728\u7ebf\u4eba\u6570: \u00a7f" + count + " \u00a77| \u00a7e\u7b49\u5f85\u7ba1\u7406\u5458\u5f00\u59cb\u4e0b\u4e00\u5c40";
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (player.m_284548_() != server.m_129783_()) continue;
            NetworkManager.sendWaitingStatus(player, text, true);
        }
    }

    private void broadcastHubStatusThrottled() {
        boolean intervalElapsed;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        int count = server.m_7416_();
        boolean countChanged = count != this.lastHubBroadcastPlayerCount;
        boolean bl = intervalElapsed = this.deployTickCounter - this.lastHubBroadcastTick >= 100;
        if (countChanged || intervalElapsed || this.lastHubBroadcastTick == Integer.MIN_VALUE) {
            this.broadcastHubStatus();
        }
    }

    public void markDeployClassSelected(UUID uuid) {
        this.deployClassSelected.add(uuid);
    }

    public boolean isDeployClassSelected(UUID uuid) {
        return this.deployClassSelected.contains(uuid);
    }

    public int getTeamSelectedCount() {
        return this.teamSelectedPlayers.size();
    }

    public int getWaitingForTeamCount() {
        return this.waitingForTeam.size();
    }

    public boolean isGameStarted() {
        return this.currentPhase == GamePhase.BATTLE;
    }

    public void forceStartGame() {
        MinecraftServer server = Espetro.getServer();
        if (server != null && this.currentPhase.isLobbyLike()) {
            this.prestart(server);
        }
    }

    public Map<String, SpawnPointConfig.SpawnPoint> getAllSpawnPoints() {
        return SpawnPointConfig.getAllSpawnPoints();
    }

    public void setTeamSpawnPoint(String team, double x, double y, double z, float yaw) {
        SpawnPointConfig.setSpawnPoint(team, x, y, z, yaw);
        BlockPos newPos = new BlockPos((int)x, (int)y, (int)z);
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            ServerLevel overworld = BattlefieldContext.requireBattlefield(server);
            BastionManager bastionMgr = BastionManager.getInstance();
            for (UUID uuid : this.teamSelectedPlayers) {
                ServerPlayer player;
                String playerTeam = ClassCountManager.getInstance().getPlayerTeam(uuid);
                if (playerTeam == null) {
                    playerTeam = GameStateManager.getTeamFromFactionStatic(ClassCountManager.getInstance().getPlayerFaction(uuid));
                }
                if (!team.equals(playerTeam) || (player = server.m_6846_().m_11259_(uuid)) == null) continue;
                bastionMgr.savePlayerDeployPoint(player, newPos, overworld);
            }
        }
    }

    @Deprecated
    public int getReadyCount() {
        return this.teamSelectedPlayers.size();
    }

    public int getWaitingCount() {
        return this.waitingForTeam.size();
    }

    public static enum HoldAnchor {
        BATTLEFIELD_WAIT,
        HUB_HIGH,
        CURRENT_LOCK,
        AUTO;

    }
}

