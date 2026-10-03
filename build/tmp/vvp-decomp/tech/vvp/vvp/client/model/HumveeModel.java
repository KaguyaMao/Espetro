/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.core.animation.AnimationState
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.HumveeEntity;

public class HumveeModel
extends VvpVehicleModel<HumveeEntity> {
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<HumveeEntity> collectTransform(String boneName) {
        if ("Thehatch".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float turretYRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretYRotO(), (float)vehicle.getTurretYRot());
                bone.setRotY(turretYRot * ((float)Math.PI / 180));
            };
        }
        if ("Thehatch2".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float progress = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevHatchProgress(), (float)vehicle.getHatchProgress());
                bone.setRotX((float)Math.toRadians(progress * 110.0f));
            };
        }
        if ("cannon".equals(boneName)) {
            return (bone, vehicle, state) -> {};
        }
        if ("barrel".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float turretXRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretXRotO(), (float)vehicle.getTurretXRot());
                float pitch = Mth.m_14036_((float)(-turretXRot), (float)vehicle.getTurretMinPitch(), (float)vehicle.getTurretMaxPitch()) * ((float)Math.PI / 180);
                bone.setRotX(pitch);
            };
        }
        if ("steeringwheel".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
                bone.setRotZ((float)Math.toRadians(steeringAngle * 8.0f));
            };
        }
        return switch (boneName) {
            case "wheel1" -> this.frontWheel(false);
            case "wheel2" -> this.frontWheel(false);
            case "wheel3", "wheel4" -> this.driveWheel(false);
            default -> super.collectTransform(boneName);
        };
    }

    private VehicleModel.TransformContext<HumveeEntity> frontWheel(boolean invertSpin) {
        return (bone, vehicle, state) -> {
            HumveeModel.applyWheelSpin(bone, vehicle, (AnimationState<HumveeEntity>)state, invertSpin);
            HumveeModel.applySteering(bone, vehicle, (AnimationState<HumveeEntity>)state);
        };
    }

    private VehicleModel.TransformContext<HumveeEntity> driveWheel(boolean invertSpin) {
        return (bone, vehicle, state) -> HumveeModel.applyWheelSpin(bone, vehicle, (AnimationState<HumveeEntity>)state, invertSpin);
    }

    private static void applyWheelSpin(CoreGeoBone bone, HumveeEntity vehicle, AnimationState<HumveeEntity> state, boolean invert) {
        float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
        float signedRot = invert ? wheelRot : -wheelRot;
        bone.setRotX((float)Math.toRadians(signedRot));
    }

    private static void applySteering(CoreGeoBone bone, HumveeEntity vehicle, AnimationState<HumveeEntity> state) {
        float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
        steeringAngle = Mth.m_14036_((float)steeringAngle, (float)-35.0f, (float)35.0f);
        bone.setRotY((float)Math.toRadians(steeringAngle));
    }
}

