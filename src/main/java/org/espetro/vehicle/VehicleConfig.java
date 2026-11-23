package org.espetro.vehicle;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import org.espetro.Espetro;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.VehSpawnSnapshot;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 载具配置加载器
 * 完全从各编制 JSON 的 vehicles 节加载可部署载具。
 */
public class VehicleConfig {
    // factionId -> (vehicleType -> VehicleTypeConfig)
    private static final Map<String, Map<String, VehicleTypeConfig>> VEHICLE_CONFIGS = new LinkedHashMap<>();

    /**
     * 载具类型配置
     */
    public static class VehicleTypeConfig {
        public int max;
        public int perMaxCount = 1;
        public int respawnMinutes;
        /** 实体类型注册名，如 "minecraft:minecart" 或任意模组实体ID。 */
        @Nullable
        public String entityTypeStr;
        /** 显示名，含颜色代码，如 "§6运输卡车" */
        @Nullable
        public String displayName;
        public int troopValue;
        /** null = legacy SBW type defaults; 0 = no restricted seats. */
        @Nullable
        public Integer vehicleCrewSeats;
        public Set<String> entityTags = new LinkedHashSet<>();
        public DeploymentConfig deployment = new DeploymentConfig();
        /** 抢滩前哨出生点（按下标与实体槽位对应；为空表示该类型没有前哨点）。 */
        public List<DeploymentPointConfig> outpostAttack = new ArrayList<>();
        public List<DeploymentPointConfig> outpostDefend = new ArrayList<>();
        /** 前哨点在「第几批据点全部被占领」后启用。 */
        public int outpostActivateAfterBatch = 1;

        @Nullable
        public DeploymentPointConfig outpostForTeam(String team, int slotIndex) {
            List<DeploymentPointConfig> list = "ATTACK".equalsIgnoreCase(team)
                ? outpostAttack : outpostDefend;
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.get(Math.floorMod(slotIndex, list.size()));
        }

        public boolean hasOutpostPoints(String team) {
            List<DeploymentPointConfig> list = "ATTACK".equalsIgnoreCase(team)
                ? outpostAttack : outpostDefend;
            return list != null && !list.isEmpty();
        }
        /** Ordered entity slots; index matches the map's ordered spawn points. */
        public List<VehicleSlotConfig> slots = new ArrayList<>();
        /** Type-level SNBT applied to every slot unless a slot overrides it. */
        @Nullable
        public String nbt;
        /**
         * 战术地图图标文件名（如 {@code "map_trackedheavyifv.png"}），来自编制 JSON 的
         * {@code vehicles.<type>.map_icon}；null/空 = 沿用 EsPoints 的车型关键字兜底图标。
         * 只是同步给客户端的展示数据，不参与服务端逻辑。
         */
        @Nullable
        public String mapIcon;
        /** 部署时写入载具集装箱的物品（"<itemId>[<snbt>] <count>"），slot 未单独指定时使用。 */
        public List<String> items = new ArrayList<>();
        /** 未显式配置 items 时，是否套用 data/<ns>/supply_station/ 的补给站弹药配置（默认开）。 */
        public boolean supplyLoadout = true;
        /** 补给载具：可装载弹药和建材 */
        public boolean supplyVeh;
        /** 步兵战斗载具：仅携带弹药，不携带建材 */
        public boolean fightVeh;
        /** 载具补给总容量 */
        public int supplyCapacity;
        /** 旧编制未声明载具补给类型时，是否按战斗载具兼容。 */
        boolean legacyDefaultedToFightVehicle;
        /**
         * 编制 JSON 是否显式声明 {@code fightveh: true}（步兵战斗载具）。
         * 只有这类载具允许在载具内更换完整职业列表；补给载具、显式双 false 的载具
         * 以及未声明标签的旧载具都只能换成驾驶员/载具队长，
         * 见 {@link VehicleClassChangePolicy#allowsFullClassChange}。
         */
        public boolean explicitFightVehicle;
        /** 作为攻方时首次可部署前等待秒数（开战起算） */
        public int initialDeployDelayAttackSeconds;
        /** 作为守方时首次可部署前等待秒数（开战起算） */
        public int initialDeployDelayDefendSeconds;
        /** 是否可装载建材 */
        public boolean canCarryConstruction() { return supplyVeh; }
        /**
         * 战斗载具与补给载具都可作为移动换装点，换装消耗其携带的弹药。
         * 可用职业列表另由 {@link VehicleClassChangePolicy} 按载具类别收敛。
         */
        public boolean canChangeClass() { return supplyVeh || fightVeh; }

        public int initialDeployDelaySeconds(String team) {
            if ("DEFEND".equalsIgnoreCase(team)) {
                return Math.max(0, initialDeployDelayDefendSeconds);
            }
            return Math.max(0, initialDeployDelayAttackSeconds);
        }

        public VehicleTypeConfig(int max, int respawnMinutes) {
            this.max = max;
            this.respawnMinutes = respawnMinutes;
        }

        /** 刷新时间（毫秒） */
        public long respawnMillis() {
            return respawnMinutes * 60_000L;
        }

        /**
         * 从注册名解析 EntityType，失败返回 null
         */
        @Nullable
        public EntityType<?> getEntityType() {
            if (entityTypeStr == null || entityTypeStr.isEmpty()) return null;
            ResourceLocation rl = ResourceLocation.tryParse(entityTypeStr);
            if (rl == null) return null;
            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) return null;
            return BuiltInRegistries.ENTITY_TYPE.get(rl);
        }
    }

    public static class VehicleSlotConfig {
        public int index;
        public String entityTypeStr;
        public DeploymentPointConfig attack;
        public DeploymentPointConfig defend;
        /** 该槽载具的独立刷新时间（分钟）。 */
        public int respawnMinutes = 5;
        /** 出生时套用的 SBW 皮肤 id；null = 模型默认外观。 */
        @Nullable
        public String skinId;
        /** 该槽载具作为攻方时的首次部署延迟（秒）。 */
        public int initialDeployDelayAttackSeconds;
        /** 该槽载具作为守方时的首次部署延迟（秒）。 */
        public int initialDeployDelayDefendSeconds;
        /** 该槽载具的显示名（注册表提供，回落到类型级）。 */
        @Nullable
        public String displayName;
        /** 该槽载具的地图图标（注册表提供，回落到类型级）。 */
        @Nullable
        public String mapIcon;
        /** 该槽载具被摧毁时扣除的兵力。 */
        public int troopValue;
        /**
         * 该槽载具要求的载具组员座位数（从 0 号座位起算）。
         * null = 该槽未单独声明，回落到类型级配置。
         */
        @Nullable
        public Integer vehicleCrewSeats;
        public boolean supplyVeh;
        public boolean fightVeh;
        /** Optional SNBT merged onto the entity at deploy time (e.g. Energy). */
        @Nullable
        public String nbt;
        /** 部署时写入载具集装箱的物品（"<itemId>[<snbt>] <count>"）。 */
        public List<String> items = new ArrayList<>();

        public long respawnMillis() {
            return Math.max(1, respawnMinutes) * 60_000L;
        }

        public int initialDeployDelaySeconds(String team) {
            if ("DEFEND".equalsIgnoreCase(team)) {
                return Math.max(0, initialDeployDelayDefendSeconds);
            }
            return Math.max(0, initialDeployDelayAttackSeconds);
        }

        @Nullable
        public EntityType<?> getEntityType() {
            ResourceLocation rl = ResourceLocation.tryParse(entityTypeStr);
            return rl != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl)
                ? BuiltInRegistries.ENTITY_TYPE.get(rl) : null;
        }

        @Nullable
        public DeploymentPointConfig forTeam(String team) {
            return "ATTACK".equalsIgnoreCase(team) ? attack
                : "DEFEND".equalsIgnoreCase(team) ? defend : null;
        }
    }

    public static class DeploymentConfig {
        @Nullable
        public DeploymentPointConfig attack;
        @Nullable
        public DeploymentPointConfig defend;

        @Nullable
        public DeploymentPointConfig forTeam(@Nullable String team) {
            if ("ATTACK".equalsIgnoreCase(team)) return attack;
            if ("DEFEND".equalsIgnoreCase(team)) return defend;
            return null;
        }
    }

    public static class DeploymentPointConfig {
        public int[] position;
        public float yaw = 0f;
    }

    /**
     * 加载载具配置（完全从各编制 JSON 读取）
     */
    public static void loadConfig(MinecraftServer server) {
        VEHICLE_CONFIGS.clear();
        int legacyVehicleTypes = 0;

        // 确保编制数据已加载（含 vehicles 节）
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        loader.ensureLoaded(server.getResourceManager());

        Map<String, Map<String, FactionDataLoader.VehicleData>> factionVehicles =
            loader.getAllFactionVehicles(FactionDataLoader.TIER_MID);

        for (Map.Entry<String, Map<String, FactionDataLoader.VehicleData>> entry : factionVehicles.entrySet()) {
            String factionId = entry.getKey();
            Map<String, VehicleTypeConfig> typeMap = new LinkedHashMap<>();

            for (Map.Entry<String, FactionDataLoader.VehicleData> vEntry : entry.getValue().entrySet()) {
                String vehicleType = vEntry.getKey();
                FactionDataLoader.VehicleData vd = vEntry.getValue();

                VehicleTypeConfig vtc = buildVehicleConfig(vehicleType, vd);
                if (vtc.legacyDefaultedToFightVehicle) legacyVehicleTypes++;

                typeMap.put(vehicleType, vtc);
            }

            VEHICLE_CONFIGS.put(factionId, typeMap);
        }

        Espetro.LOGGER.info("载具配置已加载: {} 个编制自定义了载具", VEHICLE_CONFIGS.size());
        warnLegacyVehicleTypes(legacyVehicleTypes);
    }

    /**
     * Build runtime vehicle slots from frozen EsFactions + the selected map's
     * VehSpawn.json. No disk access occurs here.
     */
    public static void applyActiveMap(ActiveMapConfig map) {
        VEHICLE_CONFIGS.clear();
        // 换图 / 开图 = 前哨点回到未解锁状态。
        org.espetro.vehicle.OutpostSpawnGate.reset();
        if (map == null || !map.usable) return;
        int legacyVehicleTypes = 0;
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        String tier = FactionDataLoader.tierForMapSize(map.mapSize());
        FactionDataLoader.setActiveTier(tier);
        String environment = map.environment();
        Espetro.LOGGER.info("[载具注册] 地图 {} 尺寸档位 {}，环境 {}",
            map.displayName, tier, environment);
        for (Map.Entry<String, Map<String, FactionDataLoader.VehicleData>> formation
            : loader.getAllFactionVehicles(tier).entrySet()) {
            if (!loader.isCompatibleWithMap(formation.getKey(), map)) continue;
            Map<String, VehicleTypeConfig> typeMap = new LinkedHashMap<>();
            for (Map.Entry<String, FactionDataLoader.VehicleData> vehicle : formation.getValue().entrySet()) {
                String type = vehicle.getKey();
                FactionDataLoader.VehicleData data = vehicle.getValue();
                List<VehSpawnSnapshot.SpawnPoint> points = map.vehSpawn.spawnPointsByType.get(type);
                if (points == null || data.entities == null || data.entities.isEmpty()
                    || data.entities.size() > points.size()) continue;

                boolean registryBacked = data.slotVehicles != null && !data.slotVehicles.isEmpty();
                int perMax = Math.max(1, data.perMaxCount);
                int respawn = registryBacked
                    ? Math.max(1, data.respawnMinutes)
                    : (data.respawnMinutes > 0
                        ? data.respawnMinutes : FactionDataLoader.VehicleData.DEFAULT_RESPAWN_MINUTES);
                VehicleTypeConfig cfg = new VehicleTypeConfig(data.entities.size() * perMax, respawn);
                cfg.perMaxCount = perMax;
                cfg.displayName = firstNonBlank(data.displayName, type);
                cfg.troopValue = Math.max(0, data.troopValue);
                cfg.mapIcon = firstNonBlank(data.mapIcon, null);
                cfg.nbt = firstNonBlank(data.nbt, null);
                addVehicleItems(cfg.items, data.items);
                if (data.supplyLoadout != null) cfg.supplyLoadout = data.supplyLoadout;
                // 必须复制组员座位配置，否则运行时所有载具都会被当作无需组员装备
                cfg.vehicleCrewSeats = data.vehicleCrewSeats;
                applySupplyProfile(cfg, data.supplyVeh, data.fightVeh, data.capacity);
                if (cfg.legacyDefaultedToFightVehicle) legacyVehicleTypes++;
                if (data.initialDeployDelay != null) {
                    cfg.initialDeployDelayAttackSeconds = Math.max(0, data.initialDeployDelay.attack);
                    cfg.initialDeployDelayDefendSeconds = Math.max(0, data.initialDeployDelay.defend);
                }
                if (data.entityTags != null) {
                    for (String tag : data.entityTags) {
                        if (tag != null && !tag.isBlank()) cfg.entityTags.add(tag);
                    }
                }
                for (int i = 0; i < data.entities.size(); i++) {
                    VehicleSlotConfig slot = new VehicleSlotConfig();
                    slot.index = i;
                    if (registryBacked) {
                        FactionDataLoader.SlotVehicle ref = data.slotVehicles.get(i);
                        EsVehicleRegistry.EsVehicleDef def = ref.def();
                        slot.entityTypeStr = def.entity;
                        slot.respawnMinutes = Math.max(1, ref.effectiveRespawnMinutes());
                        slot.skinId = def.skinFor(environment);
                        slot.initialDeployDelayAttackSeconds = ref.delayAttack();
                        slot.initialDeployDelayDefendSeconds = ref.delayDefend();
                        slot.displayName = firstNonBlank(def.displayName, cfg.displayName);
                        slot.mapIcon = firstNonBlank(def.mapIcon, cfg.mapIcon);
                        slot.troopValue = Math.max(0, def.troopValue);
                        slot.supplyVeh = def.supplyVeh;
                        slot.fightVeh = def.fightVeh;
                        slot.vehicleCrewSeats = def.vehicleCrewSeats;
                        if (i == 0) {
                            // 类型级指标取槽位 0 的注册表值，供部署界面与补给逻辑使用。
                            if (def.displayName != null && !def.displayName.isBlank()) {
                                cfg.displayName = def.displayName;
                            }
                            if (def.mapIcon != null && !def.mapIcon.isBlank()) {
                                cfg.mapIcon = def.mapIcon;
                            }
                            cfg.troopValue = Math.max(0, def.troopValue);
                            cfg.initialDeployDelayAttackSeconds = Math.max(0, ref.delayAttack());
                            cfg.initialDeployDelayDefendSeconds = Math.max(0, ref.delayDefend());
                        }
                    } else {
                        slot.entityTypeStr = data.entities.get(i);
                        slot.respawnMinutes = cfg.respawnMinutes;
                        slot.initialDeployDelayAttackSeconds = cfg.initialDeployDelayAttackSeconds;
                        slot.initialDeployDelayDefendSeconds = cfg.initialDeployDelayDefendSeconds;
                        slot.displayName = cfg.displayName;
                        slot.mapIcon = cfg.mapIcon;
                        slot.troopValue = cfg.troopValue;
                        slot.supplyVeh = cfg.supplyVeh;
                        slot.fightVeh = cfg.fightVeh;
                        slot.vehicleCrewSeats = cfg.vehicleCrewSeats;
                    }
                    slot.attack = fromPose(points.get(i).attack());
                    slot.defend = fromPose(points.get(i).defend());
                    slot.nbt = cfg.nbt;
                    slot.items = cfg.items;
                    cfg.slots.add(slot);
                }
                if (!cfg.slots.isEmpty()) {
                    cfg.entityTypeStr = cfg.slots.get(0).entityTypeStr;
                    cfg.deployment.attack = cfg.slots.get(0).attack;
                    cfg.deployment.defend = cfg.slots.get(0).defend;
                    if (registryBacked) {
                        // 注册表模式下刷新时间/首次部署延迟按槽位各自生效；
                        // 类型级字段取「最早」的那一刻，供部署面板与旧路径兜底。
                        int minRespawn = Integer.MAX_VALUE;
                        int minAttack = Integer.MAX_VALUE;
                        int minDefend = Integer.MAX_VALUE;
                        for (VehicleSlotConfig s : cfg.slots) {
                            minRespawn = Math.min(minRespawn, s.respawnMinutes);
                            minAttack = Math.min(minAttack, s.initialDeployDelayAttackSeconds);
                            minDefend = Math.min(minDefend, s.initialDeployDelayDefendSeconds);
                        }
                        if (minRespawn != Integer.MAX_VALUE) cfg.respawnMinutes = Math.max(0, minRespawn);
                        if (minAttack != Integer.MAX_VALUE) cfg.initialDeployDelayAttackSeconds = Math.max(0, minAttack);
                        if (minDefend != Integer.MAX_VALUE) cfg.initialDeployDelayDefendSeconds = Math.max(0, minDefend);
                        // 组员座位：槽位可能停不同载具，类型级取最严格的一档兜底，
                        // 实际准入按槽位自己的 vehicleCrewSeats 判定。
                        Integer strictest = null;
                        for (VehicleSlotConfig s : cfg.slots) {
                            if (s.vehicleCrewSeats != null) {
                                strictest = strictest == null
                                    ? s.vehicleCrewSeats : Math.max(strictest, s.vehicleCrewSeats);
                            }
                        }
                        if (strictest != null) cfg.vehicleCrewSeats = strictest;
                    }
                }
                List<VehSpawnSnapshot.SpawnPoint> outposts =
                    map.vehSpawn.outpostPointsByType.get(type);
                if (outposts != null && !outposts.isEmpty()) {
                    cfg.outpostActivateAfterBatch = map.vehSpawn.outpostActivateAfterBatch;
                    for (VehSpawnSnapshot.SpawnPoint point : outposts) {
                        cfg.outpostAttack.add(fromPose(point.attack()));
                        cfg.outpostDefend.add(fromPose(point.defend()));
                    }
                }
                typeMap.put(type, cfg);
            }
            VEHICLE_CONFIGS.put(formation.getKey(), typeMap);
        }
        Espetro.LOGGER.info("活动地图载具配置已建立: {} 个兼容编制", VEHICLE_CONFIGS.size());
        warnLegacyVehicleTypes(legacyVehicleTypes);
    }

    private static DeploymentPointConfig fromPose(VehSpawnSnapshot.Pose pose) {
        DeploymentPointConfig point = new DeploymentPointConfig();
        point.position = new int[] {
            (int) Math.floor(pose.x()), (int) Math.floor(pose.y()), (int) Math.floor(pose.z())
        };
        point.yaw = pose.yaw();
        return point;
    }

    private static VehicleTypeConfig buildVehicleConfig(String vehicleType, FactionDataLoader.VehicleData vd) {
        int max = vd.max > 0 ? vd.max : FactionDataLoader.VehicleData.DEFAULT_MAX;
        int respawn = vd.respawnMinutes > 0
            ? vd.respawnMinutes
            : FactionDataLoader.VehicleData.DEFAULT_RESPAWN_MINUTES;

        VehicleTypeConfig cfg = new VehicleTypeConfig(max, respawn);
        cfg.perMaxCount = Math.max(1, vd.perMaxCount);
        cfg.entityTypeStr = vd.entityTypeStr;
        cfg.displayName = firstNonBlank(vd.displayName, vehicleType);
        cfg.troopValue = Math.max(0, vd.troopValue);
        cfg.mapIcon = firstNonBlank(vd.mapIcon, null);
        cfg.vehicleCrewSeats = vd.vehicleCrewSeats;
        cfg.nbt = firstNonBlank(vd.nbt, null);
        addVehicleItems(cfg.items, vd.items);
        if (vd.supplyLoadout != null) cfg.supplyLoadout = vd.supplyLoadout;
        // 载具补给类型和容量
        applySupplyProfile(cfg, vd.supplyVeh, vd.fightVeh, vd.capacity);
        if (vd.initialDeployDelay != null) {
            cfg.initialDeployDelayAttackSeconds = Math.max(0, vd.initialDeployDelay.attack);
            cfg.initialDeployDelayDefendSeconds = Math.max(0, vd.initialDeployDelay.defend);
        }
        if (vd.entityTags != null) {
            for (String tag : vd.entityTags) {
                if (tag != null && !tag.isBlank()) {
                    cfg.entityTags.add(tag);
                }
            }
        }
        cfg.deployment = buildDeploymentConfig(vehicleType, vd);
        if (vd.entities != null && !vd.entities.isEmpty()) {
            cfg.max = vd.entities.size() * cfg.perMaxCount;
        }
        if (cfg.entityTypeStr == null || cfg.entityTypeStr.isBlank()) {
            Espetro.LOGGER.warn("载具 {} 未配置 entity_type；请在对应编制 JSON 的 vehicles 节中配置", vehicleType);
        }
        return cfg;
    }

    /**
     * 兼容新增 fightveh/supplyveh 字段之前的编制：两项都缺失时按战斗载具处理（装卸与容量语义）。
     * 任一字段被显式声明后不再启用兼容回退，因此两个 false 仍可明确关闭载具轮盘。
     *
     * <p>注意：载具内换职白名单只认显式 {@code fightveh: true}；兼容回退来的“无标签载具”
     * 与补给载具一样只能换成驾驶员/载具队长，见 {@link VehicleClassChangePolicy}。</p>
     */
    static void applySupplyProfile(VehicleTypeConfig cfg, @Nullable Boolean supplyVeh,
                                   @Nullable Boolean fightVeh, @Nullable Integer capacity) {
        boolean legacy = supplyVeh == null && fightVeh == null;
        cfg.supplyVeh = Boolean.TRUE.equals(supplyVeh);
        cfg.fightVeh = Boolean.TRUE.equals(fightVeh) || legacy;
        cfg.legacyDefaultedToFightVehicle = legacy;
        cfg.explicitFightVehicle = Boolean.TRUE.equals(fightVeh);

        if (capacity != null && capacity > 0) {
            cfg.supplyCapacity = capacity;
        } else if (cfg.supplyVeh) {
            cfg.supplyCapacity = 3000;
        } else if (cfg.fightVeh) {
            cfg.supplyCapacity = 500;
        } else {
            cfg.supplyCapacity = 300;
        }
    }

    private static void warnLegacyVehicleTypes(int count) {
        if (count <= 0) return;
        Espetro.LOGGER.warn(
            "检测到 {} 个未声明 fightveh/supplyveh 的旧载具类型，已按 fightveh=true 兼容装卸；"
                + "其载具内换职按“无标签载具”处理（仅驾驶员/载具队长），"
                + "需要完整职业列表请显式配置 fightveh=true，补给载具请显式配置 supplyveh=true", count);
    }

    private static DeploymentConfig buildDeploymentConfig(String vehicleType, FactionDataLoader.VehicleData vd) {
        DeploymentConfig cfg = new DeploymentConfig();

        FactionDataLoader.VehicleDeploymentData raw = vd.deployment;
        if (raw != null) {
            cfg.attack = buildDeploymentPoint(raw.attack);
            cfg.defend = buildDeploymentPoint(raw.defend);
        }

        if (cfg.attack == null) {
            Espetro.LOGGER.warn("载具 {} 未配置有效 deployment.ATTACK.position 坐标；必须在编制 JSON 中直接指定攻方坐标", vehicleType);
        }
        if (cfg.defend == null) {
            Espetro.LOGGER.warn("载具 {} 未配置有效 deployment.DEFEND.position 坐标；必须在编制 JSON 中直接指定守方坐标", vehicleType);
        }
        return cfg;
    }

    @Nullable
    private static DeploymentPointConfig buildDeploymentPoint(@Nullable FactionDataLoader.VehicleDeploymentPointData raw) {
        if (raw == null || !validVector(raw.position)) {
            return null;
        }

        DeploymentPointConfig point = new DeploymentPointConfig();
        point.position = raw.position;
        if (raw.yaw != null) {
            point.yaw = raw.yaw;
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

    /** 复制编制里配置的载具集装箱物品（"<itemId>[<snbt>] <count>"），跳过空项。 */
    private static void addVehicleItems(List<String> target, @Nullable String[] source) {
        if (source == null) return;
        for (String item : source) {
            if (item != null && !item.isBlank()) target.add(item.trim());
        }
    }

    /**
     * 获取指定编制的载具配置
     */
    public static Map<String, VehicleTypeConfig> getFactionVehicles(String factionId) {
        return VEHICLE_CONFIGS.getOrDefault(factionId, Collections.emptyMap());
    }

    /**
     * 获取某个编制的某个载具类型配置
     */
    @Nullable
    public static VehicleTypeConfig getVehicleConfig(String factionId, String vehicleType) {
        Map<String, VehicleTypeConfig> map = VEHICLE_CONFIGS.get(factionId);
        if (map == null) return null;
        return map.get(vehicleType);
    }

    /**
     * 获取所有编制配置
     */
    public static Map<String, Map<String, VehicleTypeConfig>> getAllConfigs() {
        return new LinkedHashMap<>(VEHICLE_CONFIGS);
    }

    /**
     * 获取所有载具类型 key（用于命令补全）
     */
    public static Set<String> getAllVehicleTypeKeys() {
        Set<String> keys = new LinkedHashSet<>();
        for (Map<String, VehicleTypeConfig> typeMap : VEHICLE_CONFIGS.values()) {
            keys.addAll(typeMap.keySet());
        }
        return keys;
    }
}
