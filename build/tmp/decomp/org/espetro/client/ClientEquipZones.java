/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client;

import java.util.ArrayList;
import java.util.List;
import org.espetro.network.EquipZoneSyncPacket;

public final class ClientEquipZones {
    private static final List<EquipZoneSyncPacket.Zone> ZONES = new ArrayList<EquipZoneSyncPacket.Zone>();

    private ClientEquipZones() {
    }

    public static void setZones(List<EquipZoneSyncPacket.Zone> zones) {
        ZONES.clear();
        if (zones != null) {
            ZONES.addAll(zones);
        }
    }

    public static void clear() {
        ZONES.clear();
    }

    public static List<EquipZoneSyncPacket.Zone> getZones() {
        return List.copyOf(ZONES);
    }
}

