/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

@Cancelable
@Event.HasResult
public class BonemealEvent
extends PlayerEvent {
    private final Level level;
    private final BlockPos pos;
    private final BlockState block;
    private final ItemStack stack;

    public BonemealEvent(@NotNull Player player, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState block, @NotNull ItemStack stack) {
        super(player);
        this.level = level;
        this.pos = pos;
        this.block = block;
        this.stack = stack;
    }

    public Level getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getBlock() {
        return this.block;
    }

    @NotNull
    public ItemStack getStack() {
        return this.stack;
    }
}

