/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.vehicle;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

final class InitialVehicleDeploymentLedger {
    private final Set<SlotKey> scheduled = new HashSet<SlotKey>();

    InitialVehicleDeploymentLedger() {
    }

    boolean claim(SlotKey key) {
        return key != null && this.scheduled.add(key);
    }

    int size() {
        return this.scheduled.size();
    }

    void clear() {
        this.scheduled.clear();
    }

    record SlotKey(String factionId, String vehicleType, int slotIndex, String team) {
        SlotKey {
            factionId = factionId == null ? "" : factionId;
            vehicleType = vehicleType == null ? "" : vehicleType;
            team = team == null ? "" : team.trim().toUpperCase(Locale.ROOT);
        }
    }
}

