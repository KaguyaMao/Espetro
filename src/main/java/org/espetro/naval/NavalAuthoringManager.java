package org.espetro.naval;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.espetro.Espetro;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 舰船结构编排：框选 → 逐卷导出 → 写 manifest。
 *
 * <p>与工事编辑器的分工：交互协议相同（同一根选定棒，左键=角A、右键=角B、潜行+右键=锚点、
 * 潜行+左键=清空），但存储走 {@code naval_structures/<name>/}（多卷），限额也按舰船分级
 * （单卷 256 格 / 200 万方块，整船 1024 格 / 4000 万方块 / 64 卷）。</p>
 */
public final class NavalAuthoringManager {

    public static final int PERMISSION_LEVEL = 2;

    /** 玩家身上的“处于舰船编排模式”标记（跨重启保留）。 */
    private static final String PERSIST_KEY = "EspetroNavalMode";

    private static final NavalAuthoringManager INSTANCE = new NavalAuthoringManager();

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private NavalAuthoringManager() {
    }

    public static NavalAuthoringManager getInstance() {
        return INSTANCE;
    }

    /** 每个管理员的选区会话。 */
    public static final class Session {
        @Nullable
        public BlockPos pos1;
        @Nullable
        public BlockPos pos2;
        @Nullable
        public BlockPos anchor;
        /** 手持棒子时点击归舰船编排（否则归工事编辑器）。 */
        public boolean navalMode;

        public boolean hasCorners() {
            return pos1 != null && pos2 != null;
        }

        public BlockPos min() {
            BlockPos a = pos1;
            BlockPos b = pos2;
            return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ()));
        }

        public BlockPos max() {
            BlockPos a = pos1;
            BlockPos b = pos2;
            return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ()));
        }

        public Vec3i size() {
            return new Vec3i(max().getX() - min().getX() + 1,
                max().getY() - min().getY() + 1,
                max().getZ() - min().getZ() + 1);
        }
    }

    /** 导出结果。 */
    public record Outcome(boolean ok, List<String> lines) {
        static Outcome ok(List<String> lines) {
            return new Outcome(true, lines);
        }

        static Outcome fail(String message) {
            return new Outcome(false, List.of("§c" + message));
        }
    }

    public static boolean hasPermission(ServerPlayer player) {
        return player.hasPermissions(PERMISSION_LEVEL);
    }

    public Session session(ServerPlayer player) {
        return sessions.computeIfAbsent(player.getUUID(), id -> new Session());
    }

    @Nullable
    public Session existing(UUID playerId) {
        return sessions.get(playerId);
    }

    public boolean isEditing(UUID playerId) {
        Session session = sessions.get(playerId);
        return session != null && (session.pos1 != null || session.pos2 != null);
    }

    /** 进入舰船编排模式：手持选定棒时点击归本管理器的会话。 */
    public void enterMode(ServerPlayer player) {
        session(player).navalMode = true;
        player.getPersistentData().putBoolean(PERSIST_KEY, true);
    }

    public boolean isNavalMode(UUID playerId) {
        Session session = sessions.get(playerId);
        return session != null && session.navalMode;
    }

    /**
     * 带持久化回退的判断：会话里的开关会随重启丢失，玩家身上的标记不会。
     */
    public boolean isNavalMode(ServerPlayer player) {
        if (isNavalMode(player.getUUID())) {
            return true;
        }
        if (player.getPersistentData().getBoolean(PERSIST_KEY)) {
            session(player).navalMode = true;
            return true;
        }
        return false;
    }

    public void exitMode(ServerPlayer player) {
        Session session = sessions.get(player.getUUID());
        if (session != null) {
            session.navalMode = false;
        }
        player.getPersistentData().remove(PERSIST_KEY);
    }

    public void forget(UUID playerId) {
        sessions.remove(playerId);
    }

    public void clear(ServerPlayer player) {
        Session session = sessions.computeIfAbsent(player.getUUID(), id -> new Session());
        session.pos1 = null;
        session.pos2 = null;
        session.anchor = null;
    }

    public String setCorner(ServerPlayer player, boolean first, BlockPos pos) {
        Session session = session(player);
        if (first) {
            session.pos1 = pos.immutable();
        } else {
            session.pos2 = pos.immutable();
        }
        if (session.anchor == null && session.pos1 != null && session.pos2 != null) {
            session.anchor = session.min();
        }
        return describe(player);
    }

    public String setAnchor(ServerPlayer player, BlockPos pos) {
        session(player).anchor = pos.immutable();
        return "§e锚点已设为 " + pos.toShortString() + "（贴图时整船最小角对齐此处）";
    }

    /** 当前选区的一行摘要。 */
    public String describe(ServerPlayer player) {
        Session session = session(player);
        StringBuilder sb = new StringBuilder("§6[舰船编排] ");
        sb.append(session.pos1 == null ? "§7A=未选" : "§fA=" + session.pos1.toShortString());
        sb.append(" ");
        sb.append(session.pos2 == null ? "§7B=未选" : "§fB=" + session.pos2.toShortString());
        if (session.hasCorners()) {
            Vec3i size = session.size();
            long volume = (long) size.getX() * size.getY() * size.getZ();
            int parts = partCount(size);
            sb.append(" §7尺寸 §f").append(size.toShortString())
                .append(" §7体积 §f").append(volume)
                .append(" §7预计 §f").append(parts).append(" 卷");
            if (session.anchor != null) {
                sb.append(" §7锚点 §f").append(session.anchor.toShortString());
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------- markers

    /** 在玩家脚下放一个标记块（{@code espetro:<kind>}）。 */
    public String placeMarker(ServerLevel level, ServerPlayer player, String kind) {
        BlockPos pos = player.blockPosition();
        Session session = session(player);
        if (session.hasCorners()) {
            BlockPos min = session.min();
            BlockPos max = session.max();
            boolean inside = pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
            if (!inside) {
                return "§c标记必须放在选区内（当前脚下 " + pos.toShortString() + " 在选区外）";
            }
        }
        level.setBlock(pos, Blocks.STRUCTURE_BLOCK.defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof StructureBlockEntity be) {
            be.setMode(StructureMode.DATA);
            be.setMetaData("espetro:" + kind);
            be.setShowAir(false);
            be.setShowBoundingBox(false);
            be.setChanged();
        }
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        return "§e已放置标记 §f" + "espetro:" + kind + " §7@ " + pos.toShortString();
    }

    /** 清掉选区内的所有 espetro 标记块。 */
    public String clearMarkers(ServerLevel level, ServerPlayer player) {
        Session session = session(player);
        if (!session.hasCorners()) {
            return "§c请先选好两个角点";
        }
        BlockPos min = session.min();
        BlockPos max = session.max();
        int removed = 0;
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!level.getBlockState(pos).is(Blocks.STRUCTURE_BLOCK)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof StructureBlockEntity be
                && be.getMetaData().startsWith("espetro:")) {
                level.setBlock(pos.immutable(), Blocks.AIR.defaultBlockState(), 3);
                removed++;
            }
        }
        return "§e已清除 " + removed + " 个标记块";
    }

    // ------------------------------------------------------------- export

    /**
     * 三轴分块后的总卷数（每块三轴均不超过 {@link NavalStructureService#PART_SIZE}）。
     */
    public static int partCount(Vec3i size) {
        int step = NavalStructureService.PART_SIZE;
        long parts = (long) ceilDiv(size.getX(), step)
            * ceilDiv(size.getY(), step)
            * ceilDiv(size.getZ(), step);
        return (int) Math.min(Integer.MAX_VALUE, Math.max(1L, parts));
    }

    private static int ceilDiv(int value, int step) {
        return (value + step - 1) / step;
    }

    /**
     * 逐块导出。同步执行（每块 128³ 上限；一艘 300×40×30 的船 ≈ 3 块，耗时百毫秒级）。
     */
    public Outcome save(MinecraftServer server, ServerLevel level, ServerPlayer player, String name,
                        boolean includeEntities, boolean carve, boolean force) {
        if (!NavalStructureStore.isValidName(name)) {
            return Outcome.fail("名称只能用小写字母/数字/下划线，长度 ≤ 48");
        }
        Session session = session(player);
        if (!session.hasCorners()) {
            return Outcome.fail("请先选好两个角点（左键=A，右键=B）");
        }
        Vec3i size = session.size();
        BlockPos min = session.min();
        long volume = (long) size.getX() * size.getY() * size.getZ();
        if (volume > NavalStructureService.MAX_TOTAL_BLOCKS) {
            return Outcome.fail("选区内 " + volume + " 格超过整船上限 "
                + NavalStructureService.MAX_TOTAL_BLOCKS);
        }
        int parts = partCount(size);
        if (parts > NavalStructureService.MAX_PARTS) {
            return Outcome.fail("需要 " + parts + " 卷，超过上限 " + NavalStructureService.MAX_PARTS);
        }

        Path root = NavalStructureStore.directory();
        Path target = NavalStructureStore.dirForName(name);
        Path temp = root.resolve("." + name + ".tmp");
        try {
            Files.createDirectories(root);
            if (Files.isDirectory(temp)) {
                deleteTree(temp);
            }
            Files.createDirectories(temp);
        } catch (IOException e) {
            return Outcome.fail("创建临时目录失败: " + e.getMessage());
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        List<String> lines = new ArrayList<>();
        NavalManifest manifest = new NavalManifest();
        manifest.name = name;
        manifest.id = "espetro:naval/" + name;
        manifest.size = new int[]{size.getX(), size.getY(), size.getZ()};
        manifest.anchor = session.anchor == null
            ? new int[]{min.getX(), min.getY(), min.getZ()}
            : new int[]{session.anchor.getX(), session.anchor.getY(), session.anchor.getZ()};
        manifest.pivot = new int[]{
            manifest.anchor[0] - min.getX(),
            manifest.anchor[1] - min.getY(),
            manifest.anchor[2] - min.getZ()};
        manifest.author = player.getGameProfile().getName();
        manifest.exported_at = fmt.format(new Date());

        long startNanos = System.nanoTime();
        long blocksTotal = 0L;
        int index = 0;
        int step = NavalStructureService.PART_SIZE;
        int nx = ceilDiv(size.getX(), step);
        int ny = ceilDiv(size.getY(), step);
        int nz = ceilDiv(size.getZ(), step);
        try {
            for (int ix = 0; ix < nx; ix++) {
                for (int iy = 0; iy < ny; iy++) {
                    for (int iz = 0; iz < nz; iz++) {
                        int ox = ix * step;
                        int oy = iy * step;
                        int oz = iz * step;
                        Vec3i sliceSize = new Vec3i(
                            Math.min(step, size.getX() - ox),
                            Math.min(step, size.getY() - oy),
                            Math.min(step, size.getZ() - oz));
                        BlockPos sliceMin = min.offset(ox, oy, oz);
                        StructureTemplate template = new StructureTemplate();
                        ensureChunksLoaded(level, sliceMin, sliceSize, index);
                        template.fillFromWorld(level, sliceMin, sliceSize, includeEntities,
                            carve ? Blocks.STRUCTURE_VOID : Blocks.AIR);
                        CompoundTag tag = template.save(new CompoundTag());
                        String file = String.format(Locale.ROOT, "part_%03d.nbt", index);
                        Path partPath = temp.resolve(file);
                        NbtIo.writeCompressed(tag, partPath.toFile());
                        ListTag blocks = tag.getList("blocks", Tag.TAG_COMPOUND);
                        blocksTotal += blocks.size();
                        int[] offset = new int[]{ox, oy, oz};
                        NavalManifest.Part part = NavalManifest.part(file, offset,
                            new int[]{sliceSize.getX(), sliceSize.getY(), sliceSize.getZ()},
                            blocks.size());
                        List<NavalStructureService.Marker> markers = NavalStructureService.readMarkers(tag,
                            new BlockPos(offset[0], offset[1], offset[2]));
                        for (NavalStructureService.Marker marker : markers) {
                            part.markers.add(marker.metadata());
                        }
                        manifest.parts.add(part);
                        index++;
                        lines.add("§7卷 " + file + " §f" + sliceSize.toShortString()
                            + " §7@ " + sliceMin.toShortString()
                            + " §7方块 §f" + blocks.size()
                            + (markers.isEmpty() ? "" : " §7标记 §f" + markers.size()));
                    }
                }
            }
            manifest.total_blocks = blocksTotal;
            long blocksLimit = NavalStructureService.MAX_TOTAL_BLOCKS;
            if (blocksTotal > blocksLimit) {
                throw new IOException("总方块数 " + blocksTotal + " 超过上限 " + blocksLimit);
            }
            manifest.write(temp.resolve(NavalStructureStore.MANIFEST));

            // 备份旧版本，再原子替换
            if (Files.isDirectory(target)) {
                Path backup = root.resolve(name + ".bak-" + stamp);
                Files.move(target, backup, StandardCopyOption.REPLACE_EXISTING);
                lines.add("§7旧版本已备份 §f" + backup.getFileName());
            }
            Path oldFile = NavalStructureStore.pathForName(name);
            if (Files.isRegularFile(oldFile)) {
                Files.move(oldFile, root.resolve(name + ".nbt.bak-" + stamp),
                    StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicFailed) {
                Files.move(temp, target);
            }
        } catch (Exception e) {
            try {
                deleteTree(temp);
            } catch (Exception ignored) {
            }
            Espetro.LOGGER.error("[舰船编排] 导出失败 {}: {}", name, e.getMessage(), e);
            return Outcome.fail("导出失败: " + e.getMessage());
        }

        long millis = (System.nanoTime() - startNanos) / 1_000_000L;
        lines.add(0, "§a导出完成 §f" + name + " §7" + size.toShortString()
            + " / " + manifest.parts.size() + " 卷 / " + blocksTotal + " 方块 / "
            + millis + " ms");
        lines.add("§7目录 §f" + target);
        return Outcome.ok(lines);
    }

    /**
     * 块内所有区块必须已加载，否则 {@link StructureTemplate#fillFromWorld} 会静默丢方块。
     */
    private static void ensureChunksLoaded(ServerLevel level, BlockPos min, Vec3i size, int part) throws IOException {
        int minChunkX = min.getX() >> 4;
        int maxChunkX = (min.getX() + size.getX() - 1) >> 4;
        int minChunkZ = min.getZ() >> 4;
        int maxChunkZ = (min.getZ() + size.getZ() - 1) >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!level.hasChunk(cx, cz)) {
                    throw new IOException("第 " + part + " 卷的区块 [" + cx + ", " + cz + "] 未加载，请靠近后重试");
                }
            }
        }
    }

    // ------------------------------------------------------------- list / info / delete

    public List<String> list() {
        List<String> names = NavalStructureStore.listNames();
        if (names.isEmpty()) {
            return List.of("§7naval_structures/ 里还没有任何结构");
        }
        List<String> lines = new ArrayList<>();
        for (String name : names) {
            Path dir = NavalStructureStore.dirForName(name);
            if (NavalStructureStore.hasParts(name)) {
                try {
                    NavalManifest manifest = NavalManifest.read(NavalStructureStore.manifestForName(name));
                    lines.add("§a" + name + " §7多卷 " + manifest.parts.size() + " 卷 "
                        + manifest.sizeX() + "×" + manifest.sizeY() + "×" + manifest.sizeZ()
                        + " / " + manifest.total_blocks + " 方块");
                } catch (IOException e) {
                    lines.add("§c" + name + " §7manifest 读取失败: " + e.getMessage());
                }
            } else if (Files.isRegularFile(NavalStructureStore.pathForName(name))) {
                lines.add("§a" + name + " §7单文件");
            } else if (Files.isDirectory(dir)) {
                lines.add("§e" + name + " §7目录中没有 manifest.json（无效）");
            }
        }
        return lines;
    }

    public List<String> info(String name) {
        if (!NavalStructureStore.isValidName(name)) {
            return List.of("§c名称不合法");
        }
        List<String> lines = new ArrayList<>();
        if (NavalStructureStore.hasParts(name)) {
            try {
                NavalManifest manifest = NavalManifest.read(NavalStructureStore.manifestForName(name));
                lines.add("§6[舰船] §f" + name + " §7多卷 " + manifest.parts.size() + " 卷");
                lines.add("§7尺寸 §f" + manifest.sizeX() + "×" + manifest.sizeY() + "×" + manifest.sizeZ()
                    + " §7总方块 §f" + manifest.total_blocks);
                lines.add("§7锚点(导出时) §f" + manifest.anchor[0] + "," + manifest.anchor[1] + ","
                    + manifest.anchor[2] + " §7pivot §f" + manifest.pivot[0] + "," + manifest.pivot[1]
                    + "," + manifest.pivot[2]);
                int markers = 0;
                for (NavalManifest.Part part : manifest.parts) {
                    markers += part.markers == null ? 0 : part.markers.size();
                }
                lines.add("§7标记 §f" + markers + " §7作者 §f" + manifest.author
                    + " §7导出 §f" + manifest.exported_at);
                for (NavalManifest.Part part : manifest.parts) {
                    lines.add("§8  " + part.file + " offset=" + part.offset[0] + "," + part.offset[1]
                        + "," + part.offset[2] + " size=" + part.size[0] + "×" + part.size[1] + "×"
                        + part.size[2] + " blocks=" + part.blocks
                        + (part.markers == null || part.markers.isEmpty() ? "" : " markers=" + part.markers));
                }
            } catch (IOException e) {
                lines.add("§c读取 manifest 失败: " + e.getMessage());
            }
            return lines;
        }
        Path file = NavalStructureStore.pathForName(name);
        if (Files.isRegularFile(file)) {
            lines.add("§6[舰船] §f" + name + " §7单文件 §f" + file.getFileName());
            return lines;
        }
        lines.add("§c没有这个结构: " + name);
        return lines;
    }

    public Outcome delete(String name) {
        if (!NavalStructureStore.isValidName(name)) {
            return Outcome.fail("名称不合法");
        }
        Path dir = NavalStructureStore.dirForName(name);
        Path file = NavalStructureStore.pathForName(name);
        boolean removed = false;
        try {
            if (Files.isDirectory(dir) && Files.isRegularFile(dir.resolve(NavalStructureStore.MANIFEST))) {
                deleteTree(dir);
                removed = true;
            }
            if (Files.isRegularFile(file)) {
                Files.delete(file);
                removed = true;
            }
        } catch (IOException e) {
            return Outcome.fail("删除失败: " + e.getMessage());
        }
        return removed ? Outcome.ok(List.of("§a已删除 §f" + name))
            : Outcome.fail("没有这个结构: " + name);
    }

    // ------------------------------------------------------------- fs helpers

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
