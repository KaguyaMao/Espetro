package org.espetro.team;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 复活点配置加载器（多主基地）。
 *
 * <p>每支队伍可以持有 <b>多个</b> 主基地重生点（有序）。索引 0 是「第一主基地」：
 * 部署等待屏障与主基地无敌区都以它为中心，其余点只提供出生位置与配套补给。
 * 旧的单点 API（{@link #getSpawnPoint(String)} / {@link #setSpawnPoint}）保留，
 * 语义等同于「取索引 0 或替换为单点」。</p>
 */
public class SpawnPointConfig {

    private static final Gson GSON = new Gson();
    private static final String CONFIG_PATH = "espetro/config/spawn_points.json";

    /** 每队最多保留的主基地数量（与地图快照一致）。 */
    public static final int MAX_MAIN_SPAWNS = 8;

    // 队伍复活点配置（有序，索引 0 = 第一主基地）
    private static final Map<String, List<SpawnPoint>> SPAWN_POINTS = new HashMap<>();

    // 默认复活点（如果配置文件不存在或读取失败）
    static {
        SPAWN_POINTS.put("ATTACK", List.of(new SpawnPoint(100.5, 65, 0.5, 0, "主基地 #1")));
        SPAWN_POINTS.put("DEFEND", List.of(new SpawnPoint(-100.5, 65, 0.5, 180, "主基地 #1")));
    }

    /**
     * 复活点数据类
     */
    public static class SpawnPoint {
        public double x;
        public double y;
        public double z;
        public float yaw;
        public String name;

        public SpawnPoint() {}

        public SpawnPoint(double x, double y, double z, float yaw) {
            this(x, y, z, yaw, null);
        }

        public SpawnPoint(double x, double y, double z, float yaw, String name) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.name = name;
        }

        public float getPitch() {
            return 0f;
        }

        public SpawnPoint copy() {
            return new SpawnPoint(x, y, z, yaw, name);
        }
    }

    /**
     * 加载复活点配置
     */
    /** @deprecated 不从 datapack 加载；由 GameConfigBridge 设置。 */
    @Deprecated
    public static void loadConfig(MinecraftServer server) {
        // 有意留空
    }

    /**
     * 重置为默认复活点
     */
    private static void applyDefaults() {
        replaceSpawnPoints(defaultSpawnPoints());
    }

    /**
     * 解析并应用配置（旧路径：单点对象）
     */
    private static void parseAndApplyConfig(String json) {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        if (root == null || !root.has("spawnPoints")) {
            return;
        }
        JsonObject spawnPoints = root.getAsJsonObject("spawnPoints");
        Map<String, List<SpawnPoint>> updated = defaultSpawnPoints();
        for (String team : new String[]{"ATTACK", "DEFEND"}) {
            if (!spawnPoints.has(team) || !spawnPoints.get(team).isJsonObject()) {
                continue;
            }
            JsonObject point = spawnPoints.getAsJsonObject(team);
            SpawnPoint spawn = new SpawnPoint();
            spawn.x = getDouble(point, "x", 0);
            spawn.y = getDouble(point, "y", 65);
            spawn.z = getDouble(point, "z", 0);
            spawn.yaw = (float) getDouble(point, "yaw", team.equals("ATTACK") ? 0 : 180);
            spawn.name = point.has("name") ? point.get("name").getAsString() : "主基地 #1";
            updated.put(team, new ArrayList<>(List.of(spawn)));
        }
        replaceSpawnPoints(updated);
    }

    private static double getDouble(JsonObject obj, String key, double defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsDouble();
        }
        return defaultValue;
    }

    /**
     * 获取指定队伍的第一主基地（= 屏障/无敌区中心）。
     */
    public static SpawnPoint getSpawnPoint(String team) {
        List<SpawnPoint> points = SPAWN_POINTS.get(team);
        if (points == null || points.isEmpty()) {
            points = SPAWN_POINTS.get("DEFEND");
        }
        if (points == null || points.isEmpty()) {
            return new SpawnPoint(0, 65, 0, 0, "主基地 #1");
        }
        return points.get(0);
    }

    /**
     * 获取指定队伍第 {@code index} 个主基地；越界返回 {@code null}。
     */
    public static SpawnPoint getSpawnPoint(String team, int index) {
        List<SpawnPoint> points = SPAWN_POINTS.get(team);
        if (points == null || index < 0 || index >= points.size()) {
            return null;
        }
        return points.get(index);
    }

    /**
     * 获取指定队伍的全部主基地（不可变列表，索引 0 = 第一主基地）。
     */
    public static List<SpawnPoint> getSpawnPoints(String team) {
        List<SpawnPoint> points = SPAWN_POINTS.get(team);
        return points == null ? List.of() : List.copyOf(points);
    }

    /** 队伍主基地数量。 */
    public static int mainSpawnCount(String team) {
        List<SpawnPoint> points = SPAWN_POINTS.get(team);
        return points == null ? 0 : points.size();
    }

    /**
     * 动态设置复活点（单点覆盖，旧语义）
     */
    public static void setSpawnPoint(String team, double x, double y, double z, float yaw) {
        setSpawnPoints(team, List.of(new SpawnPoint(x, y, z, yaw, "主基地 #1")));
    }

    /**
     * 动态设置一支队伍的全部主基地（索引 0 = 第一主基地）。
     */
    public static void setSpawnPoints(String team, List<SpawnPoint> points) {
        if (points == null || points.isEmpty()) {
            return;
        }
        List<SpawnPoint> limited = new ArrayList<>();
        for (int i = 0; i < points.size() && i < MAX_MAIN_SPAWNS; i++) {
            SpawnPoint point = points.get(i);
            if (point == null) {
                continue;
            }
            SpawnPoint copy = point.copy();
            if (copy.name == null || copy.name.isBlank()) {
                copy.name = "主基地 #" + (i + 1);
            }
            limited.add(copy);
        }
        if (limited.isEmpty()) {
            return;
        }
        if (sameSpawnPoints(SPAWN_POINTS.get(team), limited)) {
            return;
        }
        SPAWN_POINTS.put(team, List.copyOf(limited));
        EspetroAPI.markTacticalMapStateDirty();
        Espetro.LOGGER.info("动态设置 {} 复活点: {} 个主基地", team, limited.size());
        for (int i = 0; i < limited.size(); i++) {
            SpawnPoint p = limited.get(i);
            Espetro.LOGGER.info("  主基地 #{} {}: ({}, {}, {}), yaw: {}",
                i + 1, p.name, p.x, p.y, p.z, p.yaw);
        }
    }

    /**
     * 覆盖某支队伍指定索引的主基地，其余点保持不变（越界则追加到末尾）。
     *
     * <p>两栖地图用它把攻方「第一主基地」换成舰上甲板点，同时保留地图里配置的其它攻方主基地。</p>
     */
    public static void overrideMainSpawn(String team, int index, SpawnPoint point) {
        if (point == null) {
            return;
        }
        List<SpawnPoint> current = new ArrayList<>(getSpawnPoints(team));
        SpawnPoint copy = point.copy();
        if (copy.name == null || copy.name.isBlank()) {
            copy.name = "主基地 #" + (index + 1);
        }
        if (index < 0 || index > MAX_MAIN_SPAWNS - 1) {
            return;
        }
        while (current.size() <= index) {
            current.add(null);
        }
        current.set(index, copy);
        List<SpawnPoint> filtered = new ArrayList<>();
        for (int i = 0; i < current.size(); i++) {
            SpawnPoint p = current.get(i);
            if (p == null) {
                continue;
            }
            if (p.name == null || p.name.isBlank()) {
                p.name = "主基地 #" + (i + 1);
            }
            filtered.add(p);
        }
        if (filtered.isEmpty() || sameSpawnPoints(SPAWN_POINTS.get(team), filtered)) {
            return;
        }
        SPAWN_POINTS.put(team, List.copyOf(filtered));
        EspetroAPI.markTacticalMapStateDirty();
        Espetro.LOGGER.info("覆盖 {} 主基地 #{}: ({}, {}, {}), yaw: {}", team, index + 1,
            copy.x, copy.y, copy.z, copy.yaw);
    }

    /**
     * 获取所有复活点配置（每队第一主基地，用于命令显示）
     */
    public static Map<String, SpawnPoint> getAllSpawnPoints() {
        Map<String, SpawnPoint> out = new HashMap<>();
        for (Map.Entry<String, List<SpawnPoint>> entry : SPAWN_POINTS.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                out.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return out;
    }

    /** 获取所有复活点配置（全部主基地）。 */
    public static Map<String, List<SpawnPoint>> getAllSpawnPointLists() {
        Map<String, List<SpawnPoint>> out = new HashMap<>();
        for (Map.Entry<String, List<SpawnPoint>> entry : SPAWN_POINTS.entrySet()) {
            out.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return out;
    }

    private static Map<String, List<SpawnPoint>> defaultSpawnPoints() {
        Map<String, List<SpawnPoint>> defaults = new HashMap<>();
        defaults.put("ATTACK", List.of(new SpawnPoint(100.5, 65, 0.5, 0, "主基地 #1")));
        defaults.put("DEFEND", List.of(new SpawnPoint(-100.5, 65, 0.5, 180, "主基地 #1")));
        return defaults;
    }

    private static void replaceSpawnPoints(Map<String, List<SpawnPoint>> updated) {
        boolean same = updated.keySet().equals(SPAWN_POINTS.keySet());
        if (same) {
            for (Map.Entry<String, List<SpawnPoint>> entry : updated.entrySet()) {
                if (!sameSpawnPoints(SPAWN_POINTS.get(entry.getKey()), entry.getValue())) {
                    same = false;
                    break;
                }
            }
        }
        if (same) {
            return;
        }
        SPAWN_POINTS.clear();
        SPAWN_POINTS.putAll(updated);
        EspetroAPI.markTacticalMapStateDirty();
    }

    private static boolean sameSpawnPoints(List<SpawnPoint> first, List<SpawnPoint> second) {
        if (first == null || second == null) {
            return first == second;
        }
        if (first.size() != second.size()) {
            return false;
        }
        for (int i = 0; i < first.size(); i++) {
            SpawnPoint a = first.get(i);
            SpawnPoint b = second.get(i);
            if (a == b) {
                continue;
            }
            if (a == null || b == null) {
                return false;
            }
            if (Double.compare(a.x, b.x) != 0
                || Double.compare(a.y, b.y) != 0
                || Double.compare(a.z, b.z) != 0
                || Float.compare(a.yaw, b.yaw) != 0) {
                return false;
            }
        }
        return true;
    }
}
