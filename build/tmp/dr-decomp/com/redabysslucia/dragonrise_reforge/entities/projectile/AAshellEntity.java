/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.tools.CustomExplosion$Builder
 *  com.atsuishio.superbwarfare.tools.DamageHandler
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  software.bernie.geckolib.animatable.GeoEntity
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AAshellEntity
extends FastThrowableProjectile
implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AAshellEntity(EntityType<? extends AAshellEntity> type, Level level) {
        super(type, level);
        this.f_19811_ = true;
        this.setExplosionDamage(80.0f);
        this.setExplosionRadius(15.0f);
    }

    @NotNull
    protected Item m_7881_() {
        return (Item)ModItems.SMALL_SHELL_AA.get();
    }

    public void m_5790_(@NotNull EntityHitResult result) {
        super.m_5790_(result);
        Entity entity = result.m_82443_();
        if (this.m_19749_() != null && this.m_19749_().m_20202_() != null && entity == this.m_19749_().m_20202_()) {
            return;
        }
        if (this.m_9236_() instanceof ServerLevel) {
            DamageHandler.doDamage((Entity)entity, (DamageSource)ModDamageTypes.causeProjectileHitDamage((RegistryAccess)this.m_9236_().m_9598_(), (Entity)this, (Entity)this.m_19749_()), (float)this.getExplosionDamageValue());
            if (entity instanceof LivingEntity) {
                entity.f_19802_ = 0;
            }
            if (this.f_19797_ > 0) {
                this.causeExplode(result.m_82450_(), true);
            }
            this.m_146870_();
        }
    }

    public void m_8060_(@NotNull BlockHitResult blockHitResult) {
        Block block;
        float hardness;
        super.m_8060_(blockHitResult);
        BlockPos resultPos = blockHitResult.m_82425_();
        BlockState state = this.m_9236_().m_8055_(resultPos);
        if (this.m_9236_() instanceof ServerLevel && (hardness = this.m_9236_().m_8055_(resultPos).m_60734_().m_155943_()) != -1.0f && ((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue() && ((Boolean)ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()).booleanValue()) {
            boolean destroy;
            boolean bl = destroy = Math.random() < Mth.m_14008_((double)(1.0f - hardness / 50.0f), (double)0.1, (double)1.0);
            if (destroy) {
                this.m_9236_().m_46961_(resultPos, true);
            }
        }
        if ((block = state.m_60734_()) instanceof BellBlock) {
            BellBlock bell = (BellBlock)block;
            bell.m_49712_(this.m_9236_(), resultPos, blockHitResult.m_82434_());
        }
        if (this.m_9236_() instanceof ServerLevel) {
            this.causeExplode(blockHitResult.m_82450_(), false);
        }
        this.m_146870_();
    }

    private void causeExplode(Vec3 vec3, boolean hitEntity) {
        new CustomExplosion.Builder((Entity)this).attacker(this.m_19749_()).damage(this.getExplosionDamageValue() * 1.25f).radius(this.getExplosionRadiusValue()).position(vec3).withParticleType(ParticleTool.particleTypeForRadius((float)this.getExplosionRadiusValue())).destroyBlock(!hitEntity).explode();
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_19749_() != null && this.m_20280_(this.m_19749_()) > 1048576.0) {
            if (this.m_9236_() instanceof ServerLevel) {
                this.causeExplode(this.m_20182_());
            }
            this.m_146870_();
        }
        if (this.m_9236_() instanceof ServerLevel) {
            double radius = 8.0;
            for (Entity entity : this.m_9236_().m_45933_((Entity)this, this.m_20191_().m_82400_(radius))) {
                if (entity == this || entity == this.m_19749_() || entity.m_20096_()) continue;
                this.causeExplode(this.m_20182_());
                this.m_146870_();
                break;
            }
        }
    }

    public boolean isFastMoving() {
        return false;
    }

    public boolean forceLoadChunk() {
        return true;
    }
}

