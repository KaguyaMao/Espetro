/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tactical;

import com.example.espoints.tactical.TacticalMarkerType;
import java.util.UUID;

public record TacticalMarker(UUID id, TacticalMarkerType type, double x, double y, double z, String team, UUID ownerId, String ownerName, long createdAtMillis, int ownerSquadId, boolean ownerCommander) {
    public static final int NO_SQUAD = -1;

    public TacticalMarker(UUID id, TacticalMarkerType type, double x, double z, String team, UUID ownerId, String ownerName, long createdAtMillis) {
        this(id, type, x, 0.0, z, team, ownerId, ownerName, createdAtMillis, -1, false);
    }

    public TacticalMarker(UUID id, TacticalMarkerType type, double x, double z, String team, UUID ownerId, String ownerName, long createdAtMillis, int ownerSquadId, boolean ownerCommander) {
        this(id, type, x, 0.0, z, team, ownerId, ownerName, createdAtMillis, ownerSquadId, ownerCommander);
    }
}

