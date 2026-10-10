/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.example.espoints.network;

import com.example.espoints.tile.TacticalMapTileService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record RequestTacticalMapTileMessage(long session, int level, int x, int y) {
    private static final Map<UUID, RequestWindow> REQUESTS = new ConcurrentHashMap<UUID, RequestWindow>();
    private static final int MAX_REQUESTS_PER_SECOND = 64;

    public static void encode(RequestTacticalMapTileMessage message, FriendlyByteBuf buf) {
        buf.m_130103_(message.session);
        buf.m_130130_(message.level);
        buf.m_130130_(message.x);
        buf.m_130130_(message.y);
    }

    public static RequestTacticalMapTileMessage decode(FriendlyByteBuf buf) {
        long session = buf.m_130258_();
        int level = buf.m_130242_();
        int x = buf.m_130242_();
        int y = buf.m_130242_();
        if (session <= 0L || level < 0 || level >= 16 || x < 0 || x >= 64 || y < 0 || y >= 64) {
            throw new IllegalArgumentException("Invalid tactical tile request");
        }
        return new RequestTacticalMapTileMessage(session, level, x, y);
    }

    public static void handle(RequestTacticalMapTileMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        if (sender != null && RequestTacticalMapTileMessage.allow(sender.m_20148_(), System.currentTimeMillis())) {
            TacticalMapTileService.get().enqueue(sender.m_20148_(), message.session, message.level, message.x, message.y);
        }
        context.setPacketHandled(true);
    }

    public static void clearPlayer(UUID playerId) {
        if (playerId != null) {
            REQUESTS.remove(playerId);
            TacticalMapTileService.get().removePlayer(playerId);
        }
    }

    public static void clearAll() {
        REQUESTS.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean allow(UUID playerId, long now) {
        RequestWindow window;
        RequestWindow requestWindow = window = REQUESTS.computeIfAbsent(playerId, ignored -> new RequestWindow());
        synchronized (requestWindow) {
            if (now < window.startedAt || now - window.startedAt >= 1000L) {
                window.startedAt = now;
                window.count = 0;
            }
            boolean bl = ++window.count <= 64;
            return bl;
        }
    }

    private static final class RequestWindow {
        private long startedAt;
        private int count;

        private RequestWindow() {
        }
    }
}

