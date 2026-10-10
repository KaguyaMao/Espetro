/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.TurretWreckEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 */
package org.espetro.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.TurretWreckEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;

public final class WreckDecayService {
    public static final double LIFETIME_SECONDS = 5.0;
    public static final float TURRET_WRECK_START_HP = 100.0f;

    private WreckDecayService() {
    }

    public static double drainFractionPerTick() {
        double ticks = 100.0;
        if (!(ticks > 0.0)) {
            return 0.0;
        }
        return 1.0 / ticks;
    }

    public static double drainPerTick(double healthSpan) {
        if (!(healthSpan > 0.0)) {
            return 0.0;
        }
        return healthSpan * WreckDecayService.drainFractionPerTick();
    }

    public static void tickVehicleWreck(VehicleEntity vehicle) {
        if (vehicle == null || vehicle.m_9236_().f_46443_ || vehicle.m_213877_()) {
            return;
        }
        if (!vehicle.isWreck()) {
            return;
        }
        float max = vehicle.getMaxHealth();
        if (!(max > 0.0f)) {
            return;
        }
        float drain = (float)WreckDecayService.drainPerTick(max);
        if (!(drain > 0.0f)) {
            return;
        }
        vehicle.setHealth(vehicle.getHealth() - drain);
    }

    public static void tickTurretWreck(TurretWreckEntity wreck) {
        if (wreck == null || wreck.m_9236_().f_46443_ || wreck.m_213877_()) {
            return;
        }
        float drain = (float)WreckDecayService.drainPerTick(100.0);
        if (!(drain > 0.0f)) {
            return;
        }
        wreck.setHealth(wreck.getHealth() - drain);
    }
}

