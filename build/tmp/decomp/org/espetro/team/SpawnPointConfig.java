/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 */
package org.espetro.team;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;

public class SpawnPointConfig {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_PATH = "espetro/config/spawn_points.json";
    private static final Map<String, SpawnPoint> SPAWN_POINTS = new HashMap<String, SpawnPoint>();

    @Deprecated
    public static void loadConfig(MinecraftServer server) {
    }

    private static void applyDefaults() {
        SpawnPointConfig.replaceSpawnPoints(SpawnPointConfig.defaultSpawnPoints());
    }

    private static void parseAndApplyConfig(String json) {
        JsonObject root = (JsonObject)GSON.fromJson(json, JsonObject.class);
        if (root.has("spawnPoints")) {
            JsonObject spawnPoints = root.getAsJsonObject("spawnPoints");
            Map<String, SpawnPoint> updatedSpawnPoints = SpawnPointConfig.defaultSpawnPoints();
            for (String team : new String[]{"ATTACK", "DEFEND"}) {
                if (!spawnPoints.has(team)) continue;
                JsonObject point = spawnPoints.getAsJsonObject(team);
                SpawnPoint spawn = new SpawnPoint();
                spawn.x = SpawnPointConfig.getDouble(point, "x", 0.0);
                spawn.y = SpawnPointConfig.getDouble(point, "y", 65.0);
                spawn.z = SpawnPointConfig.getDouble(point, "z", 0.0);
                spawn.yaw = (float)SpawnPointConfig.getDouble(point, "yaw", team.equals("ATTACK") ? 0.0 : 180.0);
                updatedSpawnPoints.put(team, spawn);
                Espetro.LOGGER.info("\u52a0\u8f7d {} \u590d\u6d3b\u70b9: ({}, {}, {}), yaw: {}", new Object[]{team, spawn.x, spawn.y, spawn.z, Float.valueOf(spawn.yaw)});
            }
            SpawnPointConfig.replaceSpawnPoints(updatedSpawnPoints);
        }
    }

    private static double getDouble(JsonObject obj, String key, double defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsDouble();
        }
        return defaultValue;
    }

    public static SpawnPoint getSpawnPoint(String team) {
        SpawnPoint point = SPAWN_POINTS.get(team);
        if (point == null) {
            point = SPAWN_POINTS.get("DEFEND");
        }
        return point;
    }

    public static void setSpawnPoint(String team, double x, double y, double z, float yaw) {
        SpawnPoint updated = new SpawnPoint(x, y, z, yaw);
        if (SpawnPointConfig.sameSpawnPoint(SPAWN_POINTS.get(team), updated)) {
            return;
        }
        SPAWN_POINTS.put(team, updated);
        EspetroAPI.markTacticalMapStateDirty();
        Espetro.LOGGER.info("\u52a8\u6001\u8bbe\u7f6e {} \u590d\u6d3b\u70b9: ({}, {}, {}), yaw: {}", new Object[]{team, x, y, z, Float.valueOf(yaw)});
    }

    public static Map<String, SpawnPoint> getAllSpawnPoints() {
        return new HashMap<String, SpawnPoint>(SPAWN_POINTS);
    }

    private static Map<String, SpawnPoint> defaultSpawnPoints() {
        HashMap<String, SpawnPoint> defaults = new HashMap<String, SpawnPoint>();
        defaults.put("ATTACK", new SpawnPoint(100.5, 65.0, 0.5, 0.0f));
        defaults.put("DEFEND", new SpawnPoint(-100.5, 65.0, 0.5, 180.0f));
        return defaults;
    }

    private static void replaceSpawnPoints(Map<String, SpawnPoint> updatedSpawnPoints) {
        if (SpawnPointConfig.sameSpawnPoints(SPAWN_POINTS, updatedSpawnPoints)) {
            return;
        }
        SPAWN_POINTS.clear();
        SPAWN_POINTS.putAll(updatedSpawnPoints);
        EspetroAPI.markTacticalMapStateDirty();
    }

    private static boolean sameSpawnPoints(Map<String, SpawnPoint> first, Map<String, SpawnPoint> second) {
        if (!first.keySet().equals(second.keySet())) {
            return false;
        }
        for (Map.Entry<String, SpawnPoint> entry : first.entrySet()) {
            if (SpawnPointConfig.sameSpawnPoint(entry.getValue(), second.get(entry.getKey()))) continue;
            return false;
        }
        return true;
    }

    private static boolean sameSpawnPoint(SpawnPoint first, SpawnPoint second) {
        if (first == second) {
            return true;
        }
        return first != null && second != null && Double.compare(first.x, second.x) == 0 && Double.compare(first.y, second.y) == 0 && Double.compare(first.z, second.z) == 0 && Float.compare(first.yaw, second.yaw) == 0;
    }

    static {
        SPAWN_POINTS.put("ATTACK", new SpawnPoint(100.5, 65.0, 0.5, 0.0f));
        SPAWN_POINTS.put("DEFEND", new SpawnPoint(-100.5, 65.0, 0.5, 180.0f));
    }

    public static class SpawnPoint {
        public double x;
        public double y;
        public double z;
        public float yaw;

        public SpawnPoint() {
        }

        public SpawnPoint(double x, double y, double z, float yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
        }

        public float getPitch() {
            return 0.0f;
        }
    }
}

