/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 */
package com.redabysslucia.dragonrise_reforge.utils;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.redabysslucia.dragonrise_reforge.utils.AirshipInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class AirshipEngineUtils {
    public static void airshipEngine(VehicleEntity vehicle, AirshipInfo engineInfo) {
        double buoyancy = engineInfo.getBuoyancy();
        float liftSpeedRate = engineInfo.liftSpeedRate;
        float sinkSpeedRate = engineInfo.sinkSpeedRate;
        float steeringSpeed = engineInfo.steeringSpeed;
        float maxForwardSpeed = engineInfo.maxForwardSpeedRate;
        float maxBackwardSpeed = engineInfo.maxBackwardSpeedRate;
        float powerAdd = engineInfo.getIncrement();
        float powerReduce = engineInfo.getDecrement();
        float dragHorizontal = engineInfo.dragHorizontal;
        float dragVertical = engineInfo.dragVertical;
        int energyCost = (int)(engineInfo.getEnergyCostRate() * (double)Mth.m_14154_((float)((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue()));
        if (vehicle.getEnergy() < energyCost || vehicle.getMaxEnergy() > 0 && vehicle.getEnergy() <= 0) {
            vehicle.setForwardInputDown(false);
            vehicle.setBackInputDown(false);
            vehicle.setLeftInputDown(false);
            vehicle.setRightInputDown(false);
            vehicle.setUpInputDown(false);
            vehicle.setDownInputDown(false);
            vehicle.m_20088_().m_135381_(VehicleEntity.POWER, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() * 0.95f));
            vehicle.m_20088_().m_135381_(VehicleEntity.DELTA_ROT, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() * 0.5f));
        }
        if (vehicle.m_146895_() == null) {
            vehicle.setLeftInputDown(false);
            vehicle.setRightInputDown(false);
            vehicle.setForwardInputDown(false);
            vehicle.setBackInputDown(false);
            vehicle.setUpInputDown(false);
            vehicle.setDownInputDown(false);
            vehicle.m_20088_().m_135381_(VehicleEntity.POWER, (Object)Float.valueOf(0.0f));
        }
        if (vehicle.forwardInputDown()) {
            vehicle.m_20088_().m_135381_(VehicleEntity.POWER, (Object)Float.valueOf(Math.min(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() + (((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() < 0.0f ? powerAdd * 2.0f : powerAdd), 1.0f)));
        }
        if (vehicle.backInputDown()) {
            vehicle.m_20088_().m_135381_(VehicleEntity.POWER, (Object)Float.valueOf(org.joml.Math.max((float)(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() - (((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() > 0.0f ? powerReduce * 2.0f : powerReduce)), (float)-1.0f)));
            if (vehicle.rightInputDown()) {
                vehicle.m_20088_().m_135381_(VehicleEntity.DELTA_ROT, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() + steeringSpeed));
            } else if (vehicle.leftInputDown()) {
                vehicle.m_20088_().m_135381_(VehicleEntity.DELTA_ROT, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() - steeringSpeed));
            }
        } else if (vehicle.rightInputDown()) {
            vehicle.m_20088_().m_135381_(VehicleEntity.DELTA_ROT, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() - steeringSpeed));
        } else if (vehicle.leftInputDown()) {
            vehicle.m_20088_().m_135381_(VehicleEntity.DELTA_ROT, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() + steeringSpeed));
        }
        if (!vehicle.forwardInputDown() && !vehicle.backInputDown()) {
            vehicle.m_20088_().m_135381_(VehicleEntity.POWER, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() * 0.96f));
        }
        Vec3 delta = vehicle.m_20184_();
        if (vehicle.upInputDown()) {
            delta = delta.m_82520_(0.0, (double)liftSpeedRate, 0.0);
        }
        if (vehicle.downInputDown()) {
            delta = delta.m_82520_(0.0, (double)(-sinkSpeedRate), 0.0);
        }
        delta = delta.m_82520_(0.0, buoyancy, 0.0);
        Vec3 viewVec = vehicle.m_20252_(1.0f);
        delta = delta.m_82549_(viewVec.m_82490_((double)((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue()));
        delta = delta.m_82542_((double)(1.0f - dragHorizontal), (double)(1.0f - dragVertical), (double)(1.0f - dragHorizontal));
        double horizSpeed = delta.m_165924_();
        float currentPower = ((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue();
        if (currentPower > 0.0f && horizSpeed > (double)maxForwardSpeed) {
            delta = new Vec3(delta.f_82479_ / horizSpeed * (double)maxForwardSpeed, delta.f_82480_, delta.f_82481_ / horizSpeed * (double)maxForwardSpeed);
        } else if (currentPower < 0.0f && horizSpeed > (double)maxBackwardSpeed) {
            delta = new Vec3(delta.f_82479_ / horizSpeed * (double)maxBackwardSpeed, delta.f_82480_, delta.f_82481_ / horizSpeed * (double)maxBackwardSpeed);
        }
        vehicle.m_20256_(delta);
        float yawDelta = (float)((double)((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() * 6.0);
        vehicle.m_146922_(vehicle.m_146908_() - yawDelta);
        vehicle.m_20088_().m_135381_(VehicleEntity.DELTA_ROT, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.DELTA_ROT)).floatValue() * 0.85f));
        if (vehicle.m_9236_() instanceof ServerLevel) {
            vehicle.consumeEnergy(energyCost);
        }
        if (((Boolean)vehicle.m_20088_().m_135370_(VehicleEntity.MAIN_ENGINE_DAMAGED)).booleanValue()) {
            vehicle.m_20088_().m_135381_(VehicleEntity.POWER, (Object)Float.valueOf(((Float)vehicle.m_20088_().m_135370_(VehicleEntity.POWER)).floatValue() * 0.96f));
        }
    }
}

