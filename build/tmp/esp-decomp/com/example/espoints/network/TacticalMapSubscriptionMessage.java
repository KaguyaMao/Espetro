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
import com.example.espoints.network.SyncTacticalMapBackgroundMessage;
import com.example.espoints.tile.TacticalMapTileService;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public final class TacticalMapSubscriptionMessage {
    private final boolean active;
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;
    private final int screenWidth;
    private final int screenHeight;

    public TacticalMapSubscriptionMessage(boolean active) {
        this(active, 0.0, 0.0, 1.0, 1.0, 256, 256);
    }

    public TacticalMapSubscriptionMessage(boolean active, double minX, double minY, double maxX, double maxY, int screenWidth, int screenHeight) {
        this.active = active;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public static void encode(TacticalMapSubscriptionMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.active);
        if (!message.active) {
            return;
        }
        buffer.writeDouble(message.minX);
        buffer.writeDouble(message.minY);
        buffer.writeDouble(message.maxX);
        buffer.writeDouble(message.maxY);
        buffer.m_130130_(Math.max(1, message.screenWidth));
        buffer.m_130130_(Math.max(1, message.screenHeight));
    }

    public static TacticalMapSubscriptionMessage decode(FriendlyByteBuf buffer) {
        boolean active = buffer.readBoolean();
        if (!active) {
            return new TacticalMapSubscriptionMessage(false);
        }
        return new TacticalMapSubscriptionMessage(true, buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.m_130242_(), buffer.m_130242_());
    }

    public static void handle(TacticalMapSubscriptionMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        if (sender != null && RequestRateLimiter.allow(sender.m_20148_(), "map_subscription", System.currentTimeMillis(), 250L)) {
            CapturePointManager.getInstance().setTacticalMapSubscription(sender, message.active);
            if (message.active) {
                SyncTacticalMapBackgroundMessage.sendDescriptorOnly(sender);
                TacticalMapTileService.get().updatePlayerViewport(sender.m_20148_(), message.minX, message.minY, message.maxX, message.maxY, message.screenWidth, message.screenHeight);
                TacticalMapTileService.get().enqueueViewport(sender.m_20148_());
            } else {
                TacticalMapTileService.get().removePlayer(sender.m_20148_());
            }
        }
        context.setPacketHandled(true);
    }
}

