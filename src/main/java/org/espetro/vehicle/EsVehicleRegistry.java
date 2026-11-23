package org.espetro.vehicle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 载具注册表：{@code config/EsVehicle/vehicles.json}。
 *
 * <p>编制 JSON 只写“槽位 → 载具注册 id → 首次部署延迟”，载具本身的实体、图标、显示名、
 * 重生时间、补给能力与皮肤都由这里定义；同一种载具可按用途注册多次（如补给型/战斗型）。</p>
 *
 * <p>启动时一次性读取并冻结（与 {@code EsFactions} 一致），{@code /reload} 不会重读。</p>
 */
public final class EsVehicleRegistry {

    /** 相对实例根目录的配置文件路径。 */
    public static final String CONFIG_PATH = "config/EsVehicle/vehicles.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<String, EsVehicleDef> DEFS = new LinkedHashMap<>();
    private static volatile boolean loaded = false;
    private static volatile boolean failed = false;

    private EsVehicleRegistry() {
    }

    /** 单条载具注册。 */
    public static final class EsVehicleDef {
        public transient String id;
        public String entity;
        // 注册表 JSON 用下划线命名；Gson 默认按字段名匹配，必须显式标注，
        // 否则 display_name / respawn_minutes 这类键会被静默忽略（表现为名字回落成 id、刷新时间全 5 分钟）。
        @com.google.gson.annotations.SerializedName("display_name")
        public String displayName;
        @com.google.gson.annotations.SerializedName("map_icon")
        public String mapIcon;
        @com.google.gson.annotations.SerializedName("per_max_count")
        public int perMaxCount = 1;
        @com.google.gson.annotations.SerializedName("respawn_minutes")
        public int respawnMinutes = 0;
        @com.google.gson.annotations.SerializedName("troop_value")
        public int troopValue = 0;
        @com.google.gson.annotations.SerializedName(value = "supplyveh", alternate = {"supply_veh"})
        public boolean supplyVeh = false;
        @com.google.gson.annotations.SerializedName(value = "fightveh", alternate = {"fight_veh"})
        public boolean fightVeh = false;
        public Integer capacity;
        @com.google.gson.annotations.SerializedName("vehicle_crew_seats")
        public Integer vehicleCrewSeats;
        public String nbt;
        @com.google.gson.annotations.SerializedName("entity_tags")
        public String[] entityTags;
        public String[] items;
        @com.google.gson.annotations.SerializedName("supply_loadout")
        public Boolean supplyLoadout;
        /** 环境标签 → 皮肤 id，例如 {"normal": "green", "desert": "sand"}。 */
        public Map<String, String> skin = new LinkedHashMap<>();

        @Nullable
        public EntityType<?> entityType() {
            if (entity == null || entity.isBlank()) return null;
            ResourceLocation rl = ResourceLocation.tryParse(entity.trim());
            if (rl == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) return null;
            return BuiltInRegistries.ENTITY_TYPE.get(rl);
        }

        /** 该环境应使用的皮肤 id；没有配置返回 null（= 模型默认外观）。 */
        @Nullable
        public String skinFor(@Nullable String environment) {
            String env = environment == null ? "" : environment.trim().toLowerCase(java.util.Locale.ROOT);
            String skinId = env.isEmpty() ? null : skin.get(env);
            if (skinId == null || skinId.isBlank()) {
                skinId = skin.get("normal");
            }
            return skinId == null || skinId.isBlank() ? null : skinId.trim();
        }

        /** 未配置时回落到默认 5 分钟。 */
        public int effectiveRespawnMinutes() {
            return respawnMinutes > 0 ? respawnMinutes : 5;
        }
    }

    /** 启动期加载；重复调用只生效一次。 */
    public static synchronized void loadIfNeeded() {
        if (loaded || failed) {
            return;
        }
        Path path = FMLPaths.GAMEDIR.get().resolve(CONFIG_PATH);
        load(path);
    }

    /** 显式加载（测试/引导用）。 */
    public static synchronized void load(@Nullable Path path) {
        DEFS.clear();
        failed = false;
        loaded = true;
        if (path == null || !Files.exists(path)) {
            Espetro.LOGGER.warn("[载具注册表] 未找到 {}，编制中的载具引用将全部失败", CONFIG_PATH);
            return;
        }
        JsonObject root;
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            root = JsonParser.parseString(text).getAsJsonObject();
        } catch (IOException | RuntimeException e) {
            failed = true;
            Espetro.LOGGER.error("[载具注册表] 读取失败 {}: {}", CONFIG_PATH, e.toString());
            return;
        }
        JsonObject vehicles = root.has("vehicles") && root.get("vehicles").isJsonObject()
            ? root.getAsJsonObject("vehicles") : root;
        int errors = 0;
        for (Map.Entry<String, com.google.gson.JsonElement> entry : vehicles.entrySet()) {
            String id = entry.getKey();
            if (id == null || id.isBlank() || !entry.getValue().isJsonObject()) {
                errors++;
                Espetro.LOGGER.error("[载具注册表] 条目 {} 格式非法，已跳过", id);
                continue;
            }
            EsVehicleDef def;
            try {
                def = GSON.fromJson(entry.getValue(), EsVehicleDef.class);
            } catch (RuntimeException e) {
                errors++;
                Espetro.LOGGER.error("[载具注册表] 条目 {} 解析失败: {}", id, e.toString());
                continue;
            }
            if (def == null) {
                errors++;
                continue;
            }
            def.id = id.trim();
            String problem = validate(def);
            if (problem != null) {
                errors++;
                Espetro.LOGGER.error("[载具注册表] 条目 {} 无效: {}", id, problem);
                continue;
            }
            if (def.skin == null) {
                def.skin = new LinkedHashMap<>();
            }
            DEFS.put(def.id, def);
        }
        warnMissingSkins();
        Espetro.LOGGER.info("[载具注册表] 已加载 {} 条（校验失败 {}）", DEFS.size(), errors);
        warnMissingDisplayNames();
    }

    /** 缺 display_name 时界面上只能显示 id，容易以为是读取失败，单独提示一次。 */
    private static void warnMissingDisplayNames() {
        StringBuilder missing = new StringBuilder();
        int count = 0;
        for (EsVehicleDef def : DEFS.values()) {
            if (def.displayName == null || def.displayName.isBlank()) {
                count++;
                if (missing.length() > 0) missing.append(", ");
                if (missing.length() < 200) missing.append(def.id);
            }
        }
        if (count > 0) {
            Espetro.LOGGER.warn("[载具注册表] {} 条缺少 display_name，界面将回落显示 id: {}",
                count, missing);
        }
    }

    @Nullable
    private static String validate(EsVehicleDef def) {
        if (def.entity == null || def.entity.isBlank()) {
            return "缺少 entity";
        }
        ResourceLocation rl = ResourceLocation.tryParse(def.entity.trim());
        if (rl == null) {
            return "entity 不是合法 ResourceLocation: " + def.entity;
        }
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
            return "entity 未注册: " + def.entity;
        }
        if (def.perMaxCount < 1) {
            def.perMaxCount = 1;
        }
        if (def.respawnMinutes < 0) {
            def.respawnMinutes = 0;
        }
        if (def.capacity != null && def.capacity <= 0) {
            def.capacity = null;
        }
        if (def.vehicleCrewSeats != null && def.vehicleCrewSeats < 0) {
            return "vehicle_crew_seats 不能小于 0";
        }
        return null;
    }

    private static void warnMissingSkins() {
        for (EsVehicleDef def : DEFS.values()) {
            if (def.skin == null || def.skin.isEmpty()) {
                continue;
            }
            Set<String> available = querySkinIds(def);
            if (available == null) {
                continue; // 没有 SBW 或查询失败：跳过校验，运行期再容错
            }
            for (Map.Entry<String, String> entry : def.skin.entrySet()) {
                String skinId = entry.getValue();
                if (skinId == null || skinId.isBlank()) {
                    continue;
                }
                if (!available.contains(skinId)) {
                    Espetro.LOGGER.warn("[载具注册表] {} 的 {} 皮肤 '{}' 不存在（可用: {}）",
                        def.id, entry.getKey(), skinId, available);
                }
            }
        }
    }

    /**
     * 通过反射读取 SBW 已注册皮肤 id，避免对载具模组形成编译期强依赖。
     * @return 可用皮肤 id；无法查询时返回 null
     */
    @Nullable
    private static Set<String> querySkinIds(EsVehicleDef def) {
        EntityType<?> type = def.entityType();
        if (type == null) {
            return null;
        }
        try {
            Class<?> skinClass = Class.forName("com.atsuishio.superbwarfare.data.vehicle_skin.VehicleSkin");
            Object data = skinClass.getMethod("getSkins", EntityType.class).invoke(null, type);
            if (data == null) {
                return Collections.emptySet();
            }
            Object skins = data.getClass().getMethod("getSkins").invoke(data);
            Set<String> ids = new LinkedHashSet<>();
            if (skins instanceof Iterable<?> iterable) {
                for (Object skin : iterable) {
                    if (skin == null) continue;
                    Object id = skin.getClass().getMethod("getId").invoke(skin);
                    if (id instanceof String s && !s.isBlank()) {
                        ids.add(s);
                    }
                }
            }
            return ids;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Nullable
    public static EsVehicleDef get(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        loadIfNeeded();
        return DEFS.get(id.trim());
    }

    public static Map<String, EsVehicleDef> all() {
        loadIfNeeded();
        return Collections.unmodifiableMap(DEFS);
    }

    public static int size() {
        loadIfNeeded();
        return DEFS.size();
    }

    public static boolean isLoaded() {
        return loaded;
    }

    /** 供测试清空。 */
    static synchronized void resetForTests() {
        DEFS.clear();
        loaded = false;
        failed = false;
    }
}
