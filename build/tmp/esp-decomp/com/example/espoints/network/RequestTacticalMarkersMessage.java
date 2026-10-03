/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.example.espoints.network;

import com.example.espoints.network.RequestRateLimiter;
import com.example.espoints.tactical.TacticalMarkerManager;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class RequestTacticalMarkersMessage {
    public static void encode(RequestTacticalMarkersMessage message, FriendlyByteBuf buf) {
    }

    public static RequestTacticalMarkersMessage decode(FriendlyByteBuf buf) {
        return new RequestTacticalMarkersMessage();
    }

    public static void handle(RequestTacticalMarkersMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        context.enqueueWork(() -> {
            if (sender != null && RequestRateLimiter.allow(sender.m_20148_(), "marker_snapshot", System.currentTimeMillis(), 1000L)) {
                TacticalMarkerManager.sendTo(sender);
            }
        });
        context.setPacketHandled(true);
    }
}

