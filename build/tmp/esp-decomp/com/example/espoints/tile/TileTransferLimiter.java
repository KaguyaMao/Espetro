/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TileTransferLimiter {
    private static final int BURST_SECONDS = 2;
    private static final long FIRST_GLANCE_BYTES = 0x180000L;
    private static final long FIRST_GLANCE_MILLIS = 3000L;
    private final Map<UUID, Bucket> players = new HashMap<UUID, Bucket>();
    private final Map<UUID, Long> firstGlanceUntil = new HashMap<UUID, Long>();
    private final Bucket global = new Bucket();

    public synchronized void grantFirstGlance(UUID playerId, long nowMillis) {
        if (playerId != null) {
            this.firstGlanceUntil.put(playerId, nowMillis + 3000L);
        }
    }

    public synchronized boolean allow(UUID playerId, int bytes, long nowMillis, long playerBytesPerSecond, long globalBytesPerSecond) {
        long globalCapacity;
        if (playerId == null || bytes <= 0 || playerBytesPerSecond <= 0L || globalBytesPerSecond <= 0L || bytes > 0x200000) {
            return false;
        }
        long playerCapacity = TileTransferLimiter.saturatedMultiply(playerBytesPerSecond, 2);
        Long glanceUntil = this.firstGlanceUntil.get(playerId);
        if (glanceUntil != null && nowMillis <= glanceUntil) {
            playerCapacity = Math.max(playerCapacity, 0x180000L);
        }
        if ((long)bytes > (globalCapacity = TileTransferLimiter.saturatedMultiply(globalBytesPerSecond, 2))) {
            return false;
        }
        Bucket player = this.players.computeIfAbsent(playerId, ignored -> new Bucket());
        player.refill(nowMillis, playerBytesPerSecond, playerCapacity);
        this.global.refill(nowMillis, globalBytesPerSecond, globalCapacity);
        if ((long)bytes <= playerCapacity) {
            if (player.tokens < (double)bytes || this.global.tokens < (double)bytes) {
                return false;
            }
            player.tokens -= (double)bytes;
            this.global.tokens -= (double)bytes;
            return true;
        }
        if (player.tokens + 0.5 < (double)playerCapacity || this.global.tokens < (double)bytes) {
            return false;
        }
        player.tokens = 0.0;
        this.global.tokens -= (double)bytes;
        return true;
    }

    public synchronized void clear() {
        this.players.clear();
        this.firstGlanceUntil.clear();
        this.global.reset();
    }

    public synchronized void removePlayer(UUID playerId) {
        if (playerId != null) {
            this.players.remove(playerId);
            this.firstGlanceUntil.remove(playerId);
        }
    }

    private static long saturatedMultiply(long value, int multiplier) {
        return value > Long.MAX_VALUE / (long)multiplier ? Long.MAX_VALUE : value * (long)multiplier;
    }

    private static final class Bucket {
        private long lastRefill;
        private double tokens;

        private Bucket() {
        }

        private void refill(long now, long rate, long capacity) {
            if (this.lastRefill == 0L || now < this.lastRefill) {
                this.lastRefill = now;
                this.tokens = capacity;
                return;
            }
            long elapsed = now - this.lastRefill;
            this.lastRefill = now;
            this.tokens = Math.min((double)capacity, this.tokens + (double)(elapsed * rate) / 1000.0);
        }

        private void reset() {
            this.lastRefill = 0L;
            this.tokens = 0.0;
        }
    }
}

