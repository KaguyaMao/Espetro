/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Info(value="Invoked when a block is placed.\n")
public class BlockPlacedEventJS
extends EntityEventJS {
    private final Entity entity;
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;

    public BlockPlacedEventJS(@Nullable Entity entity, Level level, BlockPos pos, BlockState state) {
        this.entity = entity;
        this.level = level;
        this.pos = pos;
        this.state = state;
    }

    @Override
    @Info(value="The level of the block that was placed.")
    public Level getLevel() {
        return this.level;
    }

    @Override
    @Info(value="The entity that placed the block. Can be `null`, e.g. when a block is placed by a dispenser.")
    public Entity getEntity() {
        return this.entity;
    }

    @Info(value="The block that is placed.")
    public BlockContainerJS getBlock() {
        return new BlockContainerJS(this.level, this.pos){

            @Override
            public BlockState getBlockState() {
                return BlockPlacedEventJS.this.state;
            }
        };
    }
}

