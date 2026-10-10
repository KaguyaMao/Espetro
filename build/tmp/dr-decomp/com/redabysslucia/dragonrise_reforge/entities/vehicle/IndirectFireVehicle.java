/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 */
package com.redabysslucia.dragonrise_reforge.entities.vehicle;

import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlComputation;
import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlStatus;
import com.redabysslucia.dragonrise_reforge.firecontrol.TrajectoryMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface IndirectFireVehicle {
    public boolean isFireControlActive();

    public BlockPos getFireControlTarget();

    public int getFireControlRadius();

    public TrajectoryMode getFireControlTrajectory();

    public FireControlStatus getFireControlStatus();

    public FireControlComputation getFireControlComputation();

    public boolean applyFireControl(BlockPos var1, int var2, TrajectoryMode var3, Entity var4);

    public void clearFireControl(Entity var1);

    public boolean isFireControlTakeoverEnabled();

    public void setFireControlTakeover(boolean var1, Entity var2);
}

