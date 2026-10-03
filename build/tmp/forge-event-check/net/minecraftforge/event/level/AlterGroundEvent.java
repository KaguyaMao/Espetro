/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.LevelSimulatedReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public class AlterGroundEvent
extends Event {
    private final LevelSimulatedReader level;
    private final RandomSource random;
    private final BlockPos pos;
    private final BlockState originalAltered;
    private BlockState newAltered;

    @ApiStatus.Internal
    public AlterGroundEvent(LevelSimulatedReader level, RandomSource random, BlockPos pos, BlockState altered) {
        this.level = level;
        this.random = random;
        this.pos = pos;
        this.originalAltered = altered;
        this.newAltered = altered;
    }

    public LevelSimulatedReader getLevel() {
        return this.level;
    }

    public RandomSource getRandom() {
        return this.random;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getOriginalAlteredState() {
        return this.originalAltered;
    }

    public BlockState getNewAlteredState() {
        return this.newAltered;
    }

    public void setNewAlteredState(BlockState newAltered) {
        this.newAltered = newAltered;
    }
}

