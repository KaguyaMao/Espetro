/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package com.redabysslucia.dragonrise_reforge.utils;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PitchAdjustUtil {
    public static void adjustedPassengerPitchOnTurret(Entity entity, float turretMinPitch, float turretMaxPitch, VehicleEntity vehicle, Float[][] pitchAdjustments) {
        float yaw = vehicle.getTurretYaw(1.0f);
        float r = (Mth.m_14154_((float)yaw) - 90.0f) / 90.0f;
        float r2 = Mth.m_14154_((float)yaw) <= 90.0f ? yaw / 90.0f : (yaw < 0.0f ? -(180.0f + yaw) / 90.0f : (180.0f - yaw) / 90.0f);
        float min = -turretMaxPitch - r * vehicle.m_146909_() - r2 * vehicle.getRoll();
        float max = -turretMinPitch - r * vehicle.m_146909_() - r2 * vehicle.getRoll();
        for (Float[] adjust : pitchAdjustments) {
            float factor;
            float yawStart = adjust[0].floatValue();
            float yawEnd = adjust[1].floatValue();
            float move = adjust[2].floatValue();
            float minAdjust = adjust[3].floatValue();
            float maxAdjust = adjust[4].floatValue();
            if (yaw >= 0.0f) {
                if (yaw >= yawStart && yaw <= yawEnd) {
                    min -= minAdjust;
                    max += maxAdjust;
                    continue;
                }
                if (!(yaw >= yawStart - move) || !(yaw < yawStart)) continue;
                factor = Mth.m_14179_((float)((yawStart - yaw) / move), (float)1.0f, (float)0.0f);
                min -= minAdjust * factor;
                max += maxAdjust * factor;
                continue;
            }
            if (yaw <= yawStart && yaw >= yawEnd) {
                min -= minAdjust;
                max += maxAdjust;
                continue;
            }
            if (!(yaw <= yawStart + move) || !(yaw > yawStart)) continue;
            factor = Mth.m_14179_((float)((yawStart - yaw) / -move), (float)1.0f, (float)0.0f);
            min -= minAdjust * factor;
            max += maxAdjust * factor;
        }
        float f = Mth.m_14177_((float)entity.m_146909_());
        float f1 = Mth.m_14036_((float)f, (float)min, (float)max);
        entity.f_19860_ += f1 - f;
        entity.m_146926_(entity.m_146909_() + f1 - f);
    }
}

