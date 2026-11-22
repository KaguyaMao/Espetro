package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

/** S→C force open map vote screen. */
public class OpenMapVoteScreenPacket {

    public OpenMapVoteScreenPacket() {
    }

    public static OpenMapVoteScreenPacket read(FriendlyByteBuf buf) {
        return new OpenMapVoteScreenPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnClient(ctx, "OpenMapVoteScreen",
            () -> () -> org.espetro.client.ClientPacketHandlers.handleOpenMapVoteScreen());
    }
}
