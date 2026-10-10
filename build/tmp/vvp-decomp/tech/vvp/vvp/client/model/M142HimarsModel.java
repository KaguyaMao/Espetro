/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.core.animation.AnimationState
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.data.gun.GunData;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.client.model.util.ModelBoneTransforms;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

public class M142HimarsModel
extends VvpVehicleModel<HimarsEntity> {
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<HimarsEntity> collectTransform(String boneName) {
        if ("barrel".equals(boneName)) {
            return this::applyBarrelPitch;
        }
        if ("turret".equals(boneName)) {
            return this::applyTurretYaw;
        }
        return switch (boneName) {
            case "rocket1" -> this.rocketBone(6);
            case "rocket2" -> this.rocketBone(5);
            case "rocket3" -> this.rocketBone(4);
            case "rocket4" -> this.rocketBone(3);
            case "rocket5" -> this.rocketBone(2);
            case "rocket6" -> this.rocketBone(1);
            case "wheel1" -> this.frontWheel(false, false);
            case "wheel2" -> this.frontWheel(false, false);
            case "wheel3", "wheel4", "wheel5", "wheel6" -> this.driveWheel(false);
            default -> super.collectTransform(boneName);
        };
    }

    private VehicleModel.TransformContext<HimarsEntity> rocketBone(int minAmmo) {
        return (bone, vehicle, state) -> bone.setHidden(this.shouldHideRocket((HimarsEntity)vehicle, minAmmo));
    }

    private VehicleModel.TransformContext<HimarsEntity> frontWheel(boolean mirrorSteering, boolean invertSpin) {
        return (bone, vehicle, state) -> {
            M142HimarsModel.applyWheelSpin(bone, vehicle, (AnimationState<HimarsEntity>)state, invertSpin);
            M142HimarsModel.applySteering(bone, vehicle, (AnimationState<HimarsEntity>)state, mirrorSteering);
        };
    }

    private VehicleModel.TransformContext<HimarsEntity> driveWheel(boolean invertSpin) {
        return (bone, vehicle, state) -> M142HimarsModel.applyWheelSpin(bone, vehicle, (AnimationState<HimarsEntity>)state, invertSpin);
    }

    private static void applyWheelSpin(CoreGeoBone bone, HimarsEntity vehicle, AnimationState<HimarsEntity> state, boolean invert) {
        float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
        float signedRot = invert ? wheelRot : -wheelRot;
        bone.setRotX((float)Math.toRadians(signedRot));
    }

    private static void applySteering(CoreGeoBone bone, HimarsEntity vehicle, AnimationState<HimarsEntity> state, boolean mirror) {
        float steeringAngle = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevSteeringAngle(), (float)vehicle.getSteeringAngle());
        steeringAngle = Mth.m_14036_((float)steeringAngle, (float)-35.0f, (float)35.0f);
        if (mirror) {
            steeringAngle = -steeringAngle;
        }
        bone.setRotY((float)Math.toRadians(steeringAngle));
    }

    private void applyBarrelPitch(CoreGeoBone bone, HimarsEntity vehicle, AnimationState<HimarsEntity> state) {
        ModelBoneTransforms.clearRecoilOffsets(bone);
        float turretXRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretXRotO(), (float)vehicle.getTurretXRot());
        float pitch = Mth.m_14036_((float)(-turretXRot), (float)vehicle.getTurretMinPitch(), (float)vehicle.getTurretMaxPitch()) * ((float)Math.PI / 180);
        bone.setRotX(pitch);
    }

    private void applyTurretYaw(CoreGeoBone bone, HimarsEntity vehicle, AnimationState<HimarsEntity> state) {
        float turretYRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretYRotO(), (float)vehicle.getTurretYRot());
        bone.setRotY(turretYRot * ((float)Math.PI / 180));
    }

    private boolean shouldHideRocket(HimarsEntity vehicle, int minAmmo) {
        GunData gunData = vehicle.getGunData("GMLRS");
        if (gunData == null) {
            return false;
        }
        return gunData.ammo.get() < minAmmo;
    }
}

