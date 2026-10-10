/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.vehicle.DismountServer;
import org.espetro.vehicle.SbwVehicleSeatResolver;

public final class DismountRequestPacket {
    public static DismountRequestPacket read(FriendlyByteBuf buf) {
        return new DismountRequestPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sender = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (sender == null) {
                return;
            }
            if (sender.m_20202_() == null || !SbwVehicleSeatResolver.isSupportedVehicle(sender.m_20202_())) {
                return;
            }
            DismountServer.markReady(sender);
            sender.m_8127_();
            DismountServer.consumeReady(sender);
        });
        ctx.get().setPacketHandled(true);
    }
}

