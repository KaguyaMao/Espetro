/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.HitResult
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import java.util.Collection;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public interface IForgeEntity
extends ICapabilitySerializable<CompoundTag> {
    private Entity self() {
        return (Entity)this;
    }

    @Override
    default public void deserializeNBT(CompoundTag nbt) {
        this.self().m_20258_(nbt);
    }

    @Override
    default public CompoundTag serializeNBT() {
        CompoundTag ret = new CompoundTag();
        String id = this.self().m_20078_();
        if (id != null) {
            ret.m_128359_("id", this.self().m_20078_());
        }
        return this.self().m_20240_(ret);
    }

    public boolean canUpdate();

    public void canUpdate(boolean var1);

    @Nullable
    public Collection<ItemEntity> captureDrops();

    public Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> var1);

    public CompoundTag getPersistentData();

    default public boolean shouldRiderSit() {
        return true;
    }

    default public ItemStack getPickedResult(HitResult target) {
        ItemStack result = this.self().m_142340_();
        if (result == null) {
            SpawnEggItem egg = ForgeSpawnEggItem.fromEntityType(this.self().m_6095_());
            result = egg != null ? new ItemStack((ItemLike)egg) : ItemStack.f_41583_;
        }
        return result;
    }

    default public boolean canRiderInteract() {
        return false;
    }

    default public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return type.canRideVehicleUnder(this.self(), rider);
    }

    public boolean canTrample(BlockState var1, BlockPos var2, float var3);

    default public MobCategory getClassification(boolean forSpawnCount) {
        return this.self().m_6095_().m_20674_();
    }

    public boolean isAddedToWorld();

    public void onAddedToWorld();

    public void onRemovedFromWorld();

    public void revive();

    default public boolean isMultipartEntity() {
        return false;
    }

    @Nullable
    default public PartEntity<?>[] getParts() {
        return null;
    }

    default public float getStepHeight() {
        LivingEntity living;
        AttributeInstance stepHeightAttribute;
        float vanillaStep = this.self().m_274421_();
        Entity entity = this.self();
        if (entity instanceof LivingEntity && (stepHeightAttribute = (living = (LivingEntity)entity).m_21051_(ForgeMod.STEP_HEIGHT_ADDITION.get())) != null) {
            return (float)Math.max(0.0, (double)vanillaStep + stepHeightAttribute.m_22135_());
        }
        return vanillaStep;
    }

    public double getFluidTypeHeight(FluidType var1);

    public FluidType getMaxHeightFluidType();

    default public boolean isInFluidType(FluidState state) {
        return this.isInFluidType(state.getFluidType());
    }

    default public boolean isInFluidType(FluidType type) {
        return this.getFluidTypeHeight(type) > 0.0;
    }

    default public boolean isInFluidType(BiPredicate<FluidType, Double> predicate) {
        return this.isInFluidType(predicate, false);
    }

    public boolean isInFluidType(BiPredicate<FluidType, Double> var1, boolean var2);

    public boolean isInFluidType();

    public FluidType getEyeInFluidType();

    default public boolean isEyeInFluidType(FluidType type) {
        return type == this.getEyeInFluidType();
    }

    default public boolean canStartSwimming() {
        return !this.getEyeInFluidType().isAir() && this.canSwimInFluidType(this.getEyeInFluidType());
    }

    default public double getFluidMotionScale(FluidType type) {
        return type.motionScale(this.self());
    }

    default public boolean isPushedByFluid(FluidType type) {
        return this.self().m_6063_() && type.canPushEntity(this.self());
    }

    default public boolean canSwimInFluidType(FluidType type) {
        return type.canSwim(this.self());
    }

    default public boolean canFluidExtinguish(FluidType type) {
        return type.canExtinguish(this.self());
    }

    default public float getFluidFallDistanceModifier(FluidType type) {
        return type.getFallDistanceModifier(this.self());
    }

    default public boolean canHydrateInFluidType(FluidType type) {
        return type.canHydrate(this.self());
    }

    @Nullable
    default public SoundEvent getSoundFromFluidType(FluidType type, SoundAction action) {
        return type.getSound(this.self(), action);
    }

    default public boolean hasCustomOutlineRendering(Player player) {
        return false;
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    default public float getEyeHeightForge(Pose pose, EntityDimensions size) {
        return this.self().getEyeHeightAccess(pose, size);
    }

    default public boolean shouldUpdateFluidWhileBoating(FluidState state, Boat boat) {
        return boat.shouldUpdateFluidWhileRiding(state, this.self());
    }
}

