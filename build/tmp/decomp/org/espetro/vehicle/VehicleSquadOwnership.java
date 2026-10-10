/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.vehicle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class VehicleSquadOwnership {
    private static final String SQUAD_ID_KEY = "espetro_vehicle_squad_id";
    private static final String SQUAD_TEAM_KEY = "espetro_vehicle_squad_team";

    private VehicleSquadOwnership() {
    }

    public static int getSquadId(Entity vehicle) {
        CompoundTag data = vehicle.getPersistentData();
        if (data.m_128425_(SQUAD_ID_KEY, 3)) {
            return data.m_128451_(SQUAD_ID_KEY);
        }
        return -1;
    }

    public static String getSquadTeam(Entity vehicle) {
        CompoundTag data = vehicle.getPersistentData();
        if (data.m_128425_(SQUAD_TEAM_KEY, 8)) {
            return data.m_128461_(SQUAD_TEAM_KEY);
        }
        return null;
    }

    public static void setOwner(Entity vehicle, int squadId, String team) {
        vehicle.getPersistentData().m_128405_(SQUAD_ID_KEY, squadId);
        vehicle.getPersistentData().m_128359_(SQUAD_TEAM_KEY, team);
    }

    public static void clearOwner(Entity vehicle) {
        vehicle.getPersistentData().m_128473_(SQUAD_ID_KEY);
        vehicle.getPersistentData().m_128473_(SQUAD_TEAM_KEY);
    }

    public static boolean isOwned(Entity vehicle) {
        return VehicleSquadOwnership.getSquadId(vehicle) != -1;
    }
}

