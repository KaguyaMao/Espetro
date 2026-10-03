/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
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
import com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AirBomb500kgEntity
extends DestroyableProjectile
implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    private int explosionTimer = -1;
    private static final int EXPLOSION_DELAY = 40;

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AirBomb500kgEntity(EntityType<? extends AirBomb500kgEntity> type, Level level) {
        super(type, level);
        this.f_19811_ = true;
        this.setExplosionRadius(20.0f);
        this.setExplosionDamage(800.0f);
    }

    public boolean m_6469_(@NotNull DamageSource source, float amount) {
        AirBomb500kgEntity bombEntity;
        Entity entity = source.m_7640_();
        if (entity instanceof AirBomb500kgEntity && (bombEntity = (AirBomb500kgEntity)entity).m_19749_() == this.m_19749_()) {
            return false;
        }
        return super.m_6469_(source, amount);
    }

    @NotNull
    protected Item m_7881_() {
        return (Item)ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    public void m_5790_(@NotNull EntityHitResult result) {
        super.m_5790_(result);
        Entity entity = result.m_82443_();
        if (entity == this.m_19749_() || this.m_19749_() != null && entity == this.m_19749_().m_20202_() || entity instanceof AirBomb500kgEntity) {
            return;
        }
        if (this.m_9236_() instanceof ServerLevel) {
            if (((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue() && ((Boolean)ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()).booleanValue()) {
                AABB aabb = new AABB(result.m_82450_(), result.m_82450_()).m_82400_(5.0);
                BlockPos.m_121921_((AABB)aabb).forEach(pos -> {
                    float hard = this.m_9236_().m_8055_(pos).m_60734_().m_155943_();
                    if (hard != -1.0f) {
                        Vec3 vec3 = new Vec3((double)pos.m_123341_(), (double)pos.m_123342_(), (double)pos.m_123343_());
                        if (vec3.m_82554_(result.m_82450_()) < 3.0) {
                            this.m_9236_().m_46961_(pos, true);
                        }
                    }
                });
            }
            this.startExplosionTimer();
        }
    }

    public void m_8060_(@NotNull BlockHitResult blockHitResult) {
        super.m_8060_(blockHitResult);
        if (this.m_9236_() instanceof ServerLevel) {
            if (((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue() && ((Boolean)ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()).booleanValue()) {
                AABB aabb = new AABB(blockHitResult.m_82450_(), blockHitResult.m_82450_()).m_82400_(5.0);
                BlockPos.m_121921_((AABB)aabb).forEach(pos -> {
                    float hard = this.m_9236_().m_8055_(pos).m_60734_().m_155943_();
                    if (hard != -1.0f) {
                        Vec3 vec3 = new Vec3((double)pos.m_123341_(), (double)pos.m_123342_(), (double)pos.m_123343_());
                        if (vec3.m_82554_(blockHitResult.m_82450_()) < 3.0) {
                            this.m_9236_().m_46961_(pos, true);
                        }
                    }
                });
            }
            this.startExplosionTimer();
        }
    }

    private void startExplosionTimer() {
        if (this.explosionTimer == -1) {
            this.explosionTimer = 40;
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
            }
        }
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.explosionTimer > 0) {
            --this.explosionTimer;
            if (this.explosionTimer <= 0) {
                if (this.m_9236_() instanceof ServerLevel) {
                    this.causeExplode(this.m_20182_());
                }
                this.m_146870_();
                return;
            }
        }
        if (this.explosionTimer == -1) {
            float customFriction = 0.99f;
            Vec3 vec3 = this.m_20184_();
            this.m_20256_(vec3.m_82490_((double)(1.0f / customFriction)));
        } else {
            this.m_20334_(0.0, 0.0, 0.0);
        }
    }

    @NotNull
    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.SHELL_FLY.get();
    }

    public float getVolume() {
        return 0.7f;
    }

    public float getMaxHealth() {
        return 50.0f;
    }
}

