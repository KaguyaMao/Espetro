/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.redabysslucia.dragonrise_reforge.entities.utils.DragonriseVehicleBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Ztd05Entity
extends DragonriseVehicleBase {
    public Ztd05Entity(EntityType<Ztd05Entity> type, Level world) {
        super(type, world);
    }

    @Override
    protected boolean isSplashDefaultOpen() {
        return true;
    }

    public void travel() {
        super.travel();
        if (!this.m_9236_().f_46443_ && this.isInFluidType()) {
            float power = ((Float)this.f_19804_.m_135370_(VehicleEntity.POWER)).floatValue();
            Vec3 viewVec = this.m_20252_(1.0f).m_82541_();
            Vec3 delta = this.m_20184_();
            delta = delta.m_82542_(1.06, 1.0, 1.06);
            this.m_20256_(delta.m_82549_(viewVec.m_82490_((double)power * 0.35)));
        }
    }
}

