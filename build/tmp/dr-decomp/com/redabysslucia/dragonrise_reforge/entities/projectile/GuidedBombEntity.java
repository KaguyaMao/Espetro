/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.entity.projectile.AerialBombEntity
 *  com.atsuishio.superbwarfare.entity.projectile.BasicGeoProjectileEntity
 *  com.atsuishio.superbwarfare.entity.projectile.BasicGeoProjectileEntity$DefaultImpls
 *  com.atsuishio.superbwarfare.entity.projectile.MissileProjectile
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.AerialBombEntity;
import com.atsuishio.superbwarfare.entity.projectile.BasicGeoProjectileEntity;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class GuidedBombEntity
extends MissileProjectile
implements BasicGeoProjectileEntity {
    private static final EntityDataAccessor<Boolean> LOCKED = new EntityDataAccessor(200, EntityDataSerializers.f_135035_);
    private static final double LASER_SEEK_DISTANCE = 1024.0;
    protected Vec3 currentTarget = null;

    public boolean isLocked() {
        return (Boolean)this.f_19804_.m_135370_(LOCKED);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(LOCKED, (Object)false);
    }

    protected double getCorrectionDegreesPerTick() {
        return 5.0;
    }

    public GuidedBombEntity(EntityType<? extends GuidedBombEntity> type, Level level) {
        super(type, level);
        this.setExplosionDamage(1300.0f);
        this.setExplosionRadius(32.0f);
        this.setDistracted(false);
    }

    public boolean m_20068_() {
        return false;
    }

    public float getCustomGravity() {
        return this.getGravityValue();
    }

    public boolean canPassThroughFluid() {
        return true;
    }

    public Item m_7881_() {
        return (Item)ModItems.LARGE_AERIAL_BOMB.get();
    }

    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.SHELL_FLY.get();
    }

    public float getVolume() {
        return 0.7f;
    }

    public float getMaxHealth() {
        return 90.0f;
    }

    public void distractedByDecoy() {
    }

    public void afterHitEntity(EntityHitResult result) {
        Entity owner;
        Entity entity = result.m_82443_();
        if (entity == (owner = this.m_19749_()) || owner != null && entity == owner.m_20202_() || entity instanceof AerialBombEntity) {
            return;
        }
        if (this.m_9236_() instanceof ServerLevel) {
            this.destroyNearbyBlocks(result.m_82450_());
            this.causeExplode(result.m_82450_());
            this.m_146870_();
        }
    }

    public void afterHitBlock(BlockHitResult result) {
        if (this.m_9236_() instanceof ServerLevel) {
            this.destroyNearbyBlocks(result.m_82450_());
            this.causeExplode(result.m_82450_());
            this.m_146870_();
        }
    }

    private void destroyNearbyBlocks(Vec3 hitPos) {
        if (((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue() && ((Boolean)ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()).booleanValue() && this.getExplosionDestroyValue()) {
            AABB aabb = new AABB(hitPos, hitPos).m_82400_(5.0);
            BlockPos.m_121921_((AABB)aabb).forEach(pos -> {
                float hard = this.m_9236_().m_8055_(pos).m_60734_().m_155943_();
                if (hard != -1.0f) {
                    Vec3 vec3 = new Vec3((double)pos.m_123341_(), (double)pos.m_123342_(), (double)pos.m_123343_());
                    if (vec3.m_82554_(hitPos) < 3.0) {
                        this.m_9236_().m_46961_(pos, true);
                    }
                }
            });
        }
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_() instanceof ServerLevel) {
            boolean locked;
            boolean bl = locked = this.getGuideType() == 1 || !"none".equals(this.getTargetUUID());
            if ((Boolean)this.f_19804_.m_135370_(LOCKED) != locked) {
                this.f_19804_.m_135381_(LOCKED, (Object)locked);
            }
        }
        if (this.m_9236_() instanceof ServerLevel && this.m_6084_() && this.m_19749_() != null) {
            if (this.f_19797_ % 5 == 0) {
                this.updateTarget();
            }
            if (this.currentTarget != null) {
                this.correctVelocityTowards(this.currentTarget, this.getCorrectionDegreesPerTick() / 5.0);
            }
        }
    }

    protected void updateTarget() {
        if (this.getGuideType() == 1) {
            this.currentTarget = this.getTargetPos();
        } else {
            this.currentTarget = this.calculatePodAimPoint();
            if (this.currentTarget != null) {
                this.setTargetPos(this.currentTarget);
            }
        }
    }

    private Vec3 calculatePodAimPoint() {
        BlockHitResult hit;
        Vec3 podPos;
        Entity owner = this.m_19749_();
        if (owner == null) {
            return null;
        }
        Vec3 dir = owner.m_20154_();
        Entity entity = owner.m_20202_();
        if (entity instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)entity;
            podPos = vehicle.getViewPos(owner, 1.0f);
            if (podPos == null) {
                podPos = owner.m_146892_();
            }
        } else {
            podPos = owner.m_146892_();
        }
        return (hit = this.m_9236_().m_45547_(new ClipContext(podPos, podPos.m_82549_(dir.m_82490_(1024.0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner))).m_6662_() == HitResult.Type.MISS ? podPos.m_82549_(dir.m_82490_(1024.0)) : hit.m_82450_();
    }

    protected void correctVelocityTowards(Vec3 target, double maxDegrees) {
        Vec3 toTarget;
        Vec3 cur = this.m_20184_();
        if (cur.m_82556_() < 1.0E-8) {
            return;
        }
        Vec3 curDir = cur.m_82541_();
        double angle = Math.toDegrees(Math.acos(Mth.m_14008_((double)curDir.m_82526_(toTarget = target.m_82546_(this.m_20182_()).m_82541_()), (double)-1.0, (double)1.0)));
        if (angle < 0.01) {
            return;
        }
        double turn = Math.min(angle, maxDegrees);
        Vec3 axis = curDir.m_82537_(toTarget);
        double len = axis.m_82553_();
        if (len < 1.0E-8) {
            return;
        }
        axis = axis.m_82490_(1.0 / len);
        double rad = Math.toRadians(turn);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        Vec3 rotated = cur.m_82490_(cos).m_82549_(axis.m_82537_(cur).m_82490_(sin)).m_82549_(axis.m_82490_(axis.m_82526_(cur) * (1.0 - cos)));
        this.m_20256_(rotated);
    }

    public ResourceLocation getModel() {
        return BasicGeoProjectileEntity.DefaultImpls.getModel((BasicGeoProjectileEntity)this);
    }

    public ResourceLocation getAnimation() {
        return BasicGeoProjectileEntity.DefaultImpls.getAnimation((BasicGeoProjectileEntity)this);
    }

    public BasicProjectileAnimationInstance<?> getAnimationInstance() {
        return BasicGeoProjectileEntity.DefaultImpls.getAnimationInstance((BasicGeoProjectileEntity)this);
    }

    public ResourceLocation getEmissiveTexture() {
        return BasicGeoProjectileEntity.DefaultImpls.getEmissiveTexture((BasicGeoProjectileEntity)this);
    }

    public int getHiddenTicks() {
        return BasicGeoProjectileEntity.DefaultImpls.getHiddenTicks((BasicGeoProjectileEntity)this);
    }

    public int getFlareHiddenTicks() {
        return BasicGeoProjectileEntity.DefaultImpls.getFlareHiddenTicks((BasicGeoProjectileEntity)this);
    }

    public BakedModelInstance getModelInstance() {
        return BasicGeoProjectileEntity.DefaultImpls.getModelInstance((BasicGeoProjectileEntity)this);
    }
}

