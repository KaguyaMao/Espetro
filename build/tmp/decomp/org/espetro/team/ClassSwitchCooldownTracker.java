/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class ClassSwitchCooldownTracker {
    private final Map<UUID, Long> readyAtMillis = new HashMap<UUID, Long>();

    ClassSwitchCooldownTracker() {
    }

    int getRemainingSeconds(UUID playerId, long nowMillis) {
        Long readyAt = this.readyAtMillis.get(playerId);
        if (readyAt == null) {
            return 0;
        }
        long remainingMillis = readyAt - nowMillis;
        if (remainingMillis <= 0L) {
            this.readyAtMillis.remove(playerId);
            return 0;
        }
        return (int)Math.min(Integer.MAX_VALUE, (remainingMillis + 999L) / 1000L);
    }

    void start(UUID playerId, int cooldownSeconds, long nowMillis) {
        if (cooldownSeconds <= 0) {
            this.readyAtMillis.remove(playerId);
            return;
        }
        this.readyAtMillis.put(playerId, nowMillis + (long)cooldownSeconds * 1000L);
    }

    void clearAll() {
        this.readyAtMillis.clear();
    }
}

