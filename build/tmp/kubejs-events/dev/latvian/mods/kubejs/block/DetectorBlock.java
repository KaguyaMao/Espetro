/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.DetectorBlockEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class DetectorBlock
extends Block {
    private final Builder builder;

    public DetectorBlock(Builder b) {
        super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50752_));
        this.builder = b;
        this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)BlockStateProperties.f_61448_, (Comparable)Boolean.valueOf(false)));
    }

    @Deprecated
    public void m_6861_(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        boolean p;
        boolean bl2 = p = (Boolean)blockState.m_61143_((Property)BlockStateProperties.f_61448_) == false;
        if (p == level.m_276867_(blockPos)) {
            level.m_7731_(blockPos, (BlockState)blockState.m_61124_((Property)BlockStateProperties.f_61448_, (Comparable)Boolean.valueOf(p)), 2);
            if (BlockEvents.DETECTOR_CHANGED.hasListeners(this.builder.detectorId) || (p ? BlockEvents.DETECTOR_POWERED : BlockEvents.DETECTOR_UNPOWERED).hasListeners(this.builder.detectorId)) {
                DetectorBlockEventJS e = new DetectorBlockEventJS(this.builder.detectorId, level, blockPos, p);
                BlockEvents.DETECTOR_CHANGED.post((ScriptTypeHolder)level, (Object)this.builder.detectorId, e);
                if (p) {
                    BlockEvents.DETECTOR_POWERED.post((ScriptTypeHolder)level, (Object)this.builder.detectorId, e);
                } else {
                    BlockEvents.DETECTOR_UNPOWERED.post((ScriptTypeHolder)level, (Object)this.builder.detectorId, e);
                }
            }
        }
    }

    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        builder.m_61104_(new Property[]{BlockStateProperties.f_61448_});
    }

    public static class Builder
    extends BlockBuilder {
        public transient String detectorId;

        public Builder(ResourceLocation i) {
            super(i);
            this.detectorId = (String)(this.id.m_135827_().equals("kubejs") ? "" : this.id.m_135827_() + ".") + this.id.m_135815_().replace('/', '.');
            if (this.detectorId.endsWith("_detector")) {
                this.detectorId = this.detectorId.substring(0, this.detectorId.length() - 9);
            }
            if (this.detectorId.startsWith("detector_")) {
                this.detectorId = this.detectorId.substring(9);
            }
            this.displayName((Component)Component.m_237113_((String)("KubeJS Detector [" + this.detectorId + "]")));
        }

        public Builder detectorId(String id) {
            this.detectorId = id;
            this.displayName((Component)Component.m_237113_((String)("KubeJS Detector [" + this.detectorId + "]")));
            return this;
        }

        @Override
        public Block createObject() {
            return new DetectorBlock(this);
        }

        @Override
        public void generateAssetJsons(AssetJsonGenerator generator) {
            generator.blockState(this.id, bs -> {
                bs.simpleVariant("powered=false", "kubejs:block/detector");
                bs.simpleVariant("powered=true", "kubejs:block/detector_on");
            });
            generator.itemModel(this.id, m -> m.parent("kubejs:block/detector"));
        }
    }
}

