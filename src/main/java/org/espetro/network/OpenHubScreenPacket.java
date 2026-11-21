package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** S→C open hub / lobby screen. */
public class OpenHubScreenPacket {

    public final int onlineCount;
    public final String statusMessage;

    public OpenHubScreenPacket(int onlineCount, String statusMessage) {
        this.onlineCount = onlineCount;
        this.statusMessage = statusMessage == null ? "" : statusMessage;
    }

    public static OpenHubScreenPacket read(FriendlyByteBuf buf) {
        return new OpenHubScreenPacket(buf.readVarInt(), buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(onlineCount);
        buf.writeUtf(statusMessage);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketContexts.handleOnClient(ctx, "handleOpenHubScreen", OpenHubScreenPacket.class, this);
    }
}
