/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.network.EquipZoneSyncPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.SpawnPointConfig;

public final class ClassEquipmentZones {
    public static final double RANGE = 6.0;

    private ClassEquipmentZones() {
    }

    public static boolean isPlayerNearOriginalSpawn(ServerPlayer player) {
        SpawnPointConfig.SpawnPoint spawn;
        if (player == null) {
            return false;
        }
        BlockPos playerPos = player.m_20183_();
        ClassCountManager counts = ClassCountManager.getInstance();
        String team = counts.getEffectivePlayerTeam(player.m_20148_());
        if (team != null && (spawn = SpawnPointConfig.getSpawnPoint(team)) != null) {
            BlockPos teamSpawn = new BlockPos((int)spawn.x, (int)spawn.y, (int)spawn.z);
            return playerPos.m_123314_(teamSpawn, 6.0);
        }
        return false;
    }

    public static List<EquipZoneSyncPacket.Zone> collectForPlayer(ServerPlayer player) {
        ArrayList<EquipZoneSyncPacket.Zone> zones = new ArrayList<EquipZoneSyncPacket.Zone>();
        if (player == null) {
            return zones;
        }
        String team = ClassCountManager.getInstance().getEffectivePlayerTeam(player.m_20148_());
        if (team == null) {
            return zones;
        }
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        if (spawn != null) {
            BlockPos teamSpawn = new BlockPos((int)spawn.x, (int)spawn.y, (int)spawn.z);
            zones.add(ClassEquipmentZones.zone("spawn", teamSpawn));
        }
        return zones;
    }

    private static EquipZoneSyncPacket.Zone zone(String type, BlockPos pos) {
        return new EquipZoneSyncPacket.Zone(type, (double)pos.m_123341_() + 0.5, pos.m_123342_(), (double)pos.m_123343_() + 0.5, 6.0);
    }
}

