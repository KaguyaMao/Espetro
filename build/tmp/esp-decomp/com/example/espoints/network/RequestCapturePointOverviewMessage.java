/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.example.espoints.network;

import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.network.RequestRateLimiter;
import com.example.espoints.network.SyncCapturePointOverviewMessage;
import com.example.espoints.util.ModLogger;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class RequestCapturePointOverviewMessage {
    public static void encode(RequestCapturePointOverviewMessage msg, FriendlyByteBuf buf) {
    }

    public static RequestCapturePointOverviewMessage decode(FriendlyByteBuf buf) {
        return new RequestCapturePointOverviewMessage();
    }

    public static void handle(RequestCapturePointOverviewMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ((NetworkEvent.Context)ctx.get()).getSender();
            if (sender == null) {
                return;
            }
            if (!RequestRateLimiter.allow(sender.m_20148_(), "capture_overview", System.currentTimeMillis(), 1000L)) {
                return;
            }
            try {
                SyncCapturePointOverviewMessage.sendOpenToPlayer(sender, CapturePointManager.getInstance().getOverviewSerializablePoints());
            }
            catch (Exception e) {
                ModLogger.syncError("Failed to send capture point overview: " + e.getMessage());
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

