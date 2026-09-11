package org.espetro.team;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.governance.CommanderGovernanceManager;

import java.util.*;

/**
 * 指挥官投票管理器
 * 双方并行投票：同时开始、统一倒计时（取攻/守配置较大者），结束时同时结算双方。
 */
public class VoteManager {

    private static VoteManager INSTANCE;

    // 投票是否在进行
    private boolean votingActive = false;
    // 兼容旧路径：串行模式下的当前投票队伍（并行模式为 null）
    private String currentVotingTeam = null;

    // 投票计时器（并行统一倒计时）
    private int voteTickCounter = 0;
    private int timeoutSeconds = 20;
    private static final int TICKS_PER_SECOND = 20;

    // 攻方投票: 玩家UUID -> 投票目标玩家UUID
    private final Map<UUID, UUID> attackVotes = new HashMap<>();
    // 守方投票: 玩家UUID -> 投票目标玩家UUID
    private final Map<UUID, UUID> defendVotes = new HashMap<>();

    // 攻方玩家列表
    private final Set<UUID> attackPlayers = new HashSet<>();
    // 守方玩家列表
    private final Set<UUID> defendPlayers = new HashSet<>();

    // 投票结果
    private UUID attackCommander = null;
    private UUID defendCommander = null;

    private VoteManager() {
        INSTANCE = this;
    }

    public static VoteManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VoteManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new VoteManager();
    }

    /**
     * 初始化投票（收集双方玩家）
     */
    public void initPlayers() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        attackPlayers.clear();
        defendPlayers.clear();

        String factionId;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            factionId = ClassCountManager.getInstance().getPlayerFaction(player.getUUID());
            if (factionId != null) {
                String team = GameStateManager.getTeamFromFactionStatic(factionId);
                if ("ATTACK".equals(team)) {
                    attackPlayers.add(player.getUUID());
                } else {
                    defendPlayers.add(player.getUUID());
                }
            }
        }

        Espetro.LOGGER.info("指挥官投票初始化！攻方{}人，守方{}人", attackPlayers.size(), defendPlayers.size());
    }

    /**
     * 开始双方并行指挥官投票。
     * 统一倒计时 = max(攻方配置, 守方配置)，到点同时结算双方。
     */
    public void startBothVote() {
        votingActive = true;
        currentVotingTeam = null; // 并行模式
        voteTickCounter = 0;
        attackVotes.clear();
        defendVotes.clear();

        timeoutSeconds = Math.max(
            GameConfig.getAttackCommanderVoteSeconds(),
            GameConfig.getDefendCommanderVoteSeconds());
        Espetro.LOGGER.info("双方指挥官投票开始（并行）！限时{}秒", timeoutSeconds);

        // 双方并行：各自收到本方候选 + 统一倒计时
        org.espetro.network.NetworkManager.broadcastBothCommanderVoteScreens(timeoutSeconds);
    }

    /**
     * 开始守方指挥官投票（旧串行路径保留）
     */
    public void startDefendVote() {
        votingActive = true;
        currentVotingTeam = "DEFEND";
        voteTickCounter = 0;
        defendVotes.clear();

        int timeout = GameConfig.getDefendCommanderVoteSeconds();
        timeoutSeconds = timeout;
        Espetro.LOGGER.info("守方指挥官投票开始！限时{}秒", timeout);

        org.espetro.network.NetworkManager.broadcastCommanderVoteScreenForTeam("DEFEND", timeout);
    }

    /**
     * 开始攻方指挥官投票（旧串行路径保留）
     */
    public void startAttackVote() {
        votingActive = true;
        currentVotingTeam = "ATTACK";
        voteTickCounter = 0;
        attackVotes.clear();

        int timeout = GameConfig.getAttackCommanderVoteSeconds();
        timeoutSeconds = timeout;
        Espetro.LOGGER.info("攻方指挥官投票开始！限时{}秒", timeout);

        org.espetro.network.NetworkManager.broadcastCommanderVoteScreenForTeam("ATTACK", timeout);
    }

    /**
     * 玩家投票（并行：按投票者自身队伍写入对应票箱，目标必须与投票者同队）
     */
    public boolean castVote(ServerPlayer voter, UUID targetUUID) {
        if (!votingActive) return false;
        if (voter == null || voter.getUUID().equals(targetUUID)) return false;

        String voterTeam = getPlayerTeam(voter.getUUID());
        if (voterTeam == null) {
            Espetro.sendToPlayer(voter, "§c你尚未加入任何队伍，无法投票！");
            return false;
        }
        // 并行模式下两方均可投；串行模式仍需匹配当前投票方
        if (!isParallel() && !voterTeam.equals(currentVotingTeam)) {
            Espetro.sendToPlayer(voter, "§c当前不是你的投票时间！");
            return false;
        }

        String targetTeam = getPlayerTeam(targetUUID);
        if (targetTeam == null || !targetTeam.equals(voterTeam)) {
            return false;
        }

        if ("ATTACK".equals(voterTeam)) {
            attackVotes.put(voter.getUUID(), targetUUID);
            Espetro.LOGGER.info("玩家 {} 投票给攻方玩家 {}", voter.getName().getString(), targetUUID);
        } else {
            defendVotes.put(voter.getUUID(), targetUUID);
            Espetro.LOGGER.info("玩家 {} 投票给守方玩家 {}", voter.getName().getString(), targetUUID);
        }

        broadcastVoteUpdate();
        return true;
    }

    public boolean isParallel() {
        return votingActive && currentVotingTeam == null;
    }

    private String getPlayerTeam(UUID uuid) {
        if (attackPlayers.contains(uuid)) return "ATTACK";
        if (defendPlayers.contains(uuid)) return "DEFEND";
        return null;
    }

    /**
     * 获取玩家的投票目标
     */
    public UUID getVoteTarget(UUID voterUUID) {
        if (attackVotes.containsKey(voterUUID)) {
            return attackVotes.get(voterUUID);
        }
        return defendVotes.get(voterUUID);
    }

    /**
     * 计算某玩家在其所属队伍的得票数（按目标玩家 UUID 匹配）
     */
    public int getVoteCount(UUID playerUUID) {
        if (attackPlayers.contains(playerUUID)) {
            return countVotesIn(attackVotes, playerUUID);
        }
        if (defendPlayers.contains(playerUUID)) {
            return countVotesIn(defendVotes, playerUUID);
        }
        return 0;
    }

    private static int countVotesIn(Map<UUID, UUID> votes, UUID target) {
        int count = 0;
        for (UUID t : votes.values()) {
            if (target.equals(t)) count++;
        }
        return count;
    }

    /**
     * 获取指定队伍的投票 Map（视图）
     */
    public Map<UUID, UUID> getTeamVotes(String team) {
        return "ATTACK".equals(team) ? attackVotes : defendVotes;
    }

    /**
     * 获取当前投票方的得票最高者
     */
    private UUID getWinningCandidate(Set<UUID> players, Map<UUID, UUID> votes) {
        if (players.isEmpty()) return null;

        Map<UUID, Integer> voteCounts = new HashMap<>();
        for (UUID player : players) {
            voteCounts.put(player, 0);
        }

        for (UUID target : votes.values()) {
            voteCounts.merge(target, 1, Integer::sum);
        }

        int maxVotes = 0;
        for (int count : voteCounts.values()) {
            if (count > maxVotes) maxVotes = count;
        }

        List<UUID> candidates = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() == maxVotes) {
                candidates.add(entry.getKey());
            }
        }

        if (candidates.size() > 1) {
            return candidates.get(new Random().nextInt(candidates.size()));
        }

        return candidates.isEmpty() ? null : candidates.get(0);
    }

    /**
     * 结算指定队伍并完成治理同步。
     */
    private void settleTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        UUID commander;
        if ("DEFEND".equals(team)) {
            defendCommander = getWinningCandidate(defendPlayers, defendVotes);
            commander = defendCommander;
            String name = getPlayerName(server, defendCommander);
            Espetro.broadcastToTeam("DEFEND", "§6★ 你所在的队伍指挥官为§9" + name + "§6！★");
            if (defendCommander != null) {
                ServerPlayer cmd = server == null ? null : server.getPlayerList().getPlayer(defendCommander);
                if (cmd != null) {
                    Espetro.sendToPlayer(cmd, "§a你已被选为§9守方§a指挥官！");
                    org.espetro.network.NetworkManager.sendCommanderSkillSync(cmd);
                }
            }
            Espetro.LOGGER.info("守方指挥官投票结束！指挥官: {}", name);
            CommanderGovernanceManager.getInstance()
                .acceptElectionResult("DEFEND", defendCommander);
        } else {
            attackCommander = getWinningCandidate(attackPlayers, attackVotes);
            commander = attackCommander;
            String name = getPlayerName(server, attackCommander);
            Espetro.broadcastToTeam("ATTACK", "§6★ 你所在的队伍指挥官为§c" + name + "§6！★");
            if (attackCommander != null) {
                ServerPlayer cmd = server == null ? null : server.getPlayerList().getPlayer(attackCommander);
                if (cmd != null) {
                    Espetro.sendToPlayer(cmd, "§a你已被选为§c攻方§a指挥官！");
                    org.espetro.network.NetworkManager.sendCommanderSkillSync(cmd);
                }
            }
            Espetro.LOGGER.info("攻方指挥官投票结束！指挥官: {}", name);
            CommanderGovernanceManager.getInstance()
                .acceptElectionResult("ATTACK", attackCommander);
        }
        // 指挥官选出后自动创建名为「指挥小队」的默认小队（走正常建队流程，指挥官为队长）。
        if (commander != null && server != null) {
            ServerPlayer commanderPlayer = server.getPlayerList().getPlayer(commander);
            if (commanderPlayer == null) {
                Espetro.LOGGER.info("[指挥小队] {} 指挥官 {} 不在线，跳过自动建队",
                    team, commander);
            } else if (org.espetro.team.SquadManager.getInstance().getPlayerSquadId(commander)
                    != org.espetro.team.SquadManager.NO_SQUAD) {
                Espetro.LOGGER.info("[指挥小队] {} 指挥官 {} 已在小队中，跳过自动建队",
                    team, commanderPlayer.getName().getString());
            } else {
                var result = org.espetro.team.SquadManager.getInstance().createSquad(
                    commanderPlayer, org.espetro.team.SquadManager.COMMAND_SQUAD_NAME);
                Espetro.LOGGER.info("[指挥小队] {} 自动建队结果 success={} team={} msg={}",
                    team, result.success, result.team, result.message);
                if (result.success && result.team != null) {
                    org.espetro.team.TeamPackManager.getInstance().reconcileTeam(result.team);
                    org.espetro.team.TeamPackManager.getInstance().handleSquadLeaderTransition(
                        commanderPlayer,
                        result.team,
                        org.espetro.team.SquadManager.NO_SQUAD,
                        false,
                        result.team,
                        org.espetro.team.SquadManager.getInstance().getPlayerSquadId(commander),
                        true
                    );
                    org.espetro.network.NetworkManager.sendCommanderSkillSync(commanderPlayer);
                    org.espetro.network.NetworkManager.syncSquadsToTeam(result.team);
                    org.espetro.network.NetworkManager.broadcastClassCounts(result.team,
                        org.espetro.team.ClassCountManager.getInstance()
                            .getPlayerFaction(commander));
                    org.espetro.network.NetworkManager.broadcastMatchStats(
                        org.espetro.stats.PlayerMatchStatsManager.getInstance());
                    org.espetro.network.NetworkManager.syncUnifiedDeployScreen(commanderPlayer, -1);
                }
            }
        }
    }

    /**
     * 结束投票（并行：同时结算双方；串行兼容：结算当前队）。
     * @return 并行模式返回 "BOTH"，串行模式返回完成队伍 "DEFEND"/"ATTACK"；无投票返回 null
     */
    public String finishCurrentVote() {
        if (!votingActive) return null;

        // 先读并行标记再复位：isParallel() 依赖 votingActive/currentVotingTeam，
        // 复位后判断会恒 false 导致 settleTeam 不执行（指挥官/指挥小队不产生）。
        boolean parallel = isParallel();
        String legacyTeam = currentVotingTeam;
        votingActive = false;
        currentVotingTeam = null;

        if (parallel) {
            settleTeam("ATTACK");
            settleTeam("DEFEND");
            return "BOTH";
        }
        // 旧串行路径兼容：结算当前投票队
        if (legacyTeam != null) {
            settleTeam(legacyTeam);
            return legacyTeam;
        }
        return null;
    }

    private String getPlayerName(MinecraftServer server, UUID uuid) {
        if (uuid == null) return "无";
        if (server == null) return "离线";
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        return player != null ? player.getName().getString() : "离线玩家";
    }

    /**
     * 广播投票数据更新：双方各自收到本方候选得票 + 统一倒计时。
     */
    private void broadcastVoteUpdate() {
        if (!votingActive) return;

        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        int remaining = getRemainingSeconds();
        sendTeamVoteUpdate(server, "ATTACK", remaining);
        if (isParallel()) {
            sendTeamVoteUpdate(server, "DEFEND", remaining);
        } else {
            // 串行：非投票方收到空票 + 对方倒计时
            String active = currentVotingTeam;
            String waiting = "ATTACK".equals(active) ? "DEFEND" : "ATTACK";
            org.espetro.network.VoteDataPacket waitingPacket =
                new org.espetro.network.VoteDataPacket(Collections.emptyMap(), 0, remaining);
            for (UUID uuid : ("ATTACK".equals(waiting) ? attackPlayers : defendPlayers)) {
                ServerPlayer p = server.getPlayerList().getPlayer(uuid);
                if (p != null) {
                    org.espetro.network.NetworkManager.NET.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> p), waitingPacket);
                }
            }
        }
    }

    private void sendTeamVoteUpdate(MinecraftServer server, String team, int remaining) {
        Set<UUID> players = "ATTACK".equals(team) ? attackPlayers : defendPlayers;
        Map<UUID, UUID> votes = "ATTACK".equals(team) ? attackVotes : defendVotes;

        Map<String, Integer> voteCounts = new java.util.HashMap<>();
        for (UUID uuid : players) {
            ServerPlayer p = server.getPlayerList().getPlayer(uuid);
            if (p != null) {
                voteCounts.put(p.getName().getString(), countVotesIn(votes, uuid));
            }
        }

        org.espetro.network.VoteDataPacket activePacket =
            new org.espetro.network.VoteDataPacket(voteCounts, remaining, -1);
        for (UUID uuid : players) {
            ServerPlayer p = server.getPlayerList().getPlayer(uuid);
            if (p != null) {
                org.espetro.network.NetworkManager.NET.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> p), activePacket);
            }
        }
    }

    /**
     * 服务器Tick - 处理投票超时
     */
    public void onServerTick() {
        if (!votingActive) return;

        voteTickCounter++;
        int secondsRemaining = timeoutSeconds - (voteTickCounter / TICKS_PER_SECOND);

        if (voteTickCounter % TICKS_PER_SECOND == 0) {
            broadcastVoteUpdate();
        }
    }

    /**
     * 检查当前投票是否超时
     */
    public boolean isCurrentVoteTimedOut() {
        if (!votingActive) return false;
        return voteTickCounter >= timeoutSeconds * TICKS_PER_SECOND;
    }

    /**
     * 检查投票是否进行中
     */
    public boolean isVotingActive() {
        return votingActive;
    }

    /**
     * 获取当前投票的队伍（并行模式返回 null）
     */
    public String getCurrentVotingTeam() {
        return currentVotingTeam;
    }

    /**
     * 获取投票剩余秒数
     */
    public int getRemainingSeconds() {
        if (!votingActive) return 0;
        return Math.max(0, timeoutSeconds - (voteTickCounter / TICKS_PER_SECOND));
    }

    /**
     * 获取指定队伍的在线玩家名（用于投票界面候选）。
     */
    public String[] getPlayerNamesForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return new String[0];
        Set<UUID> uuids = "ATTACK".equals(team) ? attackPlayers : defendPlayers;
        List<String> names = new ArrayList<>();
        for (UUID uuid : uuids) {
            ServerPlayer p = server.getPlayerList().getPlayer(uuid);
            if (p != null) {
                names.add(p.getName().getString());
            }
        }
        return names.toArray(new String[0]);
    }

    /**
     * 获取攻方玩家列表
     */
    public Set<UUID> getAttackPlayers() {
        return new HashSet<>(attackPlayers);
    }

    /**
     * 获取守方玩家列表
     */
    public Set<UUID> getDefendPlayers() {
        return new HashSet<>(defendPlayers);
    }

    /**
     * 获取攻方指挥官
     */
    public UUID getAttackCommander() {
        return attackCommander;
    }

    public void setAttackCommander(UUID uuid) {
        this.attackCommander = uuid;
    }

    public void setDefendCommander(UUID uuid) {
        this.defendCommander = uuid;
    }

    /**
     * 获取守方指挥官
     */
    public UUID getDefendCommander() {
        return defendCommander;
    }

    /**
     * 检查玩家是否是指挥官
     */
    public boolean isCommander(UUID uuid) {
        return (attackCommander != null && attackCommander.equals(uuid)) ||
               (defendCommander != null && defendCommander.equals(uuid));
    }

    /**
     * 检查玩家是否是指定队伍的指挥官
     */
    public boolean isCommanderOf(UUID uuid, String team) {
        if ("ATTACK".equals(team)) {
            return attackCommander != null && attackCommander.equals(uuid);
        } else {
            return defendCommander != null && defendCommander.equals(uuid);
        }
    }

    /**
     * 添加攻方玩家（战局中加入）
     */
    public void addAttackPlayer(UUID uuid) {
        defendPlayers.remove(uuid);
        attackPlayers.add(uuid);
    }

    /**
     * Disconnect/reselection cleanup. Commander result fields intentionally
     * remain so battle-start governance can detect an offline incumbent.
     */
    public void removePlayer(UUID uuid) {
        if (uuid == null) return;
        attackPlayers.remove(uuid);
        defendPlayers.remove(uuid);
        attackVotes.remove(uuid);
        defendVotes.remove(uuid);
        attackVotes.entrySet().removeIf(entry -> uuid.equals(entry.getValue()));
        defendVotes.entrySet().removeIf(entry -> uuid.equals(entry.getValue()));
        if (votingActive) {
            broadcastVoteUpdate();
        }
    }

    /**
     * 添加守方玩家（战局中加入）
     */
    public void addDefendPlayer(UUID uuid) {
        attackPlayers.remove(uuid);
        defendPlayers.add(uuid);
    }

    /**
     * 重置投票
     */
    public void reset() {
        votingActive = false;
        currentVotingTeam = null;
        voteTickCounter = 0;
        attackVotes.clear();
        defendVotes.clear();
        attackPlayers.clear();
        defendPlayers.clear();
        attackCommander = null;
        defendCommander = null;
    }
}
