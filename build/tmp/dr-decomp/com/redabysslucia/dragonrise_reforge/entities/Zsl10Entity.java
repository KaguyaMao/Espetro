/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.redabysslucia.dragonrise_reforge.entities.utils.SyncCameraVehicle;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Zsl10Entity
extends SyncCameraVehicle {
    private static final EntityDataAccessor<Integer> FLAP_STATE = new EntityDataAccessor(100, EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FLAP_TIMER = new EntityDataAccessor(101, EntityDataSerializers.f_135028_);
    private int waterCheckCooldown = 0;

    public Zsl10Entity(EntityType<Zsl10Entity> type, Level world) {
        super(type, world);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(FLAP_STATE, (Object)0);
        this.f_19804_.m_135372_(FLAP_TIMER, (Object)0);
    }

    @Override
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

