/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import org.espetro.Espetro;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.VehSpawnSnapshot;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;

public class VehicleConfig {
    private static final Map<String, Map<String, VehicleTypeConfig>> VEHICLE_CONFIGS = new LinkedHashMap<String, Map<String, VehicleTypeConfig>>();

    public static void loadConfig(MinecraftServer server) {
        VEHICLE_CONFIGS.clear();
        int legacyVehicleTypes = 0;
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        loader.ensureLoaded(server.m_177941_());
        Map<String, Map<String, FactionDataLoader.VehicleData>> factionVehicles = loader.getAllFactionVehicles();
        for (Map.Entry<String, Map<String, FactionDataLoader.VehicleData>> entry : factionVehicles.entrySet()) {
            String factionId = entry.getKey();
            LinkedHashMap<String, VehicleTypeConfig> typeMap = new LinkedHashMap<String, VehicleTypeConfig>();
            for (Map.Entry<String, FactionDataLoader.VehicleData> vEntry : entry.getValue().entrySet()) {
                String vehicleType = vEntry.getKey();
                FactionDataLoader.VehicleData vd = vEntry.getValue();
                VehicleTypeConfig vtc = VehicleConfig.buildVehicleConfig(vehicleType, vd);
                if (vtc.legacyDefaultedToFightVehicle) {
                    ++legacyVehicleTypes;
                }
                typeMap.put(vehicleType, vtc);
            }
            VEHICLE_CONFIGS.put(factionId, typeMap);
        }
        Espetro.LOGGER.info("\u8f7d\u5177\u914d\u7f6e\u5df2\u52a0\u8f7d: {} \u4e2a\u7f16\u5236\u81ea\u5b9a\u4e49\u4e86\u8f7d\u5177", (Object)VEHICLE_CONFIGS.size());
        VehicleConfig.warnLegacyVehicleTypes(legacyVehicleTypes);
    }

    public static void applyActiveMap(ActiveMapConfig map) {
        VEHICLE_CONFIGS.clear();
        if (map == null || !map.usable) {
            return;
        }
        int legacyVehicleTypes = 0;
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        for (Map.Entry<String, Map<String, FactionDataLoader.VehicleData>> formation : loader.getAllFactionVehicles().entrySet()) {
            if (!loader.isCompatibleWithMap(formation.getKey(), map)) continue;
            LinkedHashMap<String, VehicleTypeConfig> typeMap = new LinkedHashMap<String, VehicleTypeConfig>();
            for (Map.Entry<String, FactionDataLoader.VehicleData> vehicle : formation.getValue().entrySet()) {
                String type = vehicle.getKey();
                FactionDataLoader.VehicleData data = vehicle.getValue();
                List<VehSpawnSnapshot.SpawnPoint> points = map.vehSpawn.spawnPointsByType.get(type);
                if (points == null || data.entities == null || data.entities.size() > points.size()) continue;
                int perMax = Math.max(1, data.perMaxCount);
                int respawn = data.respawnMinutes > 0 ? data.respawnMinutes : 5;
                VehicleTypeConfig cfg = new VehicleTypeConfig(data.entities.size() * perMax, respawn);
                cfg.perMaxCount = perMax;
                cfg.displayName = VehicleConfig.firstNonBlank(data.displayName, type);
                cfg.troopValue = Math.max(0, data.troopValue);
                cfg.nbt = VehicleConfig.firstNonBlank(data.nbt, null);
                VehicleConfig.applySupplyProfile(cfg, data.supplyVeh, data.fightVeh, data.capacity);
                if (cfg.legacyDefaultedToFightVehicle) {
                    ++legacyVehicleTypes;
                }
                if (data.initialDeployDelay != null) {
                    cfg.initialDeployDelayAttackSeconds = Math.max(0, data.initialDeployDelay.attack);
                    cfg.initialDeployDelayDefendSeconds = Math.max(0, data.initialDeployDelay.defend);
                }
                if (data.entityTags != null) {
                    for (String tag : data.entityTags) {
                        if (tag == null || tag.isBlank()) continue;
                        cfg.entityTags.add(tag);
                    }
                }
                for (int i = 0; i < data.entities.size(); ++i) {
                    VehicleSlotConfig slot = new VehicleSlotConfig();
                    slot.index = i;
                    slot.entityTypeStr = data.entities.get(i);
                    slot.attack = VehicleConfig.fromPose(points.get(i).attack());
                    slot.defend = VehicleConfig.fromPose(points.get(i).defend());
                    slot.nbt = cfg.nbt;
                    cfg.slots.add(slot);
                }
                if (!cfg.slots.isEmpty()) {
                    cfg.entityTypeStr = cfg.slots.get((int)0).entityTypeStr;
                    cfg.deployment.attack = cfg.slots.get((int)0).attack;
                    cfg.deployment.defend = cfg.slots.get((int)0).defend;
                }
                typeMap.put(type, cfg);
            }
            VEHICLE_CONFIGS.put(formation.getKey(), typeMap);
        }
        Espetro.LOGGER.info("\u6d3b\u52a8\u5730\u56fe\u8f7d\u5177\u914d\u7f6e\u5df2\u5efa\u7acb: {} \u4e2a\u517c\u5bb9\u7f16\u5236", (Object)VEHICLE_CONFIGS.size());
        VehicleConfig.warnLegacyVehicleTypes(legacyVehicleTypes);
    }

    private static DeploymentPointConfig fromPose(VehSpawnSnapshot.Pose pose) {
        DeploymentPointConfig point = new DeploymentPointConfig();
        point.position = new int[]{(int)Math.floor(pose.x()), (int)Math.floor(pose.y()), (int)Math.floor(pose.z())};
        point.yaw = pose.yaw();
        return point;
    }

    private static VehicleTypeConfig buildVehicleConfig(String vehicleType, FactionDataLoader.VehicleData vd) {
        int max = vd.max > 0 ? vd.max : 1;
        int respawn = vd.respawnMinutes > 0 ? vd.respawnMinutes : 5;
        VehicleTypeConfig cfg = new VehicleTypeConfig(max, respawn);
        cfg.perMaxCount = Math.max(1, vd.perMaxCount);
        cfg.entityTypeStr = vd.entityTypeStr;
        cfg.displayName = VehicleConfig.firstNonBlank(vd.displayName, vehicleType);
        cfg.troopValue = Math.max(0, vd.troopValue);
        cfg.vehicleCrewSeats = vd.vehicleCrewSeats;
        cfg.nbt = VehicleConfig.firstNonBlank(vd.nbt, null);
        VehicleConfig.applySupplyProfile(cfg, vd.supplyVeh, vd.fightVeh, vd.capacity);
        if (vd.initialDeployDelay != null) {
            cfg.initialDeployDelayAttackSeconds = Math.max(0, vd.initialDeployDelay.attack);
            cfg.initialDeployDelayDefendSeconds = Math.max(0, vd.initialDeployDelay.defend);
        }
        if (vd.entityTags != null) {
            for (String tag : vd.entityTags) {
                if (tag == null || tag.isBlank()) continue;
                cfg.entityTags.add(tag);
            }
        }
        cfg.deployment = VehicleConfig.buildDeploymentConfig(vehicleType, vd);
        if (vd.entities != null && !vd.entities.isEmpty()) {
            cfg.max = vd.entities.size() * cfg.perMaxCount;
        }
        if (cfg.entityTypeStr == null || cfg.entityTypeStr.isBlank()) {
            Espetro.LOGGER.warn("\u8f7d\u5177 {} \u672a\u914d\u7f6e entity_type\uff1b\u8bf7\u5728\u5bf9\u5e94\u7f16\u5236 JSON \u7684 vehicles \u8282\u4e2d\u914d\u7f6e", (Object)vehicleType);
        }
        return cfg;
    }

    static void applySupplyProfile(VehicleTypeConfig cfg, @Nullable Boolean supplyVeh, @Nullable Boolean fightVeh, @Nullable Integer capacity) {
        boolean legacy = supplyVeh == null && fightVeh == null;
        cfg.supplyVeh = Boolean.TRUE.equals(supplyVeh);
        cfg.fightVeh = Boolean.TRUE.equals(fightVeh) || legacy;
        cfg.legacyDefaultedToFightVehicle = legacy;
        cfg.supplyCapacity = capacity != null && capacity > 0 ? capacity : (cfg.supplyVeh ? 3000 : (cfg.fightVeh ? 500 : 300));
    }

    private static void warnLegacyVehicleTypes(int count) {
        if (count <= 0) {
            return;
        }
        Espetro.LOGGER.warn("\u68c0\u6d4b\u5230 {} \u4e2a\u672a\u58f0\u660e fightveh/supplyveh \u7684\u65e7\u8f7d\u5177\u7c7b\u578b\uff0c\u5df2\u6309 fightveh=true \u517c\u5bb9\uff1b\u8865\u7ed9\u8f7d\u5177\u8bf7\u663e\u5f0f\u914d\u7f6e supplyveh=true", (Object)count);
    }

    private static DeploymentConfig buildDeploymentConfig(String vehicleType, FactionDataLoader.VehicleData vd) {
        DeploymentConfig cfg = new DeploymentConfig();
        FactionDataLoader.VehicleDeploymentData raw = vd.deployment;
        if (raw != null) {
            cfg.attack = VehicleConfig.buildDeploymentPoint(raw.attack);
            cfg.defend = VehicleConfig.buildDeploymentPoint(raw.defend);
        }
        if (cfg.attack == null) {
            Espetro.LOGGER.warn("\u8f7d\u5177 {} \u672a\u914d\u7f6e\u6709\u6548 deployment.ATTACK.position \u5750\u6807\uff1b\u5fc5\u987b\u5728\u7f16\u5236 JSON \u4e2d\u76f4\u63a5\u6307\u5b9a\u653b\u65b9\u5750\u6807", (Object)vehicleType);
        }
        if (cfg.defend == null) {
            Espetro.LOGGER.warn("\u8f7d\u5177 {} \u672a\u914d\u7f6e\u6709\u6548 deployment.DEFEND.position \u5750\u6807\uff1b\u5fc5\u987b\u5728\u7f16\u5236 JSON \u4e2d\u76f4\u63a5\u6307\u5b9a\u5b88\u65b9\u5750\u6807", (Object)vehicleType);
        }
        return cfg;
    }

    @Nullable
    private static DeploymentPointConfig buildDeploymentPoint(@Nullable FactionDataLoader.VehicleDeploymentPointData raw) {
        if (raw == null || !VehicleConfig.validVector(raw.position)) {
            return null;
        }
        DeploymentPointConfig point = new DeploymentPointConfig();
        point.position = raw.position;
        if (raw.yaw != null) {
            point.yaw = raw.yaw.floatValue();
        }
        return point;
    }

    private static boolean validVector(@Nullable int[] vector) {
        return vector != null && vector.length >= 3;
    }

    @Nullable
    private static String firstNonBlank(@Nullable String first, @Nullable String fallback) {
        return first != null && !first.isBlank() ? first : fallback;
    }

    public static Map<String, VehicleTypeConfig> getFactionVehicles(String factionId) {
        return VEHICLE_CONFIGS.getOrDefault(factionId, Collections.emptyMap());
    }

    @Nullable
    public static VehicleTypeConfig getVehicleConfig(String factionId, String vehicleType) {
        Map<String, VehicleTypeConfig> map = VEHICLE_CONFIGS.get(factionId);
        if (map == null) {
            return null;
        }
        return map.get(vehicleType);
    }

    public static Map<String, Map<String, VehicleTypeConfig>> getAllConfigs() {
        return new LinkedHashMap<String, Map<String, VehicleTypeConfig>>(VEHICLE_CONFIGS);
    }

    public static Set<String> getAllVehicleTypeKeys() {
        LinkedHashSet<String> keys = new LinkedHashSet<String>();
        for (Map<String, VehicleTypeConfig> typeMap : VEHICLE_CONFIGS.values()) {
            keys.addAll(typeMap.keySet());
        }
        return keys;
    }

    public static class VehicleTypeConfig {
        public int max;
        public int perMaxCount = 1;
        public int respawnMinutes;
        @Nullable
        public String entityTypeStr;
        @Nullable
        public String displayName;
        public int troopValue;
        @Nullable
        public Integer vehicleCrewSeats;
        public Set<String> entityTags = new LinkedHashSet<String>();
        public DeploymentConfig deployment = new DeploymentConfig();
        public List<VehicleSlotConfig> slots = new ArrayList<VehicleSlotConfig>();
        @Nullable
        public String nbt;
        public boolean supplyVeh;
        public boolean fightVeh;
        public int supplyCapacity;
        boolean legacyDefaultedToFightVehicle;
        public int initialDeployDelayAttackSeconds;
        public int initialDeployDelayDefendSeconds;

        public boolean canCarryConstruction() {
            return this.supplyVeh;
        }

        public boolean canChangeClass() {
            return this.supplyVeh;
        }

        public int initialDeployDelaySeconds(String team) {
            if ("DEFEND".equalsIgnoreCase(team)) {
                return Math.max(0, this.initialDeployDelayDefendSeconds);
            }
            return Math.max(0, this.initialDeployDelayAttackSeconds);
        }

        public VehicleTypeConfig(int max, int respawnMinutes) {
            this.max = max;
            this.respawnMinutes = respawnMinutes;
        }

        public long respawnMillis() {
            return (long)this.respawnMinutes * 60000L;
        }

        @Nullable
        public EntityType<?> getEntityType() {
            if (this.entityTypeStr == null || this.entityTypeStr.isEmpty()) {
                return null;
            }
            ResourceLocation rl = ResourceLocation.m_135820_(this.entityTypeStr);
            if (rl == null) {
                return null;
            }
            if (!BuiltInRegistries.f_256780_.m_7804_(rl)) {
                return null;
            }
            return BuiltInRegistries.f_256780_.m_7745_(rl);
        }
    }

    public static class VehicleSlotConfig {
        public int index;
        public String entityTypeStr;
        public DeploymentPointConfig attack;
        public DeploymentPointConfig defend;
        @Nullable
        public String nbt;

        @Nullable
        public EntityType<?> getEntityType() {
            ResourceLocation rl = ResourceLocation.m_135820_(this.entityTypeStr);
            return rl != null && BuiltInRegistries.f_256780_.m_7804_(rl) ? BuiltInRegistries.f_256780_.m_7745_(rl) : null;
        }

        @Nullable
        public DeploymentPointConfig forTeam(String team) {
            return "ATTACK".equalsIgnoreCase(team) ? this.attack : ("DEFEND".equalsIgnoreCase(team) ? this.defend : null);
        }
    }

    public static class DeploymentPointConfig {
        public int[] position;
        public float yaw = 0.0f;
    }

    public static class DeploymentConfig {
        @Nullable
        public DeploymentPointConfig attack;
        @Nullable
        public DeploymentPointConfig defend;

        @Nullable
        public DeploymentPointConfig forTeam(@Nullable String team) {
            if ("ATTACK".equalsIgnoreCase(team)) {
                return this.attack;
            }
            if ("DEFEND".equalsIgnoreCase(team)) {
                return this.defend;
            }
            return null;
        }
    }
}

