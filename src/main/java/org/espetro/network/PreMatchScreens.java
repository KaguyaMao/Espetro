package org.espetro.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.*;
import org.espetro.network.core.PacketRegistrar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 开局前各阶段的服务端界面：阵营/编制选择、指挥官投票、地图投票与揭示、
 * 队伍分配展示、编制揭示。
 */
public final class PreMatchScreens {

    private PreMatchScreens() {
    }

    /** 单张编制图允许随包发送的最大字节数，超过则回退客户端本地加载。 */
    private static final int MAX_SELECTION_IMAGE_BYTES = 180_000;

    /** 整个编制选择包内图片数据的总预算，避免超过 Forge 网络包 1MB 上限。 */
    private static final int MAX_SELECTION_IMAGE_TOTAL_BYTES = 700_000;

    /**
     * 发送打开阵营选择界面包给指定玩家（同时发送当前队伍状态，含编制图片）。
     */
    public static void sendOpenFactionScreen(ServerPlayer player) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), new OpenFactionScreenPacket());
        // 同时发送当前队伍选择状态（含人数 + 编制图片），让中途加入者看到实时数据
        sendCurrentTeamSelectState(player);
    }

    /** 向单个玩家发送当前的队伍选择状态。 */
    public static void sendCurrentTeamSelectState(ServerPlayer player) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        GameStateManager gsm = GameStateManager.getInstance();
        int attack = 0, defend = 0;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            String t = Espetro.getPlayerTeam(p);
            if ("ATTACK".equals(t)) attack++;
            else if ("DEFEND".equals(t)) defend++;
        }
        // 获取编制图片
        ClassSelectManager csm = ClassSelectManager.getInstance();
        String atkImg = FactionDisplay.getFactionSelectionImage(csm.getFinalAttackClass());
        String defImg = FactionDisplay.getFactionSelectionImage(csm.getFinalDefendClass());
        long end = server.overworld().getGameTime() + 999999L;
        String myTeam = Espetro.getPlayerTeam(player);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new TeamSelectStatePacket(attack, defend, 0, end, false, myTeam, null, atkImg, defImg));
    }

    /**
     * 发送编制选择界面给指定玩家
     */
    public static void sendClassSelectScreen(ServerPlayer player, String team, boolean isCommander, int timeRemaining) {
        List<ClassSelectScreenPacket.FactionInfo> factionList = getFactionListForTeam(team);
        String opponentTeamName = FactionDisplay.teamDisplayName(FactionDisplay.oppositeTeam(team));
        String opponentFaction = FactionDisplay.getOpponentFactionDisplayName(team);
        ClassSelectScreenPacket packet = new ClassSelectScreenPacket(team, isCommander, factionList,
            timeRemaining, opponentTeamName, opponentFaction, -1,
            ClassSelectManager.getInstance().getPlayerFactionVote(player.getUUID(), team));
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * 根据队伍获取可选编制列表（服务端调用）
     * 攻守双方使用同一个随机编制池，但排除对方已确定的编制。
     */
    private static List<ClassSelectScreenPacket.FactionInfo> getFactionListForTeam(String team) {
        List<String> pool = ClassSelectManager.getInstance().getAvailableFactionPoolForTeam(team);
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        java.util.Map<String, Integer> voteCounts =
            ClassSelectManager.getInstance().getFactionVoteCounts(team);
        List<ClassSelectScreenPacket.FactionInfo> list = new ArrayList<>();
        int totalImageBytes = 0;

        for (String id : pool) {
            FactionDataLoader.FactionData faction = loader.getFaction(id);
            String name = faction != null ? faction.name : id;
            String selectionImage = faction != null ? faction.selectionImage : "";
            byte[] imageData = loadEsFactionsImage(selectionImage);
            if (imageData != null
                && (imageData.length > MAX_SELECTION_IMAGE_BYTES
                    || totalImageBytes + imageData.length > MAX_SELECTION_IMAGE_TOTAL_BYTES)) {
                // 图片太大就不随包发送；客户端会尝试从本地 EsFactions/ 或资源包加载。
                imageData = null;
            } else if (imageData != null) {
                totalImageBytes += imageData.length;
            }
            list.add(new ClassSelectScreenPacket.FactionInfo(
                id, name, selectionImage, voteCounts.getOrDefault(id, 0), imageData));
        }
        return list;
    }

    /**
     * 从服务端 EsFactions/ 目录加载编制图片字节。
     * 仅当 selectionImage 为简单文件名（不含冒号）且文件存在时返回数据。
     */
    private static byte[] loadEsFactionsImage(String selectionImage) {
        if (selectionImage == null || selectionImage.isBlank()) return null;
        // 含冒号的是 ResourceLocation 格式，从 jar/资源包加载，不需要发送字节
        if (selectionImage.contains(":")) return null;
        try {
            Path imagePath = FMLPaths.GAMEDIR.get().resolve("EsFactions")
                .resolve(selectionImage).normalize();
            Path esFactionsDir = FMLPaths.GAMEDIR.get().resolve("EsFactions").normalize();
            if (!imagePath.startsWith(esFactionsDir)) return null;
            if (!Files.isRegularFile(imagePath)) return null;
            if (Files.size(imagePath) > MAX_SELECTION_IMAGE_BYTES) return null;
            return Files.readAllBytes(imagePath);
        } catch (Exception e) {
            Espetro.LOGGER.debug("EsFactions 图片读取失败: {} ({})", selectionImage, e.toString());
            return null;
        }
    }

    /**
     * 广播打开阵营选择界面包给所有玩家
     */
    public static void broadcastOpenFactionScreen() {
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            OpenFactionScreenPacket packet = new OpenFactionScreenPacket();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    /**
     * 广播指挥官投票界面给指定队伍的玩家
     * @param team "DEFEND" 或 "ATTACK"
     * @param timeRemaining 剩余时间（秒）
     */
    public static void broadcastCommanderVoteScreenForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        sendCommanderVoteScreenForTeamView(server, team, timeRemaining, -1);
        sendCommanderVoteScreenForTeamView(server, FactionDisplay.oppositeTeam(team), 0, timeRemaining);
    }

    /**
     * 双方并行指挥官投票：给攻守两队各自发送「本方候选 + 统一倒计时」的投票界面。
     */
    public static void broadcastBothCommanderVoteScreens(int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        sendCommanderVoteScreenForTeamView(server, "ATTACK", timeRemaining, -1);
        sendCommanderVoteScreenForTeamView(server, "DEFEND", timeRemaining, -1);
    }

    private static void sendCommanderVoteScreenForTeamView(MinecraftServer server, String viewTeam,
                                                           int timeRemaining, int opponentTimeRemaining) {
        VoteManager voteManager = VoteManager.getInstance();

        // 收集该队伍玩家名
        Set<UUID> teamUuids = "ATTACK".equals(viewTeam) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        List<String> teamPlayers = new ArrayList<>();
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                teamPlayers.add(player.getName().getString());
            }
        }

        // 获取对手编制信息
        String opponentTeamName = FactionDisplay.teamDisplayName(FactionDisplay.oppositeTeam(viewTeam));
        String opponentFaction = FactionDisplay.getOpponentFactionDisplayName(viewTeam);

        CommanderVotePacket packet = new CommanderVotePacket(viewTeam, teamPlayers, timeRemaining,
            opponentTeamName, opponentFaction, opponentTimeRemaining);

        // 只发送给该队伍的玩家
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    /**
     * 发送指挥官投票界面给单个指定玩家（并行模式：本方候选 + 剩余倒计时）。
     */
    public static void sendCommanderVoteScreenToPlayer(ServerPlayer player, String team,
                                                       String[] candidateNames, int timeRemaining) {
        if (player == null || team == null) return;
        List<String> names = candidateNames == null
            ? List.of() : java.util.Arrays.asList(candidateNames);
        CommanderVotePacket packet = new CommanderVotePacket(team, names, timeRemaining,
            "", "", -1);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * 发送指挥官投票界面给单个指定玩家（用于中途加入者同步）
     * @param player 目标玩家
     * @param team 玩家所在队伍 "DEFEND" 或 "ATTACK"
     * @param timeRemaining 投票剩余时间（秒）
     */
    public static void sendCommanderVoteScreenToPlayer(ServerPlayer player, String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        VoteManager voteManager = VoteManager.getInstance();

        // 收集该队伍玩家名
        Set<UUID> teamUuids = "ATTACK".equals(team) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        List<String> teamPlayers = new ArrayList<>();
        for (UUID uuid : teamUuids) {
            ServerPlayer p = server.getPlayerList().getPlayer(uuid);
            if (p != null) {
                teamPlayers.add(p.getName().getString());
            }
        }

        // 获取对手编制信息
        String opponentTeamName = FactionDisplay.teamDisplayName(FactionDisplay.oppositeTeam(team));
        String opponentFaction = FactionDisplay.getOpponentFactionDisplayName(team);
        String activeTeam = voteManager.getCurrentVotingTeam();
        int ownTimeRemaining = team.equals(activeTeam) ? timeRemaining : 0;
        int opponentTimeRemaining = team.equals(activeTeam) ? -1 : timeRemaining;

        CommanderVotePacket packet = new CommanderVotePacket(team, teamPlayers, ownTimeRemaining,
            opponentTeamName, opponentFaction, opponentTimeRemaining);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * 只给指定队伍发送编制选择界面（投票后即时反馈，不给对方发）
     */
    public static void sendClassSelectScreenForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        sendClassSelectScreenForTeamView(server, team, timeRemaining, -1);
    }

    /**
     * 广播编制选择界面给指定队伍的玩家（含对方倒计时同步）
     * @param team "DEFEND" 或 "ATTACK"
     * @param timeRemaining 剩余时间（秒）
     */
    public static void broadcastClassSelectScreenForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        sendClassSelectScreenForTeamView(server, team, timeRemaining, -1);
        sendClassSelectScreenForTeamView(server, FactionDisplay.oppositeTeam(team), 0, timeRemaining);
    }

    /**
     * 每秒轻量同步编制倒计时（不附带 faction 列表，避免整页刷新感）。
     */
    public static void broadcastClassSelectTimerForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) return;

        VoteManager voteManager = VoteManager.getInstance();
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        sendClassSelectTimerForTeamView(server, team, timeRemaining, -1, voteManager, selectManager);
        sendClassSelectTimerForTeamView(server, FactionDisplay.oppositeTeam(team), 0, timeRemaining, voteManager, selectManager);
    }

    private static void sendClassSelectTimerForTeamView(MinecraftServer server, String viewTeam,
                                                        int timeRemaining, int opponentTimeRemaining,
                                                        VoteManager voteManager,
                                                        ClassSelectManager selectManager) {
        Set<UUID> teamUuids = "ATTACK".equals(viewTeam)
            ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) continue;
            boolean isCommander = voteManager.isCommanderOf(uuid, viewTeam);
            String selected = selectManager.getPlayerFactionVote(uuid, viewTeam);
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
                new ClassSelectTimerPacket(timeRemaining, opponentTimeRemaining, selected, isCommander));
        }
    }

    private static void sendClassSelectScreenForTeamView(MinecraftServer server, String viewTeam,
                                                         int timeRemaining, int opponentTimeRemaining) {
        VoteManager voteManager = VoteManager.getInstance();

        Set<UUID> teamUuids = "ATTACK".equals(viewTeam) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();

        // 对手编制信息
        String opponentTeamName = FactionDisplay.teamDisplayName(FactionDisplay.oppositeTeam(viewTeam));
        String opponentFaction = FactionDisplay.getOpponentFactionDisplayName(viewTeam);
        List<ClassSelectScreenPacket.FactionInfo> factionList = getFactionListForTeam(viewTeam);

        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) continue;

            boolean isCommander = voteManager.isCommanderOf(uuid, viewTeam);
            ClassSelectScreenPacket packet = new ClassSelectScreenPacket(viewTeam, isCommander, factionList,
                timeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining,
                ClassSelectManager.getInstance().getPlayerFactionVote(uuid, viewTeam));
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * 广播双方最终编制揭示界面给所有在线玩家。
     */
    public static void broadcastFactionRevealScreen(String attackFactionId, String defendFactionId, int durationSeconds) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        FactionRevealPacket packet = new FactionRevealPacket(
            FactionDisplay.getFactionDisplayName(attackFactionId),
            FactionDisplay.getFactionDisplayName(defendFactionId),
            FactionDisplay.getFactionSelectionImage(attackFactionId),
            FactionDisplay.getFactionSelectionImage(defendFactionId),
            durationSeconds
        );

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    // ==================== 队伍分配提示页 / 地图揭晓页 ====================

    /**
     * 广播队伍分配提示页：每个玩家收到自己所在队伍（5 秒后由服务端推进到指挥官投票）。
     */
    public static void broadcastTeamAssignShow(MinecraftServer server) {
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String team = org.espetro.team.ClassCountManager.getInstance()
                .getPlayerTeam(player.getUUID());
            if (team == null) team = org.espetro.team.GameStateManager
                .getTeamFromFactionStatic(
                    org.espetro.team.ClassCountManager.getInstance()
                        .getPlayerFaction(player.getUUID()));
            if (team == null) continue;
            sendTeamAssignShowTo(player, team, 5);
        }
    }

    /** 向单个玩家发送队伍分配提示页（剩余秒数用于页面倒计时展示）。 */
    public static void sendTeamAssignShowTo(ServerPlayer player, String team, int remainingSeconds) {
        if (player == null || player.connection == null || team == null) return;
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new TeamAssignPacket(team, Math.max(1, remainingSeconds)));
    }

    /**
     * 广播地图揭晓页（胜出地图 + 预览图）。
     */
    public static void broadcastMapRevealScreen(MinecraftServer server,
                                                 org.espetro.mapconfig.ActiveMapConfig winner,
                                                 int durationSeconds) {
        if (server == null || winner == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
                new MapRevealPacket(winner.displayName, winner.mapFolder,
                    Math.max(1, durationSeconds)));
        }
    }

    /** 向单个玩家发送地图揭晓页（中途加入同步）。 */
    public static void broadcastMapRevealScreenTo(ServerPlayer player,
                                                  org.espetro.mapconfig.ActiveMapConfig winner,
                                                  int durationSeconds) {
        if (player == null || player.connection == null || winner == null) return;
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new MapRevealPacket(winner.displayName, winner.mapFolder,
                Math.max(1, durationSeconds)));
    }

    /**
     * 广播职业选择界面给所有玩家
     * 根据玩家所在队伍的编制发送对应的职业列表
     */
    public static void broadcastClassSelectionScreen(String attackFactionId, String defendFactionId) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        FactionDataLoader loader = org.espetro.team.FactionDataProvider.getOrCreateLoader();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String playerTeam = Espetro.getPlayerTeam(player);
            if (playerTeam == null) continue;

            String factionId = "ATTACK".equals(playerTeam) ? attackFactionId : defendFactionId;
            if (factionId == null) continue;

            OpenClassSelectionPacket packet = new OpenClassSelectionPacket(factionId, loader);
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * 发送职业选择界面给指定玩家
     */
    public static void sendClassSelectionScreen(ServerPlayer player, String factionId) {
        FactionDataLoader loader = org.espetro.team.FactionDataProvider.getOrCreateLoader();
        OpenClassSelectionPacket packet = new OpenClassSelectionPacket(factionId, loader);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    // ===== multi-dimension helpers =====

    public static void broadcastMapVoteState(org.espetro.team.MapVoteManager mgr) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendMapVoteState(player, mgr);
        }
    }

    public static void sendMapVoteState(ServerPlayer player, org.espetro.team.MapVoteManager mgr) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), MapVoteStatePacket.from(mgr, player));
    }

    public static void sendOpenMapVoteScreen(ServerPlayer player) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), new OpenMapVoteScreenPacket());
    }

    public static void broadcastTeamSelectState(int attack, int defend, int remaining, long endGameTime, boolean active, String lockedTeam) {
        broadcastTeamSelectState(attack, defend, remaining, endGameTime, active, lockedTeam, null, null);
    }

    /** 带 faction 编制图片的版本。 */
    public static void broadcastTeamSelectState(int attack, int defend, int remaining,
                                                 long endGameTime, boolean active, String lockedTeam,
                                                 String attackFactionImage, String defendFactionImage) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String team = Espetro.getPlayerTeam(player);
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
                new TeamSelectStatePacket(attack, defend, remaining, endGameTime, active, team, lockedTeam,
                    attackFactionImage, defendFactionImage));
        }
    }
}
