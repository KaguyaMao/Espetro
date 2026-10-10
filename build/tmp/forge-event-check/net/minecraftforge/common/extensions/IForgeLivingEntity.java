/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.fluids.FluidType;

public interface IForgeLivingEntity
extends IForgeEntity {
    default public LivingEntity self() {
        return (LivingEntity)this;
    }

    @Override
    default public boolean canSwimInFluidType(FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get()) {
            return !this.self().m_6126_();
        }
        return IForgeEntity.super.canSwimInFluidType(type);
    }

    default public void jumpInFluid(FluidType type) {
        this.self().m_20256_(this.self().m_20184_().m_82520_(0.0, (double)0.04f * this.self().m_21133_(ForgeMod.SWIM_SPEED.get()), 0.0));
    }

    default public void sinkInFluid(FluidType type) {
        this.self().m_20256_(this.self().m_20184_().m_82520_(0.0, (double)-0.04f * this.self().m_21133_(ForgeMod.SWIM_SPEED.get()), 0.0));
    }

    default public boolean canDrownInFluidType(FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get()) {
            return !this.self().m_6040_();
        }
        return type.canDrownIn(this.self());
    }

    default public boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
        return state.move(this.self(), movementVector, gravity);
    }
}

