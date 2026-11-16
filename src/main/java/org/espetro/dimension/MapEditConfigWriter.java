package org.espetro.dimension;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.espetro.dimension.MapEditDraft.Box;
import org.espetro.dimension.MapEditDraft.PointDraft;
import org.espetro.dimension.MapEditDraft.Side;
import org.espetro.dimension.MapEditDraft.Spot;
import org.espetro.dimension.MapEditDraft.VehPoint;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 把 {@link MapEditDraft} 写回 {@code EsWorld/<map>/EsConfig}：
 * {@code CapturePoints.json}（或 {@code Points/<file>.json}）、{@code spawn_points.json}、{@code VehSpawn.json}。
 *
 * <p>写入策略是「读-改-写」：用 Gson 解析原文件，只替换目标键，保留 {@code modes}、
 * {@code _comment}、{@code raas} 等未知键与原有顺序；先写临时文件再原子替换。
 * 写之前把即将改动的文件备份到 {@code <存档>/espetro-map-backups/<map>-EsConfig-<stamp>/}。</p>
 *
 * <p>本类不引用任何 Minecraft 类，便于独立自检。</p>
 */
public final class MapEditConfigWriter {

    public static final String CAPTURE_FILE = "CapturePoints.json";
    public static final String SPAWN_FILE = "spawn_points.json";
    public static final String VEH_FILE = "VehSpawn.json";
    private static final List<String> TYPE_ALIASES = List.of("VehTypes", "vehtypes", "vehicle_types");

    public record Result(boolean ok,
                         @Nullable String error,
                         List<String> written,
                         @Nullable String backup,
                         List<String> notes) {

        public static Result fail(String error) {
            return new Result(false, error, List.of(), null, List.of());
        }
    }

    private MapEditConfigWriter() {
    }

    // ------------------------------------------------------------------
    // 入口
    // ------------------------------------------------------------------

    public static Result write(Path esConfigDir,
                               MapEditDraft draft,
                               Path backupRoot,
                               String stamp) {
        return write(esConfigDir, draft, backupRoot, stamp, false);
    }

    /**
     * @param amphibious 两栖登陆地图：进攻方出生点/进攻侧载具点由舰船提供，
     *                   允许草稿只填防守侧（写入时把缺失一侧按已有侧占位，运行时被舰船标记覆盖）
     */
    public static Result write(Path esConfigDir,
                               MapEditDraft draft,
                               Path backupRoot,
                               String stamp,
                               boolean amphibious) {
        List<String> problems = draft.problems(amphibious);
        if (!problems.isEmpty()) {
            return Result.fail("草稿校验未通过：" + String.join("；", problems));
        }
        List<String> notes = new ArrayList<>();
        List<String> written = new ArrayList<>();
        try {
            Path mapRoot = esConfigDir.getParent();
            Path pointsDir = mapRoot == null ? null : mapRoot.resolve("Points");
            Path captureFile = resolveCaptureFile(esConfigDir, draft.captureTarget());
            if (captureFile == null) {
                return Result.fail("无法解析据点写入目标: " + draft.captureTarget());
            }

            String backup = null;
            if (backupRoot != null) {
                backup = backupFiles(backupRoot, draft.mapFolder(), stamp,
                    Map.of(
                        "capture", captureFile,
                        "spawn", esConfigDir.resolve(SPAWN_FILE),
                        "veh", esConfigDir.resolve(VEH_FILE)),
                    mapRoot, pointsDir);
            }

            writeCapture(captureFile, draft, notes);
            written.add(captureFile.toString());
            writeSpawn(esConfigDir.resolve(SPAWN_FILE), draft, amphibious, notes);
            written.add(esConfigDir.resolve(SPAWN_FILE).toString());
            writeVehSpawn(esConfigDir.resolve(VEH_FILE), draft, notes, amphibious);
            written.add(esConfigDir.resolve(VEH_FILE).toString());

            if (MapEditDraft.hasPresetFiles(pointsDir) && MapEditDraft.LEGACY_TARGET.equals(draft.captureTarget())) {
                notes.add("该地图存在 Points/ 预设，线上会优先用预设；本次写入的 CapturePoints.json 可能不生效。"
                    + "用 /espetro edit capture target 切换到具体预设。");
            }
            notes.add("EsConfig 只在开服时加载，保存后需要重启服务器才会生效。");
            return new Result(true, null, written, backup, notes);
        } catch (IOException | RuntimeException e) {
            return Result.fail("写入 EsConfig 失败: " + e.getMessage());
        }
    }

    /** 可选写入目标：legacy + Points/ 下的每个预设。 */
    public static List<String> captureTargets(Path esConfigDir) {
        List<String> targets = new ArrayList<>();
        targets.add(MapEditDraft.LEGACY_TARGET);
        Path mapRoot = esConfigDir.getParent();
        Path pointsDir = mapRoot == null ? null : mapRoot.resolve("Points");
        if (pointsDir != null && Files.isDirectory(pointsDir)) {
            try (var stream = Files.newDirectoryStream(pointsDir, "*.json")) {
                List<String> names = new ArrayList<>();
                for (Path file : stream) {
                    names.add(file.getFileName().toString());
                }
                names.sort(String::compareTo);
                for (String name : names) {
                    targets.add("Points/" + name);
                }
            } catch (IOException ignored) {
                // 列不出就只给 legacy
            }
        }
        return targets;
    }

    // ------------------------------------------------------------------
    // 据点
    // ------------------------------------------------------------------

    private static @Nullable Path resolveCaptureFile(Path esConfigDir, String target) {
        if (target == null || target.isBlank() || MapEditDraft.LEGACY_TARGET.equalsIgnoreCase(target)) {
            return esConfigDir.resolve(CAPTURE_FILE);
        }
        String name = target.trim();
        if (name.startsWith("Points/") || name.startsWith("Points\\")) {
            name = name.substring("Points/".length());
        }
        if (!name.matches("[A-Za-z0-9_.\\-]+\\.json") || name.contains("..")) {
            return null;
        }
        Path mapRoot = esConfigDir.getParent();
        if (mapRoot == null) {
            return null;
        }
        return mapRoot.resolve("Points").resolve(name);
    }

    private static void writeCapture(Path file, MapEditDraft draft, List<String> notes) throws IOException {
        JsonObject root = readObject(file);
        if (root == null) {
            root = new JsonObject();
            root.addProperty("endBehavior", "terminate");
            JsonObject reinforcements = new JsonObject();
            reinforcements.addProperty("ATTACK", 75);
            reinforcements.addProperty("DEFEND", 350);
            root.add("teamReinforcements", reinforcements);
            notes.add("据点文件不存在，已按最小默认结构新建: " + file.getFileName());
        }
        JsonArray planned = new JsonArray();
        int maxBatch = 0;
        for (PointDraft point : draft.points()) {
            planned.add(point.toPlannedPoint());
            maxBatch = Math.max(maxBatch, point.batch());
        }
        root.add("plannedPoints", planned);
        root.addProperty("totalBatches", Math.max(1, maxBatch));
        writeObject(file, root);
    }

    // ------------------------------------------------------------------
    // 出生点
    // ------------------------------------------------------------------

    private static void writeSpawn(Path file, MapEditDraft draft, boolean amphibious,
                                   List<String> notes) throws IOException {
        JsonObject root = readObject(file);
        if (root == null) {
            root = new JsonObject();
        }
        JsonObject spawnPoints = root.has("spawnPoints") && root.get("spawnPoints").isJsonObject()
            ? root.getAsJsonObject("spawnPoints") : new JsonObject();
        for (Side side : Side.values()) {
            Spot spot = draft.spawn(side);
            if (spot == null) {
                if (amphibious && side == Side.ATTACK) {
                    notes.add("两栖地图：进攻方出生点由舰船 amphibious.json 提供，spawn_points.json 未写 ATTACK。");
                }
                continue;
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("x", spot.x());
            obj.addProperty("y", spot.y());
            obj.addProperty("z", spot.z());
            obj.addProperty("yaw", spot.yaw());
            spawnPoints.add(side.name(), obj);
        }
        root.add("spawnPoints", spawnPoints);
        writeObject(file, root);
    }

    // ------------------------------------------------------------------
    // 载具出生点
    // ------------------------------------------------------------------

    private static void writeVehSpawn(Path file, MapEditDraft draft, List<String> notes,
                                      boolean amphibious) throws IOException {
        JsonObject root = readObject(file);
        if (root == null) {
            root = new JsonObject();
            notes.add("VehSpawn.json 不存在，已新建");
        }
        String alias = pickTypeAlias(root);
        JsonArray types = root.has(alias) && root.get(alias).isJsonArray()
            ? root.getAsJsonArray(alias) : new JsonArray();
        Set<String> declared = new LinkedHashSet<>();
        for (JsonElement element : types) {
            if (element.isJsonPrimitive()) {
                declared.add(MapEditDraft.normalizeType(element.getAsString()));
            }
        }
        for (String type : draft.vehTypesOrdered()) {
            if (declared.add(type)) {
                types.add(type);
            }
        }
        root.add(alias, types);

        JsonObject spawnRoot = root.has("spawn_points") && root.get("spawn_points").isJsonObject()
            ? root.getAsJsonObject("spawn_points") : new JsonObject();
        int mirrored = 0;
        for (String type : draft.vehTypesOrdered()) {
            List<VehPoint> points = draft.vehPoints(type);
            JsonElement existing = spawnRoot.get(type);
            boolean legacyObject = existing != null && existing.isJsonObject();
            if (legacyObject) {
                JsonObject object = new JsonObject();
                for (VehPoint point : points) {
                    VehPoint written = amphibious ? mirrorOneSided(point) : point;
                    if (written != point) {
                        mirrored++;
                    }
                    object.add(point.id(), pointObject(written));
                }
                spawnRoot.add(type, object);
            } else {
                JsonArray array = new JsonArray();
                for (VehPoint point : points) {
                    VehPoint written = amphibious ? mirrorOneSided(point) : point;
                    if (written != point) {
                        mirrored++;
                    }
                    array.add(pointObject(written));
                }
                spawnRoot.add(type, array);
            }
        }
        if (mirrored > 0) {
            notes.add("两栖地图：" + mirrored + " 个载具点只填了一侧，缺失侧已按已有侧占位；"
                + "开图时进攻侧会被舰船甲板标记覆盖。");
        }
        root.add("spawn_points", spawnRoot);
        writeObject(file, root);
    }

    /**
     * 两栖地图上把只填了一侧的载具点补齐为两侧，保证 VehSpawn.json 仍能被解析器接受；
     * 运行时会用舰船标记/两栖配置覆盖进攻侧，所以这个占位值不会真正生效。
     */
    private static VehPoint mirrorOneSided(VehPoint point) {
        if (point.attack() != null && point.defend() != null) {
            return point;
        }
        if (point.attack() == null && point.defend() == null) {
            return point;
        }
        Spot spot = point.defend() != null ? point.defend() : point.attack();
        return new VehPoint(point.id(), spot, spot);
    }

    private static JsonObject pointObject(VehPoint point) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", point.id());
        if (point.attack() != null) {
            obj.add("attack", MapEditDraft.spotJson(point.attack()));
        }
        if (point.defend() != null) {
            obj.add("defend", MapEditDraft.spotJson(point.defend()));
        }
        return obj;
    }

    private static String pickTypeAlias(JsonObject root) {
        List<String> present = new ArrayList<>();
        for (String alias : TYPE_ALIASES) {
            if (root.has(alias)) {
                present.add(alias);
            }
        }
        // 解析器要求只能出现一个别名字段：保留第一个，移除多余的
        String keep = present.isEmpty() ? TYPE_ALIASES.get(0) : present.get(0);
        for (int i = 1; i < present.size(); i++) {
            root.remove(present.get(i));
        }
        return keep;
    }

    // ------------------------------------------------------------------
    // 备份 / IO
    // ------------------------------------------------------------------

    private static @Nullable String backupFiles(Path backupRoot,
                                                String mapFolder,
                                                String stamp,
                                                Map<String, Path> files,
                                                @Nullable Path mapRoot,
                                                @Nullable Path pointsDir) throws IOException {
        Path backup = backupRoot.resolve(mapFolder + "-EsConfig-" + stamp);
        boolean copied = false;
        for (Map.Entry<String, Path> entry : files.entrySet()) {
            Path file = entry.getValue();
            if (file == null || !Files.isRegularFile(file)) {
                continue;
            }
            Path relative = file.getFileName();
            if (pointsDir != null && file.getParent() != null
                && file.getParent().equals(pointsDir)) {
                relative = Path.of("Points").resolve(file.getFileName());
            }
            Path destination = backup.resolve(relative);
            Files.createDirectories(destination.getParent());
            Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
            copied = true;
        }
        return copied ? backup.toString() : null;
    }

    private static @Nullable JsonObject readObject(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            return null;
        }
        String text = Files.readString(file, StandardCharsets.UTF_8);
        if (text.isBlank()) {
            return null;
        }
        JsonElement parsed = JsonParser.parseString(text);
        return parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
    }

    private static void writeObject(Path file, JsonObject root) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root);
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        Files.writeString(tmp, json, StandardCharsets.UTF_8);
        try {
            Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** 供指令层复用的「某载具类型当前点数」展示。 */
    public static String describeBox(Box box) {
        return String.format(Locale.ROOT, "%d×%d×%d @ %.1f,%.1f",
            box.sizeX(), box.sizeY(), box.sizeZ(), box.centerX(), box.centerZ());
    }
}
