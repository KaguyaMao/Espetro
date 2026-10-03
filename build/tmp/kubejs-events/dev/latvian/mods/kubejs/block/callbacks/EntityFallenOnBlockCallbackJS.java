/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.block.callbacks.EntitySteppedOnBlockCallbackJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityFallenOnBlockCallbackJS
extends EntitySteppedOnBlockCallbackJS {
    private final float fallHeight;

    public EntityFallenOnBlockCallbackJS(Level level, Entity entity, BlockPos pos, BlockState state, float fallHeight) {
        super(level, entity, pos, state);
        this.fallHeight = fallHeight;
    }

    @Info(value="Get the height the entity has fallen")
    public float getFallHeight() {
        return this.fallHeight;
    }

    @Info(value="Applies default fall damage to the entity.\nNote this does not force it, so entities that do not take fall damage are not affected.\n")
    public boolean applyFallDamage() {
        return this.applyFallDamage(1.0f);
    }

    @Info(value="Applies fall damage to the entity, multiplier by the multiplier.\nNote this does not force it, so entities that do not take fall damage are not affected.\n")
    public boolean applyFallDamage(float multiplier) {
        return this.applyFallDamage(this.fallHeight, multiplier);
    }

    @Info(value="Applies fall damage to the entity as if they had fallen from the provided height, and multiplies it by the provided multiplier.\nNote this does not force it, so entities that do not take fall damage are not affected.\n")
    public boolean applyFallDamage(float fallHeight, float multiplier) {
        return this.applyFallDamage(fallHeight, multiplier, this.entity.m_269291_().m_268989_());
    }

    @Info(value="Damages the entity using the provided damage source, using the fall height and multiplier to calculate the damage amount.\nNote this does not force the damage, so entities that do not take fall damage are not affected.\n")
    public boolean applyFallDamage(float fallHeight, float multiplier, DamageSource damageSource) {
        return this.entity.m_142535_(fallHeight, multiplier, damageSource);
    }
}

