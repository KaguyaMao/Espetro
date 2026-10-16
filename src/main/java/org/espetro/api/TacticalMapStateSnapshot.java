package org.espetro.api;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Immutable, level-reference-free tactical state. The revision changes only
 * when content changes (or a new battlefield session starts).
 */
public record TacticalMapStateSnapshot(
    long revision,
    long battlefieldSession,
    List<EspetroAPI.FobSnapshot> structures,
    List<RallySnapshot> rallies,
    List<TeamBaseSnapshot> teamBases,
    List<PlayerDeployPointSnapshot> playerDeployPoints,
    List<VehicleSupplyStationSnapshot> vehicleSupplyStations,
    List<FixedWeaponSnapshot> fixedWeapons
) {
    /** 兼容新增固定武器分量之前的调用点（无固定武器）。 */
    public TacticalMapStateSnapshot(
        long revision,
        long battlefieldSession,
        List<EspetroAPI.FobSnapshot> structures,
        List<RallySnapshot> rallies,
        List<TeamBaseSnapshot> teamBases,
        List<PlayerDeployPointSnapshot> playerDeployPoints,
        List<VehicleSupplyStationSnapshot> vehicleSupplyStations
    ) {
        this(revision, battlefieldSession, structures, rallies, teamBases,
            playerDeployPoints, vehicleSupplyStations, List.of());
    }

    public TacticalMapStateSnapshot {
        structures = List.copyOf(structures);
        rallies = List.copyOf(rallies);
        teamBases = List.copyOf(teamBases);
        playerDeployPoints = List.copyOf(playerDeployPoints);
        vehicleSupplyStations = List.copyOf(vehicleSupplyStations);
        fixedWeapons = List.copyOf(fixedWeapons);
    }

    public record RallySnapshot(UUID id, String team, int squadId, String dimension,
                                int x, int y, int z, long nextWaveAtMillis) {
        public long nextWaveSeconds() {
            return Math.max(0L,
                (nextWaveAtMillis - System.currentTimeMillis() + 999L) / 1000L);
        }
    }

    public record TeamBaseSnapshot(String team, String name, String dimension,
                                   int x, int y, int z, float yaw) {
    }

    public record PlayerDeployPointSnapshot(UUID playerId, String dimension,
                                            int x, int y, int z) {
    }

    public record VehicleSupplyStationSnapshot(UUID id, String name, String team,
                                               String dimension, int x, int y, int z) {
    }

    /**
     * 已建成的固定武器（{@code fixed_weapon.enabled} 的工事）。
     * {@code weaponTypeId} = 工事定义 id；{@code mapIcon} = 编制 {@code map_icon}
     * 给的地图图标文件名，null/空表示交由客户端按 {@code weaponTypeId} 兜底。
     */
    public record FixedWeaponSnapshot(String id, String fortId, String team, String dimension,
                                      int x, int y, int z, String weaponTypeId,
                                      @Nullable String mapIcon) {
    }
}
