/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tactical;

import com.example.espoints.client.PingWheelMarkerBridge;
import com.example.espoints.tactical.TacticalMarker;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClientTacticalMarkerState {
    private static List<TacticalMarker> markers = List.of();

    private ClientTacticalMarkerState() {
    }

    public static void setMarkers(List<TacticalMarker> next) {
        markers = next == null ? List.of() : List.copyOf(next);
        PingWheelMarkerBridge.replaceSnapshot(markers);
    }

    public static void applyDelta(boolean clear, List<UUID> removals, List<TacticalMarker> additions) {
        ArrayList<TacticalMarker> next;
        ArrayList<TacticalMarker> arrayList = next = clear ? new ArrayList<TacticalMarker>() : new ArrayList<TacticalMarker>(markers);
        if (clear) {
            PingWheelMarkerBridge.clear();
        }
        if (removals != null && !removals.isEmpty()) {
            for (UUID id : removals) {
                next.removeIf(marker -> marker.id().equals(id));
                PingWheelMarkerBridge.remove(id);
            }
        }
        if (additions != null) {
            for (TacticalMarker marker2 : additions) {
                next.removeIf(existing -> existing.id().equals(marker2.id()));
                next.add(marker2);
                PingWheelMarkerBridge.add(marker2, true);
            }
        }
        markers = List.copyOf(next);
    }

    public static List<TacticalMarker> getMarkers() {
        return markers;
    }

    public static void clear() {
        markers = List.of();
        PingWheelMarkerBridge.clear();
    }
}

