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
 * 对局进行中的服务端广播：阶段、倒计时、兵力、等待提示、指挥官技能、
 * 战绩、治理、主城与回合结束。
 */
public final class MatchBroadcasts {

    private MatchBroadcasts() {
    }

    /** 工事编辑器选区同步（仅发给对应的管理员）。 */
    public static void sendFortificationWand(ServerPlayer player, FortificationWandPacket packet) {
        if (player == null || packet == null) return;
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * 要求指定玩家客户端立即关闭对局内模组界面（设为观察者/移出对局时调用）。
     * 主城菜单等基础界面保留。
     */
    public static void sendCloseModScreens(ServerPlayer player) {
        if (player == null) return;
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), new CloseModScreensPacket());
    }

    /**
     * 发送等待状态消息给指定玩家
     * @param player 目标玩家
     * @param message 消息内容
     * @param isActionBar 是否使用操作栏显示
     */
    public static void sendWaitingStatus(ServerPlayer player, String message, boolean isActionBar) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), new WaitingStatusPacket(message, isActionBar));
    }

    /**
     * 广播等待状态消息给所有已准备但等待中的玩家
     */
    public static void broadcastWaitingStatus(String message, boolean isActionBar) {
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            WaitingStatusPacket packet = new WaitingStatusPacket(message, isActionBar);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    /**
     * 广播游戏阶段到所有玩家
     */
    public static void broadcastGamePhase(GamePhase phase) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        GamePhaseSyncPacket packet = new GamePhaseSyncPacket(
            phase,
            org.espetro.team.GameStateManager.getInstance().getCurrentMapFolder(),
            BattlefieldContext.getObjectiveMode());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * 广播战局倒计时到所有在线玩家。
     */
    public static void broadcastBattleTimer(int remainingSeconds) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        BattleTimerPacket packet = new BattleTimerPacket(remainingSeconds);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * 广播兵力统计给所有玩家
     */
    public static void broadcastTroopCounts(int attack, int defend) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        TroopCountSyncPacket packet = new TroopCountSyncPacket(attack, defend);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    /**
     * 发送指挥官技能同步包给指定玩家（S→C）
     */
    public static void sendCommanderSkillSync(ServerPlayer player) {
        boolean isCommander = org.espetro.team.VoteManager.getInstance().isCommander(player.getUUID());
        org.espetro.team.CommanderSkillManager skills =
            org.espetro.team.CommanderSkillManager.getInstance();
        // 仅同步该玩家 usableBy 允许的技能；冷却仍按个人 UUID
        java.util.Map<String, Integer> cooldowns = skills.getCooldownData(player.getUUID());
        java.util.List<org.espetro.team.CommanderSkillManager.SkillView> views =
            skills.getSkillViewsFor(player);
        // isCommander 字段：有可用技能或是指挥官时客户端显示技能入口
        boolean showSkillsEntry = isCommander || !views.isEmpty();
        CommanderSkillSyncPacket packet = new CommanderSkillSyncPacket(showSkillsEntry, cooldowns, views);
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void broadcastMatchStats(org.espetro.stats.PlayerMatchStatsManager mgr) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        MatchStatsSyncPacket packet = MatchStatsSyncPacket.from(mgr);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    public static void broadcastGovernanceState(org.espetro.governance.CommanderGovernanceManager mgr) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
                GovernanceStatePacket.from(mgr, player.getUUID()));
        }
    }

    public static void sendOpenHubScreen(ServerPlayer player, int online, String status) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), new OpenHubScreenPacket(online, status));
    }

    public static void broadcastRoundEnd(String winner, int seconds,
                                          String winnerShowName, String loserShowName,
                                          int attackTickets, int defendTickets,
                                          int resultLevel, boolean attackerTimedOut) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        RoundEndPacket packet = new RoundEndPacket(winner, seconds,
            winnerShowName, loserShowName, attackTickets, defendTickets,
            resultLevel, attackerTimedOut);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }
}
