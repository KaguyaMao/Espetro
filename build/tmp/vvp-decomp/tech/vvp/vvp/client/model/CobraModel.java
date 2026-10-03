/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.CobraEntity;

public class CobraModel
extends VvpVehicleModel<CobraEntity> {
    @Nullable
    public VehicleModel.TransformContext<CobraEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "propeller" -> (bone, vehicle, state) -> bone.setRotY(-Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            case "tailPropeller" -> (bone, vehicle, state) -> bone.setRotX(6.0f * Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            case "missile1" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 8));
            case "missile2" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 7));
            case "missile3" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 6));
            case "missile4" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 5));
            case "missile5" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 4));
            case "missile6" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 3));
            case "missile7" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 2));
            case "missile8" -> (bone, vehicle, state) -> bone.setHidden(this.shouldHideMissile(vehicle, 1));
            default -> super.collectTransform(boneName);
        };
    }

    public boolean shouldHideMissile(VehicleEntity vehicle, int ammo) {
        GunData gunData = vehicle.getGunData("PassengerMissile");
        if (gunData == null) {
            return false;
        }
        return gunData.ammo.get() < ammo;
    }
}

