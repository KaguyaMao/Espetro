/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.core.BlockKJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface EntityBlockKJS
extends BlockKJS,
EntityBlock {
    @Nullable
    default public BlockEntity m_142194_(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Nullable
    default public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return null;
    }
}

