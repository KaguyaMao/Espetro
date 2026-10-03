/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.core.block.ArchitecturyLiquidBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.FlowingFluid
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.fluid;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.Nullable;

public class FluidBlockBuilder
extends BlockBuilder {
    private final FluidBuilder fluidBuilder;

    public FluidBlockBuilder(FluidBuilder b) {
        super(b.id);
        this.fluidBuilder = b;
        this.defaultTranslucent();
        this.noItem();
        this.noDrops();
    }

    @Override
    public Block createObject() {
        return new ArchitecturyLiquidBlock(() -> Objects.requireNonNull((FlowingFluid)this.fluidBuilder.flowingFluid.get(), "Flowing Fluid is null!"), BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49990_).m_60910_().m_60978_(100.0f).m_222994_());
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        generator.blockState(this.id, m -> m.simpleVariant("", this.id.m_135827_() + ":block/" + this.id.m_135815_()));
        generator.blockModel(this.id, m -> {
            m.parent("");
            m.texture("particle", this.fluidBuilder.stillTexture.toString());
        });
    }

    @Override
    public BlockBuilder item(@Nullable Consumer<BlockItemBuilder> i) {
        if (i != null) {
            throw new IllegalStateException("Fluid blocks cannot have items!");
        }
        return super.item(null);
    }
}

