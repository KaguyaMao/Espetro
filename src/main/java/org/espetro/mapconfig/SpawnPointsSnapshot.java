package org.espetro.mapconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Player spawn points for ATTACK / DEFEND on one map.
 *
 * <p>支持两种写法（向后兼容）：
 * <pre>
 * "spawnPoints": { "ATTACK": {"x":..,"y":..,"z":..,"yaw":..}, "DEFEND": {...} }
 * "spawnPoints": { "ATTACK": [ {...}, {...} ], "DEFEND": [ {...} ] }
 * </pre>
 * 数组形式下 <b>索引 0 是「第一主基地」</b>：屏障与无敌区只认它；其余点只提供出生与补给。</p>
 */
public final class SpawnPointsSnapshot {

    /** 单队最多保留的主基地数量（防止部署界面被刷爆）。 */
    public static final int MAX_MAIN_SPAWNS = 8;

    public record Point(double x, double y, double z, float yaw, String name) {
        public Point(double x, double y, double z, float yaw) {
            this(x, y, z, yaw, null);
        }
    }

    public final List<Point> attackPoints;
    public final List<Point> defendPoints;
    /** 兼容字段：等于列表首元素。 */
    public final Point attack;
    /** 兼容字段：等于列表首元素。 */
    public final Point defend;
    public final boolean valid;
    public final String error;

    public SpawnPointsSnapshot(List<Point> attackPoints, List<Point> defendPoints,
                               boolean valid, String error) {
        this.attackPoints = attackPoints == null ? List.of() : List.copyOf(attackPoints);
        this.defendPoints = defendPoints == null ? List.of() : List.copyOf(defendPoints);
        this.attack = this.attackPoints.isEmpty() ? null : this.attackPoints.get(0);
        this.defend = this.defendPoints.isEmpty() ? null : this.defendPoints.get(0);
        this.valid = valid;
        this.error = error;
    }

    /** 队伍的全部主基地（索引 0 为第一主基地）。 */
    public List<Point> forTeamList(String team) {
        return "ATTACK".equalsIgnoreCase(team) ? attackPoints : defendPoints;
    }

    /** 队伍的第一主基地（兼容旧调用）。 */
    public Point forTeam(String team) {
        return "ATTACK".equalsIgnoreCase(team) ? attack : defend;
    }

    public int count(String team) {
        return forTeamList(team).size();
    }

    public static SpawnPointsSnapshot parse(JsonObject root) {
        if (root == null || !root.has("spawnPoints") || !root.get("spawnPoints").isJsonObject()) {
            return new SpawnPointsSnapshot(null, null, false, "spawn_points.json 缺少 spawnPoints");
        }
        JsonObject sp = root.getAsJsonObject("spawnPoints");
        List<Point> attack = parseTeam(sp, "ATTACK");
        List<Point> defend = parseTeam(sp, "DEFEND");
        if (attack.isEmpty() || defend.isEmpty()) {
            return new SpawnPointsSnapshot(attack, defend, false, "spawn_points.json 缺少 ATTACK 或 DEFEND");
        }
        return new SpawnPointsSnapshot(attack, defend, true, null);
    }

    private static List<Point> parseTeam(JsonObject parent, String team) {
        List<Point> out = new ArrayList<>();
        if (!parent.has(team)) {
            return out;
        }
        JsonElement element = parent.get(team);
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement entry : array) {
                if (!entry.isJsonObject()) {
                    continue;
                }
                Point point = parsePoint(entry.getAsJsonObject(), team, out.size());
                if (point != null) {
                    out.add(point);
                }
                if (out.size() >= MAX_MAIN_SPAWNS) {
                    break;
                }
            }
        } else if (element.isJsonObject()) {
            Point point = parsePoint(element.getAsJsonObject(), team, 0);
            if (point != null) {
                out.add(point);
            }
        }
        return out;
    }

    private static Point parsePoint(JsonObject p, String team, int index) {
        try {
            float defaultYaw = "ATTACK".equals(team) ? 0f : 180f;
            float yaw = p.has("yaw") ? p.get("yaw").getAsFloat() : defaultYaw;
            String name = p.has("name") && !p.get("name").isJsonNull()
                ? p.get("name").getAsString() : null;
            double x = p.get("x").getAsDouble();
            double y = p.get("y").getAsDouble();
            double z = p.get("z").getAsDouble();
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                return null;
            }
            if (name == null || name.isBlank()) {
                name = "主基地 #" + (index + 1);
            }
            return new Point(x, y, z, yaw, name);
        } catch (Exception e) {
            return null;
        }
    }
}
