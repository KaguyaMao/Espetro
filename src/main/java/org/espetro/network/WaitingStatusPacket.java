package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

/**
 * 等待状态消息数据包
 * 发送"等待玩家集结中[X/20]"等消息给玩家
 */
public class WaitingStatusPacket {

    private String message;
    private boolean isActionBar;

    public WaitingStatusPacket(String message, boolean isActionBar) {
        this.message = message;
        this.isActionBar = isActionBar;
    }

    public WaitingStatusPacket() {
    }

    public static WaitingStatusPacket read(FriendlyByteBuf buf) {
        String message = buf.readUtf();
        boolean isActionBar = buf.readBoolean();
        return new WaitingStatusPacket(message, isActionBar);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(message);
        buf.writeBoolean(isActionBar);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnClient(ctx, "WaitingStatus",
            () -> () -> org.espetro.client.ClientPacketHandlers.handleWaitingStatus(message, isActionBar));
    }

    public String getMessage() {
        return message;
    }

    public boolean isActionBar() {
        return isActionBar;
    }
}
