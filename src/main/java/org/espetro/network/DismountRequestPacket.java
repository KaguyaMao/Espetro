package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.vehicle.DismountServer;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

public final class DismountRequestPacket {

    public static DismountRequestPacket read(FriendlyByteBuf buf) {
        return new DismountRequestPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnServer(ctx, sender -> {
            if (sender.getVehicle() == null
                || !SbwVehicleSeatResolver.isSupportedVehicle(sender.getVehicle())) {
                return;
            }
            DismountServer.markReady(sender);
            sender.stopRiding();
            DismountServer.consumeReady(sender);
        });
    }
}
