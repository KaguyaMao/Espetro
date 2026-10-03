/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.BlockPathTypes
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public interface IForgeFluidState {
    private FluidState self() {
        return (FluidState)this;
    }

    default public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
        return this.self().m_76152_().getExplosionResistance(this.self(), level, pos, explosion);
    }

    default public FluidType getFluidType() {
        return this.self().m_76152_().getFluidType();
    }

    default public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
        return this.self().m_76152_().move(this.self(), entity, movementVector, gravity);
    }

    default public boolean canConvertToSource(Level level, BlockPos pos) {
        return this.self().m_76152_().canConvertToSource(this.self(), level, pos);
    }

    default public boolean supportsBoating(Boat boat) {
        return this.self().m_76152_().supportsBoating(this.self(), boat);
    }

    default public boolean shouldUpdateWhileBoating(Boat boat, Entity rider) {
        return this.self().m_76152_().shouldUpdateWhileBoating(this.self(), boat, rider);
    }

    @Nullable
    default public BlockPathTypes getBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
        return this.self().m_76152_().getBlockPathType(this.self(), level, pos, mob, canFluidLog);
    }

    @Nullable
    default public BlockPathTypes getAdjacentBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
        return this.self().m_76152_().getAdjacentBlockPathType(this.self(), level, pos, mob, originalType);
    }

    default public boolean canHydrate(BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos) {
        return this.self().m_76152_().canHydrate(this.self(), getter, pos, source, sourcePos);
    }

    default public boolean canExtinguish(BlockGetter getter, BlockPos pos) {
        return this.self().m_76152_().canExtinguish(this.self(), getter, pos);
    }
}

