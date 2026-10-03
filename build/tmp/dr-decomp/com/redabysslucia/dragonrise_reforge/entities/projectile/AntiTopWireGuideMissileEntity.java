/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance
 *  com.atsuishio.superbwarfare.entity.projectile.BasicGeoProjectileEntity
 *  com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile
 *  com.atsuishio.superbwarfare.entity.projectile.WireGuideMissileEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance;
import com.atsuishio.superbwarfare.entity.projectile.BasicGeoProjectileEntity;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.WireGuideMissileEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AntiTopWireGuideMissileEntity
extends WireGuideMissileEntity
implements BasicGeoProjectileEntity {
    public AntiTopWireGuideMissileEntity(EntityType<? extends AntiTopWireGuideMissileEntity> type, Level level) {
        super(type, level);
    }

    public Item m_7881_() {
        return (Item)ModItems.MEDIUM_ANTI_GROUND_MISSILE.get();
    }

    public void m_8119_() {
        super.m_8119_();
        if (!this.m_9236_().f_46443_ && this.f_19797_ > 5) {
            this.checkTopAttack();
        }
    }

    private void spawnTopAttackParticles(Vec3 pos) {
        double speed;
        double dz;
        double dy;
        double dx;
        double phi;
        double theta;
        int i;
        ServerLevel serverLevel = (ServerLevel)this.m_9236_();
        for (i = 0; i < 25; ++i) {
            theta = Math.random() * Math.toRadians(20.0);
            phi = Math.random() * 2.0 * Math.PI;
            dx = Math.sin(theta) * Math.cos(phi);
            dy = Math.cos(theta);
            dz = Math.sin(theta) * Math.sin(phi);
            speed = 0.8 + Math.random() * 1.2;
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123744_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 0, dx * speed, -dy * speed, dz * speed, 0.1);
        }
        for (i = 0; i < 15; ++i) {
            theta = Math.random() * Math.toRadians(20.0);
            phi = Math.random() * 2.0 * Math.PI;
            dx = Math.sin(theta) * Math.cos(phi);
            dy = Math.cos(theta);
            dz = Math.sin(theta) * Math.sin(phi);
            speed = 0.3 + Math.random() * 0.6;
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123777_, pos.f_82479_, pos.f_82480_, pos.f_82481_, 0, dx * speed, -dy * speed * 0.5, dz * speed, 0.02);
        }
    }

    private float getDamageAmount() {
        try {
            Field field = FastThrowableProjectile.class.getDeclaredField("damageValue");
            field.setAccessible(true);
            return field.getFloat((Object)this);
        }
        catch (Exception e) {
            return 400.0f;
        }
    }

    private void checkTopAttack() {
        Vec3 pos = this.m_20182_();
        AABB searchBox = new AABB(pos.f_82479_ - 2.0, pos.f_82480_ - 2.0, pos.f_82481_ - 2.0, pos.f_82479_ + 2.0, pos.f_82480_, pos.f_82481_ + 2.0);
        List entities = this.m_9236_().m_6249_((Entity)this, searchBox, entity -> entity instanceof VehicleEntity);
        UUID launcherUUID = super.getLauncherVehicleUUID();
        for (Entity entity2 : entities) {
            if (!(entity2 instanceof VehicleEntity)) continue;
            VehicleEntity vehicleEntity = (VehicleEntity)entity2;
            if (launcherUUID != null && launcherUUID.equals(entity2.m_20148_())) continue;
            this.spawnTopAttackParticles(pos);
            float damage = this.getDamageAmount();
            Entity owner = this.m_19749_();
            if (owner instanceof LivingEntity) {
                LivingEntity livingOwner = (LivingEntity)owner;
                vehicleEntity.m_6469_(this.m_269291_().m_269299_((Entity)this, livingOwner), damage);
            } else {
                vehicleEntity.m_6469_(this.m_269291_().m_269264_(), damage);
            }
            this.causeExplode(pos);
            this.m_146870_();
            return;
        }
    }

    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.ROCKET_FLY.get();
    }

    public float getVolume() {
        return 0.4f;
    }

    public float getMaxHealth() {
        return 20.0f;
    }

    public ResourceLocation getModel() {
        return new ResourceLocation("dragonrise_reforge", "projectile/anti_top_wire_guide_missile");
    }

    public ResourceLocation getAnimation() {
        return null;
    }

    public BasicProjectileAnimationInstance<?> getAnimationInstance() {
        return null;
    }

    public ResourceLocation getEmissiveTexture() {
        return null;
    }

    public int getHiddenTicks() {
        return 0;
    }

    public int getFlareHiddenTicks() {
        return 3;
    }
}

