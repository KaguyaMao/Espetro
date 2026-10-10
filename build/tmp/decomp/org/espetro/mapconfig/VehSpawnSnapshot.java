/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package org.espetro.mapconfig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class VehSpawnSnapshot {
    public final List<String> vehicleTypes;
    public final Map<String, List<SpawnPoint>> spawnPointsByType;
    public final List<String> errors;

    public VehSpawnSnapshot(List<String> vehicleTypes, Map<String, List<SpawnPoint>> spawnPointsByType, List<String> errors) {
        this.vehicleTypes = List.copyOf(vehicleTypes);
        LinkedHashMap copy = new LinkedHashMap();
        for (Map.Entry<String, List<SpawnPoint>> e : spawnPointsByType.entrySet()) {
            copy.put(e.getKey(), List.copyOf((Collection)e.getValue()));
        }
        this.spawnPointsByType = Collections.unmodifiableMap(copy);
        this.errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return this.errors.isEmpty() && !this.vehicleTypes.isEmpty();
    }

    public int pointCount(String type) {
        List<SpawnPoint> list = this.spawnPointsByType.get(type);
        return list == null ? 0 : list.size();
    }

    public static VehSpawnSnapshot parse(JsonObject root) {
        ArrayList<String> errors = new ArrayList<String>();
        ArrayList<String> types = new ArrayList<String>();
        LinkedHashMap<String, List<SpawnPoint>> points = new LinkedHashMap<String, List<SpawnPoint>>();
        int aliasCount = 0;
        JsonElement typesEl = null;
        if (root.has("VehTypes")) {
            ++aliasCount;
            typesEl = root.get("VehTypes");
        }
        if (root.has("vehtypes")) {
            ++aliasCount;
            typesEl = root.get("vehtypes");
        }
        if (root.has("vehicle_types")) {
            ++aliasCount;
            typesEl = root.get("vehicle_types");
        }
        if (aliasCount > 1) {
            errors.add("VehSpawn.json \u540c\u65f6\u51fa\u73b0\u591a\u4e2a\u7c7b\u578b\u522b\u540d\u5b57\u6bb5 (VehTypes/vehtypes/vehicle_types)");
            return new VehSpawnSnapshot(types, points, errors);
        }
        if (typesEl == null) {
            errors.add("VehSpawn.json \u7f3a\u5c11 VehTypes \u6570\u7ec4");
            return new VehSpawnSnapshot(types, points, errors);
        }
        if (!typesEl.isJsonArray()) {
            errors.add("VehTypes \u5fc5\u987b\u662f JSON \u6570\u7ec4");
            return new VehSpawnSnapshot(types, points, errors);
        }
        LinkedHashSet<String> seen = new LinkedHashSet<String>();
        for (JsonElement el : typesEl.getAsJsonArray()) {
            if (!el.isJsonPrimitive()) {
                errors.add("VehTypes \u542b\u6709\u975e\u5b57\u7b26\u4e32\u5143\u7d20");
                continue;
            }
            String t = el.getAsString().trim().toLowerCase(Locale.ROOT);
            if (t.isEmpty()) {
                errors.add("VehTypes \u542b\u6709\u7a7a\u7c7b\u578b\u540d");
                continue;
            }
            if (!seen.add(t)) {
                errors.add("\u91cd\u590d\u7684\u8f7d\u5177\u7c7b\u578b: " + t);
                continue;
            }
            types.add(t);
        }
        if (!root.has("spawn_points") || !root.get("spawn_points").isJsonObject()) {
            errors.add("VehSpawn.json \u7f3a\u5c11 spawn_points \u5bf9\u8c61");
            return new VehSpawnSnapshot(types, points, errors);
        }
        JsonObject spawnRoot = root.getAsJsonObject("spawn_points");
        for (String type : types) {
            if (!spawnRoot.has(type)) {
                errors.add("spawn_points \u7f3a\u5c11\u7c7b\u578b: " + type);
                continue;
            }
            JsonElement typeEl = spawnRoot.get(type);
            ArrayList list = new ArrayList();
            if (typeEl.isJsonArray()) {
                idx = 0;
                for (JsonElement pointEl : typeEl.getAsJsonArray()) {
                    ++idx;
                    if (!pointEl.isJsonObject()) {
                        errors.add(type + " \u7684\u7b2c " + idx + " \u4e2a\u51fa\u751f\u70b9\u4e0d\u662f\u5bf9\u8c61");
                        continue;
                    }
                    Optional<SpawnPoint> parsed = VehSpawnSnapshot.parsePoint(type, pointEl.getAsJsonObject(), idx, errors);
                    parsed.ifPresent(list::add);
                }
            } else if (typeEl.isJsonObject()) {
                idx = 0;
                for (Map.Entry entry : typeEl.getAsJsonObject().entrySet()) {
                    ++idx;
                    if (!((JsonElement)entry.getValue()).isJsonObject()) {
                        errors.add(type + "." + (String)entry.getKey() + " \u4e0d\u662f\u5bf9\u8c61");
                        continue;
                    }
                    JsonObject obj = ((JsonElement)entry.getValue()).getAsJsonObject();
                    if (!obj.has("id")) {
                        obj.addProperty("id", (String)entry.getKey());
                    }
                    Optional<SpawnPoint> parsed = VehSpawnSnapshot.parsePoint(type, obj, idx, errors);
                    parsed.ifPresent(list::add);
                }
            } else {
                errors.add("spawn_points." + type + " \u5fc5\u987b\u662f\u6570\u7ec4\u6216\u5bf9\u8c61");
            }
            points.put(type, list);
        }
        return new VehSpawnSnapshot(types, points, errors);
    }

    private static Optional<SpawnPoint> parsePoint(String type, JsonObject obj, int idx, List<String> errors) {
        String id = obj.has("id") && obj.get("id").isJsonPrimitive() ? obj.get("id").getAsString() : type + "_" + idx;
        Optional<Pose> attack = VehSpawnSnapshot.parsePose(obj, "attack");
        Optional<Pose> defend = VehSpawnSnapshot.parsePose(obj, "defend");
        if (attack.isEmpty()) {
            errors.add(type + "/" + id + " \u7f3a\u5c11\u6709\u6548 attack \u5750\u6807");
        }
        if (defend.isEmpty()) {
            errors.add(type + "/" + id + " \u7f3a\u5c11\u6709\u6548 defend \u5750\u6807");
        }
        if (attack.isEmpty() || defend.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new SpawnPoint(id, attack.get(), defend.get()));
    }

    private static Optional<Pose> parsePose(JsonObject parent, String key) {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            return Optional.empty();
        }
        JsonObject p = parent.getAsJsonObject(key);
        if (!(p.has("x") && p.has("y") && p.has("z"))) {
            return Optional.empty();
        }
        try {
            double x = p.get("x").getAsDouble();
            double y = p.get("y").getAsDouble();
            double z = p.get("z").getAsDouble();
            float yaw = p.has("yaw") ? p.get("yaw").getAsFloat() : 0.0f;
            return Optional.of(new Pose(x, y, z, yaw));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    public record SpawnPoint(String id, Pose attack, Pose defend) {
    }

    public record Pose(double x, double y, double z, float yaw) {
    }
}

