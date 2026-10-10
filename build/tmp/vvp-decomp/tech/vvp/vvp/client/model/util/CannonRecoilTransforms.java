/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 */
package tech.vvp.vvp.client.model.util;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import tech.vvp.vvp.client.model.util.ModelBoneTransforms;

public final class CannonRecoilTransforms {
    private static final float REFERENCE_TICKS = 42.0f;

    private CannonRecoilTransforms() {
    }

    @Nullable
    private static RecoilOffsets computeRecoilOffsets(VehicleEntity vehicle, Profile profile) {
        int recoilTime = vehicle.getCannonRecoilTime();
        if (recoilTime <= 0) {
            return null;
        }
        float force = Mth.m_14036_((float)vehicle.getCannonRecoilForce(), (float)0.0f, (float)2.0f);
        float progress = (float)recoilTime / 42.0f;
        float slide = force * profile.slideMax * progress * progress;
        float kick = force * profile.kickMaxDeg * progress * progress * (float)Math.sin(0.6283185307179586 * ((double)recoilTime - 2.5)) * ((float)Math.PI / 180);
        return new RecoilOffsets(slide, kick);
    }

    public static void apply(CoreGeoBone bone, VehicleEntity vehicle, Profile profile) {
        ModelBoneTransforms.clearRecoilOffsets(bone);
        RecoilOffsets recoil = CannonRecoilTransforms.computeRecoilOffsets(vehicle, profile);
        if (recoil == null) {
            return;
        }
        bone.setPosZ(recoil.slide);
        bone.setRotX(recoil.kick);
    }

    public static void applyBarrelPitchAndRecoil(CoreGeoBone bone, VehicleEntity vehicle, float modelTurretXRot, Profile profile) {
        CannonRecoilTransforms.applyBarrelPitchAndRecoil(bone, vehicle, modelTurretXRot, profile, null);
    }

    public static void applyBarrelPitchAndRecoil(CoreGeoBone bone, VehicleEntity vehicle, float modelTurretXRot, Profile profile, @Nullable String requiredWeapon) {
        float pitch = Mth.m_14036_((float)(-modelTurretXRot), (float)vehicle.getTurretMinPitch(), (float)vehicle.getTurretMaxPitch()) * ((float)Math.PI / 180);
        bone.setRotX(pitch);
        bone.setPosZ(0.0f);
        if (requiredWeapon != null && !requiredWeapon.equals(vehicle.getGunName(0))) {
            return;
        }
        RecoilOffsets recoil = CannonRecoilTransforms.computeRecoilOffsets(vehicle, profile);
        if (recoil == null) {
            return;
        }
        bone.setPosZ(recoil.slide);
        bone.setRotX(pitch + recoil.kick);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchBarrel(String boneName) {
        return CannonRecoilTransforms.matchBarrel(boneName, Profile.STANDARD);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchBarrel(String boneName, Profile profile) {
        return CannonRecoilTransforms.matchBarrelForWeapon(boneName, profile, null);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchBarrelForWeapon(String boneName, Profile profile, @Nullable String requiredWeapon) {
        if (!"barrel".equals(boneName)) {
            return null;
        }
        return (geoBone, vehicle, state) -> {
            float turretXRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretXRotO(), (float)vehicle.getTurretXRot());
            CannonRecoilTransforms.applyBarrelPitchAndRecoil(geoBone, vehicle, turretXRot, profile, requiredWeapon);
        };
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchBarrelForWeapon(String boneName, @Nullable String requiredWeapon) {
        return CannonRecoilTransforms.matchBarrelForWeapon(boneName, Profile.STANDARD, requiredWeapon);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> match(String boneName, String bone) {
        return CannonRecoilTransforms.match(boneName, bone, Profile.STANDARD);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> match(String boneName, String bone, Profile profile) {
        if (!bone.equals(boneName)) {
            return null;
        }
        return (geoBone, vehicle, state) -> CannonRecoilTransforms.apply(geoBone, vehicle, profile);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> weaponRecoil(String boneName, String bone, String weaponName, Profile profile) {
        if (!bone.equals(boneName)) {
            return null;
        }
        return (geoBone, vehicle, state) -> {
            ModelBoneTransforms.clearRecoilOffsets(geoBone);
            if (vehicle.getCannonRecoilTime() <= 0) {
                return;
            }
            if (!weaponName.equals(vehicle.getGunName(0))) {
                return;
            }
            CannonRecoilTransforms.apply(geoBone, vehicle, profile);
        };
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> weaponRecoil(String boneName, String bone, String weaponName) {
        return CannonRecoilTransforms.weaponRecoil(boneName, bone, weaponName, Profile.STANDARD);
    }

    public static enum Profile {
        STANDARD(12.0f, 3.0f),
        LIGHT(3.0f, 0.8f);

        private final float slideMax;
        private final float kickMaxDeg;

        private Profile(float slideMax, float kickMaxDeg) {
            this.slideMax = slideMax;
            this.kickMaxDeg = kickMaxDeg;
        }
    }

    private record RecoilOffsets(float slide, float kick) {
    }
}

