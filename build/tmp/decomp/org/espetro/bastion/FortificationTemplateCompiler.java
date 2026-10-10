/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.bastion;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.espetro.bastion.FortificationConfig;
import org.espetro.bastion.FortificationNbtSanitizer;
import org.espetro.bastion.FortificationTransform;

final class FortificationTemplateCompiler {
    private static final Set<String> FORBIDDEN_BLOCKS = Set.of("minecraft:command_block", "minecraft:chain_command_block", "minecraft:repeating_command_block", "minecraft:structure_block", "minecraft:jigsaw");
    private static final TagKey<Block> FORBIDDEN_TAG = TagKey.m_203882_(Registries.f_256747_, new ResourceLocation("espetro:forbidden_fortification_blocks"));

    private FortificationTemplateCompiler() {
    }

    static Map<String, CompiledTemplate> compile(MinecraftServer server, FortificationConfig.FortificationDef def, FortificationConfig.Limits limits) throws Exception {
        FortificationConfig.Placement placement = def.placement;
        if ("entity".equals(placement.type)) {
            ResourceLocation entityId = ResourceLocation.m_135820_(placement.entityId);
            if (entityId == null) {
                throw new IllegalArgumentException("\u975e\u6cd5 entity_id");
            }
            CompoundTag root = placement.entityNbt == null ? new CompoundTag() : TagParser.m_129359_(placement.entityNbt.toString());
            root.m_128359_("id", entityId.toString());
            placement.sanitizedEntityNbt = FortificationNbtSanitizer.sanitizeRootEntity(root, entityId, limits.maxPassengerDepth, Math.min(65536, limits.maxTemplateNbtBytes));
            boolean registered = BuiltInRegistries.f_256780_.m_7804_(entityId);
            if (!registered && placement.fallbackTemplate == null) {
                throw new IllegalArgumentException("\u5b9e\u4f53\u7c7b\u578b\u672a\u6ce8\u518c\u4e14\u6ca1\u6709 fallback_template: " + entityId);
            }
            if (placement.fallbackTemplate == null) {
                return Map.of();
            }
            CompiledTemplate fallback = FortificationTemplateCompiler.compileOne(server, FortificationTemplateCompiler.requireId(placement.fallbackTemplate, "fallback_template"), placement, def, limits, false);
            return Map.of("default", fallback);
        }
        LinkedHashMap<String, ResourceLocation> sources = new LinkedHashMap<String, ResourceLocation>();
        sources.put("default", FortificationTemplateCompiler.requireId(placement.template, "template"));
        if (placement.templateByTeam != null) {
            for (Map.Entry<String, String> entry : placement.templateByTeam.entrySet()) {
                sources.put(entry.getKey().toLowerCase(Locale.ROOT), FortificationTemplateCompiler.requireId(entry.getValue(), "template_by_team." + entry.getKey()));
            }
        }
        LinkedHashMap<String, CompiledTemplate> result = new LinkedHashMap<String, CompiledTemplate>();
        for (Map.Entry entry : sources.entrySet()) {
            result.put((String)entry.getKey(), FortificationTemplateCompiler.compileOne(server, (ResourceLocation)entry.getValue(), placement, def, limits, placement.includeEntities));
        }
        return Map.copyOf(result);
    }

    private static CompiledTemplate compileOne(MinecraftServer server, ResourceLocation id, FortificationConfig.Placement placement, FortificationConfig.FortificationDef def, FortificationConfig.Limits limits, boolean includeEntities) throws Exception {
        StructureTemplate template = server.m_236738_().m_230407_(id).orElseThrow(() -> new IllegalArgumentException("\u7f3a\u5c11 Structure NBT " + id));
        CompoundTag serialized = template.m_74618_(new CompoundTag());
        int estimatedBytes = serialized.toString().getBytes(StandardCharsets.UTF_8).length;
        if (estimatedBytes > limits.maxTemplateNbtBytes) {
            throw new IllegalArgumentException("\u6a21\u677f NBT \u8d85\u8fc7 " + limits.maxTemplateNbtBytes + " bytes: " + estimatedBytes);
        }
        Vec3i size = template.m_163801_();
        if (size.m_123341_() < 1 || size.m_123342_() < 1 || size.m_123343_() < 1 || size.m_123341_() > limits.maxTemplateAxis || size.m_123342_() > limits.maxTemplateAxis || size.m_123343_() > limits.maxTemplateAxis) {
            throw new IllegalArgumentException("\u6a21\u677f\u5c3a\u5bf8\u8d8a\u754c: " + size);
        }
        ListTag blocks = serialized.m_128437_("blocks", 10);
        if (blocks.size() > limits.maxTemplateBlocks) {
            throw new IllegalArgumentException("\u6a21\u677f\u65b9\u5757\u6570\u8d85\u8fc7 " + limits.maxTemplateBlocks);
        }
        ListTag palette = FortificationTemplateCompiler.selectPalette(serialized, placement.paletteIndex);
        ArrayList<RawBlock> rawBlocks = new ArrayList<RawBlock>(blocks.size());
        int damageableBlocks = 0;
        for (int i = 0; i < blocks.size(); ++i) {
            Touch touch;
            CompoundTag blockEntity;
            CompoundTag block = blocks.m_128728_(i);
            ListTag pos = block.m_128437_("pos", 3);
            if (pos.size() != 3) {
                throw new IllegalArgumentException("blocks[" + i + "].pos \u975e\u6cd5");
            }
            int stateIndex = block.m_128451_("state");
            if (stateIndex < 0 || stateIndex >= palette.size()) {
                throw new IllegalArgumentException("blocks[" + i + "].state \u8d8a\u754c");
            }
            BlockState state = NbtUtils.m_247651_(server.m_206579_().m_255025_(Registries.f_256747_), palette.m_128728_(stateIndex));
            ResourceLocation blockId = BuiltInRegistries.f_256975_.m_7981_(state.m_60734_());
            if (FORBIDDEN_BLOCKS.contains(blockId.toString()) || state.m_204336_(FORBIDDEN_TAG)) {
                throw new IllegalArgumentException("\u6a21\u677f\u5305\u542b\u5371\u9669\u65b9\u5757 " + blockId);
            }
            BlockPos local = new BlockPos(pos.m_128763_(0), pos.m_128763_(1), pos.m_128763_(2));
            CompoundTag compoundTag = blockEntity = block.m_128425_("nbt", 10) ? FortificationNbtSanitizer.sanitizeBlockEntity(block.m_128469_("nbt"), Math.min(65536, limits.maxTemplateNbtBytes)) : null;
            if (state.m_60713_(Blocks.f_50454_)) {
                touch = Touch.IGNORE;
            } else if (state.m_60795_()) {
                touch = Touch.EXPLICIT_AIR;
            } else {
                touch = Touch.BLOCK;
                ++damageableBlocks;
            }
            rawBlocks.add(new RawBlock(i, local, state, blockEntity, touch));
        }
        ArrayList<CompiledEntity> entities = new ArrayList<CompiledEntity>();
        ListTag rawEntities = serialized.m_128437_("entities", 10);
        if (rawEntities.size() > limits.maxTemplateEntities) {
            throw new IllegalArgumentException("\u6a21\u677f\u5b9e\u4f53\u6570\u8d85\u8fc7 " + limits.maxTemplateEntities);
        }
        if (includeEntities) {
            for (int i = 0; i < rawEntities.size(); ++i) {
                CompoundTag entry = rawEntities.m_128728_(i);
                ListTag pos = entry.m_128437_("pos", 6);
                ListTag blockPos = entry.m_128437_("blockPos", 3);
                if (pos.size() != 3 || blockPos.size() != 3 || !entry.m_128425_("nbt", 10)) {
                    throw new IllegalArgumentException("entities[" + i + "] \u683c\u5f0f\u975e\u6cd5");
                }
                CompoundTag sanitized = FortificationNbtSanitizer.sanitizeEntity(entry.m_128469_("nbt"), 0, limits.maxPassengerDepth, Math.min(65536, limits.maxTemplateNbtBytes));
                ResourceLocation type = FortificationNbtSanitizer.requireEntityType(sanitized);
                if (!BuiltInRegistries.f_256780_.m_7804_(type)) {
                    throw new IllegalArgumentException("\u7ed3\u6784\u5b9e\u4f53\u7c7b\u578b\u672a\u6ce8\u518c: " + type);
                }
                boolean damageable = def.durability.damageableStructureEntities.contains(i);
                entities.add(new CompiledEntity(i, new Vec3(pos.m_128772_(0), pos.m_128772_(1), pos.m_128772_(2)), new BlockPos(blockPos.m_128763_(0), blockPos.m_128763_(1), blockPos.m_128763_(2)), type, sanitized, damageable));
            }
        }
        for (Integer index : def.durability.damageableStructureEntities) {
            if (includeEntities && index < rawEntities.size()) continue;
            throw new IllegalArgumentException("damageable_structure_entities \u7d22\u5f15\u8d8a\u754c: " + index);
        }
        int damageableParts = damageableBlocks + (int)entities.stream().filter(CompiledEntity::damageable).count();
        if (damageableParts <= 0) {
            throw new IllegalArgumentException("\u6a21\u677f\u6ca1\u6709\u53ef\u635f\u4f24\u90e8\u4ef6");
        }
        BlockPos origin = FortificationTemplateCompiler.vec(placement.originOffset);
        BlockPos pivot = FortificationTemplateCompiler.vec(placement.pivot);
        EnumMap<Direction, OrientedTemplate> rotations = new EnumMap<Direction, OrientedTemplate>(Direction.class);
        for (Direction direction : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            ArrayList<OrientedBlock> orientedBlocks = new ArrayList<OrientedBlock>();
            ArrayList<BlockPos> footprint = new ArrayList<BlockPos>();
            AABB bounds = null;
            for (RawBlock raw : rawBlocks) {
                if (raw.touch == Touch.IGNORE) continue;
                BlockPos relative = FortificationTransform.world(BlockPos.f_121853_, origin, raw.local, pivot, direction);
                BlockState rotated = raw.state.m_60717_(FortificationTransform.rotation(direction));
                orientedBlocks.add(new OrientedBlock(raw.index, relative, rotated, raw.blockEntity == null ? null : raw.blockEntity.m_6426_(), raw.touch));
                AABB cell = new AABB(relative);
                AABB aABB = bounds = bounds == null ? cell : bounds.m_82367_(cell);
                if (raw.touch != Touch.BLOCK) continue;
                footprint.add(relative);
            }
            ArrayList<OrientedEntity> orientedEntities = new ArrayList<OrientedEntity>();
            for (CompiledEntity entity : entities) {
                Vec3 relative = FortificationTemplateCompiler.transformEntity(entity.localPosition, origin, pivot, direction);
                BlockPos relativeBlock = FortificationTransform.world(BlockPos.f_121853_, origin, entity.localBlockPos, pivot, direction);
                orientedEntities.add(new OrientedEntity(entity.index, relative, relativeBlock, entity.type, entity.visualNbt.m_6426_(), entity.damageable));
                AABB cell = new AABB(relativeBlock);
                bounds = bounds == null ? cell : bounds.m_82367_(cell);
            }
            if (bounds == null) {
                bounds = new AABB(BlockPos.f_121853_);
            }
            rotations.put(direction, new OrientedTemplate(List.copyOf(orientedBlocks), List.copyOf(orientedEntities), List.copyOf(footprint), bounds));
        }
        return new CompiledTemplate(id, placement.paletteIndex, size, damageableParts, Map.copyOf(rotations));
    }

    private static ListTag selectPalette(CompoundTag template, int index) {
        if (template.m_128425_("palettes", 9)) {
            ListTag palettes = template.m_128437_("palettes", 9);
            if (index < 0 || index >= palettes.size()) {
                throw new IllegalArgumentException("palette_index " + index + " \u8d8a\u754c\uff08\u5171 " + palettes.size() + "\uff09");
            }
            return palettes.m_128744_(index);
        }
        if (index != 0) {
            throw new IllegalArgumentException("\u5355 palette \u6a21\u677f\u53ea\u80fd\u4f7f\u7528 palette_index=0");
        }
        return template.m_128437_("palette", 10);
    }

    private static Vec3 transformEntity(Vec3 local, BlockPos origin, BlockPos pivot, Direction direction) {
        double x = (double)origin.m_123341_() + local.f_82479_ - (double)pivot.m_123341_();
        double y = (double)origin.m_123342_() + local.f_82480_ - (double)pivot.m_123342_();
        double z = (double)origin.m_123343_() + local.f_82481_ - (double)pivot.m_123343_();
        return switch (direction) {
            case Direction.NORTH -> new Vec3(x, y, z);
            case Direction.EAST -> new Vec3(-z, y, x);
            case Direction.SOUTH -> new Vec3(-x, y, -z);
            case Direction.WEST -> new Vec3(z, y, -x);
            default -> throw new IllegalArgumentException("facing must be horizontal");
        };
    }

    private static BlockPos vec(int[] values) {
        if (values == null || values.length != 3) {
            throw new IllegalArgumentException("\u5750\u6807\u5fc5\u987b\u4e3a3\u9879");
        }
        return new BlockPos(values[0], values[1], values[2]);
    }

    private static ResourceLocation requireId(String raw, String path) {
        ResourceLocation id = ResourceLocation.m_135820_(raw);
        if (id == null) {
            throw new IllegalArgumentException(path + " \u975e\u6cd5: " + raw);
        }
        return id;
    }

    record CompiledTemplate(ResourceLocation id, int paletteIndex, Vec3i size, int damageablePartCount, Map<Direction, OrientedTemplate> rotations) {
        OrientedTemplate oriented(Direction facing) {
            OrientedTemplate result = this.rotations.get(facing);
            if (result == null) {
                throw new IllegalArgumentException("facing must be horizontal");
            }
            return result;
        }
    }

    static enum Touch {
        BLOCK,
        EXPLICIT_AIR,
        IGNORE;

    }

    private record RawBlock(int index, BlockPos local, BlockState state, @Nullable CompoundTag blockEntity, Touch touch) {
    }

    record CompiledEntity(int index, Vec3 localPosition, BlockPos localBlockPos, ResourceLocation type, CompoundTag visualNbt, boolean damageable) {
    }

    record OrientedBlock(int templateIndex, BlockPos relativePos, BlockState state, @Nullable CompoundTag blockEntityNbt, Touch touch) {
    }

    record OrientedEntity(int templateIndex, Vec3 relativePosition, BlockPos relativeBlockPos, ResourceLocation type, CompoundTag visualNbt, boolean damageable) {
    }

    record OrientedTemplate(List<OrientedBlock> blocks, List<OrientedEntity> entities, List<BlockPos> damageableFootprint, AABB relativeBounds) {
        AABB boundsAt(BlockPos anchor) {
            return this.relativeBounds.m_82338_(anchor);
        }
    }
}

