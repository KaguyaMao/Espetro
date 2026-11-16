package org.espetro.naval;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 舰船结构模板的加载、旋转放置与标记扫描。
 *
 * <p>支持两种形态：</p>
 * <ul>
 *   <li><b>多卷</b>：{@code naval_structures/<name>/manifest.json} + {@code part_*.nbt}，
 *       每卷单独校验、单独放置；</li>
 *   <li><b>单文件</b>：{@code naval_structures/<name>.nbt} 或内置
 *       {@code data/espetro/structures/naval/<name>.nbt}（占位舰走这条）。</li>
 * </ul>
 *
 * <p>标记协议（全部用原版 {@code minecraft:structure_block}，{@code metadata} 为标记内容，
 * 放置后自动清成空气）：</p>
 * <ul>
 *   <li>{@code espetro:attack_spawn} —— 攻方出生点</li>
 *   <li>{@code espetro:veh:<type>:<index>} —— 载具点 attack 侧</li>
 *   <li>{@code espetro:veh:<type>:<index>:defend} —— 载具点 defend 侧</li>
 *   <li>{@code espetro:hull} —— 舰体外壳采样点</li>
 * </ul>
 */
public final class NavalStructureService {

    /** 单卷（一个分块）上限：三轴各自不得超过 {@link #PART_SIZE}。 */
    public static final int PART_SIZE = 128;

    /**
     * 兼容旧名：早期版本用「沿最长轴切片」，每卷横截面不得超过此值。
     * 现在改为三轴分块，单卷三轴都不得超过 {@link #PART_SIZE}。
     */
    public static final int MAX_AXIS = PART_SIZE;
    public static final long MAX_BLOCKS = 2_000_000L;

    /** 整船上限：长宽高不限，只限制总方块数与卷数。 */
    public static final long MAX_TOTAL_BLOCKS = 40_000_000L;
    public static final int MAX_PARTS = 512;
    public static final int MAX_MARKERS = 256;

    /** 每卷（每块）的边长上限。 */
    public static final int PART_THICKNESS = PART_SIZE;

    private NavalStructureService() {
    }

    /** 一卷已加载的模板。 */
    public static final class Part {
        public final StructureTemplate template;
        public final BlockPos offset;
        public final Vec3i size;
        public final int blocks;

        Part(StructureTemplate template, BlockPos offset, Vec3i size, int blocks) {
            this.template = template;
            this.offset = offset;
            this.size = size;
            this.blocks = blocks;
        }
    }

    /** 已加载并解析好的舰船（可能由多卷组成）。 */
    public static final class Loaded {
        public final Vec3i size;
        public final List<Part> parts;
        public final List<Marker> markers;
        public final int blockCount;
        /** 多卷形态时非空。 */
        @Nullable
        public final NavalManifest manifest;

        private Loaded(Vec3i size, List<Part> parts, List<Marker> markers, int blockCount,
                       @Nullable NavalManifest manifest) {
            this.size = size;
            this.parts = List.copyOf(parts);
            this.markers = List.copyOf(markers);
            this.blockCount = blockCount;
            this.manifest = manifest;
        }

        public boolean multiPart() {
            return manifest != null;
        }
    }

    /** 结构内的一个标记（local 为整船局部坐标，已含所在卷偏移、未旋转）。 */
    public record Marker(String kind, String metadata, BlockPos local, float yaw) {
    }

    public static Loaded load(MinecraftServer server, AmphibiousConfig.Ship ship) {
        net.minecraft.resources.ResourceLocation id = ship.structure;
        Path dir = NavalStructureStore.dirFor(id);
        if (dir != null) {
            return loadMultipart(server, dir, id);
        }
        StructureTemplate template = null;
        Path file = NavalStructureStore.pathFor(id);
        if (file != null && Files.isRegularFile(file)) {
            try (InputStream input = Files.newInputStream(file)) {
                CompoundTag tag = NbtIo.readCompressed(input);
                template = server.getStructureManager().readStructure(tag);
            } catch (Exception e) {
                throw new IllegalArgumentException("读取舰船模板失败 " + file.getFileName()
                    + ": " + e.getMessage());
            }
        }
        if (template == null) {
            // 内置资源：data/<ns>/structures/<path>.nbt（jar 内直接读，绕开 StructureTemplateManager 的异步加载）
            String resource = "data/" + id.getNamespace() + "/structures/" + id.getPath() + ".nbt";
            InputStream builtin = NavalStructureService.class.getClassLoader().getResourceAsStream(resource);
            if (builtin != null) {
                try (InputStream input = builtin) {
                    CompoundTag tag = NbtIo.readCompressed(input);
                    template = server.getStructureManager().readStructure(tag);
                } catch (Exception e) {
                    throw new IllegalArgumentException("读取内置舰船结构失败 " + resource
                        + ": " + e.getMessage());
                }
            }
        }
        if (template == null) {
            template = server.getStructureManager().get(id)
                .orElseThrow(() -> new IllegalArgumentException("缺少舰船结构 " + id));
        }
        CompoundTag snapshot = template.save(new CompoundTag());
        Vec3i size = template.getSize();
        checkSize(size, 0);
        ListTag blocks = snapshot.getList("blocks", Tag.TAG_COMPOUND);
        if (blocks.size() > MAX_BLOCKS) {
            throw new IllegalArgumentException("舰船结构方块数超过单卷上限 " + MAX_BLOCKS + ": " + blocks.size());
        }
        List<Marker> markers = readMarkers(snapshot, BlockPos.ZERO);
        if (markers.size() > MAX_MARKERS) {
            throw new IllegalArgumentException("舰船标记超过 " + MAX_MARKERS + " 个: " + markers.size());
        }
        List<Part> parts = List.of(new Part(template, BlockPos.ZERO, size, blocks.size()));
        return new Loaded(size, parts, markers, blocks.size(), null);
    }

    private static Loaded loadMultipart(MinecraftServer server, Path dir,
                                        net.minecraft.resources.ResourceLocation id) {
        NavalManifest manifest;
        try {
            manifest = NavalManifest.read(dir.resolve(NavalStructureStore.MANIFEST));
        } catch (IOException e) {
            throw new IllegalArgumentException("读取 manifest 失败 " + id + ": " + e.getMessage());
        }
        if (manifest.parts.size() > MAX_PARTS) {
            throw new IllegalArgumentException("舰船卷数超过上限 " + MAX_PARTS + ": " + manifest.parts.size());
        }
        Vec3i total = new Vec3i(Math.max(1, manifest.sizeX()), Math.max(1, manifest.sizeY()),
            Math.max(1, manifest.sizeZ()));
        if (manifest.sizeX() < 1 || manifest.sizeY() < 1 || manifest.sizeZ() < 1) {
            throw new IllegalArgumentException("舰船整体尺寸越界 " + id + ": " + total);
        }
        List<Part> parts = new ArrayList<>();
        List<Marker> markers = new ArrayList<>();
        long blocksTotal = 0L;
        for (NavalManifest.Part entry : manifest.parts) {
            Path file = dir.resolve(entry.file);
            if (!Files.isRegularFile(file)) {
                throw new IllegalArgumentException("缺少卷文件 " + entry.file + "（" + id + "）");
            }
            CompoundTag tag;
            StructureTemplate template;
            try (InputStream input = Files.newInputStream(file)) {
                tag = NbtIo.readCompressed(input);
                template = server.getStructureManager().readStructure(tag);
            } catch (Exception e) {
                throw new IllegalArgumentException("读取卷 " + entry.file + " 失败: " + e.getMessage());
            }
            Vec3i partSize = template.getSize();
            checkSize(partSize, parts.size() + 1);
            ListTag blocks = tag.getList("blocks", Tag.TAG_COMPOUND);
            if (blocks.size() > MAX_BLOCKS) {
                throw new IllegalArgumentException("卷 " + entry.file + " 方块数超过单卷上限 "
                    + MAX_BLOCKS + ": " + blocks.size());
            }
            BlockPos offset = new BlockPos(
                entry.offset.length > 0 ? entry.offset[0] : 0,
                entry.offset.length > 1 ? entry.offset[1] : 0,
                entry.offset.length > 2 ? entry.offset[2] : 0);
            parts.add(new Part(template, offset, partSize, blocks.size()));
            blocksTotal += blocks.size();
            markers.addAll(readMarkers(tag, offset));
        }
        if (blocksTotal > MAX_TOTAL_BLOCKS) {
            throw new IllegalArgumentException("舰船总方块数超过上限 " + MAX_TOTAL_BLOCKS + ": " + blocksTotal);
        }
        if (markers.size() > MAX_MARKERS) {
            throw new IllegalArgumentException("舰船标记超过 " + MAX_MARKERS + " 个: " + markers.size());
        }
        return new Loaded(total, parts, markers, (int) Math.min(Integer.MAX_VALUE, blocksTotal), manifest);
    }

    private static void checkSize(Vec3i size, int index) {
        if (size.getX() < 1 || size.getY() < 1 || size.getZ() < 1) {
            throw new IllegalArgumentException("舰船结构尺寸越界（第 " + index + " 卷）: " + size);
        }
        if (size.getX() > MAX_AXIS || size.getY() > MAX_AXIS || size.getZ() > MAX_AXIS) {
            throw new IllegalArgumentException("舰船单卷尺寸超过 " + MAX_AXIS + "（第 " + index + " 卷）: " + size);
        }
    }

    /** 放置整船（多卷时逐卷放置）；返回放置的方块数（按模板方块表计）。 */
    public static int place(ServerLevel level, Loaded loaded, BlockPos anchor, Rotation rotation,
                            boolean clearMarkers) {
        preloadChunks(level, anchor, loaded.size, rotation);
        StructurePlaceSettings settings = new StructurePlaceSettings()
            .setRotation(rotation)
            .setIgnoreEntities(false)
            .setKnownShape(false);
        for (Part part : loaded.parts) {
            BlockPos partAnchor = anchor.offset(rotate(part.offset, rotation));
            part.template.placeInWorld(level, partAnchor, partAnchor, settings, level.random, 19);
        }
        if (clearMarkers) {
            for (Marker marker : loaded.markers) {
                BlockPos world = worldPos(anchor, marker.local(), rotation);
                if (level.getBlockState(world).is(Blocks.STRUCTURE_BLOCK)) {
                    level.setBlock(world, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        return loaded.blockCount;
    }

    public static BlockPos worldPos(BlockPos anchor, BlockPos local, Rotation rotation) {
        BlockPos rotated = rotate(local, rotation);
        return anchor.offset(rotated);
    }

    /** 与 Minecraft 结构放置一致的旋转（绕结构原点，未含 pivot）。 */
    public static BlockPos rotate(BlockPos pos, Rotation rotation) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return switch (rotation) {
            case NONE -> new BlockPos(x, y, z);
            case CLOCKWISE_90 -> new BlockPos(-z, y, x);
            case CLOCKWISE_180 -> new BlockPos(-x, y, -z);
            case COUNTERCLOCKWISE_90 -> new BlockPos(z, y, -x);
        };
    }

    public static Rotation rotationOf(String name) {
        String value = name == null ? "none" : name.toLowerCase(Locale.ROOT);
        return switch (value) {
            case "clockwise_90" -> Rotation.CLOCKWISE_90;
            case "clockwise_180" -> Rotation.CLOCKWISE_180;
            case "counterclockwise_90" -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    /** 旋转后的结构包围盒（min ~ max，含两端）。 */
    public static BlockPos[] rotatedBounds(BlockPos anchor, Vec3i size, Rotation rotation) {
        BlockPos a = rotate(new BlockPos(0, 0, 0), rotation);
        BlockPos b = rotate(new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ() - 1), rotation);
        int minX = Math.min(a.getX(), b.getX());
        int maxX = Math.max(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int maxY = Math.max(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxZ = Math.max(a.getZ(), b.getZ());
        return new BlockPos[]{
            anchor.offset(minX, minY, minZ),
            anchor.offset(maxX, maxY, maxZ)
        };
    }

    private static void preloadChunks(ServerLevel level, BlockPos anchor, Vec3i size, Rotation rotation) {
        BlockPos[] bounds = rotatedBounds(anchor, size, rotation);
        int minChunkX = bounds[0].getX() >> 4;
        int maxChunkX = bounds[1].getX() >> 4;
        int minChunkZ = bounds[0].getZ() >> 4;
        int maxChunkZ = bounds[1].getZ() >> 4;
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    // ------------------------------------------------------------- markers

    /**
     * 从结构快照里读标记，并把 {@code offset} 加到局部坐标上（多卷时传该卷偏移）。
     * 供导出/加载两侧共用。
     */
    public static List<Marker> readMarkers(CompoundTag snapshot, BlockPos offset) {
        List<Marker> markers = new ArrayList<>();
        ListTag palette = snapshot.getList("palette", Tag.TAG_COMPOUND);
        ListTag blocks = snapshot.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag entry = blocks.getCompound(i);
            int stateIndex = entry.getInt("state");
            if (stateIndex < 0 || stateIndex >= palette.size()) {
                continue;
            }
            CompoundTag paletteEntry = palette.getCompound(stateIndex);
            if (!"minecraft:structure_block".equals(paletteEntry.getString("Name"))) {
                continue;
            }
            CompoundTag blockEntity = entry.getCompound("nbt");
            String metadata = blockEntity.getString("metadata");
            if (metadata == null || !metadata.startsWith("espetro:")) {
                continue;
            }
            ListTag posTag = entry.getList("pos", Tag.TAG_INT);
            if (posTag.size() != 3) {
                continue;
            }
            BlockPos local = new BlockPos(posTag.getInt(0), posTag.getInt(1), posTag.getInt(2))
                .offset(offset);
            String kind = metadata.substring("espetro:".length());
            float yaw = yawOf(paletteEntry);
            markers.add(new Marker(kind, metadata, local, yaw));
        }
        return markers;
    }

    private static float yawOf(CompoundTag paletteEntry) {
        if (!paletteEntry.contains("Properties", Tag.TAG_COMPOUND)) {
            return 0.0F;
        }
        CompoundTag properties = paletteEntry.getCompound("Properties");
        String facing = properties.getString("facing");
        return switch (facing == null ? "" : facing) {
            case "south" -> 0.0F;
            case "west" -> 90.0F;
            case "north" -> 180.0F;
            case "east" -> -90.0F;
            default -> 0.0F;
        };
    }
}
