package org.espetro.dimension;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.espetro.Espetro;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.mapconfig.SpawnPointsSnapshot;
import org.espetro.team.GameStateManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * 地图编辑模式：把一个 EsWorld 地图模板拷进固定的编辑维度供管理员改图，
 * 保存时把 region/entities/poi/data 回写到模板目录，放弃时直接丢弃。
 *
 * <p>整套拷贝/挂载/卸载/删档都复用 {@link BattlefieldWorldManager} 的战场管线，
 * 两者通过同一把 busy 锁互斥。编辑维度 {@code espetro:edit} 由
 * {@code data/espetro/dimension/edit.json} 在数据包加载期常驻注册。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class MapEditManager {

    public static final ResourceKey<Level> EDIT_DIMENSION = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        new ResourceLocation(Espetro.MOD_ID, "edit"));

    /** 保存时回写到地图模板的子目录；EsConfig/level.dat/map.png 一律不动。 */
    private static final List<String> SAVED_DIRS = List.of("region", "entities", "poi", "data");
    private static final int BACKUP_KEEP = 5;
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT);
    private static final MapEditManager INSTANCE = new MapEditManager();

    private enum State {
        /** 没有编辑会话。 */
        NONE,
        /** 正在拷贝/挂载/保存，禁止其它操作。 */
        WORKING,
        /** 维度已挂载，可以进入编辑。 */
        READY
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.NONE);
    @Nullable
    private volatile ActiveMapConfig map;
    @Nullable
    private volatile String createdBy;
    @Nullable
    private volatile MapEditDraft draft;

    private MapEditManager() {
    }

    public static MapEditManager getInstance() {
        return INSTANCE;
    }

    /** 编辑会话是否处于可进入状态（开局前需要检查它，避免编辑期间开新对局）。 */
    public boolean hasSession() {
        return state.get() == State.READY;
    }

    public boolean isWorking() {
        return state.get() == State.WORKING;
    }

    public String describeSession() {
        ActiveMapConfig current = map;
        if (current == null) {
            return "无";
        }
        return current.displayName + "（" + current.mapFolder + "）"
            + (createdBy == null ? "" : "，由 " + createdBy + " 创建");
    }

    /** 当前编辑会话的地图配置；没有会话时返回 null。 */
    @Nullable
    public ActiveMapConfig currentMap() {
        return map;
    }

    /** 当前编辑会话的草稿；没有会话或草稿未初始化时返回 null。 */
    @Nullable
    public MapEditDraft draft() {
        return draft;
    }

    /** 供指令层使用的“必须先有会话”守卫。 */
    @Nullable
    public MapEditDraft requireDraft(CommandSourceStack source) {
        if (state.get() != State.READY) {
            source.sendSystemMessage(Component.literal("§c编辑维度未创建，请先 /espetro edit <地图名>。"));
            return null;
        }
        MapEditDraft current = draft;
        if (current == null) {
            source.sendSystemMessage(Component.literal("§c编辑草稿尚未就绪，请稍后再试。"));
        }
        return current;
    }

    /** 把草稿落盘到存档内（会话中断/重开时可继续编辑）。 */
    public boolean persistDraft(MinecraftServer server) {
        ActiveMapConfig current = map;
        MapEditDraft current2 = draft;
        if (current == null || current2 == null) {
            return false;
        }
        try {
            current2.save(draftFile(server, current.mapFolder));
            return true;
        } catch (IOException e) {
            Espetro.LOGGER.warn("保存编辑草稿失败: {}", e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------------
    // 自动落盘：编辑会话每 5 秒把草稿写进存档，随时可恢复/可被读取
    // ------------------------------------------------------------------
    private static final int AUTO_SAVE_INTERVAL_TICKS = 100;
    private static int autoSaveCooldown;

    @SubscribeEvent
    public static void onAutoSaveTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (++autoSaveCooldown < AUTO_SAVE_INTERVAL_TICKS) {
            return;
        }
        autoSaveCooldown = 0;
        MapEditManager manager = INSTANCE;
        if (manager.state.get() != State.READY || manager.draft == null) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            manager.persistDraft(server);
        }
    }

    // ------------------------------------------------------------------
    // /espetro edit <英文地图名>
    // ------------------------------------------------------------------

    public int create(CommandSourceStack source, String rawName) {
        MinecraftServer server = source.getServer();
        if (!GameStateManager.getInstance().getCurrentPhase().isLobbyLike()) {
            source.sendSystemMessage(Component.literal("§c对局进行中，无法进入地图编辑模式；请先结束对局。"));
            return 0;
        }
        if (state.get() != State.NONE) {
            source.sendSystemMessage(Component.literal("§e已存在编辑会话：" + describeSession()
                + "。请先 /espetro editsave 或 /espetro editabandon。"));
            return 0;
        }

        ActiveMapConfig target = ExternalConfigBootstrap.findByMapFolder(rawName).orElse(null);
        if (target == null) {
            source.sendSystemMessage(Component.literal("§c未找到地图 " + rawName + "。可用地图："));
            for (ActiveMapConfig candidate : ExternalConfigBootstrap.getUsableMaps()) {
                source.sendSystemMessage(Component.literal(
                    "§7 - §f" + candidate.mapFolder + " §7(" + candidate.displayName + ")"));
            }
            return 0;
        }
        if (!target.usable) {
            source.sendSystemMessage(Component.literal("§c地图 " + target.mapFolder
                + " 未通过启动校验：" + String.join("；", target.rejectionReasons)));
            return 0;
        }

        BattlefieldWorldManager battlefield = BattlefieldWorldManager.getInstance();
        if (!battlefield.isPrepared(target)) {
            source.sendSystemMessage(Component.literal("§c地图 " + target.mapFolder + " 尚未准备完成，请稍后再试。"));
            return 0;
        }
        if (!battlefield.acquireEditorLock()) {
            source.sendSystemMessage(Component.literal("§c战场正在切换或编辑任务进行中，请稍后再试。"));
            return 0;
        }

        state.set(State.WORKING);
        String operator = source.getTextName();
        Path dimPath = battlefield.editorDimensionDirectory(server, EDIT_DIMENSION);
        Path tempPath = BattlefieldWorldManager.importingPathFor(dimPath);
        source.sendSystemMessage(Component.literal("§7开始把地图 §f" + target.displayName
            + " §7拷贝进编辑维度……"));

        CompletableFuture
            .supplyAsync(() -> {
                try {
                    // 残留目录（异常退出/上次崩溃）先清掉，避免旧区块混进编辑维度
                    BattlefieldWorldManager.deleteEditorTree(tempPath);
                    BattlefieldWorldManager.deleteEditorTree(dimPath);
                } catch (Exception e) {
                    return BattlefieldWorldManager.Result.fail("清理旧编辑维度失败: " + e.getMessage());
                }
                return battlefield.copyTemplateForEditor(target, dimPath, tempPath);
            }, BattlefieldWorldManager.ioExecutor())
            .whenComplete((result, error) -> server.execute(() -> {
                if (error != null) {
                    finishFailure(server, "复制地图失败: " + error.getMessage());
                    return;
                }
                if (result == null || !result.success()) {
                    finishFailure(server, result == null ? "复制地图失败" : result.error());
                    return;
                }
                ServerLevel mounted = battlefield.mountEditorDimension(server, EDIT_DIMENSION);
                if (mounted == null) {
                    finishFailure(server, "挂载编辑维度失败: " + EDIT_DIMENSION.location());
                    return;
                }
                map = target;
                createdBy = operator;
                draft = loadDraft(server, target);
                state.set(State.READY);
                battlefield.releaseEditorLock();
                Espetro.LOGGER.info("地图编辑维度已就绪: {}（{}）", target.mapFolder, operator);
                source.sendSystemMessage(Component.literal("§a编辑维度已创建：§f" + target.displayName
                    + " §7(" + target.mapFolder + ")。使用 §f/espetro edit in §7进入。"));
            }));
        return 1;
    }

    // ------------------------------------------------------------------
    // /espetro edit in
    // ------------------------------------------------------------------

    public int enter(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendSystemMessage(Component.literal("§c该命令必须由玩家执行。"));
            return 0;
        }
        MinecraftServer server = source.getServer();
        if (state.get() != State.READY) {
            source.sendSystemMessage(Component.literal("§c编辑维度未创建。"));
            return 0;
        }
        ServerLevel level = server.getLevel(EDIT_DIMENSION);
        if (level == null) {
            source.sendSystemMessage(Component.literal("§c编辑维度未创建。"));
            return 0;
        }

        ActiveMapConfig current = map;
        SpawnPointsSnapshot.Point point = current != null && current.spawnPoints != null
            ? current.spawnPoints.defend : null;
        double x = point != null ? point.x() : 0.5;
        double y = point != null ? point.y() + 1.0 : 128.0;
        double z = point != null ? point.z() : 0.5;
        float yaw = point != null ? point.yaw() : 0f;

        player.teleportTo(level, x, y, z, yaw, 0f);
        player.sendSystemMessage(Component.literal("§a已进入编辑维度：§f"
            + (current == null ? "未知地图" : current.displayName)
            + " §7(/espetro editsave 保存，/espetro editabandon 放弃)"));
        return 1;
    }

    // ------------------------------------------------------------------
    // /espetro editsave
    // ------------------------------------------------------------------

    public int save(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        ActiveMapConfig current = map;
        if (state.get() != State.READY || current == null) {
            source.sendSystemMessage(Component.literal("§c没有可保存的编辑会话。"));
            return 0;
        }
        BattlefieldWorldManager battlefield = BattlefieldWorldManager.getInstance();
        if (!battlefield.acquireEditorLock()) {
            source.sendSystemMessage(Component.literal("§c战场正在切换或保存任务进行中，请稍后再试。"));
            return 0;
        }
        state.set(State.WORKING);

        MapEditDraft sessionDraft = draft;
        if (sessionDraft != null) {
            MapEditConfigWriter.Result written = writeConfig(server, current, sessionDraft);
            if (!written.ok()) {
                // 校验失败也先把手上的编辑内容落成草稿，避免玩家的选区丢失
                boolean persisted = persistDraft(server);
                state.set(State.READY);
                battlefield.releaseEditorLock();
                source.sendSystemMessage(Component.literal("§c保存 EsConfig 失败：" + written.error()));
                source.sendSystemMessage(Component.literal(persisted
                    ? "§e已把当前编辑内容存入草稿，修正后可用 §f/espetro edit configsave §e重新写入。"
                    : "§c草稿保存也失败了，请先别关闭维度，把上面的报错发给我。"));
                return 0;
            }
            for (String note : written.notes()) {
                source.sendSystemMessage(Component.literal("§7· " + note));
            }
            if (written.backup() != null) {
                source.sendSystemMessage(Component.literal("§7配置备份：§f" + written.backup()));
            }
        }

        source.sendSystemMessage(Component.literal("§7正在保存地图 §f" + current.displayName
            + " §7并把所有人送回主城……"));

        Path dimPath = battlefield.editorDimensionDirectory(server, EDIT_DIMENSION);
        ServerLevel level = battlefield.detachEditorForSave(server, EDIT_DIMENSION);

        CompletableFuture
            .supplyAsync(() -> writeBack(server, current, dimPath, level), BattlefieldWorldManager.ioExecutor())
            .whenComplete((result, error) -> server.execute(() -> {
                if (error != null) {
                    finishFailure(server, "保存地图失败: " + error.getMessage());
                    return;
                }
                if (result == null || !result.success()) {
                    finishFailure(server, result == null ? "保存地图失败" : result.error());
                    return;
                }
                deleteDraft(server, current.mapFolder);
                finishSession("§a[Espetro] 地图 " + current.displayName + " 已保存并覆盖存档，编辑维度已关闭。");
            }));
        return 1;
    }

    /**
     * 后台线程：卸载后的维度先关存储句柄，再把模板旧目录备份、把编辑数据整目录替换回模板。
     */
    private BattlefieldWorldManager.Result writeBack(
        MinecraftServer server,
        ActiveMapConfig current,
        Path dimPath,
        @Nullable ServerLevel detached
    ) {
        BattlefieldWorldManager battlefield = BattlefieldWorldManager.getInstance();
        try {
            BattlefieldWorldManager.Result closed = battlefield.closeEditorLevel(detached);
            if (!closed.success()) {
                Espetro.LOGGER.warn("编辑维度关闭存储时出错（继续保存）: {}", closed.error());
            }
            Path template = current.templateWorldDir;
            if (template == null || !Files.isDirectory(template)) {
                return BattlefieldWorldManager.Result.fail("地图模板目录不存在: " + template);
            }

            Path backup = backupTemplate(server, current, template);
            for (String dir : SAVED_DIRS) {
                Path source = dimPath.resolve(dir);
                if (!Files.isDirectory(source)) {
                    continue;
                }
                Path incoming = template.resolve(dir + ".incoming");
                Path destination = template.resolve(dir);
                if (Files.exists(incoming)) {
                    BattlefieldWorldManager.deleteEditorTree(incoming);
                }
                BattlefieldWorldManager.copyEditorTree(source, incoming);
                if (Files.exists(destination)) {
                    BattlefieldWorldManager.deleteEditorTree(destination);
                }
                moveDirectory(incoming, destination);
            }

            BattlefieldWorldManager.deleteEditorTree(dimPath);
            Espetro.LOGGER.info("地图 {} 编辑结果已写回模板: {}（备份 {}）",
                current.mapFolder, template, backup);
            return BattlefieldWorldManager.Result.ok();
        } catch (Exception e) {
            Espetro.LOGGER.error("写回地图模板失败: {}", current.mapFolder, e);
            return BattlefieldWorldManager.Result.fail("写回地图模板失败: " + e.getMessage());
        }
    }

    private Path backupTemplate(MinecraftServer server, ActiveMapConfig current, Path template)
        throws IOException {
        Path root = server.storageSource.getLevelPath(LevelResource.ROOT)
            .resolve("espetro-map-backups");
        Files.createDirectories(root);
        String stamp = LocalDateTime.now().format(STAMP);
        Path backup = root.resolve(current.mapFolder + "-" + stamp);
        for (String dir : SAVED_DIRS) {
            Path source = template.resolve(dir);
            if (Files.isDirectory(source)) {
                BattlefieldWorldManager.copyEditorTree(source, backup.resolve(dir));
            }
        }
        pruneBackups(root);
        return backup;
    }

    private void pruneBackups(Path root) throws IOException {
        try (Stream<Path> entries = Files.list(root)) {
            List<Path> backups = entries
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing((Path path) -> path.getFileName().toString()).reversed())
                .toList();
            for (int i = BACKUP_KEEP; i < backups.size(); i++) {
                BattlefieldWorldManager.deleteEditorTree(backups.get(i));
            }
        }
    }

    private static void moveDirectory(Path source, Path destination) throws IOException {
        try {
            Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ------------------------------------------------------------------
    // /espetro editabandon
    // ------------------------------------------------------------------

    public int abandon(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        ActiveMapConfig current = map;
        if (state.get() != State.READY) {
            source.sendSystemMessage(Component.literal("§c没有可放弃的编辑会话。"));
            return 0;
        }
        BattlefieldWorldManager battlefield = BattlefieldWorldManager.getInstance();
        if (!battlefield.acquireEditorLock()) {
            source.sendSystemMessage(Component.literal("§c战场正在切换或任务进行中，请稍后再试。"));
            return 0;
        }
        state.set(State.WORKING);

        Path dimPath = battlefield.editorDimensionDirectory(server, EDIT_DIMENSION);
        ServerLevel level = battlefield.detachEditorWithoutSave(server, EDIT_DIMENSION);

        CompletableFuture
            .supplyAsync(() -> battlefield.deleteEditorDirectory(level, dimPath),
                BattlefieldWorldManager.ioExecutor())
            .whenComplete((result, error) -> server.execute(() -> {
                if (error != null) {
                    finishFailure(server, "放弃编辑失败: " + error.getMessage());
                    return;
                }
                if (result == null || !result.success()) {
                    finishFailure(server, result == null ? "放弃编辑失败" : result.error());
                    return;
                }
                if (current != null) {
                    deleteDraft(server, current.mapFolder);
                }
                finishSession("§e已放弃编辑，地图存档未改动。");
                Espetro.broadcastToAll("§e[Espetro] 已放弃地图编辑，编辑维度已关闭。");
            }));
        return 1;
    }

    // ------------------------------------------------------------------
    // /espetro edit configsave
    // ------------------------------------------------------------------

    /** 只把点位/出生点/载具点写回 EsConfig，不关闭维度、不写区块。 */
    public int configSave(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        ActiveMapConfig current = map;
        MapEditDraft currentDraft = draft;
        if (state.get() != State.READY || current == null || currentDraft == null) {
            source.sendSystemMessage(Component.literal("§c没有可保存的编辑会话。"));
            return 0;
        }
        MapEditConfigWriter.Result written = writeConfig(server, current, currentDraft);
        if (!written.ok()) {
            boolean persisted = persistDraft(server);
            source.sendSystemMessage(Component.literal("§c写入 EsConfig 失败：" + written.error()));
            source.sendSystemMessage(Component.literal(persisted
                ? "§e当前编辑内容已存入草稿（存档目录 espetro-edit-draft/），修正后可再执行 §f/espetro edit configsave§e。"
                : "§c草稿保存也失败了，请把上面的报错发给我。"));
            return 0;
        }
        persistDraft(server);
        source.sendSystemMessage(Component.literal("§a已写入 §f" + written.written().size()
            + " §a个配置文件："));
        for (String file : written.written()) {
            source.sendSystemMessage(Component.literal("§7 - §f" + file));
        }
        if (written.backup() != null) {
            source.sendSystemMessage(Component.literal("§7配置备份：§f" + written.backup()));
        }
        for (String note : written.notes()) {
            source.sendSystemMessage(Component.literal("§7· " + note));
        }
        return 1;
    }

    private MapEditConfigWriter.Result writeConfig(MinecraftServer server,
                                                   ActiveMapConfig current,
                                                   MapEditDraft currentDraft) {
        Path esConfigDir = current.esConfigDir;
        if (esConfigDir == null) {
            return MapEditConfigWriter.Result.fail("地图 " + current.mapFolder + " 的 EsConfig 目录不可用");
        }
        Path backupRoot = server.storageSource.getLevelPath(LevelResource.ROOT)
            .resolve("espetro-map-backups");
        String stamp = LocalDateTime.now().format(STAMP);
        boolean amphibious = current.amphibious != null && current.amphibious.enabled;
        return MapEditConfigWriter.write(esConfigDir, currentDraft, backupRoot, stamp, amphibious);
    }

    private Path draftFile(MinecraftServer server, String mapFolder) {
        return server.storageSource.getLevelPath(LevelResource.ROOT)
            .resolve("espetro-edit-draft")
            .resolve(mapFolder + ".json");
    }

    private MapEditDraft loadDraft(MinecraftServer server, ActiveMapConfig target) {
        Path file = draftFile(server, target.mapFolder);
        MapEditDraft loaded = MapEditDraft.load(file, target.mapFolder);
        if (loaded != null) {
            Espetro.LOGGER.info("已恢复地图 {} 的编辑草稿: {}", target.mapFolder, file);
            return loaded;
        }
        if (target.esConfigDir == null) {
            return new MapEditDraft(target.mapFolder);
        }
        return MapEditDraft.seedFrom(target.esConfigDir, target.spawnPoints, target.vehSpawn);
    }

    private void deleteDraft(MinecraftServer server, String mapFolder) {
        try {
            Files.deleteIfExists(draftFile(server, mapFolder));
        } catch (IOException e) {
            Espetro.LOGGER.warn("清理编辑草稿失败: {}", e.getMessage());
        }
    }

    // ------------------------------------------------------------------

    private void finishFailure(MinecraftServer server, @Nullable String message) {
        BattlefieldWorldManager.getInstance().releaseEditorLock();
        state.set(State.NONE);
        map = null;
        createdBy = null;
        draft = null;
        String safe = message == null ? "未知错误" : message;
        Espetro.LOGGER.error("地图编辑失败: {}", safe);
        Espetro.broadcastToAll("§c[Espetro] 地图编辑失败：" + safe);
    }

    private void finishSession(String message) {
        BattlefieldWorldManager.getInstance().releaseEditorLock();
        state.set(State.NONE);
        map = null;
        createdBy = null;
        draft = null;
        Espetro.broadcastToAll(message);
    }
}
