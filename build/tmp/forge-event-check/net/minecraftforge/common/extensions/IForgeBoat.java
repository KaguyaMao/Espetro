/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.level.material.FluidState
 */
package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

public interface IForgeBoat {
    private Boat self() {
        return (Boat)this;
    }

    default public boolean canBoatInFluid(FluidState state) {
        return state.supportsBoating(this.self());
    }

    default public boolean canBoatInFluid(FluidType type) {
        return type.supportsBoating(this.self());
    }

    default public boolean shouldUpdateFluidWhileRiding(FluidState state, Entity rider) {
        return state.shouldUpdateWhileBoating(this.self(), rider);
    }
}

