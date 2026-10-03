/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.AnimationPlayType
 *  com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.client.animation.AnimationPlayType;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class M3A3Entity
extends VehicleEntity {
    public static final EntityDataAccessor<Integer> MISSILE_STATE = SynchedEntityData.m_135353_(M3A3Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    public static final EntityDataAccessor<Integer> DEPLOY_TIMER = SynchedEntityData.m_135353_(M3A3Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private String lastMissileAnim = "";
    private final Map<UUID, Integer> lastMessageTick = new HashMap<UUID, Integer>();

    public M3A3Entity(EntityType<M3A3Entity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(MISSILE_STATE, (Object)0);
        this.f_19804_.m_135372_(DEPLOY_TIMER, (Object)0);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.25f) * damage);
    }

    private boolean isMoving() {
        Vec3 motion = this.m_20184_();
        return Math.abs(motion.f_82479_) > 0.01 || Math.abs(motion.f_82481_) > 0.01;
    }

    private boolean anyPassengerHasMissile() {
        for (Entity passenger : this.m_20197_()) {
            int seatIndex = this.getSeatIndex(passenger);
            if (seatIndex < 0 || this.getSelectedWeapon(seatIndex) != 1) continue;
            return true;
        }
        return false;
    }

    private void showClientMessage(LivingEntity living, String key) {
        if (this.m_9236_().f_46443_ && living instanceof Player) {
            int lastTick;
            Player player = (Player)living;
            if (ClientEventHandler.holdFireVehicle && this.f_19797_ - (lastTick = this.lastMessageTick.getOrDefault(player.m_20148_(), -100).intValue()) > 20) {
                this.lastMessageTick.put(player.m_20148_(), this.f_19797_);
                player.m_5661_((Component)Component.m_237115_((String)key), true);
            }
        }
    }

    public boolean canShoot(LivingEntity living) {
        int seatIndex = this.getSeatIndex((Entity)living);
        if (seatIndex < 0) {
            return super.canShoot(living);
        }
        int selectedWeapon = this.getSelectedWeapon(seatIndex);
        if (selectedWeapon != 1) {
            return super.canShoot(living);
        }
        if (this.isMoving()) {
            this.showClientMessage(living, "message.dragonrise_reforge.m3a3_stop_to_shoot");
            return false;
        }
        if ((Integer)this.f_19804_.m_135370_(MISSILE_STATE) != 2) {
            this.showClientMessage(living, "message.dragonrise_reforge.m3a3_wait_deploy");
            return false;
        }
        return super.canShoot(living);
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().f_46443_) {
            this.tickMissileAnimation();
            return;
        }
        int currentState = (Integer)this.f_19804_.m_135370_(MISSILE_STATE);
        boolean anyMissileSelected = this.anyPassengerHasMissile();
        boolean moving = this.isMoving();
        if (moving) {
            if (currentState == 2 || currentState == 1) {
                this.f_19804_.m_135381_(MISSILE_STATE, (Object)3);
                this.f_19804_.m_135381_(DEPLOY_TIMER, (Object)30);
            }
        } else if (anyMissileSelected) {
            if (currentState == 0) {
                this.f_19804_.m_135381_(MISSILE_STATE, (Object)1);
                this.f_19804_.m_135381_(DEPLOY_TIMER, (Object)30);
            }
        } else if (currentState == 2 || currentState == 1) {
            this.f_19804_.m_135381_(MISSILE_STATE, (Object)3);
            this.f_19804_.m_135381_(DEPLOY_TIMER, (Object)30);
        }
        if (currentState == 1 || currentState == 3) {
            int timer = (Integer)this.f_19804_.m_135370_(DEPLOY_TIMER) - 1;
            this.f_19804_.m_135381_(DEPLOY_TIMER, (Object)timer);
            if (timer <= 0) {
                this.f_19804_.m_135381_(MISSILE_STATE, (Object)(currentState == 1 ? 2 : 0));
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void tickMissileAnimation() {
    }

    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        Player player;
        VehicleAnimationInstance ani;
        int selectedWeapon;
        int seatIndex = this.getSeatIndex((Entity)living);
        int n = selectedWeapon = seatIndex >= 0 ? this.getSelectedWeapon(seatIndex) : -1;
        if (this.m_9236_().f_46443_ && selectedWeapon == 0 && (ani = this.getAnimationInstance()) != null) {
            ani.getContext().playAnimation("animation.m3a3.main_cannon", AnimationPlayType.PLAY_ONCE_STOP, 0);
        }
        if (seatIndex < 0) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        if (selectedWeapon != 1) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        if (this.isMoving()) {
            if (living instanceof Player) {
                player = (Player)living;
                player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.m3a3_stop_to_shoot"), true);
            }
            return;
        }
        if ((Integer)this.f_19804_.m_135370_(MISSILE_STATE) != 2) {
            if (living instanceof Player) {
                player = (Player)living;
                player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.m3a3_wait_deploy"), true);
            }
            return;
        }
        super.vehicleShoot(living, uuid, targetPos);
    }

    public int getMissileState() {
        return (Integer)this.f_19804_.m_135370_(MISSILE_STATE);
    }

    public int getDeployTimer() {
        return (Integer)this.f_19804_.m_135370_(DEPLOY_TIMER);
    }

    public boolean shouldShowMissileOn(VehicleEntity vehicle, int missileWeaponIndex) {
        for (Entity passenger : vehicle.m_20197_()) {
            GunData gunData;
            int currentWeaponIndex;
            int seatIndex = vehicle.getSeatIndex(passenger);
            if (seatIndex < 0 || (currentWeaponIndex = vehicle.getSelectedWeapon(seatIndex)) != missileWeaponIndex || (gunData = vehicle.getGunData(seatIndex)) == null || gunData.ammo.get() <= 0 && gunData.backupAmmoCount.get() <= 0) continue;
            return true;
        }
        return false;
    }
}

