/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntitySteppedOnBlockCallbackJS {
    protected final Level level;
    protected final Entity entity;
    protected final BlockContainerJS block;
    protected final BlockState state;

    public EntitySteppedOnBlockCallbackJS(Level level, Entity entity, BlockPos pos, BlockState state) {
        this.level = level;
        this.entity = entity;
        this.block = new BlockContainerJS(level, pos);
        this.state = state;
    }

    @Info(value="Returns the level")
    public Level getLevel() {
        return this.level;
    }

    @Info(value="Returns the entity")
    public Entity getEntity() {
        return this.entity;
    }

    @Info(value="Returns the block")
    public BlockContainerJS getBlock() {
        return this.block;
    }

    @Info(value="Returns the BlockState")
    public BlockState getState() {
        return this.state;
    }

    @Info(value="Returns the block's position")
    public BlockPos getPos() {
        return this.block.getPos();
    }

    @Info(value="Returns if the entity is suppressing bouncing (for players this is true if the player is crouching)")
    public boolean isSuppressingBounce() {
        return this.entity.m_20162_();
    }
}

