package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

/**
 * 服务端→客户端：命中体积（碰撞箱）显示策略。
 * {@code renderHitBoxes} 为 false 时，客户端强制关闭 F3+B 的碰撞体积显示。
 */
public final class HitboxPolicyPacket {

    private final boolean renderHitBoxes;

    public HitboxPolicyPacket(boolean renderHitBoxes) {
        this.renderHitBoxes = renderHitBoxes;
    }

    public static HitboxPolicyPacket read(FriendlyByteBuf buf) {
        return new HitboxPolicyPacket(buf.readBoolean());
    }

    public boolean getRenderHitBoxes() {
        return renderHitBoxes;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(renderHitBoxes);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnClient(ctx, "HitboxPolicy",
            () -> () -> org.espetro.client.ClientPacketHandlers.handleHitboxPolicy(this));
    }
}
