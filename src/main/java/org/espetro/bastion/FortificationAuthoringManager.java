package org.espetro.bastion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.team.GameStateManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工事可视化编辑器（选定棒）的服务端核心：选区状态、导出前预检、模板导出、
 * 定义写入、免重启热重载。**仅管理员可用**（permission 2）。
 *
 * <p>导出策略要点：默认 {@code ignoreBlock = Blocks.AIR}，即只保存实际存在的方块；
 * 空气格不写入模板（否则编译后是 {@code EXPLICIT_AIR}，放置时会要求整个包围盒清空）。
 * 管理员若想让某格"保持原样"，在该格放 {@code structure_void}。</p>
 */
public final class FortificationAuthoringManager {

    /** 与 /espetro 根命令一致：仅管理员。 */
    public static final int PERMISSION_LEVEL = 2;

    private static final DateTimeFormatter STAMP =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> FORBIDDEN_IDS = Set.of(
        "minecraft:command_block", "minecraft:chain_command_block",
        "minecraft:repeating_command_block", "minecraft:structure_block",
        "minecraft:jigsaw");
    private static final TagKey<Block> FORBIDDEN_TAG = TagKey.create(Registries.BLOCK,
        new ResourceLocation("espetro:forbidden_fortification_blocks"));
    /** 自动刷新 HUD 统计的最大体积（超过只刷新尺寸，避免每 2 秒扫 20 万格）。 */
    private static final int AUTO_STATS_MAX_VOLUME = 32_768;

    private static final FortificationAuthoringManager INSTANCE = new FortificationAuthoringManager();

    /** 选区同步失败计数（tick 里调用，失败只记日志不崩服）。 */
    private static final Map<UUID, Integer> SYNC_FAILURES = new ConcurrentHashMap<>();

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private FortificationAuthoringManager() {
    }

    public static FortificationAuthoringManager getInstance() {
        return INSTANCE;
    }

    // ==================== 选区 ====================

    public static final class Session {
        public ResourceKey<Level> dimension;
        public BlockPos a;
        public BlockPos b;
        public BlockPos anchor;
        public Stats cachedStats;
        public long cachedAtTick;

        void clear() {
            dimension = null;
            a = null;
            b = null;
            anchor = null;
            cachedStats = null;
        }
    }

    public record Stats(int sizeX, int sizeY, int sizeZ, int blockCount, int entityCount,
                        int ignoredBlocks, BlockPos min, BlockPos max, BlockPos pivot,
                        List<String> errors, List<String> warnings) {
        public boolean ready() {
            return errors.isEmpty();
        }

        public int volume() {
            return sizeX * sizeY * sizeZ;
        }
    }

    public record SaveOptions(String displayName, String icon, int cost, int progress,
                              boolean radioRange, List<String> roles, boolean includeEntities,
                              boolean carve, List<Integer> damageableEntities,
                              boolean writeDefinition, boolean force, boolean instant) {
        public static SaveOptions defaults(String name) {
            return new SaveOptions(name, "minecraft:item/stick", 100, 100, true,
                List.of("commander", "squad_leader", "fireteam_leader"), true, false,
                List.of(), true, false, false);
        }
    }

    @Nullable
    public Session session(ServerPlayer player) {
        return player == null ? null : sessions.get(player.getUUID());
    }

    public Session sessionOrCreate(ServerPlayer player) {
        return sessions.computeIfAbsent(player.getUUID(), k -> new Session());
    }

    public void clear(ServerPlayer player) {
        if (player == null) return;
        Session session = sessions.get(player.getUUID());
        if (session != null) {
            session.clear();
            session.cachedStats = null;
        }
    }

    public void forget(UUID playerId) {
        if (playerId != null) sessions.remove(playerId);
    }

    public static boolean hasPermission(@Nullable ServerPlayer player) {
        return player != null && player.hasPermissions(PERMISSION_LEVEL);
    }

    private static int allowedAxis() {
        return Math.max(1, Math.min(FortificationConfig.HARD_MAX_TEMPLATE_AXIS,
            FortificationConfig.limits().maxTemplateAxis));
    }

    /** 设置角点（{@code first=true} 表示角 A）。 */
    public String setCorner(ServerPlayer player, boolean first, BlockPos pos) {
        Session session = sessionOrCreate(player);
        if (session.dimension != null && !session.dimension.equals(player.level().dimension())) {
            session.clear();
        }
        session.dimension = player.level().dimension();
        if (first) session.a = pos.immutable();
        else session.b = pos.immutable();
        session.cachedStats = null;

        int axis = allowedAxis();
        if (session.a != null && session.b != null) {
            int dx = Math.abs(session.a.getX() - session.b.getX()) + 1;
            int dy = Math.abs(session.a.getY() - session.b.getY()) + 1;
            int dz = Math.abs(session.a.getZ() - session.b.getZ()) + 1;
            if (dx > axis || dy > axis || dz > axis) {
                if (first) session.a = null;
                else session.b = null;
                session.cachedStats = null;
                return "§c选区 " + dx + "×" + dy + "×" + dz + " 超过单轴上限 " + axis + "，已撤销该角。";
            }
            // 锚点若不在新选区内则清空
            if (session.anchor != null && !inside(session.anchor, session.a, session.b)) {
                session.anchor = null;
            }
        }
        return cornerMessage(session, first ? "角 A" : "角 B", pos);
    }

    public String setAnchor(ServerPlayer player, BlockPos pos) {
        Session session = sessionOrCreate(player);
        if (session.a == null || session.b == null) {
            return "§c请先选定两个角，再设锚点。";
        }
        if (!inside(pos, session.a, session.b)) {
            return "§c锚点必须在选区内。";
        }
        session.anchor = pos.immutable();
        session.cachedStats = null;
        return "§a锚点已设为 §f(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()
            + "§a) §7= 放置时的旋转中心，转向时该格不动。";
    }

    private static String cornerMessage(Session session, String label, BlockPos pos) {
        StringBuilder sb = new StringBuilder("§a").append(label).append(" §f(")
            .append(pos.getX()).append(", ").append(pos.getY()).append(", ")
            .append(pos.getZ()).append("§a)");
        if (session.a != null && session.b != null) {
            sb.append(" §7尺寸 ").append(Math.abs(session.a.getX() - session.b.getX()) + 1)
                .append("×").append(Math.abs(session.a.getY() - session.b.getY()) + 1)
                .append("×").append(Math.abs(session.a.getZ() - session.b.getZ()) + 1);
            if (session.anchor == null) sb.append(" §e还需要设锚点（潜行+右键）");
        } else {
            sb.append(" §7还要选另一个角");
        }
        return sb.toString();
    }

    private static boolean inside(BlockPos pos, BlockPos a, BlockPos b) {
        return pos.getX() >= Math.min(a.getX(), b.getX()) && pos.getX() <= Math.max(a.getX(), b.getX())
            && pos.getY() >= Math.min(a.getY(), b.getY()) && pos.getY() <= Math.max(a.getY(), b.getY())
            && pos.getZ() >= Math.min(a.getZ(), b.getZ()) && pos.getZ() <= Math.max(a.getZ(), b.getZ());
    }

    // ==================== 预检 ====================

    /** 重新计算选区统计与预检报告（命令/导出时调用）。 */
    @Nullable
    public Stats stats(ServerPlayer player) {
        Session session = sessions.get(player.getUUID());
        if (session == null || session.a == null || session.b == null) return null;
        Stats stats = computeStats(player, session);
        session.cachedStats = stats;
        session.cachedAtTick = player.serverLevel().getGameTime();
        return stats;
    }

    /** 定时刷新（HUD 用）：体积过大时只给尺寸，不扫方块。 */
    @Nullable
    public Stats cachedStats(ServerPlayer player) {
        Session session = sessions.get(player.getUUID());
        if (session == null || session.a == null || session.b == null) return null;
        BlockPos a = session.a;
        BlockPos b = session.b;
        int dx = Math.abs(a.getX() - b.getX()) + 1;
        int dy = Math.abs(a.getY() - b.getY()) + 1;
        int dz = Math.abs(a.getZ() - b.getZ()) + 1;
        long volume = (long) dx * dy * dz;
        long now = player.serverLevel().getGameTime();
        boolean cheap = volume <= AUTO_STATS_MAX_VOLUME;
        if (cheap && (session.cachedStats == null || now - session.cachedAtTick >= 40)) {
            session.cachedStats = computeStats(player, session);
            session.cachedAtTick = now;
        }
        if (session.cachedStats != null) return session.cachedStats;

        // 大选区：只报尺寸与锚点，避免每 2 秒扫几十万格；save/info 会走完整预检。
        BlockPos min = new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()),
            Math.min(a.getZ(), b.getZ()));
        BlockPos max = new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()),
            Math.max(a.getZ(), b.getZ()));
        List<String> errors = new ArrayList<>();
        int axis = allowedAxis();
        if (dx > axis || dy > axis || dz > axis) {
            errors.add("选区 " + dx + "×" + dy + "×" + dz + " 超过单轴上限 " + axis);
        }
        return new Stats(dx, dy, dz, 0, 0, 0, min, max, pivotOf(session, min), errors, List.of());
    }

    private Stats computeStats(ServerPlayer player, Session session) {
        ServerLevel level = player.serverLevel();
        BlockPos a = session.a;
        BlockPos b = session.b;
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxX = Math.max(a.getX(), b.getX());
        int maxY = Math.max(a.getY(), b.getY());
        int maxZ = Math.max(a.getZ(), b.getZ());
        BlockPos min = new BlockPos(minX, minY, minZ);
        BlockPos max = new BlockPos(maxX, maxY, maxZ);
        int dx = maxX - minX + 1;
        int dy = maxY - minY + 1;
        int dz = maxZ - minZ + 1;

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        FortificationConfig.Limits limits = FortificationConfig.limits();
        FortificationConfig.EntityPolicy policy = FortificationConfig.entityPolicy();
        int axis = allowedAxis();
        if (dx > axis || dy > axis || dz > axis) {
            errors.add("尺寸 " + dx + "×" + dy + "×" + dz + " 超过单轴上限 " + axis);
        }
        long volume = (long) dx * dy * dz;
        if (volume > 4_000_000L) {
            errors.add("选区体积 " + volume + " 过大（上限 4000000）");
            return new Stats(dx, dy, dz, 0, 0, 0, min, max, pivotOf(session, min), errors, warnings);
        }

        int blocks = 0;
        int ignored = 0;
        List<String> forbidden = new ArrayList<>();
        List<String> strippedBe = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;
            if (state.is(Blocks.STRUCTURE_VOID)) {
                ignored++;
                continue;
            }
            blocks++;
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (FORBIDDEN_IDS.contains(id.toString()) || state.is(FORBIDDEN_TAG)) {
                if (forbidden.size() < 8) forbidden.add(describe(pos, id));
                continue;
            }
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                ResourceLocation beId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(
                    blockEntity.getType());
                if (beId != null && !FortificationNbtSanitizer.allowsBlockEntity(beId.toString(),
                    policy) && strippedBe.size() < 8) {
                    strippedBe.add(describe(pos, beId));
                }
            }
        }
        if (!forbidden.isEmpty()) {
            errors.add("含有危险方块（命令方块/结构方块/jigsaw）：" + String.join("、", forbidden));
        }
        if (blocks > limits.maxTemplateBlocks) {
            errors.add("方块数 " + blocks + " 超过上限 " + limits.maxTemplateBlocks);
        }
        if (blocks == 0) {
            errors.add("选区内没有任何方块（空气不写入模板）");
        }

        int entities = 0;
        List<String> droppedEntities = new ArrayList<>();
        List<Entity> found = level.getEntities((Entity) null,
            new net.minecraft.world.phys.AABB(min, max.offset(1, 1, 1)),
            e -> !(e instanceof Player));
        Map<String, Integer> droppedKinds = new LinkedHashMap<>();
        for (Entity entity : found) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            if (id == null) continue;
            if (FortificationNbtSanitizer.allowsEntity(id.toString(), policy)) {
                entities++;
            } else {
                droppedKinds.merge(id.toString(), 1, Integer::sum);
            }
        }
        if (!droppedKinds.isEmpty()) {
            droppedKinds.forEach((id, count) -> droppedEntities.add(id + "×" + count));
        }
        if (entities > limits.maxTemplateEntities) {
            errors.add("实体数 " + entities + " 超过上限 " + limits.maxTemplateEntities);
        }
        if (!strippedBe.isEmpty()) {
            String message = "方块实体 NBT 会被 entity_policy 剔除（方块本体保留）："
                + String.join("、", strippedBe);
            if (policy.strict()) errors.add(message);
            else warnings.add(message);
        }
        if (!droppedEntities.isEmpty()) {
            String message = "实体不在允许列表，放置时会被剔除："
                + String.join("、", droppedEntities)
                + "（可用 entity_policy.extra_entity_types 放开）";
            if (policy.strict()) errors.add(message);
            else warnings.add(message);
        }
        if (ignored > 0) {
            warnings.add("含 " + ignored + " 个结构空位（放置时完全不触碰这些格子）");
        }
        if (session.anchor == null) {
            errors.add("还没有设锚点（潜行+右键）");
        }
        return new Stats(dx, dy, dz, blocks, entities, ignored, min, max,
            pivotOf(session, min), errors, warnings);
    }

    private static String describe(BlockPos pos, ResourceLocation id) {
        return id + "@" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    @Nullable
    private static BlockPos pivotOf(Session session, BlockPos min) {
        if (session.anchor == null) return null;
        return session.anchor.subtract(min);
    }

    // ==================== 导出 ====================

    public String save(ServerPlayer player, String rawName, SaveOptions options) {
        if (!hasPermission(player)) return "§c仅管理员可用。";
        Session session = sessions.get(player.getUUID());
        if (session == null || session.a == null || session.b == null) {
            return "§c请先用选定棒选好两个角（左键=角A，右键=角B）。";
        }
        String name = FortificationTemplateStore.normalizedName(rawName);
        if (!FortificationTemplateStore.isValidName(name)) {
            return "§c名字只能用小写字母/数字/下划线，1~48 字符。";
        }
        String fortId = "espetro:" + name;
        if (FortificationConfig.get(fortId) != null && !options.force()) {
            return "§c已存在工事定义 " + fortId + "，换名字或加 §f--force§c 覆盖。";
        }
        Stats stats = stats(player);
        if (stats == null) return "§c选区无效。";
        if (!stats.ready()) {
            return "§c预检未通过：" + stats.errors().get(0)
                + (stats.errors().size() > 1 ? "（共 " + stats.errors().size()
                + " 项，/espetro fort info 查看）" : "");
        }
        BlockPos pivot = stats.pivot();
        if (pivot == null) return "§c还没有设锚点。";

        ServerLevel level = player.serverLevel();
        Vec3i size = new Vec3i(stats.sizeX(), stats.sizeY(), stats.sizeZ());
        String stamp = LocalDateTime.now().format(STAMP);
        int bytes;
        try {
            StructureTemplate template = new StructureTemplate();
            template.setAuthor(player.getGameProfile().getName());
            template.fillFromWorld(level, stats.min(), size, options.includeEntities(),
                options.carve() ? Blocks.STRUCTURE_VOID : Blocks.AIR);
            CompoundTag tag = template.save(new CompoundTag());
            bytes = tag.toString().getBytes(StandardCharsets.UTF_8).length;
            FortificationConfig.Limits limits = FortificationConfig.limits();
            if (bytes > limits.maxTemplateNbtBytes) {
                return "§c模板 NBT " + bytes + " 字节超过上限 " + limits.maxTemplateNbtBytes;
            }
            Path file = FortificationTemplateStore.pathForName(name);
            Files.createDirectories(file.getParent());
            String backup = FortificationTemplateStore.backup(name, stamp);
            NbtIo.writeCompressed(tag, file.toFile());
            writeMeta(name, player, session, stats, options, bytes, stamp);
            Espetro.LOGGER.info("[工事编辑器] {} 导出模板 {} ({} 字节, 方块 {} 实体 {}{})",
                player.getName().getString(), name, bytes, stats.blockCount(),
                stats.entityCount(), backup == null ? "" : ", 旧版已备份 " + backup);
        } catch (Exception e) {
            Espetro.LOGGER.error("[工事编辑器] 导出模板 {} 失败", name, e);
            return "§c导出失败：" + (e.getMessage() == null ? e.toString() : e.getMessage());
        }

        String defineNote;
        if (options.writeDefinition()) {
            try {
                defineNote = appendDefinition(name, pivot, options, player);
            } catch (Exception e) {
                Espetro.LOGGER.error("[工事编辑器] 写入定义 {} 失败", name, e);
                return "§e模板已导出，但定义写入失败：" + e.getMessage()
                    + "§e（可手动添加到 fortifications.json）";
            }
        } else {
            defineNote = "§7（--no-define，未写入定义）";
        }

        StringBuilder message = new StringBuilder("§a已导出工事模板 §f")
            .append("espetro:fortifications/").append(name)
            .append("§a：方块 ").append(stats.blockCount())
            .append("、实体 ").append(stats.entityCount())
            .append("、尺寸 ").append(stats.sizeX()).append("×").append(stats.sizeY())
            .append("×").append(stats.sizeZ())
            .append("、锚点 pivot (").append(pivot.getX()).append(", ").append(pivot.getY())
            .append(", ").append(pivot.getZ()).append(")");
        if (!stats.warnings().isEmpty()) {
            message.append("\n§e预检提醒 ").append(stats.warnings().size()).append(" 项：")
                .append(stats.warnings().get(0));
        }
        message.append("\n").append(defineNote);
        message.append("\n§7执行 §f/espetro fort reload §7即可免重启生效（需在主城且无在建工事）。");
        return message.toString();
    }

    /**
     * 追加一条 v2 定义。使用 Gson 全量重写（与内置文件的排版一致），
     * 写前带时间戳备份；解析校验失败即回滚。
     */
    private String appendDefinition(String name, BlockPos pivot, SaveOptions options,
                                    ServerPlayer player) throws IOException {
        Path configPath = FortificationConfig.serverConfigPath();
        Files.createDirectories(configPath.getParent());
        String original;
        if (Files.isRegularFile(configPath)) {
            original = Files.readString(configPath, StandardCharsets.UTF_8);
        } else {
            original = FortificationConfig.bundledDefaultJsonSafe();
            if (original == null) throw new IOException("缺少内置 fortifications.json 模板");
        }
        JsonObject root;
        try {
            JsonElement parsed = JsonParser.parseString(original);
            if (!parsed.isJsonObject()) throw new IOException("配置根不是对象");
            root = parsed.getAsJsonObject();
        } catch (Exception e) {
            throw new IOException("配置当前不是严格 JSON（可能有注释），请手动添加定义：" + e.getMessage());
        }
        if (!root.has("schema_version")) {
            throw new IOException("配置仍是 v1 老格式，请先升级到 schema_version=2");
        }
        JsonArray entries = root.has("fortifications") && root.get("fortifications").isJsonArray()
            ? root.getAsJsonArray("fortifications") : new JsonArray();
        String fortId = "espetro:" + name;
        for (JsonElement element : entries) {
            if (element.isJsonObject()
                && fortId.equals(element.getAsJsonObject().has("id")
                    ? element.getAsJsonObject().get("id").getAsString() : null)) {
                entries.remove(element);
                break;
            }
        }
        entries.add(buildDefinition(name, pivot, options));

        String backupName = configPath.getFileName() + ".bak-"
            + LocalDateTime.now().format(STAMP);
        Files.copy(configPath, configPath.resolveSibling(backupName),
            StandardCopyOption.REPLACE_EXISTING);
        String updated = GSON.toJson(root);
        JsonParser.parseString(updated);   // 自检
        Files.writeString(configPath, updated, StandardCharsets.UTF_8);
        Espetro.LOGGER.info("[工事编辑器] {} 写入定义 {}", player.getName().getString(), fortId);
        return "§a已写入定义 §f" + fortId + "§a（备份 " + backupName + "）";
    }

    private JsonObject buildDefinition(String name, BlockPos pivot, SaveOptions options) {
        JsonObject def = new JsonObject();
        def.addProperty("id", "espetro:" + name);
        def.addProperty("display_name", options.displayName() == null
            || options.displayName().isBlank() ? name : options.displayName());
        JsonObject icon = new JsonObject();
        icon.addProperty("texture", options.icon());
        def.add("icon", icon);
        def.addProperty("behavior", "generic");

        JsonObject placement = new JsonObject();
        placement.addProperty("type", "structure");
        placement.addProperty("template", "espetro:fortifications/" + name);
        placement.add("origin_offset", intArray(0, 0, 0));
        placement.add("pivot", intArray(pivot.getX(), pivot.getY(), pivot.getZ()));
        placement.addProperty("rotation", "player_facing");
        placement.addProperty("mirror", "none");
        placement.addProperty("air_policy", "reject_non_replaceable");
        placement.addProperty("include_entities", options.includeEntities());
        placement.addProperty("palette_index", 0);
        def.add("placement", placement);

        JsonObject cost = new JsonObject();
        cost.addProperty("construction", options.cost());
        cost.addProperty("ammunition", 0);
        def.add("cost", cost);

        JsonObject construction = new JsonObject();
        construction.addProperty("required_progress", options.progress());
        construction.addProperty("build_per_hit", 5);
        construction.addProperty("remove_per_hit", 5);
        if (options.instant()) construction.addProperty("instant", true);
        def.add("construction", construction);

        JsonObject durability = new JsonObject();
        durability.addProperty("structural_value", options.progress());
        durability.addProperty("repair_per_hit", 5);
        JsonArray damageable = new JsonArray();
        for (Integer index : options.damageableEntities()) {
            if (index != null && index >= 0) damageable.add(index);
        }
        durability.add("damageable_structure_entities", damageable);
        JsonObject reduction = new JsonObject();
        reduction.addProperty("explosion", 0.9);
        reduction.addProperty("projectile", 0.9);
        reduction.addProperty("direct_break", 0.0);
        durability.add("damage_reduction", reduction);
        def.add("durability", durability);

        JsonObject requirements = new JsonObject();
        requirements.addProperty("require_radio_range", options.radioRange());
        JsonArray roles = new JsonArray();
        for (String role : options.roles()) roles.add(role);
        requirements.add("usable_by", roles);
        def.add("requirements", requirements);
        return def;
    }

    private static JsonArray intArray(int x, int y, int z) {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private void writeMeta(String name, ServerPlayer player, Session session, Stats stats,
                           SaveOptions options, int bytes, String stamp) throws IOException {
        JsonObject meta = new JsonObject();
        meta.addProperty("name", name);
        meta.addProperty("author", player.getGameProfile().getName());
        meta.addProperty("time", stamp);
        meta.addProperty("dimension", player.level().dimension().location().toString());
        meta.add("size", intArray(stats.sizeX(), stats.sizeY(), stats.sizeZ()));
        meta.addProperty("blocks", stats.blockCount());
        meta.addProperty("entities", stats.entityCount());
        meta.addProperty("ignored_blocks", stats.ignoredBlocks());
        meta.addProperty("nbt_bytes", bytes);
        meta.addProperty("include_entities", options.includeEntities());
        meta.addProperty("carve", options.carve());
        JsonObject region = new JsonObject();
        region.add("a", intArray(session.a.getX(), session.a.getY(), session.a.getZ()));
        region.add("b", intArray(session.b.getX(), session.b.getY(), session.b.getZ()));
        region.add("anchor", intArray(session.anchor.getX(), session.anchor.getY(),
            session.anchor.getZ()));
        meta.add("region", region);
        Files.writeString(FortificationTemplateStore.metaPathForName(name), GSON.toJson(meta),
            StandardCharsets.UTF_8);
    }

    // ==================== 热重载 ====================

    /** 免重启重载工事定义；门槛：主城阶段 + 没有在建/已建工事。 */
    public String reload(ServerPlayer player) {
        return reload(player == null ? null : player.getServer());
    }

    /**
     * 免重启重载工事定义（可由控制台执行，{@code server} 为空时静默失败）。
     */
    public String reload(@Nullable net.minecraft.server.MinecraftServer server) {
        if (server == null) return "§c服务器未就绪，无法重载。";
        GameStateManager state = GameStateManager.getInstance();
        if (!state.getCurrentPhase().isLobbyLike()) {
            return "§c当前不是主城阶段，热重载会打断对局；请在对局结束后操作或完整重启服务器。";
        }
        if (FortificationManager.getInstance().hasActiveConstructions()) {
            return "§c存在在建/已建工事，热重载会使其失效；请完整重启服务器。";
        }
        FortificationConfig.PreparationResult result = FortificationConfig.adminReload(
            server, ExternalConfigBootstrap.getUsableMaps());
        if (!result.success()) {
            return "§c热重载失败（已回滚到重载前的定义）：" + result.error();
        }
        List<String> warnings = FortificationConfig.recentWarnings();
        String suffix = warnings.isEmpty() ? ""
            : " §e策略剔除 " + warnings.size() + " 项（/espetro fort info 查看）";
        Espetro.LOGGER.info("[工事编辑器] 工事定义热重载：global={} maps={} 策略剔除={}",
            result.definitionCount(), result.mapCount(), warnings.size());
        return "§a工事定义已热重载：global=" + result.definitionCount()
            + " maps=" + result.mapCount() + suffix;
    }

    // ==================== 列表 / 删除 ====================

    public String list() {
        List<String> names = FortificationTemplateStore.listNames();
        if (names.isEmpty()) return "§7模板目录为空：" + FortificationTemplateStore.directory();
        StringBuilder sb = new StringBuilder("§6===== 工事模板 (" + names.size() + ") =====\n§7目录："
            + FortificationTemplateStore.directory() + "\n");
        for (String name : names) {
            sb.append("§e").append(name);
            String id = "espetro:" + name;
            sb.append(FortificationConfig.get(id) != null ? " §a[已定义]" : " §c[未定义]");
            String meta = readMetaSummary(name);
            if (meta != null) sb.append(" §7").append(meta);
            sb.append('\n');
        }
        return sb.toString();
    }

    @Nullable
    private String readMetaSummary(String name) {
        Path meta = FortificationTemplateStore.metaPathForName(name);
        if (!Files.isRegularFile(meta)) return null;
        try {
            JsonObject json = JsonParser.parseString(Files.readString(meta,
                StandardCharsets.UTF_8)).getAsJsonObject();
            return json.get("blocks").getAsInt() + "块/" + json.get("entities").getAsInt()
                + "实体 " + json.get("author").getAsString() + " " + json.get("time").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    /** 删除模板文件（不动 JSON 定义）。权限由命令根的 requires 保证。 */
    public String delete(String rawName) {
        String name = FortificationTemplateStore.normalizedName(rawName);
        if (!FortificationTemplateStore.isValidName(name)) return "§c名字非法。";
        try {
            boolean removed = FortificationTemplateStore.delete(name);
            if (!removed) return "§c模板不存在：" + name;
            Espetro.LOGGER.info("[工事编辑器] 删除模板 {}", name);
            return "§a已删除模板文件 " + name + "§7（定义仍在 fortifications.json 中，如需移除请手动编辑）";
        } catch (IOException e) {
            return "§c删除失败：" + e.getMessage();
        }
    }

    // ==================== 状态输出 ====================

    public String describe(@Nullable ServerPlayer player) {
        StringBuilder sb = new StringBuilder("§6===== 工事编辑器 =====\n");
        Session session = player == null ? null : sessions.get(player.getUUID());
        if (player == null) {
            sb.append("§7（控制台执行：无个人选区信息）\n");
        } else if (session == null || session.a == null || session.b == null) {
            sb.append("§7选区：未设置（手持选定棒 左键=角A 右键=角B 潜行右键=锚点）\n");
        } else {
            Stats stats = stats(player);
            sb.append("§7角A：§f").append(fmt(session.a)).append('\n');
            sb.append("§7角B：§f").append(fmt(session.b)).append('\n');
            sb.append("§7锚点：§f").append(session.anchor == null ? "未设置" : fmt(session.anchor))
                .append('\n');
            if (stats != null) {
                sb.append("§7尺寸：§f").append(stats.sizeX()).append("×").append(stats.sizeY())
                    .append("×").append(stats.sizeZ())
                    .append(" §7方块 §f").append(stats.blockCount())
                    .append(" §7实体 §f").append(stats.entityCount())
                    .append(" §7结构空位 §f").append(stats.ignoredBlocks()).append('\n');
                sb.append("§7pivot：§f").append(stats.pivot() == null ? "(未设锚点)"
                    : fmt(stats.pivot())).append('\n');
                for (String error : stats.errors()) sb.append("§c✗ ").append(error).append('\n');
                for (String warning : stats.warnings()) sb.append("§e! ").append(warning).append('\n');
                if (stats.ready()) sb.append("§a✓ 预检通过，可以 save\n");
            }
        }
        FortificationConfig.EntityPolicy policy = FortificationConfig.entityPolicy();
        sb.append("§7实体策略：§f").append(policy.mode)
            .append(" §7附加实体类型 §f").append(policy.extraEntityTypes().size())
            .append(" §7附加方块实体 §f").append(policy.extraBlockEntityTypes().size()).append('\n');
        sb.append("§7允许实体：§f").append(String.join(", ",
            FortificationNbtSanitizer.baseEntityTypes())).append('\n');
        sb.append("§7允许方块实体：§f").append(String.join(", ",
            FortificationNbtSanitizer.baseBlockEntityTypes())).append('\n');
        List<String> warnings = FortificationConfig.recentWarnings();
        if (!warnings.isEmpty()) {
            sb.append("§e最近编译策略剔除：\n");
            for (String warning : warnings) sb.append("§e  - ").append(warning).append('\n');
        }
        sb.append("§7模板目录：§f").append(FortificationTemplateStore.directory());
        return sb.toString();
    }

    private static String fmt(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    public String usage() {
        return """
            §6===== /espetro fort =====
            §e/espetro fort wand §7- 发一根选定棒（仅管理员）
            §e/espetro fort pos1|pos2|anchor [x y z] §7- 设角/锚点（不带坐标=准星所指方块）
            §e/espetro fort info §7- 选区详情 + 预检报告
            §e/espetro fort save <名字> [参数] §7- 导出模板 + 写入定义
            §e/espetro fort list | delete <名字> §7- 管理模板文件
            §e/espetro fort reload §7- 免重启重载定义（主城 + 无在建工事）
            §e/espetro fort clear §7- 清空选区
            §7save 参数：--name 显示名 --icon 贴图 --cost N --progress N
            §7  --roles commander,squad_leader --no-entities --carve
            §7  --damageable-entities 0,2 --no-define --force --instant
            """;
    }

    /** 把当前选区同步给客户端（线框 + HUD）。 */
    public void syncTo(ServerPlayer player) {
        if (player == null) return;
        try {
            Session session = sessions.get(player.getUUID());
            if (session == null || session.a == null || session.b == null) {
                org.espetro.network.NetworkManager.sendFortificationWand(player,
                    org.espetro.network.FortificationWandPacket.clear());
                return;
            }
            Stats stats = cachedStats(player);
            if (stats == null) {
                org.espetro.network.NetworkManager.sendFortificationWand(player,
                    org.espetro.network.FortificationWandPacket.clear());
                return;
            }
            String problem = stats.errors().isEmpty() ? "" : stats.errors().get(0);
            if (problem.isEmpty() && !stats.warnings().isEmpty()) problem = stats.warnings().get(0);
            org.espetro.network.NetworkManager.sendFortificationWand(player,
                new org.espetro.network.FortificationWandPacket(true, session.a, session.b,
                    session.anchor, stats.sizeX(), stats.sizeY(), stats.sizeZ(),
                    stats.blockCount(), stats.entityCount(), stats.ready(), problem));
        } catch (Exception e) {
            // 这条同步是在服务端 tick 里发的：任何异常都不能冒泡出去崩服。
            SYNC_FAILURES.merge(player.getUUID(), 1, Integer::sum);
            if (SYNC_FAILURES.get(player.getUUID()) <= 3) {
                Espetro.LOGGER.error("[工事编辑器] 选区同步失败（已忽略，不会崩服）", e);
            }
            try {
                org.espetro.network.NetworkManager.sendFortificationWand(player,
                    org.espetro.network.FortificationWandPacket.clear());
            } catch (Exception ignored) {
                // 连清空包都发不出去就彻底放弃这次同步
            }
        }
    }

    /** 供指令使用：把 "x y z" 或准星解析为方块坐标。 */
    @Nullable
    public static BlockPos resolveTarget(ServerPlayer player, @Nullable Integer x,
                                         @Nullable Integer y, @Nullable Integer z) {
        if (x != null && y != null && z != null) return new BlockPos(x, y, z);
        net.minecraft.world.phys.HitResult hit = player.pick(64.0D, 1.0F, false);
        if (hit instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
            return blockHit.getBlockPos();
        }
        return null;
    }
}
