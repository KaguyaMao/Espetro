/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

public class FMTVModel
extends VvpVehicleModel<FMTVEntity> {
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<FMTVEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "steering_wheel" -> (bone, vehicle, state) -> {
                float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
                bone.setRotZ((float)Math.toRadians(steeringAngle * 10.0f));
            };
            case "wheelL0Turn", "wheelR0Turn" -> (bone, vehicle, state) -> {
                float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
                bone.setRotX((float)Math.toRadians(-wheelRot));
                float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
                steeringAngle = Mth.m_14036_((float)steeringAngle, (float)-30.0f, (float)30.0f);
                bone.setRotY((float)Math.toRadians(steeringAngle));
            };
            case "wheelL0", "wheelR0", "wheelL1", "wheelR1" -> (bone, vehicle, state) -> {
                float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
                bone.setRotX((float)Math.toRadians(-wheelRot));
            };
            default -> super.collectTransform(boneName);
        };
    }
}

