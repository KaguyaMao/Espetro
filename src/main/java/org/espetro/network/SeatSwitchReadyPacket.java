package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.vehicle.SeatSwitchServer;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

/** Client finished seat-switch channel; allow one SBW seat-change. */
public final class SeatSwitchReadyPacket {

    public static SeatSwitchReadyPacket read(FriendlyByteBuf buf) {
        return new SeatSwitchReadyPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnServer(ctx, SeatSwitchServer::markReady);
    }
}
