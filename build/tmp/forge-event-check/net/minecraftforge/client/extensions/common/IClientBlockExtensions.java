/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.HitResult
 *  org.joml.Vector3d
 */
package net.minecraftforge.client.extensions.common;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3d;

public interface IClientBlockExtensions {
    public static final IClientBlockExtensions DEFAULT = new IClientBlockExtensions(){};

    public static IClientBlockExtensions of(BlockState state) {
        return IClientBlockExtensions.of(state.m_60734_());
    }

    public static IClientBlockExtensions of(Block block) {
        IClientBlockExtensions e;
        Object object = block.getRenderPropertiesInternal();
        return object instanceof IClientBlockExtensions ? (e = (IClientBlockExtensions)object) : DEFAULT;
    }

    default public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        return false;
    }

    default public boolean addDestroyEffects(BlockState state, Level Level2, BlockPos pos, ParticleEngine manager) {
        return !state.m_245147_();
    }

    default public Vector3d getFogColor(BlockState state, LevelReader level, BlockPos pos, Entity entity, Vector3d originalColor, float partialTick) {
        FluidState fluidState = level.m_6425_(pos);
        if (fluidState.m_205070_(FluidTags.f_13131_)) {
            float f12 = 0.0f;
            if (entity instanceof LivingEntity) {
                LivingEntity ent = (LivingEntity)entity;
                f12 = (float)EnchantmentHelper.m_44918_((LivingEntity)ent) * 0.2f;
                if (ent.m_21023_(MobEffects.f_19608_)) {
                    f12 = f12 * 0.3f + 0.6f;
                }
            }
            return new Vector3d((double)(0.02f + f12), (double)(0.02f + f12), (double)(0.2f + f12));
        }
        if (fluidState.m_205070_(FluidTags.f_13132_)) {
            return new Vector3d((double)0.6f, (double)0.1f, 0.0);
        }
        return originalColor;
    }

    default public boolean areBreakingParticlesTinted(BlockState state, ClientLevel level, BlockPos pos) {
        return !state.m_60713_(Blocks.f_50440_);
    }
}

