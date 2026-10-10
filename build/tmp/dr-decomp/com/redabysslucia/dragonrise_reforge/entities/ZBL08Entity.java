/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.redabysslucia.dragonrise_reforge.entities.utils.DragonriseVehicleBase;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ZBL08Entity
extends DragonriseVehicleBase {
    private static final EntityDataAccessor<Integer> FLAP_STATE = new EntityDataAccessor(100, EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FLAP_TIMER = new EntityDataAccessor(101, EntityDataSerializers.f_135028_);
    private int lastShootWarningTick = 0;
    private int waterCheckCooldown = 0;

    public ZBL08Entity(EntityType<ZBL08Entity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(FLAP_STATE, (Object)0);
        this.f_19804_.m_135372_(FLAP_TIMER, (Object)0);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.25f) * damage);
    }

    private boolean isMoving() {
        Vec3 motion = this.m_20184_();
        return Math.abs(motion.f_82479_) > 0.01 || Math.abs(motion.f_82481_) > 0.01;
    }

    public boolean canShoot(LivingEntity living) {
        int seatIndex = this.getSeatIndex((Entity)living);
        if (seatIndex < 0) {
            return super.canShoot(living);
        }
        int selectedWeapon = this.getSelectedWeapon(seatIndex);
        if (selectedWeapon != 2) {
            return super.canShoot(living);
        }
        if (this.isMoving()) {
            if (this.m_9236_().f_46443_ && living instanceof Player) {
                Player player = (Player)living;
                if (this.f_19797_ - this.lastShootWarningTick > 20) {
                    this.lastShootWarningTick = this.f_19797_;
                    player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.zbl08_stop_to_shoot"), true);
                }
            }
            return false;
        }
        return super.canShoot(living);
    }

    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        int seatIndex = this.getSeatIndex((Entity)living);
        if (seatIndex < 0) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        int selectedWeapon = this.getSelectedWeapon(seatIndex);
        if (selectedWeapon != 2) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        if (this.isMoving()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                if (this.f_19797_ - this.lastShootWarningTick > 20) {
                    player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.zbl08_stop_to_shoot"), true);
                    this.lastShootWarningTick = this.f_19797_;
                }
            }
            return;
        }
        super.vehicleShoot(living, uuid, targetPos);
    }

    public void travel() {
        super.travel();
        if (!this.m_9236_().f_46443_ && this.isInFluidType() && !this.m_20096_()) {
            float power = ((Float)this.f_19804_.m_135370_(VehicleEntity.POWER)).floatValue();
            Vec3 viewVec = this.m_20252_(1.0f).m_82541_();
            this.m_20256_(this.m_20184_().m_82549_(viewVec.m_82490_((double)power * 0.004)));
        }
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().f_46443_) {
            return;
        }
        int state = (Integer)this.f_19804_.m_135370_(FLAP_STATE);
        if (state == 1 || state == 3) {
            int timer = (Integer)this.f_19804_.m_135370_(FLAP_TIMER) - 1;
            this.f_19804_.m_135381_(FLAP_TIMER, (Object)timer);
            if (timer <= 0) {
                this.f_19804_.m_135381_(FLAP_STATE, (Object)(state == 1 ? 2 : 0));
            }
        }
        if (--this.waterCheckCooldown > 0) {
            return;
        }
        this.waterCheckCooldown = 10;
        state = (Integer)this.f_19804_.m_135370_(FLAP_STATE);
        boolean inWater = this.m_20069_();
        if (inWater && state == 0) {
            this.f_19804_.m_135381_(FLAP_STATE, (Object)1);
            this.f_19804_.m_135381_(FLAP_TIMER, (Object)23);
        } else if (!(inWater || state != 1 && state != 2)) {
            this.f_19804_.m_135381_(FLAP_STATE, (Object)3);
            this.f_19804_.m_135381_(FLAP_TIMER, (Object)25);
        }
    }
}

