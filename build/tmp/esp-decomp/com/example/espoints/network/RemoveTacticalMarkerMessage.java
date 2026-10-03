/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.example.espoints.network;

import com.example.espoints.tactical.TacticalMarkerManager;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class RemoveTacticalMarkerMessage {
    private final UUID markerId;

    public RemoveTacticalMarkerMessage(UUID markerId) {
        this.markerId = markerId;
    }

    public static void encode(RemoveTacticalMarkerMessage message, FriendlyByteBuf buf) {
        buf.m_130077_(message.markerId);
    }

    public static RemoveTacticalMarkerMessage decode(FriendlyByteBuf buf) {
        return new RemoveTacticalMarkerMessage(buf.m_130259_());
    }

    public static void handle(RemoveTacticalMarkerMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        context.enqueueWork(() -> {
            if (sender != null) {
                TacticalMarkerManager.removeOwn(sender, message.markerId);
            }
        });
        context.setPacketHandled(true);
    }
}

