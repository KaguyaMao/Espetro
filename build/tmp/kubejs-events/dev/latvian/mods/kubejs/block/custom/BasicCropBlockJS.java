/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.block.custom.CropBlockBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BasicCropBlockJS
extends CropBlock {
    private final int age;
    private final ItemBuilder seedItem;
    private final List<VoxelShape> shapeByAge;
    private final boolean dropSeed;
    private final ToDoubleFunction<RandomTickCallbackJS> growSpeedCallback;
    private final ToIntFunction<RandomTickCallbackJS> fertilizerCallback;
    private final CropBlockBuilder.SurviveCallback surviveCallback;

    public BasicCropBlockJS(CropBlockBuilder builder) {
        super(builder.createProperties().m_60918_(SoundType.f_56758_).m_60977_());
        this.age = builder.age;
        this.seedItem = builder.itemBuilder;
        this.shapeByAge = builder.shapeByAge;
        this.dropSeed = builder.dropSeed;
        this.growSpeedCallback = builder.growSpeedCallback;
        this.fertilizerCallback = builder.fertilizerCallback;
        this.surviveCallback = builder.surviveCallback;
    }

    public int m_7419_() {
        return this.age;
    }

    protected ItemLike m_6404_() {
        return this.dropSeed ? (ItemLike)this.seedItem.get() : Items.f_41852_;
    }

    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        builder.m_61104_(new Property[]{this.m_7959_()});
    }

    public VoxelShape m_5940_(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapeByAge.get((Integer)blockState.m_61143_((Property)this.m_7959_()));
    }

    public void m_213898_(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        double f = this.growSpeedCallback == null ? -1.0 : this.growSpeedCallback.applyAsDouble(new RandomTickCallbackJS(new BlockContainerJS((Level)serverLevel, blockPos), random));
        int age = this.m_52305_(blockState);
        if (age < this.m_7419_()) {
            if (f < 0.0) {
                f = BasicCropBlockJS.m_52272_((Block)this, (BlockGetter)serverLevel, (BlockPos)blockPos);
            }
            if (f > 0.0 && random.m_188503_((int)(25.0 / f) + 1) == 0) {
                serverLevel.m_7731_(blockPos, this.m_52289_(age + 1), 2);
            }
        }
    }

    public void m_52263_(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.fertilizerCallback == null) {
            super.m_52263_(level, blockPos, blockState);
        } else {
            int effect = this.fertilizerCallback.applyAsInt(new RandomTickCallbackJS(new BlockContainerJS(level, blockPos), level.f_46441_));
            if (effect > 0) {
                level.m_7731_(blockPos, this.m_52289_(Integer.min(this.m_52305_(blockState) + effect, this.m_7419_())), 2);
            }
        }
    }

    public boolean m_7898_(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return this.surviveCallback != null ? this.surviveCallback.survive(blockState, levelReader, blockPos) : super.m_7898_(blockState, levelReader, blockPos);
    }
}

