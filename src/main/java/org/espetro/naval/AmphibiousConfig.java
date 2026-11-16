package org.espetro.naval;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 两栖登陆地图配置：{@code EsWorld/<地图>/EsConfig/amphibious.json}（可选文件）。
 *
 * <p>缺省或 {@code enabled=false} 时该地图按普通侵攻运行。所有解析错误都写进
 * {@link #errors}，由 {@code ActiveMapConfig} 决定是否拒载。</p>
 */
public final class AmphibiousConfig {

    public static final String FILE_NAME = "amphibious.json";

    public final boolean enabled;
    /** 冲滩编制（攻方锁定），可为 null 表示不锁定。 */
    public final String assaultFaction;
    public final double beachDirection;
    /** 地图级海面世界 Y，-1 表示未填写。 */
    public final int waterSurfaceY;
    public final List<Ship> ships;
    @Nullable
    public final OverrideSpawn overrideSpawn;
    public final List<String> errors;

    private AmphibiousConfig(boolean enabled, String assaultFaction, double beachDirection,
                             int waterSurfaceY, List<Ship> ships,
                             @Nullable OverrideSpawn overrideSpawn, List<String> errors) {
        this.enabled = enabled;
        this.assaultFaction = assaultFaction;
        this.beachDirection = beachDirection;
        this.waterSurfaceY = waterSurfaceY;
        this.ships = List.copyOf(ships);
        this.overrideSpawn = overrideSpawn;
        this.errors = List.copyOf(errors);
    }

    public boolean usable() {
        return enabled && errors.isEmpty();
    }

    @Nullable
    public Ship firstOfRole(String role) {
        for (Ship ship : ships) {
            if (ship.role.equals(role)) {
                return ship;
            }
        }
        return null;
    }

    /** 读取 EsConfig 下的 amphibious.json；文件不存在时返回空。 */
    public static Optional<AmphibiousConfig> load(Path esConfigDir) {
        Path file = esConfigDir.resolve(FILE_NAME);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try {
            String text = Files.readString(file, StandardCharsets.UTF_8);
            return Optional.of(parse(JsonParser.parseString(text).getAsJsonObject()));
        } catch (Exception e) {
            List<String> errors = new ArrayList<>();
            errors.add("amphibious.json 解析失败: " + e.getMessage());
            return Optional.of(new AmphibiousConfig(false, null, 0.0D, -1, List.of(), null, errors));
        }
    }

    public static AmphibiousConfig parse(JsonObject root) {
        List<String> errors = new ArrayList<>();
        boolean enabled = bool(root, "enabled", true);
        String assaultFaction = string(root, "assault_faction", null);
        if (assaultFaction != null) {
            assaultFaction = assaultFaction.trim();
            if (assaultFaction.isEmpty()) {
                assaultFaction = null;
            }
        }
        double beachDirection = num(root, "beach_direction", 0.0D);
        int waterSurfaceY = intOr(root, "water_surface_y", -1);

        List<Ship> ships = new ArrayList<>();
        JsonArray shipsEl = arr(root, "ships");
        if (shipsEl != null) {
            for (int i = 0; i < shipsEl.size(); i++) {
                JsonElement el = shipsEl.get(i);
                if (!el.isJsonObject()) {
                    errors.add("ships[" + i + "] 不是对象");
                    continue;
                }
                Ship ship = Ship.parse(el.getAsJsonObject(), i, errors);
                if (ship != null) {
                    ships.add(ship);
                }
            }
        }
        if (ships.isEmpty() && enabled) {
            errors.add("enabled=true 但 ships 为空");
        }

        OverrideSpawn overrideSpawn = null;
        JsonObject overrideEl = obj(root, "override_spawn");
        if (overrideEl != null) {
            overrideSpawn = OverrideSpawn.parse(overrideEl, errors);
        }
        return new AmphibiousConfig(enabled, assaultFaction, beachDirection, waterSurfaceY,
            ships, overrideSpawn, errors);
    }

    // ---------------------------------------------------------------- models

    public static final class Ship {
        public final String id;
        public final String role;
        public final ResourceLocation structure;
        public final String source;
        public final int draftAdjust;
        public final double x;
        public final double y;
        public final double z;
        public final String rotation;
        public final String barrierMode;
        public final int barrierHeight;
        public final boolean barrierCap;
        public final boolean spawnFromMarkers;
        public final boolean solid;
        public final int waterSurfaceY;

        private Ship(String id, String role, ResourceLocation structure, String source, int draftAdjust,
                     double x, double y, double z, String rotation, String barrierMode,
                     int barrierHeight, boolean barrierCap, boolean spawnFromMarkers, boolean solid,
                     int waterSurfaceY) {
            this.id = id;
            this.role = role;
            this.structure = structure;
            this.source = source;
            this.draftAdjust = draftAdjust;
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotation = rotation;
            this.barrierMode = barrierMode;
            this.barrierHeight = barrierHeight;
            this.barrierCap = barrierCap;
            this.spawnFromMarkers = spawnFromMarkers;
            this.solid = solid;
            this.waterSurfaceY = waterSurfaceY;
        }

        @Nullable
        private static Ship parse(JsonObject o, int index, List<String> errors) {
            String id = string(o, "id", "ship_" + index);
            String role = string(o, "role", "lhd").toLowerCase(Locale.ROOT);
            if (!role.equals("lhd") && !role.equals("destroyer")) {
                errors.add("ships[" + index + "] role 非法: " + role);
                return null;
            }
            String structureText = string(o, "structure", null);
            if (structureText == null) {
                errors.add("ships[" + index + "] 缺少 structure");
                return null;
            }
            ResourceLocation structure;
            try {
                structure = new ResourceLocation(structureText.trim());
            } catch (Exception e) {
                errors.add("ships[" + index + "] structure 非法: " + structureText);
                return null;
            }
            String source = string(o, "source", "config").toLowerCase(Locale.ROOT);
            if (!source.equals("config") && !source.equals("builtin") && !source.equals("world")) {
                errors.add("ships[" + index + "] source 非法: " + source);
                return null;
            }
            JsonObject anchor = obj(o, "anchor");
            double x = anchor == null ? 0.0D : num(anchor, "x", 0.0D);
            double y = anchor == null ? 0.0D : num(anchor, "y", 0.0D);
            double z = anchor == null ? 0.0D : num(anchor, "z", 0.0D);
            String rotation = string(o, "rotation", "none").toLowerCase(Locale.ROOT);
            if (!rotation.equals("none") && !rotation.equals("clockwise_90")
                && !rotation.equals("clockwise_180") && !rotation.equals("counterclockwise_90")) {
                errors.add("ships[" + index + "] rotation 非法: " + rotation);
                rotation = "none";
            }
            String barrierMode = "none";
            int barrierHeight = 0;
            boolean barrierCap = false;
            JsonObject barrier = obj(o, "barrier");
            if (barrier != null) {
                barrierMode = string(barrier, "mode", "box").toLowerCase(Locale.ROOT);
                if (!barrierMode.equals("box") && !barrierMode.equals("hull") && !barrierMode.equals("none")) {
                    errors.add("ships[" + index + "] barrier.mode 非法: " + barrierMode);
                    barrierMode = "none";
                }
                // height<=0 表示「自动」：按舰船实际包围盒高度 + 2 计算（见 AmphibiousDeployment）。
                barrierHeight = intOr(barrier, "height", 0);
                barrierCap = bool(barrier, "cap", true);
            }
            return new Ship(id, role, structure, source, intOr(o, "draft_adjust", 0),
                x, y, z, rotation, barrierMode, barrierHeight, barrierCap,
                bool(o, "spawn_from_markers", true), bool(o, "solid", false),
                intOr(o, "water_surface_y", -1));
        }
    }

    /** 兜底出生点/载具点（不依赖结构标记）。 */
    public static final class OverrideSpawn {
        @Nullable
        public final Point attack;
        /** type -> ordered points (id/attack/defend). */
        public final Map<String, List<PointEntry>> vehicles;

        private OverrideSpawn(@Nullable Point attack, Map<String, List<PointEntry>> vehicles) {
            this.attack = attack;
            this.vehicles = Map.copyOf(vehicles);
        }

        @Nullable
        private static OverrideSpawn parse(JsonObject o, List<String> errors) {
            Point attack = null;
            JsonObject attackEl = obj(o, "attack");
            if (attackEl != null) {
                attack = Point.parse(attackEl, 0.0F);
            }
            Map<String, List<PointEntry>> vehicles = new LinkedHashMap<>();
            JsonObject vehEl = obj(o, "vehicles");
            if (vehEl != null) {
                for (Map.Entry<String, JsonElement> e : vehEl.entrySet()) {
                    String type = e.getKey().toLowerCase(Locale.ROOT);
                    if (!e.getValue().isJsonArray()) {
                        errors.add("override_spawn.vehicles." + type + " 不是数组");
                        continue;
                    }
                    List<PointEntry> list = new ArrayList<>();
                    for (JsonElement pointEl : e.getValue().getAsJsonArray()) {
                        if (!pointEl.isJsonObject()) {
                            continue;
                        }
                        PointEntry entry = PointEntry.parse(pointEl.getAsJsonObject(), type, list.size());
                        if (entry != null) {
                            list.add(entry);
                        }
                    }
                    vehicles.put(type, list);
                }
            }
            return new OverrideSpawn(attack, vehicles);
        }
    }

    public record Point(double x, double y, double z, float yaw) {
        static Point parse(JsonObject o, float fallbackYaw) {
            return new Point(num(o, "x", 0.5D), num(o, "y", 0.0D), num(o, "z", 0.5D),
                (float) num(o, "yaw", fallbackYaw));
        }
    }

    public static final class PointEntry {
        public final String id;
        @Nullable
        public final Point attack;
        @Nullable
        public final Point defend;

        private PointEntry(String id, @Nullable Point attack, @Nullable Point defend) {
            this.id = id;
            this.attack = attack;
            this.defend = defend;
        }

        public static PointEntry of(String id, @Nullable Point attack, @Nullable Point defend) {
            return new PointEntry(id, attack, defend);
        }

        @Nullable
        private static PointEntry parse(JsonObject o, String type, int index) {
            String id = string(o, "id", type + "_" + (index + 1));
            JsonObject attackEl = obj(o, "attack");
            JsonObject defendEl = obj(o, "defend");
            Point attack = attackEl == null ? null : Point.parse(attackEl, 0.0F);
            Point defend = defendEl == null ? null : Point.parse(defendEl, 180.0F);
            if (attack == null && defend == null) {
                return null;
            }
            return new PointEntry(id, attack, defend);
        }
    }

    // ---------------------------------------------------------------- helpers

    private static JsonObject obj(JsonObject o, String key) {
        JsonElement el = o.get(key);
        return el != null && el.isJsonObject() ? el.getAsJsonObject() : null;
    }

    private static JsonArray arr(JsonObject o, String key) {
        JsonElement el = o.get(key);
        return el != null && el.isJsonArray() ? el.getAsJsonArray() : null;
    }

    private static String string(JsonObject o, String key, @Nullable String def) {
        JsonElement el = o.get(key);
        if (el == null || el.isJsonNull()) {
            return def;
        }
        try {
            return el.getAsString();
        } catch (Exception e) {
            return def;
        }
    }

    private static double num(JsonObject o, String key, double def) {
        JsonElement el = o.get(key);
        if (el == null || el.isJsonNull()) {
            return def;
        }
        try {
            return el.getAsDouble();
        } catch (Exception e) {
            return def;
        }
    }

    private static int intOr(JsonObject o, String key, int def) {
        JsonElement el = o.get(key);
        if (el == null || el.isJsonNull()) {
            return def;
        }
        try {
            return el.getAsInt();
        } catch (Exception e) {
            return def;
        }
    }

    private static boolean bool(JsonObject o, String key, boolean def) {
        JsonElement el = o.get(key);
        if (el == null || el.isJsonNull()) {
            return def;
        }
        try {
            return el.getAsBoolean();
        } catch (Exception e) {
            return def;
        }
    }
}
