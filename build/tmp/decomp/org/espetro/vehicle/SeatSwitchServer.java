/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.vehicle.VehicleInteractionConfig;

public final class SeatSwitchServer {
    private static final Map<UUID, Long> READY_UNTIL_TICK = new HashMap<UUID, Long>();

    private SeatSwitchServer() {
    }

    public static void markReady(ServerPlayer player) {
        if (player == null) {
            return;
        }
        long now = player.m_284548_().m_46467_();
        READY_UNTIL_TICK.put(player.m_20148_(), now + 40L);
    }

    public static boolean isReady(ServerPlayer player) {
        if (player == null) {
            return VehicleInteractionConfig.seatSwitchDelayTicks() <= 0;
        }
        if (VehicleInteractionConfig.seatSwitchDelayTicks() <= 0) {
            return true;
        }
        Long until = READY_UNTIL_TICK.get(player.m_20148_());
        if (until == null) {
            return false;
        }
        long now = player.m_284548_().m_46467_();
        if (now > until) {
            READY_UNTIL_TICK.remove(player.m_20148_());
            return false;
        }
        return true;
    }

    public static void consumeReady(ServerPlayer player) {
        if (player != null) {
            READY_UNTIL_TICK.remove(player.m_20148_());
        }
    }

    public static void clearAll() {
        READY_UNTIL_TICK.clear();
    }
}

