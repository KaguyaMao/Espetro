/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package org.espetro.mapconfig;

import com.google.gson.JsonObject;

public final class SpawnPointsSnapshot {
    public final Point attack;
    public final Point defend;
    public final boolean valid;
    public final String error;

    public SpawnPointsSnapshot(Point attack, Point defend, boolean valid, String error) {
        this.attack = attack;
        this.defend = defend;
        this.valid = valid;
        this.error = error;
    }

    public Point forTeam(String team) {
        return "ATTACK".equalsIgnoreCase(team) ? this.attack : this.defend;
    }

    public static SpawnPointsSnapshot parse(JsonObject root) {
        if (!root.has("spawnPoints") || !root.get("spawnPoints").isJsonObject()) {
            return new SpawnPointsSnapshot(null, null, false, "spawn_points.json \u7f3a\u5c11 spawnPoints");
        }
        JsonObject sp = root.getAsJsonObject("spawnPoints");
        Point attack = SpawnPointsSnapshot.parsePoint(sp, "ATTACK");
        Point defend = SpawnPointsSnapshot.parsePoint(sp, "DEFEND");
        if (attack == null || defend == null) {
            return new SpawnPointsSnapshot(attack, defend, false, "spawn_points.json \u7f3a\u5c11 ATTACK \u6216 DEFEND");
        }
        return new SpawnPointsSnapshot(attack, defend, true, null);
    }

    private static Point parsePoint(JsonObject parent, String team) {
        if (!parent.has(team) || !parent.get(team).isJsonObject()) {
            return null;
        }
        JsonObject p = parent.getAsJsonObject(team);
        try {
            return new Point(p.get("x").getAsDouble(), p.get("y").getAsDouble(), p.get("z").getAsDouble(), p.has("yaw") ? p.get("yaw").getAsFloat() : ("ATTACK".equals(team) ? 0.0f : 180.0f));
        }
        catch (Exception e) {
            return null;
        }
    }

    public record Point(double x, double y, double z, float yaw) {
    }
}

