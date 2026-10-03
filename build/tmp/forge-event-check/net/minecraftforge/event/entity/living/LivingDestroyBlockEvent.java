/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingDestroyBlockEvent
extends LivingEvent {
    private final BlockPos pos;
    private final BlockState state;

    public LivingDestroyBlockEvent(LivingEntity entity, BlockPos pos, BlockState state) {
        super(entity);
        this.pos = pos;
        this.state = state;
    }

    public BlockState getState() {
        return this.state;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

