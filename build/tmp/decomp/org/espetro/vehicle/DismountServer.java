/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.vehicle.VehicleInteractionConfig;

public final class DismountServer {
    private static final Map<UUID, Long> READY_UNTIL = new HashMap<UUID, Long>();

    private DismountServer() {
    }

    public static void markReady(ServerPlayer player) {
        if (player == null) {
            return;
        }
        READY_UNTIL.put(player.m_20148_(), player.m_284548_().m_46467_() + 40L);
    }

    public static boolean consumeReady(ServerPlayer player) {
        if (player == null) {
            return VehicleInteractionConfig.dismountDelayTicks() <= 0;
        }
        if (VehicleInteractionConfig.dismountDelayTicks() <= 0) {
            return true;
        }
        Long until = READY_UNTIL.remove(player.m_20148_());
        if (until == null) {
            return false;
        }
        return player.m_284548_().m_46467_() <= until;
    }

    public static void clearAll() {
        READY_UNTIL.clear();
    }
}

