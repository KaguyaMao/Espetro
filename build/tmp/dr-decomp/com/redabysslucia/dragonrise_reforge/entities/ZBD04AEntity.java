/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.redabysslucia.dragonrise_reforge.entities.vehicle.IndirectFireVehicleBase;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ZBD04AEntity
extends IndirectFireVehicleBase {
    private static final EntityDataAccessor<Integer> FLAP_STATE = new EntityDataAccessor(100, EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FLAP_TIMER = new EntityDataAccessor(101, EntityDataSerializers.f_135028_);
    private int lastFlapCheckTick = 0;

    public ZBD04AEntity(EntityType<ZBD04AEntity> type, Level world) {
        super((EntityType<? extends VehicleEntity>)type, world);
    }

    @Override
    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(FLAP_STATE, (Object)0);
        this.f_19804_.m_135372_(FLAP_TIMER, (Object)0);
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
        if (this.f_19797_ - this.lastFlapCheckTick < 10) {
            return;
        }
        this.lastFlapCheckTick = this.f_19797_;
        state = (Integer)this.f_19804_.m_135370_(FLAP_STATE);
        boolean inWater = this.m_20069_();
        if (inWater && state == 0) {
            this.f_19804_.m_135381_(FLAP_STATE, (Object)1);
            this.f_19804_.m_135381_(FLAP_TIMER, (Object)20);
        } else if (!(inWater || state != 1 && state != 2)) {
            this.f_19804_.m_135381_(FLAP_STATE, (Object)3);
            this.f_19804_.m_135381_(FLAP_TIMER, (Object)20);
        }
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.25f) * damage);
    }
}

