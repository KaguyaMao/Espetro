/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package com.example.espoints.objective;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public final class ObjectiveLayout {
    private static final Gson GSON = new Gson();
    public static final int MIN_RAAS_STAGES = 3;
    public static final int MAX_RAAS_STAGES = 26;
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z0-9_.-]{1,64}");
    private final JsonObject source;
    private final Mode mode;
    private final Map<String, JsonObject> points;
    private final List<Lane> lanes;

    private ObjectiveLayout(JsonObject source, Mode mode, Map<String, JsonObject> points, List<Lane> lanes) {
        this.source = source.deepCopy();
        this.mode = mode;
        this.points = Map.copyOf(points);
        this.lanes = List.copyOf(lanes);
    }

    public static ObjectiveLayout parse(JsonObject source) {
        if (source == null) {
            throw new IllegalArgumentException("\u636e\u70b9\u914d\u7f6e\u6839\u8282\u70b9\u4e0d\u80fd\u4e3a\u7a7a");
        }
        Mode mode = ObjectiveLayout.readMode(source);
        if (mode != Mode.RAAS) {
            ObjectiveLayout.validateAas(source);
        }
        LinkedHashMap<String, JsonObject> points = new LinkedHashMap<String, JsonObject>();
        ArrayList<Lane> lanes = new ArrayList<Lane>();
        if (mode != Mode.AAS) {
            JsonObject raas = ObjectiveLayout.requireObject(source, "raas");
            ObjectiveLayout.readPointPool(raas, points);
            ObjectiveLayout.readLanes(raas, points.keySet(), lanes);
        }
        return new ObjectiveLayout(source, mode, points, lanes);
    }

    public Selection select(long seed) {
        Random random = new Random(seed);
        if (this.mode == Mode.AAS) {
            JsonObject round = this.source.deepCopy();
            ObjectiveLayout.stripRouteConfig(round);
            return new Selection("AAS", "", seed, GSON.toJson((JsonElement)round));
        }
        Lane lane = this.lanes.get(random.nextInt(this.lanes.size()));
        JsonArray planned = new JsonArray();
        int letter = 0;
        int stageNumber = 1;
        for (List<String> stage : lane.stages) {
            for (String pointId : stage) {
                if (letter >= 26) {
                    throw new IllegalStateException("RAAS \u8def\u7ebf " + lane.id + " \u5c55\u5f00\u540e\u8d85\u8fc7 26 \u4e2a\u636e\u70b9");
                }
                JsonObject point = this.points.get(pointId).deepCopy();
                point.remove("id");
                point.addProperty("name", String.valueOf((char)(65 + letter++)));
                point.addProperty("batch", (Number)stageNumber);
                planned.add((JsonElement)point);
            }
            ++stageNumber;
        }
        JsonObject round = this.source.deepCopy();
        ObjectiveLayout.stripRouteConfig(round);
        round.addProperty("totalBatches", (Number)lane.stages.size());
        round.addProperty("raasFrontline", Boolean.valueOf(true));
        round.remove("raasSymmetric");
        round.add("plannedPoints", (JsonElement)planned);
        return new Selection("RAAS", lane.id, seed, GSON.toJson((JsonElement)round));
    }

    public Mode mode() {
        return this.mode;
    }

    private static Mode readMode(JsonObject source) {
        if (!source.has("objectiveMode")) {
            return Mode.AAS;
        }
        String raw = source.get("objectiveMode").getAsString().trim().toUpperCase(Locale.ROOT);
        if ("AAS".equals(raw) || raw.isEmpty()) {
            return Mode.AAS;
        }
        if ("RAAS".equals(raw)) {
            return Mode.RAAS;
        }
        throw new IllegalArgumentException("objectiveMode \u53ea\u80fd\u662f AAS \u6216 RAAS\uff08\u5df2\u79fb\u9664 RANDOM\uff09: " + raw);
    }

    private static void validateAas(JsonObject source) {
        JsonArray planned = ObjectiveLayout.requireArray(source, "plannedPoints");
        if (planned.isEmpty()) {
            throw new IllegalArgumentException("plannedPoints \u4e0d\u80fd\u4e3a\u7a7a");
        }
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        for (int i = 0; i < planned.size(); ++i) {
            JsonObject point = ObjectiveLayout.requireObject(planned.get(i), "plannedPoints[" + i + "]");
            String name = ObjectiveLayout.requireString(point, "name", "plannedPoints[" + i + "]");
            if (!names.add(name)) {
                throw new IllegalArgumentException("\u636e\u70b9\u914d\u7f6e\u5305\u542b\u91cd\u590d\u636e\u70b9\u540d\u79f0: " + name);
            }
            ObjectiveLayout.validateArea(point, "plannedPoints[" + i + "]");
        }
    }

    private static void readPointPool(JsonObject raas, Map<String, JsonObject> points) {
        JsonArray array = ObjectiveLayout.requireArray(raas, "points");
        for (int i = 0; i < array.size(); ++i) {
            JsonObject point = ObjectiveLayout.requireObject(array.get(i), "raas.points[" + i + "]");
            String id = ObjectiveLayout.requireString(point, "id", "raas.points[" + i + "]");
            if (!ID_PATTERN.matcher(id).matches()) {
                throw new IllegalArgumentException("\u975e\u6cd5 RAAS \u636e\u70b9 id: " + id);
            }
            if (points.putIfAbsent(id, point.deepCopy()) != null) {
                throw new IllegalArgumentException("\u91cd\u590d RAAS \u636e\u70b9 id: " + id);
            }
            ObjectiveLayout.validateArea(point, "raas.points[" + i + "]");
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("raas.points \u4e0d\u80fd\u4e3a\u7a7a");
        }
    }

    private static void readLanes(JsonObject raas, Set<String> pointIds, List<Lane> lanes) {
        JsonArray array = ObjectiveLayout.requireArray(raas, "lanes");
        LinkedHashSet<String> laneIds = new LinkedHashSet<String>();
        for (int i = 0; i < array.size(); ++i) {
            JsonObject object = ObjectiveLayout.requireObject(array.get(i), "raas.lanes[" + i + "]");
            String id = ObjectiveLayout.requireString(object, "id", "raas.lanes[" + i + "]");
            if (!ID_PATTERN.matcher(id).matches()) {
                throw new IllegalArgumentException("\u975e\u6cd5 RAAS \u8def\u7ebf id: " + id);
            }
            if (!laneIds.add(id)) {
                throw new IllegalArgumentException("\u91cd\u590d RAAS \u8def\u7ebf id: " + id);
            }
            JsonArray stageArray = ObjectiveLayout.requireArray(object, "stages");
            if (stageArray.size() < 3 || stageArray.size() > 26) {
                throw new IllegalArgumentException("RAAS \u8def\u7ebf " + id + " \u5fc5\u987b\u5305\u542b 3 \u5230 26 \u4e2a\u9636\u6bb5");
            }
            ArrayList stages = new ArrayList();
            LinkedHashSet<String> used = new LinkedHashSet<String>();
            int totalPoints = 0;
            for (int stageIndex = 0; stageIndex < stageArray.size(); ++stageIndex) {
                JsonElement stageElement = stageArray.get(stageIndex);
                if (!stageElement.isJsonArray() || stageElement.getAsJsonArray().isEmpty()) {
                    throw new IllegalArgumentException("RAAS \u8def\u7ebf " + id + " \u7684\u9636\u6bb5 " + (stageIndex + 1) + " \u4e0d\u80fd\u4e3a\u7a7a");
                }
                ArrayList<String> stagePoints = new ArrayList<String>();
                for (JsonElement choice : stageElement.getAsJsonArray()) {
                    String pointId = choice.getAsString();
                    if (!pointIds.contains(pointId)) {
                        throw new IllegalArgumentException("RAAS \u8def\u7ebf " + id + " \u5f15\u7528\u4e86\u4e0d\u5b58\u5728\u7684\u636e\u70b9: " + pointId);
                    }
                    if (!used.add(pointId)) {
                        throw new IllegalArgumentException("RAAS \u8def\u7ebf " + id + " \u91cd\u590d\u5f15\u7528\u636e\u70b9: " + pointId);
                    }
                    stagePoints.add(pointId);
                }
                if ((totalPoints += stagePoints.size()) > 26) {
                    throw new IllegalArgumentException("RAAS \u8def\u7ebf " + id + " \u5c55\u5f00\u540e\u636e\u70b9\u6570\u4e0d\u80fd\u8d85\u8fc7 26");
                }
                stages.add(List.copyOf(stagePoints));
            }
            lanes.add(new Lane(id, List.copyOf(stages)));
        }
        if (lanes.isEmpty()) {
            throw new IllegalArgumentException("raas.lanes \u4e0d\u80fd\u4e3a\u7a7a");
        }
    }

    private static void validateArea(JsonObject point, String path) {
        ObjectiveLayout.validatePosition(point.get("pos1"), path + ".pos1");
        ObjectiveLayout.validatePosition(point.get("pos2"), path + ".pos2");
    }

    private static void validatePosition(JsonElement element, String path) {
        if (element != null && element.isJsonArray()) {
            JsonArray position = element.getAsJsonArray();
            if (position.size() != 3) {
                throw new IllegalArgumentException(path + " \u5fc5\u987b\u5305\u542b x\u3001y\u3001z \u4e09\u4e2a\u5750\u6807");
            }
            for (JsonElement coordinate : position) {
                if (coordinate.isJsonPrimitive() && coordinate.getAsJsonPrimitive().isNumber()) continue;
                throw new IllegalArgumentException(path + " \u5750\u6807\u5fc5\u987b\u662f\u6570\u5b57");
            }
            return;
        }
        if (element == null || !element.isJsonObject()) {
            throw new IllegalArgumentException(path + " \u5fc5\u987b\u662f\u5750\u6807\u5bf9\u8c61\u6216 [x,y,z] \u6570\u7ec4");
        }
        JsonObject position = element.getAsJsonObject();
        for (String axis : List.of("x", "y", "z")) {
            if (position.has(axis) && position.get(axis).isJsonPrimitive() && position.getAsJsonPrimitive(axis).isNumber()) continue;
            throw new IllegalArgumentException(path + "." + axis + " \u5fc5\u987b\u662f\u6570\u5b57");
        }
    }

    private static JsonObject requireObject(JsonObject parent, String key) {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            throw new IllegalArgumentException(key + " \u5fc5\u987b\u662f\u5bf9\u8c61");
        }
        return parent.getAsJsonObject(key);
    }

    private static JsonObject requireObject(JsonElement element, String path) {
        if (element == null || !element.isJsonObject()) {
            throw new IllegalArgumentException(path + " \u5fc5\u987b\u662f\u5bf9\u8c61");
        }
        return element.getAsJsonObject();
    }

    private static JsonArray requireArray(JsonObject parent, String key) {
        if (!parent.has(key) || !parent.get(key).isJsonArray()) {
            throw new IllegalArgumentException(key + " \u5fc5\u987b\u662f\u6570\u7ec4");
        }
        return parent.getAsJsonArray(key);
    }

    private static String requireString(JsonObject object, String key, String path) {
        if (!(object.has(key) && object.get(key).isJsonPrimitive() && object.getAsJsonPrimitive(key).isString())) {
            throw new IllegalArgumentException(path + "." + key + " \u5fc5\u987b\u662f\u5b57\u7b26\u4e32");
        }
        String value = object.get(key).getAsString().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(path + "." + key + " \u4e0d\u80fd\u4e3a\u7a7a");
        }
        return value;
    }

    private static void stripRouteConfig(JsonObject root) {
        root.remove("objectiveMode");
        root.remove("raas");
    }

    public static enum Mode {
        AAS,
        RAAS;

    }

    public record Selection(String mode, String laneId, long seed, String capturePointsJson) {
    }

    private record Lane(String id, List<List<String>> stages) {
    }
}

