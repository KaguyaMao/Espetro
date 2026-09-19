package org.espetro.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;

/**
 * 服务端碰撞体积（F3+B）显示策略。
 * <p>管理员通过 {@code /espetro hitbox on|off} 切换；策略对所有在线玩家即时生效，
 * 新加入玩家自动同步。</p>
 */
public final class HitboxPolicyController {

    private static boolean renderHitBoxesEnabled = true;

    private HitboxPolicyController() {
    }

    public static boolean isRenderHitBoxesEnabled() {
        return renderHitBoxesEnabled;
    }

    public static void setRenderHitBoxesOnServer(boolean enabled) {
        renderHitBoxesEnabled = enabled;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NetworkManager.NET.send(
                PacketDistributor.PLAYER.with(() -> player),
                new HitboxPolicyPacket(enabled));
        }
    }

    /** 玩家加入时同步当前策略。 */
    public static void syncToPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }
        NetworkManager.NET.send(
            PacketDistributor.PLAYER.with(() -> player),
            new HitboxPolicyPacket(renderHitBoxesEnabled));
    }
}
