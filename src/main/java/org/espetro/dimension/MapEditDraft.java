package org.espetro.dimension;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.espetro.mapconfig.SpawnPointsSnapshot;
import org.espetro.mapconfig.VehSpawnSnapshot;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 地图编辑会话的「点位 / 出生点 / 载具出生点」草稿。
 *
 * <p>草稿与存档完全分离：所有编辑先落在本对象里，只有 {@code /espetro editsave}
 * （或 {@code /espetro edit configsave}）才写进 {@code EsWorld/<map>/EsConfig}。
 * 草稿会以 JSON 形式落盘到 {@code <存档>/espetro-edit-draft/<mapFolder>.json}，
 * 服务器异常重启后仍可继续。</p>
 *
 * <p>本类不引用任何 Minecraft 类，便于独立自检（见 {@code build/tmp/MapEditSelfTest.java}）。</p>
 */
public final class MapEditDraft {

    /** 写入 EsConfig/CapturePoints.json（默认）。 */
    public static final String LEGACY_TARGET = "legacy";

    public static final int MAX_BATCH = 64;
    public static final int WARN_SIDE = 256;
    public static final int MIN_SPAN = 3;

    public enum Side {
        ATTACK,
        DEFEND;

        public static @Nullable Side of(String raw) {
            if (raw == null) {
                return null;
            }
            String normalized = raw.trim().toUpperCase(Locale.ROOT);
            for (Side side : values()) {
                if (side.name().equals(normalized)) {
                    return side;
                }
            }
            return null;
        }

        public String label() {
            return this == ATTACK ? "进攻方" : "防守方";
        }
    }

    /** 一个世界坐标 + 朝向（载具点与出生点用）。 */
    public record Spot(double x, double y, double z, float yaw) {
        public Spot snapped() {
            return new Spot(Math.floor(x) + 0.5D, Math.floor(y), Math.floor(z) + 0.5D, snapYaw(yaw));
        }

        public Spot withRawYaw() {
            return new Spot(Math.floor(x) + 0.5D, Math.floor(y), Math.floor(z) + 0.5D, yaw);
        }

        public String describe() {
            return String.format(Locale.ROOT, "%.1f %.1f %.1f yaw=%.0f", x, y, z, yaw);
        }
    }

    /** 轴对齐的据点范围（含两端方块）。 */
    public record Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        public static Box of(int[] a, int[] b) {
            return new Box(a[0], a[1], a[2], b[0], b[1], b[2]);
        }

        public int minX() {
            return Math.min(x1, x2);
        }

        public int minY() {
            return Math.min(y1, y2);
        }

        public int minZ() {
            return Math.min(z1, z2);
        }

        public int maxX() {
            return Math.max(x1, x2);
        }

        public int maxY() {
            return Math.max(y1, y2);
        }

        public int maxZ() {
            return Math.max(z1, z2);
        }

        public int sizeX() {
            return Math.abs(x2 - x1) + 1;
        }

        public int sizeY() {
            return Math.abs(y2 - y1) + 1;
        }

        public int sizeZ() {
            return Math.abs(z2 - z1) + 1;
        }

        public double centerX() {
            return (minX() + maxX() + 1) / 2.0D;
        }

        public double centerZ() {
            return (minZ() + maxZ() + 1) / 2.0D;
        }

        public boolean intersects(Box other) {
            return minX() <= other.maxX() && other.minX() <= maxX()
                && minY() <= other.maxY() && other.minY() <= maxY()
                && minZ() <= other.maxZ() && other.minZ() <= maxZ();
        }

        public String describe() {
            return sizeX() + "×" + sizeY() + "×" + sizeZ();
        }
    }

    public record PointDraft(String name, int batch, Box box) {
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", name);
            obj.addProperty("batch", batch);
            obj.add("pos1", posJson(box.x1(), box.y1(), box.z1()));
            obj.add("pos2", posJson(box.x2(), box.y2(), box.z2()));
            return obj;
        }

        public JsonObject toPlannedPoint() {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", name);
            obj.addProperty("batch", batch);
            obj.add("pos1", pointJson(box.x1(), box.y1(), box.z1()));
            obj.add("pos2", pointJson(box.x2(), box.y2(), box.z2()));
            return obj;
        }
    }

    /** 载具出生点：与 VehSpawn.json 一样，一个点同时带两侧坐标，允许暂时只填一侧。 */
    public record VehPoint(String id, @Nullable Spot attack, @Nullable Spot defend) {
        public boolean complete() {
            return attack != null && defend != null;
        }

        public @Nullable Spot side(Side side) {
            return side == Side.ATTACK ? attack : defend;
        }

        public VehPoint with(Side side, Spot spot) {
            return side == Side.ATTACK ? new VehPoint(id, spot, defend) : new VehPoint(id, attack, spot);
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", id);
            if (attack != null) {
                obj.add("attack", spotJson(attack));
            }
            if (defend != null) {
                obj.add("defend", spotJson(defend));
            }
            return obj;
        }
    }

    /** 尚未提交的两角点。 */
    public static final class PendingCorner {
        public @Nullable int[] pos1;
        public @Nullable int[] pos2;

        public boolean complete() {
            return pos1 != null && pos2 != null;
        }
    }

    private final String mapFolder;
    private String captureTarget = LEGACY_TARGET;
    private final Map<String, PointDraft> points = new LinkedHashMap<>();
    private final Map<String, PendingCorner> corners = new LinkedHashMap<>();
    private final Map<Side, Spot> spawn = new EnumMap<>(Side.class);
    private final LinkedHashSet<String> vehTypes = new LinkedHashSet<>();
    private final Map<String, List<VehPoint>> vehPoints = new LinkedHashMap<>();

    public MapEditDraft(String mapFolder) {
        this.mapFolder = mapFolder;
    }

    // ------------------------------------------------------------------
    // 构造 / 读取
    // ------------------------------------------------------------------

    /**
     * 用当前地图的线上配置作为草稿基线：避免「新增一个点」在保存时把原有内容清空。
     */
    public static MapEditDraft seedFrom(Path esConfigDir,
                                        @Nullable SpawnPointsSnapshot spawnPoints,
                                        @Nullable VehSpawnSnapshot vehSpawn) {
        MapEditDraft draft = new MapEditDraft(esConfigDir.getParent() == null
            ? esConfigDir.getFileName().toString()
            : esConfigDir.getParent().getFileName().toString());

        CaptureSource source = resolveCaptureSource(esConfigDir);
        draft.captureTarget = source.label;
        for (PointDraft point : source.points) {
            draft.points.put(point.name().toLowerCase(Locale.ROOT), point);
        }

        if (spawnPoints != null) {
            if (spawnPoints.attack != null) {
                draft.spawn.put(Side.ATTACK, new Spot(
                    spawnPoints.attack.x(), spawnPoints.attack.y(), spawnPoints.attack.z(),
                    spawnPoints.attack.yaw()));
            }
            if (spawnPoints.defend != null) {
                draft.spawn.put(Side.DEFEND, new Spot(
                    spawnPoints.defend.x(), spawnPoints.defend.y(), spawnPoints.defend.z(),
                    spawnPoints.defend.yaw()));
            }
        }

        if (vehSpawn != null) {
            draft.vehTypes.addAll(vehSpawn.vehicleTypes);
            for (Map.Entry<String, List<VehSpawnSnapshot.SpawnPoint>> entry
                    : vehSpawn.spawnPointsByType.entrySet()) {
                List<VehPoint> list = new ArrayList<>();
                for (VehSpawnSnapshot.SpawnPoint point : entry.getValue()) {
                    list.add(new VehPoint(point.id(),
                        new Spot(point.attack().x(), point.attack().y(), point.attack().z(),
                            point.attack().yaw()),
                        new Spot(point.defend().x(), point.defend().y(), point.defend().z(),
                            point.defend().yaw())));
                }
                draft.vehPoints.put(entry.getKey(), list);
            }
        }
        return draft;
    }

    private record CaptureSource(String label, List<PointDraft> points) {
    }

    private static CaptureSource resolveCaptureSource(Path esConfigDir) {
        Path legacy = esConfigDir.resolve("CapturePoints.json");
        List<PointDraft> legacyPoints = readPlannedPoints(legacy);
        Path pointsDir = esConfigDir.getParent() == null ? null : esConfigDir.getParent().resolve("Points");
        boolean hasPresets = hasPresetFiles(pointsDir);
        if (legacyPoints != null) {
            // 有预设时线上实际用的是预设，但草稿基线仍取 legacy（管理员可用 capture target 切换）
            return new CaptureSource(LEGACY_TARGET, legacyPoints);
        }
        if (hasPresets) {
            try (var stream = Files.newDirectoryStream(pointsDir, "*.json")) {
                for (Path file : stream) {
                    List<PointDraft> preset = readPlannedPoints(file);
                    if (preset != null) {
                        return new CaptureSource("Points/" + file.getFileName(), preset);
                    }
                }
            } catch (IOException ignored) {
                // 读不到就退回空草稿
            }
        }
        return new CaptureSource(LEGACY_TARGET, List.of());
    }

    public static boolean hasPresetFiles(@Nullable Path pointsDir) {
        if (pointsDir == null || !Files.isDirectory(pointsDir)) {
            return false;
        }
        try (var stream = Files.newDirectoryStream(pointsDir, "*.json")) {
            return stream.iterator().hasNext();
        } catch (IOException e) {
            return false;
        }
    }

    private static @Nullable List<PointDraft> readPlannedPoints(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return null;
        }
        try {
            JsonObject root = JsonParser.parseString(
                Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("plannedPoints") || !root.get("plannedPoints").isJsonArray()) {
                return null;
            }
            List<PointDraft> result = new ArrayList<>();
            for (JsonElement element : root.getAsJsonArray("plannedPoints")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject obj = element.getAsJsonObject();
                String name = obj.has("name") ? obj.get("name").getAsString() : "?";
                int batch = obj.has("batch") ? obj.get("batch").getAsInt() : 1;
                Box box = boxFromJson(obj);
                if (box != null) {
                    result.add(new PointDraft(name, Math.max(1, batch), box));
                }
            }
            return result;
        } catch (RuntimeException | IOException e) {
            return null;
        }
    }

    private static @Nullable Box boxFromJson(JsonObject obj) {
        int[] a = posFromJson(obj.get("pos1"));
        int[] b = posFromJson(obj.get("pos2"));
        return a == null || b == null ? null : Box.of(a, b);
    }

    private static @Nullable int[] posFromJson(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("x") || !obj.has("y") || !obj.has("z")) {
            return null;
        }
        try {
            return new int[] {
                (int) Math.floor(obj.get("x").getAsDouble()),
                (int) Math.floor(obj.get("y").getAsDouble()),
                (int) Math.floor(obj.get("z").getAsDouble())
            };
        } catch (RuntimeException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // 据点
    // ------------------------------------------------------------------

    public String mapFolder() {
        return mapFolder;
    }

    public String captureTarget() {
        return captureTarget;
    }

    public void setCaptureTarget(String target) {
        this.captureTarget = target == null || target.isBlank() ? LEGACY_TARGET : target.trim();
    }

    public List<PointDraft> points() {
        List<PointDraft> list = new ArrayList<>(points.values());
        list.sort(Comparator.comparingInt(PointDraft::batch)
            .thenComparing(p -> p.name().toLowerCase(Locale.ROOT)));
        return list;
    }

    public int pointCount() {
        return points.size();
    }

    public @Nullable PointDraft point(String name) {
        return name == null ? null : points.get(name.trim().toLowerCase(Locale.ROOT));
    }

    public boolean upsertPoint(PointDraft point) {
        boolean replaced = points.put(point.name().toLowerCase(Locale.ROOT), point) != null;
        corners.remove(point.name().toLowerCase(Locale.ROOT));
        return replaced;
    }

    public boolean removePoint(String name) {
        return name != null && points.remove(name.trim().toLowerCase(Locale.ROOT)) != null;
    }

    public PendingCorner pendingCorner(String name) {
        return corners.computeIfAbsent(name.trim().toLowerCase(Locale.ROOT), k -> new PendingCorner());
    }

    public @Nullable PendingCorner existingCorner(String name) {
        return name == null ? null : corners.get(name.trim().toLowerCase(Locale.ROOT));
    }

    public Set<String> pendingCornerNames() {
        return Set.copyOf(corners.keySet());
    }

    public void clearCorners() {
        corners.clear();
    }

    // ------------------------------------------------------------------
    // 出生点
    // ------------------------------------------------------------------

    public @Nullable Spot spawn(Side side) {
        return spawn.get(side);
    }

    public void setSpawn(Side side, Spot spot) {
        spawn.put(side, spot);
    }

    // ------------------------------------------------------------------
    // 载具出生点
    // ------------------------------------------------------------------

    public Set<String> vehTypes() {
        return Set.copyOf(vehTypes);
    }

    public List<String> vehTypesOrdered() {
        return new ArrayList<>(vehTypes);
    }

    public boolean hasVehType(String type) {
        return vehTypes.contains(normalizeType(type));
    }

    public void addVehType(String type) {
        vehTypes.add(normalizeType(type));
    }

    public List<VehPoint> vehPoints(String type) {
        return List.copyOf(vehPoints.getOrDefault(normalizeType(type), List.of()));
    }

    public Map<String, List<VehPoint>> allVehPoints() {
        Map<String, List<VehPoint>> copy = new LinkedHashMap<>();
        for (String type : vehTypes) {
            copy.put(type, vehPoints(type));
        }
        return copy;
    }

    public int vehPointCount(String type) {
        return vehPoints.getOrDefault(normalizeType(type), List.of()).size();
    }

    public @Nullable VehPoint findVehPoint(String type, String id) {
        if (id == null) {
            return null;
        }
        for (VehPoint point : vehPoints.getOrDefault(normalizeType(type), List.of())) {
            if (point.id().equalsIgnoreCase(id.trim())) {
                return point;
            }
        }
        return null;
    }

    /**
     * 新增载具点（id 为空时自动 {@code <type>_<n>}），或给已有点补/改某一侧。
     *
     * @return 实际使用的 id
     */
    public String addVehPoint(String type, Side side, Spot spot, @Nullable String preferredId) {
        String normalized = normalizeType(type);
        addVehType(normalized);
        List<VehPoint> list = vehPoints.computeIfAbsent(normalized, k -> new ArrayList<>());
        if (preferredId != null && !preferredId.isBlank()) {
            String id = preferredId.trim();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id().equalsIgnoreCase(id)) {
                    list.set(i, list.get(i).with(side, spot));
                    return list.get(i).id();
                }
            }
            list.add(new VehPoint(id, side == Side.ATTACK ? spot : null,
                side == Side.DEFEND ? spot : null));
            return id;
        }
        int next = 1;
        Set<String> used = new LinkedHashSet<>();
        for (VehPoint point : list) {
            used.add(point.id().toLowerCase(Locale.ROOT));
        }
        while (used.contains((normalized + "_" + next).toLowerCase(Locale.ROOT))) {
            next++;
        }
        String id = normalized + "_" + next;
        list.add(new VehPoint(id, side == Side.ATTACK ? spot : null,
            side == Side.DEFEND ? spot : null));
        return id;
    }

    public boolean removeVehPoint(String type, String id) {
        List<VehPoint> list = vehPoints.get(normalizeType(type));
        if (list == null || id == null) {
            return false;
        }
        return list.removeIf(point -> point.id().equalsIgnoreCase(id.trim()));
    }

    // ------------------------------------------------------------------
    // 校验
    // ------------------------------------------------------------------

    /** 阻塞保存的问题。 */
    public List<String> problems() {
        return problems(false);
    }

    /**
     * 阻塞保存的问题。
     *
     * @param amphibious 两栖登陆地图：进攻方出生点由舰船 {@code override_spawn.attack} 提供，
     *                   进攻侧载具点由舰船甲板标记提供，因此草稿里不要求填写进攻侧
     */
    public List<String> problems(boolean amphibious) {
        List<String> problems = new ArrayList<>();
        for (PointDraft point : points()) {
            if (point.name().isBlank()) {
                problems.add("存在空的据点名称");
            }
            if (point.batch() < 1 || point.batch() > MAX_BATCH) {
                problems.add("据点 " + point.name() + " 的批次 " + point.batch()
                    + " 超出 1.." + MAX_BATCH);
            }
            if (point.box().sizeX() < MIN_SPAN && point.box().sizeY() < MIN_SPAN
                && point.box().sizeZ() < MIN_SPAN) {
                problems.add("据点 " + point.name() + " 体积过小（三轴都 < " + MIN_SPAN + "）");
            }
        }
        for (Side side : Side.values()) {
            if (amphibious && side == Side.ATTACK) {
                continue;
            }
            if (spawn.get(side) == null) {
                problems.add(side.label() + "出生点（" + side.name() + "）未设置");
            }
        }
        for (String type : vehTypes) {
            for (VehPoint point : vehPoints(type)) {
                if (amphibious) {
                    if (point.attack() == null && point.defend() == null) {
                        problems.add("载具点 " + type + "/" + point.id() + " 两侧都没填");
                    }
                    continue;
                }
                if (!point.complete()) {
                    problems.add("载具点 " + type + "/" + point.id()
                        + " 只填了一侧（attack=" + (point.attack() != null)
                        + ", defend=" + (point.defend() != null) + "）");
                }
            }
        }
        return problems;
    }

    /** 不阻塞保存、但需要提醒的问题。 */
    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();
        List<PointDraft> list = points();
        for (PointDraft point : list) {
            Box box = point.box();
            if (box.sizeX() > WARN_SIDE || box.sizeY() > WARN_SIDE || box.sizeZ() > WARN_SIDE) {
                warnings.add("据点 " + point.name() + " 的尺寸 " + box.describe()
                    + " 偏大（单边 > " + WARN_SIDE + "）");
            }
        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).box().intersects(list.get(j).box())) {
                    warnings.add("据点 " + list.get(i).name() + " 与 " + list.get(j).name()
                        + " 的范围相交");
                }
            }
        }
        for (String type : vehTypes) {
            List<VehPoint> points = vehPoints(type);
            for (int i = 0; i < points.size(); i++) {
                for (int j = i + 1; j < points.size(); j++) {
                    for (Side side : Side.values()) {
                        Spot a = points.get(i).side(side);
                        Spot b = points.get(j).side(side);
                        if (a == null || b == null) {
                            continue;
                        }
                        double distance = Math.sqrt(Math.pow(a.x() - b.x(), 2)
                            + Math.pow(a.z() - b.z(), 2));
                        if (distance < 5.0D) {
                            warnings.add("载具点 " + type + " 的 " + points.get(i).id() + " 与 "
                                + points.get(j).id() + " 在 " + side.name() + " 侧仅相距 "
                                + String.format(Locale.ROOT, "%.2f", distance) + " 格（< 5）");
                        }
                    }
                }
            }
        }
        if (!vehTypes.isEmpty()) {
            for (String type : vehTypes) {
                if (vehPoints(type).isEmpty()) {
                    warnings.add("载具类型 " + type + " 没有任何出生点（使用该类型的编制会被跳过）");
                }
            }
        }
        return warnings;
    }

    // ------------------------------------------------------------------
    // 序列化
    // ------------------------------------------------------------------

    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        root.addProperty("mapFolder", mapFolder);
        root.addProperty("captureTarget", captureTarget);

        JsonArray pointArray = new JsonArray();
        for (PointDraft point : points()) {
            pointArray.add(point.toJson());
        }
        root.add("points", pointArray);

        JsonObject cornerObj = new JsonObject();
        for (Map.Entry<String, PendingCorner> entry : corners.entrySet()) {
            PendingCorner corner = entry.getValue();
            JsonObject obj = new JsonObject();
            if (corner.pos1 != null) {
                obj.add("pos1", posJson(corner.pos1[0], corner.pos1[1], corner.pos1[2]));
            }
            if (corner.pos2 != null) {
                obj.add("pos2", posJson(corner.pos2[0], corner.pos2[1], corner.pos2[2]));
            }
            cornerObj.add(entry.getKey(), obj);
        }
        root.add("corners", cornerObj);

        JsonObject spawnObj = new JsonObject();
        for (Side side : Side.values()) {
            Spot spot = spawn.get(side);
            if (spot != null) {
                spawnObj.add(side.name(), spotJson(spot));
            }
        }
        root.add("spawn", spawnObj);

        JsonArray typeArray = new JsonArray();
        for (String type : vehTypes) {
            typeArray.add(type);
        }
        root.add("vehTypes", typeArray);

        JsonObject vehObj = new JsonObject();
        for (String type : vehTypes) {
            JsonArray array = new JsonArray();
            for (VehPoint point : vehPoints(type)) {
                array.add(point.toJson());
            }
            vehObj.add(type, array);
        }
        root.add("vehPoints", vehObj);
        return root;
    }

    public static MapEditDraft fromJson(JsonObject root) {
        MapEditDraft draft = new MapEditDraft(
            root.has("mapFolder") ? root.get("mapFolder").getAsString() : "unknown");
        if (root.has("captureTarget") && root.get("captureTarget").isJsonPrimitive()) {
            draft.captureTarget = root.get("captureTarget").getAsString();
        }
        if (root.has("points") && root.get("points").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("points")) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject obj = element.getAsJsonObject();
                Box box = boxFromJson(obj);
                if (box == null || !obj.has("name")) {
                    continue;
                }
                String name = obj.get("name").getAsString();
                int batch = obj.has("batch") ? obj.get("batch").getAsInt() : 1;
                PointDraft point = new PointDraft(name, batch, box);
                draft.points.put(name.toLowerCase(Locale.ROOT), point);
            }
        }
        if (root.has("corners") && root.get("corners").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry
                    : root.getAsJsonObject("corners").entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }
                JsonObject obj = entry.getValue().getAsJsonObject();
                PendingCorner corner = new PendingCorner();
                corner.pos1 = posFromJson(obj.get("pos1"));
                corner.pos2 = posFromJson(obj.get("pos2"));
                draft.corners.put(entry.getKey(), corner);
            }
        }
        if (root.has("spawn") && root.get("spawn").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry
                    : root.getAsJsonObject("spawn").entrySet()) {
                Side side = Side.of(entry.getKey());
                Spot spot = spotFromJson(entry.getValue());
                if (side != null && spot != null) {
                    draft.spawn.put(side, spot);
                }
            }
        }
        if (root.has("vehTypes") && root.get("vehTypes").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("vehTypes")) {
                if (element.isJsonPrimitive()) {
                    draft.vehTypes.add(normalizeType(element.getAsString()));
                }
            }
        }
        if (root.has("vehPoints") && root.get("vehPoints").isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry
                    : root.getAsJsonObject("vehPoints").entrySet()) {
                String type = normalizeType(entry.getKey());
                if (!entry.getValue().isJsonArray()) {
                    continue;
                }
                List<VehPoint> list = new ArrayList<>();
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject obj = element.getAsJsonObject();
                    String id = obj.has("id") ? obj.get("id").getAsString() : type + "_" + (list.size() + 1);
                    list.add(new VehPoint(id, spotFromJson(obj.get("attack")),
                        spotFromJson(obj.get("defend"))));
                }
                draft.vehTypes.add(type);
                draft.vehPoints.put(type, list);
            }
        }
        return draft;
    }

    public void save(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            .toJson(toJson());
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        Files.writeString(tmp, json, StandardCharsets.UTF_8);
        try {
            Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static @Nullable MapEditDraft load(Path file, String expectedMap) {
        if (file == null || !Files.isRegularFile(file)) {
            return null;
        }
        try {
            JsonObject root = JsonParser.parseString(
                Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            MapEditDraft draft = fromJson(root);
            if (expectedMap != null && !expectedMap.equalsIgnoreCase(draft.mapFolder)) {
                return null;
            }
            return draft;
        } catch (RuntimeException | IOException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // 摘要
    // ------------------------------------------------------------------

    public List<String> summary() {
        List<String> lines = new ArrayList<>();
        lines.add("据点 " + points.size() + " 个：" + points().stream()
            .map(p -> p.name() + "#" + p.batch())
            .reduce((a, b) -> a + ", " + b).orElse("（空）"));
        for (Side side : Side.values()) {
            Spot spot = spawn.get(side);
            lines.add(side.label() + "出生点：" + (spot == null ? "未设置" : spot.describe()));
        }
        List<String> veh = new ArrayList<>();
        for (String type : vehTypes) {
            veh.add(type + "×" + vehPoints(type).size());
        }
        lines.add("载具点：" + (veh.isEmpty() ? "（空）" : String.join(", ", veh)));
        lines.add("写入目标：" + captureTarget);
        return lines;
    }

    // ------------------------------------------------------------------
    // 工具
    // ------------------------------------------------------------------

    public static String normalizeType(String type) {
        return type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
    }

    public static float snapYaw(float yaw) {
        float normalized = ((yaw % 360f) + 360f) % 360f;
        return Math.round(normalized / 90f) * 90f % 360f;
    }

    public static JsonObject posJson(int x, int y, int z) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        obj.addProperty("z", z);
        return obj;
    }

    public static JsonObject pointJson(int x, int y, int z) {
        return posJson(x, y, z);
    }

    public static JsonObject spotJson(Spot spot) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", round(spot.x()));
        obj.addProperty("y", round(spot.y()));
        obj.addProperty("z", round(spot.z()));
        obj.addProperty("yaw", spot.yaw());
        return obj;
    }

    public static @Nullable Spot spotFromJson(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject obj = element.getAsJsonObject();
        if (!obj.has("x") || !obj.has("y") || !obj.has("z")) {
            return null;
        }
        try {
            return new Spot(obj.get("x").getAsDouble(), obj.get("y").getAsDouble(),
                obj.get("z").getAsDouble(), obj.has("yaw") ? obj.get("yaw").getAsFloat() : 0f);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static double round(double value) {
        return Math.round(value * 1000.0D) / 1000.0D;
    }

    public Collection<PointDraft> rawPoints() {
        return List.copyOf(points.values());
    }
}
