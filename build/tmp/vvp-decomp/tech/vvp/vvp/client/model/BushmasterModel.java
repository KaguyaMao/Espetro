/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.BushmasterEntity;

public class BushmasterModel
extends VvpVehicleModel<BushmasterEntity> {
    public ResourceLocation getModelResource(BushmasterEntity animatable) {
        return new ResourceLocation("vvp", "geo/bushmaster.geo.json");
    }

    public ResourceLocation getTextureResource(BushmasterEntity animatable) {
        return animatable.getCamoTextures()[animatable.getCamoType()];
    }

    public ResourceLocation getAnimationResource(BushmasterEntity animatable) {
        return new ResourceLocation("vvp", "animations/bushmaster.animation.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<BushmasterEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "steering_wheel" -> (bone, vehicle, state) -> {
                float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
                bone.setRotZ((float)Math.toRadians(steeringAngle * 10.0f));
            };
            case "wheel1", "wheel2" -> (bone, vehicle, state) -> {
                float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
                bone.setRotX((float)Math.toRadians(-wheelRot));
                float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
                steeringAngle = Mth.m_14036_((float)steeringAngle, (float)-40.0f, (float)40.0f);
                bone.setRotY((float)Math.toRadians(steeringAngle));
            };
            case "wheel3", "wheel4" -> (bone, vehicle, state) -> {
                float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
                bone.setRotX((float)Math.toRadians(-wheelRot));
            };
            default -> super.collectTransform(boneName);
        };
    }
}

