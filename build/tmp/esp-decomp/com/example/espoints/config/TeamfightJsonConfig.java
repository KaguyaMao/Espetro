/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.example.espoints.config;

import com.example.espoints.capturepoint.CapturePoint;
import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.objective.ObjectiveLayout;
import com.example.espoints.util.EspetroTeamBridge;
import com.example.espoints.util.ModLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class TeamfightJsonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIRECTORY = "espoints";
    private static final int DEFAULT_REINFORCEMENTS = 50;
    private static final int DEFAULT_ATTACK_BATCH_COMPLETION_REINFORCEMENT = 200;
    private static final int DEFAULT_CAPTURE_REINFORCEMENT = 50;
    private static final int DEFAULT_TICKET_BLEED_PER_SECOND = 1;
    private static final int MAX_POINTS_PER_BATCH = 7;
    private static final int MAX_RAAS_POINTS = 26;
    private static String frozenMapId;
    private static String frozenJson;
    private static long frozenSeed;
    private static String lastSelectedLaneId;

    private TeamfightJsonConfig() {
    }

    public static LoadResult loadConfig() {
        return TeamfightJsonConfig.loadConfig(false);
    }

    public static LoadResult loadConfig(boolean allowWhileRunning) {
        CapturePointManager manager = CapturePointManager.getInstance();
        Path configPath = TeamfightJsonConfig.getConfigPath();
        if (manager.isOperationModeRunning() && !allowWhileRunning) {
            return LoadResult.failure(configPath, "\u884c\u52a8\u6b63\u5728\u8fdb\u884c\uff0c\u672a\u52a0\u8f7d\u884c\u52a8\u6a21\u5f0fJSON\u914d\u7f6e");
        }
        if (manager.isOperationModeRunning()) {
            manager.stopOperationMode();
        }
        if (frozenJson == null || frozenJson.isBlank()) {
            return LoadResult.failure(configPath, "\u65e0\u6d3b\u52a8\u6218\u573a\uff1a\u636e\u70b9\u914d\u7f6e\u4ec5\u6765\u81ea EsWorld/<map>/Points \u9884\u8bbe\u5feb\u7167\uff0c\u8bf7\u5148\u7531 Espetro \u88c5\u8f7d\u5730\u56fe");
        }
        try {
            TeamfightConfig config = TeamfightJsonConfig.parseJson(frozenJson, frozenSeed);
            TeamfightJsonConfig.applyConfig(manager, config);
            ModLogger.debug("\u5df2\u6062\u590d\u5730\u56fe " + frozenMapId + " \u7684\u542f\u52a8\u5feb\u7167\uff0c\u8ba1\u5212\u636e\u70b9 " + config.points.size() + " \u4e2a");
            return LoadResult.success(configPath, config.points.size(), config.totalBatches, config.endBehavior);
        }
        catch (RuntimeException e) {
            ModLogger.error("\u52a0\u8f7d\u884c\u52a8\u6a21\u5f0fJSON\u914d\u7f6e\u5931\u8d25: " + e.getMessage());
            return LoadResult.failure(configPath, e.getMessage());
        }
    }

    public static synchronized LoadResult loadFrozenSnapshot(String mapId, String json) {
        return TeamfightJsonConfig.loadFrozenSnapshot(mapId, json, ThreadLocalRandom.current().nextLong());
    }

    public static synchronized LoadResult loadFrozenSnapshot(String mapId, String json, long seed) {
        Path source = TeamfightJsonConfig.snapshotPath(mapId);
        try {
            LayoutResolution resolution = TeamfightJsonConfig.resolveLayout(TeamfightJsonConfig.parseRootObject(json), seed);
            lastSelectedLaneId = resolution.laneId;
            TeamfightConfig config = TeamfightJsonConfig.parseConfig(resolution.root);
            CapturePointManager manager = CapturePointManager.getInstance();
            if (manager.isOperationModeRunning()) {
                manager.stopOperationMode();
            }
            frozenMapId = mapId == null || mapId.isBlank() ? "unknown" : mapId;
            frozenSeed = seed;
            frozenJson = GSON.toJson((JsonElement)resolution.root);
            TeamfightJsonConfig.applyConfig(manager, config);
            ModLogger.info("\u5df2\u8f7d\u5165\u5730\u56fe " + frozenMapId + " \u7684\u636e\u70b9\u914d\u7f6e\uff0c\u5171 " + config.points.size() + " \u4e2a\u8ba1\u5212\u636e\u70b9" + (config.raasFrontline ? "\uff08RAAS \u5bf9\u5411\u63a8\u7ebf\uff09" : "") + (String)(lastSelectedLaneId.isEmpty() ? "" : "\uff0c\u8def\u7ebf=" + lastSelectedLaneId));
            return LoadResult.success(source, config.points.size(), config.totalBatches, config.endBehavior);
        }
        catch (RuntimeException e) {
            return LoadResult.failure(source, e.getMessage());
        }
    }

    public static synchronized String getLastSelectedLaneId() {
        return lastSelectedLaneId == null ? "" : lastSelectedLaneId;
    }

    public static synchronized void clearFrozenSnapshot() {
        frozenMapId = null;
        frozenJson = null;
        frozenSeed = 0L;
        lastSelectedLaneId = "";
    }

    public static LoadResult saveCurrentConfig() {
        CapturePointManager manager = CapturePointManager.getInstance();
        String exportName = (frozenMapId == null ? "inactive" : frozenMapId).replaceAll("[^a-zA-Z0-9_-]", "_");
        Path configPath = TeamfightJsonConfig.getExportDirectory().resolve(exportName + "-CapturePoints.json");
        try {
            Files.createDirectories(configPath.getParent(), new FileAttribute[0]);
            TeamfightConfig config = TeamfightJsonConfig.createConfigFromManager(manager);
            TeamfightJsonConfig.writeConfig(configPath, config);
            ModLogger.info("\u5f53\u524d\u636e\u70b9\u72b6\u6001\u5df2\u5bfc\u51fa: " + String.valueOf(configPath) + "\uff0c\u8ba1\u5212\u636e\u70b9 " + config.points.size() + " \u4e2a\uff1b\u6d3b\u52a8\u5730\u56fe\u6a21\u677f\u672a\u88ab\u4fee\u6539");
            return LoadResult.success(configPath, config.points.size(), config.totalBatches, config.endBehavior);
        }
        catch (IOException e) {
            ModLogger.error("\u4fdd\u5b58\u884c\u52a8\u6a21\u5f0fJSON\u914d\u7f6e\u5931\u8d25: " + e.getMessage());
            return LoadResult.failure(configPath, e.getMessage());
        }
    }

    private static void applyConfig(CapturePointManager manager, TeamfightConfig config) {
        manager.clearPlannedCapturePoints();
        manager.clearTeamRoles();
        int attackReinforcements = config.teamReinforcements.getOrDefault("ATTACK", 50);
        int defendReinforcements = config.teamReinforcements.getOrDefault("DEFEND", 50);
        manager.setTeamRole("ATTACK", "attacker", attackReinforcements);
        manager.setTeamRole("DEFEND", "defender", defendReinforcements);
        manager.setTotalBatches(config.totalBatches);
        manager.setEndBehavior(config.endBehavior);
        manager.setRaasFrontline(config.raasFrontline);
        manager.setCaptureReinforcement(config.captureReinforcement);
        manager.setTicketBleedPerSecond(config.ticketBleedPerSecond);
        manager.setAttackBatchCompletionReinforcement(config.attackBatchCompletionReinforcement);
        for (PlannedPointConfig point : config.points) {
            if (manager.addPlannedCapturePoint(point.name, point.pos1, point.pos2, point.batch)) continue;
            throw new IllegalArgumentException("\u8ba1\u5212\u636e\u70b9\u6dfb\u52a0\u5931\u8d25: " + point.name);
        }
        manager.syncToAllClients();
    }

    private static TeamfightConfig parseJson(String json, long seed) {
        return TeamfightJsonConfig.parseConfig(TeamfightJsonConfig.resolveLayout((JsonObject)TeamfightJsonConfig.parseRootObject((String)json), (long)seed).root);
    }

    private static JsonObject parseRootObject(String json) {
        JsonElement rootElement = JsonParser.parseString((String)json);
        if (rootElement == null || !rootElement.isJsonObject()) {
            throw new JsonParseException("\u914d\u7f6e\u6839\u8282\u70b9\u5fc5\u987b\u662fJSON\u5bf9\u8c61");
        }
        return rootElement.getAsJsonObject();
    }

    private static LayoutResolution resolveLayout(JsonObject root, long seed) {
        String normalized;
        boolean hasRawRaas = root.has("raas") && root.get("raas").isJsonObject();
        boolean raasFrontline = TeamfightJsonConfig.isRaasFrontlineFlag(root);
        String mode = TeamfightJsonConfig.getOptionalString(root, "objectiveMode", "AAS");
        if (mode == null || mode.isBlank()) {
            mode = "AAS";
        }
        if ("RANDOM".equalsIgnoreCase(normalized = mode.trim())) {
            throw new JsonParseException("objectiveMode \u53ea\u80fd\u662f AAS \u6216 RAAS\uff08\u5df2\u79fb\u9664 RANDOM\uff09");
        }
        boolean isRaas = "RAAS".equalsIgnoreCase(normalized);
        if (hasRawRaas && (isRaas || raasFrontline)) {
            try {
                ObjectiveLayout.Selection selection = ObjectiveLayout.parse(root).select(seed);
                JsonObject reduced = JsonParser.parseString((String)selection.capturePointsJson()).getAsJsonObject();
                return new LayoutResolution(reduced, selection.laneId() == null ? "" : selection.laneId());
            }
            catch (IllegalArgumentException e) {
                throw new JsonParseException(e.getMessage(), (Throwable)e);
            }
        }
        if (raasFrontline) {
            return new LayoutResolution(TeamfightJsonConfig.markRaasFrontlineReduced(root), "");
        }
        return new LayoutResolution(root, "");
    }

    private static JsonObject markRaasFrontlineReduced(JsonObject root) {
        JsonObject copy = root.deepCopy();
        copy.addProperty("raasFrontline", Boolean.valueOf(true));
        copy.remove("raasSymmetric");
        copy.remove("objectiveMode");
        copy.remove("raas");
        return copy;
    }

    private static boolean isRaasFrontlineFlag(JsonObject root) {
        return TeamfightJsonConfig.getOptionalBoolean(root, "raasFrontline", false) || TeamfightJsonConfig.getOptionalBoolean(root, "raasSymmetric", false);
    }

    private static TeamfightConfig parseConfig(JsonObject root) {
        boolean raasFrontline = TeamfightJsonConfig.isRaasFrontlineFlag(root);
        List<PlannedPointConfig> points = TeamfightJsonConfig.parsePoints(root);
        TeamfightJsonConfig.validatePoints(points, raasFrontline);
        int calculatedBatches = points.stream().mapToInt(point -> point.batch).max().orElse(1);
        int totalBatches = TeamfightJsonConfig.getOptionalInt(root, "totalBatches", calculatedBatches);
        if (totalBatches < 1) {
            throw new JsonParseException("totalBatches \u5fc5\u987b\u5927\u4e8e\u7b49\u4e8e 1");
        }
        if (calculatedBatches > totalBatches) {
            throw new JsonParseException("totalBatches \u5c0f\u4e8e\u8ba1\u5212\u636e\u70b9\u6700\u5927\u6279\u6b21: " + calculatedBatches);
        }
        String endBehavior = TeamfightJsonConfig.getOptionalString(root, "endBehavior", "terminate").toLowerCase(Locale.ROOT);
        if (!"terminate".equals(endBehavior) && !"loop".equals(endBehavior)) {
            throw new JsonParseException("endBehavior \u53ea\u80fd\u662f terminate \u6216 loop");
        }
        int attackBatchReward = TeamfightJsonConfig.parseAttackBatchCompletionReinforcement(root);
        int captureReinforcement = TeamfightJsonConfig.parseCaptureReinforcement(root);
        int ticketBleed = TeamfightJsonConfig.parseTicketBleedPerSecond(root);
        return new TeamfightConfig(totalBatches, endBehavior, TeamfightJsonConfig.parseTeamReinforcements(root), points, attackBatchReward, raasFrontline, captureReinforcement, ticketBleed);
    }

    private static int parseTicketBleedPerSecond(JsonObject root) {
        JsonElement element = TeamfightJsonConfig.firstPresent(root, "ticketBleedPerSecond", "bleedPerSecond");
        if (element == null || element.isJsonNull()) {
            return 1;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException("ticketBleedPerSecond \u5fc5\u987b\u662f\u6570\u5b57");
        }
        return Math.max(0, element.getAsInt());
    }

    private static int parseAttackBatchCompletionReinforcement(JsonObject root) {
        JsonElement element = TeamfightJsonConfig.firstPresent(root, "attackBatchCompletionReinforcement", "batchCompletionAttackReinforcement", "attackCaptureReinforcement");
        if (element == null || element.isJsonNull()) {
            return 200;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException("attackBatchCompletionReinforcement \u5fc5\u987b\u662f\u6570\u5b57");
        }
        return Math.max(0, element.getAsInt());
    }

    private static int parseCaptureReinforcement(JsonObject root) {
        JsonElement element = TeamfightJsonConfig.firstPresent(root, "captureReinforcement", "attackBatchCompletionReinforcement", "batchCompletionAttackReinforcement", "attackCaptureReinforcement");
        if (element == null || element.isJsonNull()) {
            return 50;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException("captureReinforcement \u5fc5\u987b\u662f\u6570\u5b57");
        }
        return Math.max(0, element.getAsInt());
    }

    private static List<PlannedPointConfig> parsePoints(JsonObject root) {
        JsonElement pointsElement = TeamfightJsonConfig.firstPresent(root, "plannedPoints", "points", "capturePoints");
        ArrayList<PlannedPointConfig> points = new ArrayList<PlannedPointConfig>();
        if (pointsElement == null || pointsElement.isJsonNull()) {
            return points;
        }
        if (pointsElement.isJsonArray()) {
            for (JsonElement element : pointsElement.getAsJsonArray()) {
                points.add(TeamfightJsonConfig.parsePoint(null, element));
            }
            return points;
        }
        if (pointsElement.isJsonObject()) {
            for (Map.Entry entry : pointsElement.getAsJsonObject().entrySet()) {
                points.add(TeamfightJsonConfig.parsePoint((String)entry.getKey(), (JsonElement)entry.getValue()));
            }
            return points;
        }
        throw new JsonParseException("plannedPoints \u5fc5\u987b\u662f\u6570\u7ec4\u6216\u5bf9\u8c61");
    }

    private static PlannedPointConfig parsePoint(String fallbackName, JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            throw new JsonParseException("\u8ba1\u5212\u636e\u70b9\u5fc5\u987b\u662fJSON\u5bf9\u8c61");
        }
        JsonObject point = element.getAsJsonObject();
        String name = TeamfightJsonConfig.getOptionalString(point, "name", fallbackName);
        if (name != null) {
            name = name.trim().toUpperCase(Locale.ROOT);
        }
        int batch = TeamfightJsonConfig.getRequiredInt(point, "batch");
        BlockPos pos1 = TeamfightJsonConfig.parseBlockPos(TeamfightJsonConfig.firstPresent(point, "pos1", "from"));
        BlockPos pos2 = TeamfightJsonConfig.parseBlockPos(TeamfightJsonConfig.firstPresent(point, "pos2", "to"));
        return new PlannedPointConfig(name, batch, pos1, pos2);
    }

    private static void validatePoints(List<PlannedPointConfig> points, boolean raasFrontline) {
        HashSet<String> names = new HashSet<String>();
        HashMap<Integer, Integer> pointsPerBatch = new HashMap<Integer, Integer>();
        int maxPerBatch = raasFrontline ? 26 : 7;
        CapturePointManager manager = CapturePointManager.getInstance();
        for (PlannedPointConfig point : points) {
            if (!manager.isValidPointName(point.name)) {
                throw new JsonParseException("\u636e\u70b9\u540d\u79f0\u5fc5\u987b\u4e3a\u5355\u4e2a\u5927\u5199\u5b57\u6bcd(A-Z): " + point.name);
            }
            if (point.batch < 1) {
                throw new JsonParseException("\u636e\u70b9 " + point.name + " \u7684 batch \u5fc5\u987b\u5927\u4e8e\u7b49\u4e8e 1");
            }
            if (!manager.isValidCoordinates(point.pos1, point.pos2)) {
                throw new JsonParseException("\u636e\u70b9 " + point.name + " \u7684 pos1/pos2 \u5fc5\u987b\u6784\u6210\u6709\u6548\u957f\u65b9\u4f53\u533a\u57df");
            }
            if (!names.add(point.name)) {
                throw new JsonParseException("\u5b58\u5728\u91cd\u590d\u636e\u70b9\u540d\u79f0: " + point.name);
            }
            int batchCount = pointsPerBatch.merge(point.batch, 1, Integer::sum);
            if (batchCount <= maxPerBatch) continue;
            throw new JsonParseException("\u6279\u6b21 " + point.batch + " \u7684\u636e\u70b9\u6570\u91cf\u8d85\u8fc7\u4e0a\u9650 " + maxPerBatch);
        }
    }

    private static Map<String, Integer> parseTeamReinforcements(JsonObject root) {
        Map<String, Integer> reinforcements = TeamfightJsonConfig.defaultReinforcements();
        JsonElement element = root.get("teamReinforcements");
        if (element != null && !element.isJsonNull()) {
            if (!element.isJsonObject()) {
                throw new JsonParseException("teamReinforcements \u5fc5\u987b\u662fJSON\u5bf9\u8c61");
            }
            for (Map.Entry entry : element.getAsJsonObject().entrySet()) {
                String team = EspetroTeamBridge.canonicalizeTeamName((String)entry.getKey());
                if (team == null) {
                    throw new JsonParseException("\u672a\u77e5\u961f\u4f0d\u540d\u79f0: " + (String)entry.getKey());
                }
                reinforcements.put(team, TeamfightJsonConfig.parsePositiveInt((JsonElement)entry.getValue(), "teamReinforcements." + (String)entry.getKey()));
            }
        }
        TeamfightJsonConfig.putOptionalReinforcement(root, reinforcements, "attackReinforcements", "ATTACK");
        TeamfightJsonConfig.putOptionalReinforcement(root, reinforcements, "attackerReinforcements", "ATTACK");
        TeamfightJsonConfig.putOptionalReinforcement(root, reinforcements, "defendReinforcements", "DEFEND");
        TeamfightJsonConfig.putOptionalReinforcement(root, reinforcements, "defenderReinforcements", "DEFEND");
        return reinforcements;
    }

    private static void putOptionalReinforcement(JsonObject root, Map<String, Integer> reinforcements, String key, String team) {
        if (root.has(key) && !root.get(key).isJsonNull()) {
            reinforcements.put(team, TeamfightJsonConfig.parsePositiveInt(root.get(key), key));
        }
    }

    private static int parsePositiveInt(JsonElement element, String path) {
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException(path + " \u5fc5\u987b\u662f\u6570\u5b57");
        }
        int value = element.getAsInt();
        if (value <= 0) {
            throw new JsonParseException(path + " \u5fc5\u987b\u5927\u4e8e 0");
        }
        return value;
    }

    private static TeamfightConfig createConfigFromManager(CapturePointManager manager) {
        String endBehavior;
        ArrayList<PlannedPointConfig> points = new ArrayList<PlannedPointConfig>();
        HashSet<String> seenNames = new HashSet<String>();
        for (CapturePoint.SerializableCapturePoint point2 : manager.getOverviewSerializablePoints()) {
            if (!seenNames.add(point2.name)) continue;
            points.add(new PlannedPointConfig(point2.name, point2.batch, point2.pos1, point2.pos2));
        }
        points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        int totalBatches = manager.getTotalBatches();
        if (totalBatches <= 0) {
            totalBatches = Math.max(1, manager.calculateTotalBatches());
        }
        if ((endBehavior = manager.getEndBehavior()) == null || !"terminate".equalsIgnoreCase(endBehavior) && !"loop".equalsIgnoreCase(endBehavior)) {
            endBehavior = "terminate";
        }
        Map<String, Integer> reinforcements = TeamfightJsonConfig.defaultReinforcements();
        reinforcements.put("ATTACK", TeamfightJsonConfig.positiveOrDefault(manager.getTeamInitialReinforcements("ATTACK"), manager.getTeamReinforcements("ATTACK")));
        reinforcements.put("DEFEND", TeamfightJsonConfig.positiveOrDefault(manager.getTeamInitialReinforcements("DEFEND"), manager.getTeamReinforcements("DEFEND")));
        return new TeamfightConfig(totalBatches, endBehavior.toLowerCase(Locale.ROOT), reinforcements, points, manager.getAttackBatchCompletionReinforcement(), manager.isRaasFrontline(), manager.getCaptureReinforcement(), manager.getTicketBleedPerSecond());
    }

    private static int positiveOrDefault(int preferred, int fallback) {
        if (preferred > 0) {
            return preferred;
        }
        if (fallback > 0) {
            return fallback;
        }
        return 50;
    }

    private static void writeConfig(Path configPath, TeamfightConfig config) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8, new OpenOption[0]);){
            GSON.toJson((JsonElement)TeamfightJsonConfig.toJsonObject(config), (Appendable)writer);
        }
    }

    private static JsonObject toJsonObject(TeamfightConfig config) {
        JsonObject root = new JsonObject();
        root.addProperty("totalBatches", (Number)config.totalBatches);
        root.addProperty("endBehavior", config.endBehavior);
        root.addProperty("attackBatchCompletionReinforcement", (Number)config.attackBatchCompletionReinforcement);
        root.addProperty("captureReinforcement", (Number)config.captureReinforcement);
        root.addProperty("ticketBleedPerSecond", (Number)config.ticketBleedPerSecond);
        if (config.raasFrontline) {
            root.addProperty("raasFrontline", Boolean.valueOf(true));
        }
        JsonObject reinforcements = new JsonObject();
        reinforcements.addProperty("ATTACK", (Number)config.teamReinforcements.getOrDefault("ATTACK", 50));
        reinforcements.addProperty("DEFEND", (Number)config.teamReinforcements.getOrDefault("DEFEND", 50));
        root.add("teamReinforcements", (JsonElement)reinforcements);
        JsonArray points = new JsonArray();
        ArrayList<PlannedPointConfig> sortedPoints = new ArrayList<PlannedPointConfig>(config.points);
        sortedPoints.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        for (PlannedPointConfig point2 : sortedPoints) {
            JsonObject pointJson = new JsonObject();
            pointJson.addProperty("name", point2.name);
            pointJson.addProperty("batch", (Number)point2.batch);
            pointJson.add("pos1", (JsonElement)TeamfightJsonConfig.toJsonObject(point2.pos1));
            pointJson.add("pos2", (JsonElement)TeamfightJsonConfig.toJsonObject(point2.pos2));
            points.add((JsonElement)pointJson);
        }
        root.add("plannedPoints", (JsonElement)points);
        return root;
    }

    private static JsonObject toJsonObject(BlockPos pos) {
        JsonObject object = new JsonObject();
        object.addProperty("x", (Number)pos.m_123341_());
        object.addProperty("y", (Number)pos.m_123342_());
        object.addProperty("z", (Number)pos.m_123343_());
        return object;
    }

    private static BlockPos parseBlockPos(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            throw new JsonParseException("\u5750\u6807\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() != 3) {
                throw new JsonParseException("\u5750\u6807\u6570\u7ec4\u5fc5\u987b\u5305\u542b x/y/z \u4e09\u4e2a\u6570\u5b57");
            }
            return new BlockPos(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            return new BlockPos(TeamfightJsonConfig.getRequiredInt(object, "x"), TeamfightJsonConfig.getRequiredInt(object, "y"), TeamfightJsonConfig.getRequiredInt(object, "z"));
        }
        throw new JsonParseException("\u5750\u6807\u5fc5\u987b\u662f\u5bf9\u8c61\u6216\u6570\u7ec4");
    }

    private static JsonElement firstPresent(JsonObject object, String ... keys) {
        for (String key : keys) {
            if (!object.has(key)) continue;
            return object.get(key);
        }
        return null;
    }

    private static int getRequiredInt(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            throw new JsonParseException("\u7f3a\u5c11\u5fc5\u586b\u6570\u5b57\u5b57\u6bb5: " + key);
        }
        return object.get(key).getAsInt();
    }

    private static int getOptionalInt(JsonObject object, String key, int defaultValue) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return defaultValue;
        }
        return object.get(key).getAsInt();
    }

    private static String getOptionalString(JsonObject object, String key, String defaultValue) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return defaultValue;
        }
        return object.get(key).getAsString();
    }

    private static boolean getOptionalBoolean(JsonObject object, String key, boolean defaultValue) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return defaultValue;
        }
        JsonElement element = object.get(key);
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsBoolean();
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return Boolean.parseBoolean(element.getAsString());
        }
        return defaultValue;
    }

    public static Path getConfigPath() {
        return TeamfightJsonConfig.snapshotPath(frozenMapId);
    }

    private static Path snapshotPath(String mapId) {
        String safeMap = mapId == null || mapId.isBlank() ? "inactive" : mapId.replaceAll("[^a-zA-Z0-9_-]", "_");
        return Paths.get("EsWorld", safeMap, "EsConfig", "CapturePoints.json");
    }

    private static Path getExportDirectory() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Path root = server == null ? Paths.get(".", new String[0]) : server.m_6237_().toPath();
        return root.resolve("config").resolve(CONFIG_DIRECTORY).resolve("exports");
    }

    private static Map<String, Integer> defaultReinforcements() {
        HashMap<String, Integer> reinforcements = new HashMap<String, Integer>();
        reinforcements.put("ATTACK", 50);
        reinforcements.put("DEFEND", 50);
        return reinforcements;
    }

    static {
        lastSelectedLaneId = "";
    }

    public static final class LoadResult {
        private final boolean success;
        private final Path path;
        private final String message;
        private final int plannedPointCount;
        private final int totalBatches;
        private final String endBehavior;

        private LoadResult(boolean success, Path path, String message, int plannedPointCount, int totalBatches, String endBehavior) {
            this.success = success;
            this.path = path;
            this.message = message;
            this.plannedPointCount = plannedPointCount;
            this.totalBatches = totalBatches;
            this.endBehavior = endBehavior;
        }

        private static LoadResult success(Path path, int plannedPointCount, int totalBatches, String endBehavior) {
            return new LoadResult(true, path, "", plannedPointCount, totalBatches, endBehavior);
        }

        private static LoadResult failure(Path path, String message) {
            return new LoadResult(false, path, message == null ? "\u672a\u77e5\u9519\u8bef" : message, 0, 0, "");
        }

        public boolean isSuccess() {
            return this.success;
        }

        public Path getPath() {
            return this.path;
        }

        public String getMessage() {
            return this.message;
        }

        public int getPlannedPointCount() {
            return this.plannedPointCount;
        }

        public int getTotalBatches() {
            return this.totalBatches;
        }

        public String getEndBehavior() {
            return this.endBehavior;
        }
    }

    private static final class TeamfightConfig {
        private final int totalBatches;
        private final String endBehavior;
        private final Map<String, Integer> teamReinforcements;
        private final List<PlannedPointConfig> points;
        private final int attackBatchCompletionReinforcement;
        private final boolean raasFrontline;
        private final int captureReinforcement;
        private final int ticketBleedPerSecond;

        private TeamfightConfig(int totalBatches, String endBehavior, Map<String, Integer> teamReinforcements, List<PlannedPointConfig> points, int attackBatchCompletionReinforcement, boolean raasFrontline, int captureReinforcement, int ticketBleedPerSecond) {
            this.totalBatches = totalBatches;
            this.endBehavior = endBehavior;
            this.teamReinforcements = teamReinforcements;
            this.points = points;
            this.attackBatchCompletionReinforcement = Math.max(0, attackBatchCompletionReinforcement);
            this.raasFrontline = raasFrontline;
            this.captureReinforcement = Math.max(0, captureReinforcement);
            this.ticketBleedPerSecond = Math.max(0, ticketBleedPerSecond);
        }
    }

    private record LayoutResolution(JsonObject root, String laneId) {
    }

    private static final class PlannedPointConfig {
        private final String name;
        private final int batch;
        private final BlockPos pos1;
        private final BlockPos pos2;

        private PlannedPointConfig(String name, int batch, BlockPos pos1, BlockPos pos2) {
            this.name = name;
            this.batch = batch;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }
    }
}

