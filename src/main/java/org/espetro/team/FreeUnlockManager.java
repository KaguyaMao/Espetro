package org.espetro.team;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.network.NetworkManager;

import javax.annotation.Nullable;

/**
 * 装备完全解锁模式（管理员指令开关）。
 *
 * <p>开启后，选职业不再受人数类限制：{@code teammates_need}、{@code unlock_min_squad}、
 * {@code unlock_per_n}（含名额用尽）、{@code team_count} 职业的小队上限、全队
 * {@code maxPlayers}、{@code max_per_squad}、{@code strict_count} 变体独立上限。</p>
 *
 * <p><b>保留</b>：位置限制、必须先加入小队、职业切换冷却、{@code leader_only}（仅队长）、
 * 编制归属校验、载具换职的弹药消耗、兵力值结算。</p>
 *
 * <p><b>仅本局有效</b>：对局结束（回城 / 强停 / 重置）自动关闭；服务器重启自然回到关闭。</p>
 */
public final class FreeUnlockManager {

    private static volatile boolean enabled;

    private FreeUnlockManager() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置开关。
     *
     * @param actor 操作者名字（用于广播与日志），控制台可为 null
     * @return 状态是否发生变化
     */
    public static boolean setEnabled(boolean value, @Nullable String actor) {
        if (enabled == value) return false;
        enabled = value;
        String who = actor == null || actor.isBlank() ? "控制台" : actor;
        if (value) {
            Espetro.broadcastToAll("§e[系统] 装备完全解锁模式 §a已开启§e："
                + "本局选择职业不再受人数限制。");
        } else {
            Espetro.broadcastToAll("§e[系统] 装备完全解锁模式 §c已关闭§e：恢复人数限制。");
        }
        Espetro.LOGGER.info("[FreeUnlock] {} 将装备完全解锁模式设为 {}（仅本局有效）", who, value);
        refreshClients();
        return true;
    }

    /** 本局结束：自动关闭（由 GameStateManager 的回城/强停/重置路径调用）。 */
    public static void onRoundEnded() {
        if (!enabled) return;
        enabled = false;
        Espetro.broadcastToAll("§e[系统] 本局结束，装备完全解锁模式已自动关闭。");
        Espetro.LOGGER.info("[FreeUnlock] 本局结束，装备完全解锁模式自动关闭");
        refreshClients();
    }

    /** 状态文案（/espetro freeunlock status）。 */
    public static String statusText() {
        return enabled
            ? "§a装备完全解锁模式：开启§7（本局有效，选职业不受人数限制）"
            : "§7装备完全解锁模式：关闭";
    }

    /**
     * 让在场玩家的界面立刻按新状态重算：重发部署界面（不打开）与 Radio 职业列表。
     * 否则要重开界面才会看到变化。
     */
    public static void refreshClients() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String team = Espetro.getPlayerTeam(player);
            if (team == null) continue;
            NetworkManager.resendDeployScreen(player);
            String factionId = ClassCountManager.getInstance().getPlayerFaction(player.getUUID());
            if (factionId != null && !factionId.isBlank()) {
                NetworkManager.sendVehicleClassSelect(player, factionId);
            }
        }
    }
}
