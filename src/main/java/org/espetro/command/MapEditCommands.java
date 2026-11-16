package org.espetro.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.espetro.dimension.MapEditConfigWriter;
import org.espetro.dimension.MapEditDraft;
import org.espetro.dimension.MapEditDraft.Box;
import org.espetro.dimension.MapEditDraft.PointDraft;
import org.espetro.dimension.MapEditDraft.Side;
import org.espetro.dimension.MapEditDraft.Spot;
import org.espetro.dimension.MapEditDraft.VehPoint;
import org.espetro.dimension.MapEditManager;
import org.espetro.dimension.MapEditPreview;
import org.espetro.mapconfig.ActiveMapConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 地图编辑维度里的「点位 / 出生点 / 载具出生点」编辑指令。
 *
 * <p>与 {@link EspetroCommand} 里的 {@code /espetro edit <map>|in|editsave|editabandon} 共用
 * 同一个 {@code /espetro edit} 字面量（Brigadier 会自动合并子树）。</p>
 */
public final class MapEditCommands {

    private MapEditCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("espetro")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("edit")
                .then(pointNode())
                .then(spawnNode())
                .then(vehNode())
                .then(captureNode())
                .then(Commands.literal("configsave")
                    .executes(ctx -> MapEditManager.getInstance().configSave(ctx.getSource())))
                .then(Commands.literal("status")
                    .executes(ctx -> status(ctx.getSource())))
            ));
    }

    // ------------------------------------------------------------------
    // point
    // ------------------------------------------------------------------

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> pointNode() {
        return Commands.literal("point")
            .then(Commands.literal("corner")
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests(pointNameSuggestions())
                    .executes(ctx -> corner(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
            .then(Commands.literal("apply")
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests(pendingNameSuggestions())
                    .then(Commands.argument("batch", IntegerArgumentType.integer(1, MapEditDraft.MAX_BATCH))
                        .executes(ctx -> applyCorner(ctx.getSource(),
                            StringArgumentType.getString(ctx, "name"),
                            IntegerArgumentType.getInteger(ctx, "batch"))))))
            .then(Commands.literal("info")
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests(pointNameSuggestions())
                    .executes(ctx -> info(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
            .then(Commands.literal("del")
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests(pointNameSuggestions())
                    .executes(ctx -> remove(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
            .then(Commands.literal("clear")
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests(pendingNameSuggestions())
                    .executes(ctx -> clearCorner(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
            .then(Commands.literal("list")
                .executes(ctx -> list(ctx.getSource())))
            .then(Commands.literal("show")
                .executes(ctx -> show(ctx.getSource(), null))
                .then(Commands.argument("name", StringArgumentType.word())
                    .suggests(pointNameSuggestions())
                    .executes(ctx -> show(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    private static int corner(CommandSourceStack source, String name) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        Vec3 pos = source.getPosition();
        int[] corner = {(int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z)};
        MapEditDraft.PendingCorner pending = draft.pendingCorner(name);
        if (pending.pos1 == null) {
            pending.pos1 = corner;
            source.sendSystemMessage(Component.literal("§a已记录 §f" + name + " §a的第 1 角：§f"
                + corner[0] + ", " + corner[1] + ", " + corner[2]));
        } else if (pending.pos2 == null) {
            pending.pos2 = corner;
            Box box = Box.of(pending.pos1, pending.pos2);
            source.sendSystemMessage(Component.literal("§a已记录 §f" + name + " §a的第 2 角：§f"
                + corner[0] + ", " + corner[1] + ", " + corner[2]));
            source.sendSystemMessage(Component.literal("§7范围 §f" + box.describe()
                + " §7→ 用 §f/espetro edit point apply " + name + " <批次> §7提交"));
        } else {
            pending.pos1 = corner;
            pending.pos2 = null;
            source.sendSystemMessage(Component.literal("§e已重置 §f" + name + " §e的第 1 角：§f"
                + corner[0] + ", " + corner[1] + ", " + corner[2]));
        }
        return 1;
    }

    private static int applyCorner(CommandSourceStack source, String name, int batch) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        MapEditDraft.PendingCorner pending = draft.existingCorner(name);
        if (pending == null || !pending.complete()) {
            source.sendSystemMessage(Component.literal("§c请先用 §f/espetro edit point corner " + name
                + " §c站在两个对角各执行一次。"));
            return 0;
        }
        Box box = Box.of(pending.pos1, pending.pos2);
        if (box.sizeX() < MapEditDraft.MIN_SPAN || box.sizeZ() < MapEditDraft.MIN_SPAN) {
            source.sendSystemMessage(Component.literal("§c据点在水平方向至少要有 "
                + MapEditDraft.MIN_SPAN + " 格（当前 " + box.sizeX() + "×" + box.sizeZ() + "）。"));
            return 0;
        }
        boolean replaced = draft.point(name) != null;
        draft.upsertPoint(new PointDraft(name, batch, box));
        source.sendSystemMessage(Component.literal((replaced ? "§e已更新据点 §f" : "§a已新增据点 §f")
            + name + " §7批次 " + batch + "，范围 " + box.describe()));
        warnOverlap(source, draft, name, box);
        return 1;
    }

    private static void warnOverlap(CommandSourceStack source, MapEditDraft draft, String name, Box box) {
        List<String> overlaps = new ArrayList<>();
        for (PointDraft other : draft.points()) {
            if (!other.name().equalsIgnoreCase(name) && other.box().intersects(box)) {
                overlaps.add(other.name());
            }
        }
        if (!overlaps.isEmpty()) {
            source.sendSystemMessage(Component.literal("§e注意：与已有据点重叠：§f"
                + String.join(", ", overlaps)));
        }
    }

    private static int info(CommandSourceStack source, String name) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        PointDraft point = draft.point(name);
        if (point == null) {
            source.sendSystemMessage(Component.literal("§c没有名为 §f" + name + " §c的据点。"));
            return 0;
        }
        source.sendSystemMessage(Component.literal("§7据点 §f" + point.name() + " §7批次 §f" + point.batch()));
        source.sendSystemMessage(Component.literal("§7范围 §f" + point.box().describe()
            + " §7(y " + point.box().minY() + ".." + point.box().maxY() + ")"));
        return 1;
    }

    private static int remove(CommandSourceStack source, String name) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        if (!draft.removePoint(name)) {
            source.sendSystemMessage(Component.literal("§c没有名为 §f" + name + " §c的据点。"));
            return 0;
        }
        source.sendSystemMessage(Component.literal("§e已删除据点 §f" + name));
        return 1;
    }

    private static int clearCorner(CommandSourceStack source, String name) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        MapEditDraft.PendingCorner pending = draft.existingCorner(name);
        if (pending == null) {
            source.sendSystemMessage(Component.literal("§7没有待提交的据点角点：" + name));
            return 1;
        }
        pending.pos1 = null;
        pending.pos2 = null;
        source.sendSystemMessage(Component.literal("§e已清空据点角点：§f" + name));
        return 1;
    }

    private static int list(CommandSourceStack source) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        if (draft.pointCount() == 0) {
            source.sendSystemMessage(Component.literal("§7当前没有据点，用 §f/espetro edit point corner <名字> §7开始。"));
        } else {
            source.sendSystemMessage(Component.literal("§a据点（" + draft.pointCount() + "）："));
            for (PointDraft point : draft.points()) {
                source.sendSystemMessage(Component.literal("§7 - §f" + point.name()
                    + " §7批次 " + point.batch() + " §7" + point.box().describe()));
            }
        }
        if (!draft.pendingCornerNames().isEmpty()) {
            source.sendSystemMessage(Component.literal("§7待提交角点：§f"
                + String.join(", ", draft.pendingCornerNames())));
        }
        for (String warning : draft.warnings()) {
            source.sendSystemMessage(Component.literal("§e! " + warning));
        }
        return 1;
    }

    private static int show(CommandSourceStack source, @Nullable String name) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null || !(source.getEntity() instanceof ServerPlayer player)) {
            if (draft != null) {
                source.sendSystemMessage(Component.literal("§c可视化预览需要以玩家身份执行。"));
            }
            return 0;
        }
        ServerLevel level = player.serverLevel();
        if (name == null) {
            MapEditPreview.capturePoints(level, draft.points());
            source.sendSystemMessage(Component.literal("§a已显示全部据点范围（白色粒子）。"));
        } else {
            PointDraft point = draft.point(name);
            if (point == null) {
                source.sendSystemMessage(Component.literal("§c没有名为 §f" + name + " §c的据点。"));
                return 0;
            }
            MapEditPreview.capturePoint(level, point);
            source.sendSystemMessage(Component.literal("§a已显示据点 §f" + name + " §a的范围。"));
        }
        return 1;
    }

    // ------------------------------------------------------------------
    // spawn
    // ------------------------------------------------------------------

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> spawnNode() {
        return Commands.literal("spawn")
            .then(Commands.literal("attack")
                .executes(ctx -> setSpawn(ctx.getSource(), Side.ATTACK)))
            .then(Commands.literal("defend")
                .executes(ctx -> setSpawn(ctx.getSource(), Side.DEFEND)))
            .then(Commands.literal("show")
                .executes(ctx -> showSpawn(ctx.getSource())))
            .then(Commands.literal("status")
                .executes(ctx -> showSpawn(ctx.getSource())));
    }

    private static int setSpawn(CommandSourceStack source, Side side) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        Vec3 pos = source.getPosition();
        float yaw = source.getRotation().y;
        Spot spot = new Spot(pos.x, pos.y, pos.z, MapEditDraft.snapYaw(yaw));
        draft.setSpawn(side, spot);
        source.sendSystemMessage(Component.literal("§a已设置" + side.label() + "出生点：§f"
            + spot.describe()));
        return 1;
    }

    private static int showSpawn(CommandSourceStack source) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        ServerLevel level = source.getEntity() instanceof ServerPlayer player ? player.serverLevel() : null;
        for (Side side : Side.values()) {
            Spot spot = draft.spawn(side);
            if (spot == null) {
                source.sendSystemMessage(Component.literal("§7" + side.label() + "出生点：§c未设置"));
                continue;
            }
            source.sendSystemMessage(Component.literal("§7" + side.label() + "出生点：§f" + spot.describe()));
            if (level != null) {
                MapEditPreview.spawn(level, side, spot);
            }
        }
        return 1;
    }

    // ------------------------------------------------------------------
    // veh
    // ------------------------------------------------------------------

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> vehNode() {
        return Commands.literal("veh")
            .then(Commands.literal("add")
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests(typeSuggestions())
                    .then(Commands.argument("side", StringArgumentType.word())
                        .suggests(sideSuggestions())
                        .executes(ctx -> vehAdd(ctx.getSource(),
                            StringArgumentType.getString(ctx, "type"),
                            StringArgumentType.getString(ctx, "side"),
                            null))
                        .then(Commands.argument("id", StringArgumentType.word())
                            .executes(ctx -> vehAdd(ctx.getSource(),
                                StringArgumentType.getString(ctx, "type"),
                                StringArgumentType.getString(ctx, "side"),
                                StringArgumentType.getString(ctx, "id")))))))
            .then(Commands.literal("del")
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests(typeSuggestions())
                    .then(Commands.argument("id", StringArgumentType.word())
                        .suggests(idSuggestions())
                        .executes(ctx -> vehDel(ctx.getSource(),
                            StringArgumentType.getString(ctx, "type"),
                            StringArgumentType.getString(ctx, "id"))))))
            .then(Commands.literal("list")
                .executes(ctx -> vehList(ctx.getSource(), null))
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests(typeSuggestions())
                    .executes(ctx -> vehList(ctx.getSource(), StringArgumentType.getString(ctx, "type")))))
            .then(Commands.literal("show")
                .executes(ctx -> vehShow(ctx.getSource(), null))
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests(typeSuggestions())
                    .executes(ctx -> vehShow(ctx.getSource(), StringArgumentType.getString(ctx, "type")))))
            .then(Commands.literal("type")
                .then(Commands.literal("list").executes(ctx -> vehTypeList(ctx.getSource())))
                .then(Commands.literal("add")
                    .then(Commands.argument("type", StringArgumentType.word())
                        .executes(ctx -> vehTypeAdd(ctx.getSource(),
                            StringArgumentType.getString(ctx, "type"))))));
    }

    private static int vehAdd(CommandSourceStack source, String type, String sideName, @Nullable String id) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        Side side = Side.of(sideName);
        if (side == null) {
            source.sendSystemMessage(Component.literal("§c方位只能是 attack 或 defend。"));
            return 0;
        }
        Vec3 pos = source.getPosition();
        float yaw = source.getRotation().y;
        Spot spot = new Spot(pos.x, pos.y, pos.z, MapEditDraft.snapYaw(yaw));
        String result = draft.addVehPoint(type, side, spot, id);
        if (result == null) {
            source.sendSystemMessage(Component.literal("§c新增载具点失败（类型或 id 非法）。"));
            return 0;
        }
        source.sendSystemMessage(Component.literal("§a已写入载具点 §f" + type + " §7/ §f" + result
            + " §7的" + side.label() + "侧：§f" + spot.describe()));
        if (id == null) {
            source.sendSystemMessage(Component.literal("§7（自动生成 id，另一侧记得再执行一次 "
                + (side == Side.ATTACK ? "defend" : "attack") + "）"));
        }
        return 1;
    }

    private static int vehDel(CommandSourceStack source, String type, String id) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        if (!draft.removeVehPoint(type, id)) {
            source.sendSystemMessage(Component.literal("§c没有载具点 §f" + type + " §c/ §f" + id));
            return 0;
        }
        source.sendSystemMessage(Component.literal("§e已删除载具点 §f" + type + " §7/ §f" + id));
        return 1;
    }

    private static int vehList(CommandSourceStack source, @Nullable String type) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        if (type == null) {
            if (draft.vehTypesOrdered().isEmpty()) {
                source.sendSystemMessage(Component.literal("§7当前没有载具类型。"));
                return 1;
            }
            source.sendSystemMessage(Component.literal("§a载具类型（" + draft.vehTypesOrdered().size() + "）："));
            for (String current : draft.vehTypesOrdered()) {
                source.sendSystemMessage(Component.literal("§7 - §f" + current
                    + " §7共 " + draft.vehPoints(current).size() + " 个点"));
            }
            return 1;
        }
        List<VehPoint> points = draft.vehPoints(type);
        if (points.isEmpty()) {
            source.sendSystemMessage(Component.literal("§7类型 §f" + type + " §7还没有载具点。"));
            return 1;
        }
        source.sendSystemMessage(Component.literal("§a" + type + " 的载具点（" + points.size() + "）："));
        for (int i = 0; i < points.size(); i++) {
            VehPoint point = points.get(i);
            source.sendSystemMessage(Component.literal("§7 " + (i + 1) + ". §f" + point.id()
                + " §7attack=" + (point.attack() == null ? "§c-§7" : "§f" + point.attack().describe())
                + " §7defend=" + (point.defend() == null ? "§c-§7" : "§f" + point.defend().describe())));
        }
        return 1;
    }

    private static int vehShow(CommandSourceStack source, @Nullable String type) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null || !(source.getEntity() instanceof ServerPlayer player)) {
            if (draft != null) {
                source.sendSystemMessage(Component.literal("§c可视化预览需要以玩家身份执行。"));
            }
            return 0;
        }
        ServerLevel level = player.serverLevel();
        if (type == null) {
            for (String current : draft.vehTypesOrdered()) {
                MapEditPreview.vehPoints(level, current, draft.vehPoints(current));
            }
            source.sendSystemMessage(Component.literal("§a已显示全部载具点（蓝=进攻，橙=防守）。"));
        } else {
            MapEditPreview.vehPoints(level, type, draft.vehPoints(type));
            source.sendSystemMessage(Component.literal("§a已显示 §f" + type + " §a的载具点。"));
        }
        return 1;
    }

    private static int vehTypeList(CommandSourceStack source) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        source.sendSystemMessage(Component.literal("§a草稿里的载具类型：§f"
            + (draft.vehTypesOrdered().isEmpty() ? "（无）" : String.join(", ", draft.vehTypesOrdered()))));
        ActiveMapConfig map = MapEditManager.getInstance().currentMap();
        if (map != null && map.vehSpawn != null && map.vehSpawn.vehicleTypes != null) {
            source.sendSystemMessage(Component.literal("§7地图出生点类型：§f"
                + String.join(", ", map.vehSpawn.vehicleTypes)));
        }
        return 1;
    }

    private static int vehTypeAdd(CommandSourceStack source, String type) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        String normalized = MapEditDraft.normalizeType(type);
        if (normalized.isBlank()) {
            source.sendSystemMessage(Component.literal("§c类型名不能为空。"));
            return 0;
        }
        draft.addVehType(normalized);
        source.sendSystemMessage(Component.literal("§a已声明载具类型 §f" + normalized));
        return 1;
    }

    // ------------------------------------------------------------------
    // capture / status
    // ------------------------------------------------------------------

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> captureNode() {
        return Commands.literal("capture")
            .then(Commands.literal("list").executes(ctx -> captureList(ctx.getSource())))
            .then(Commands.literal("target")
                .then(Commands.argument("target", StringArgumentType.greedyString())
                    .suggests((ctx, builder) -> {
                        for (String target : availableCaptureTargets()) {
                            builder.suggest(target);
                        }
                        return builder.buildFuture();
                    })
                    .executes(ctx -> captureTarget(ctx.getSource(),
                        StringArgumentType.getString(ctx, "target")))));
    }

    private static int captureList(CommandSourceStack source) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        source.sendSystemMessage(Component.literal("§a当前写入目标：§f" + draft.captureTarget()));
        for (String target : availableCaptureTargets()) {
            source.sendSystemMessage(Component.literal("§7 - §f" + target));
        }
        return 1;
    }

    private static int captureTarget(CommandSourceStack source, String target) {
        MapEditDraft draft = MapEditManager.getInstance().requireDraft(source);
        if (draft == null) {
            return 0;
        }
        List<String> available = availableCaptureTargets();
        String match = null;
        for (String candidate : available) {
            if (candidate.equalsIgnoreCase(target.trim())) {
                match = candidate;
                break;
            }
        }
        if (match == null) {
            source.sendSystemMessage(Component.literal("§c无效的写入目标：§f" + target
                + "§c。可选：" + String.join(", ", available)));
            return 0;
        }
        draft.setCaptureTarget(match);
        source.sendSystemMessage(Component.literal("§a据点写入目标已切换为：§f" + match));
        return 1;
    }

    private static List<String> availableCaptureTargets() {
        ActiveMapConfig map = MapEditManager.getInstance().currentMap();
        if (map == null || map.esConfigDir == null) {
            return List.of(MapEditDraft.LEGACY_TARGET);
        }
        Path esConfigDir = map.esConfigDir;
        return MapEditConfigWriter.captureTargets(esConfigDir);
    }

    private static int status(CommandSourceStack source) {
        MapEditManager manager = MapEditManager.getInstance();
        source.sendSystemMessage(Component.literal("§7[地图编辑] §f" + manager.describeSession()));
        MapEditDraft draft = manager.draft();
        if (draft == null) {
            return 1;
        }
        source.sendSystemMessage(Component.literal("§7据点 §f" + draft.pointCount()
            + "§7，载具类型 §f" + draft.vehTypesOrdered().size()
            + "§7，载具点 §f" + draft.allVehPoints().values().stream().mapToInt(List::size).sum()
            + "§7，写入目标 §f" + draft.captureTarget()));
        List<String> problems = draft.problems();
        if (!problems.isEmpty()) {
            source.sendSystemMessage(Component.literal("§c保存前必须解决："));
            for (String problem : problems) {
                source.sendSystemMessage(Component.literal("§c - " + problem));
            }
        }
        return 1;
    }

    /** 供其它指令（如 configsave 的提示）复用的草稿摘要。 */
    public static List<String> summaryLines() {
        MapEditDraft draft = MapEditManager.getInstance().draft();
        return draft == null ? List.of() : draft.summary();
    }

    // ------------------------------------------------------------------
    // suggestions
    // ------------------------------------------------------------------

    private static SuggestionProvider<CommandSourceStack> pointNameSuggestions() {
        return (ctx, builder) -> {
            MapEditDraft draft = MapEditManager.getInstance().draft();
            if (draft != null) {
                for (PointDraft point : draft.points()) {
                    builder.suggest(point.name());
                }
                for (String name : draft.pendingCornerNames()) {
                    builder.suggest(name);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> pendingNameSuggestions() {
        return (ctx, builder) -> {
            MapEditDraft draft = MapEditManager.getInstance().draft();
            if (draft != null) {
                for (String name : draft.pendingCornerNames()) {
                    builder.suggest(name);
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> typeSuggestions() {
        return (ctx, builder) -> {
            Set<String> types = new LinkedHashSet<>();
            MapEditDraft draft = MapEditManager.getInstance().draft();
            if (draft != null) {
                types.addAll(draft.vehTypesOrdered());
            }
            ActiveMapConfig map = MapEditManager.getInstance().currentMap();
            if (map != null && map.vehSpawn != null && map.vehSpawn.vehicleTypes != null) {
                types.addAll(map.vehSpawn.vehicleTypes);
            }
            types.add("mbt");
            types.add("mgs");
            types.add("ifv");
            types.add("acv");
            types.add("apc");
            types.add("car");
            types.add("truck");
            types.add("supply_truck");
            types.add("transport_helicopter");
            for (String type : types) {
                builder.suggest(type);
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> idSuggestions() {
        return (ctx, builder) -> {
            MapEditDraft draft = MapEditManager.getInstance().draft();
            String type = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "type");
            if (draft != null && type != null) {
                for (VehPoint point : draft.vehPoints(type)) {
                    builder.suggest(point.id());
                }
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> sideSuggestions() {
        return (ctx, builder) -> {
            builder.suggest("attack");
            builder.suggest("defend");
            return builder.buildFuture();
        };
    }
}
