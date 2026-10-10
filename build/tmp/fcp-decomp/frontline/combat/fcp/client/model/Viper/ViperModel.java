/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Viper;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.entity.vehicle.Viper.ViperEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ViperModel
extends VehicleModel<ViperEntity> {
    public ResourceLocation getModelResource(ViperEntity animatable) {
        return new ResourceLocation("fcp", "geo/viper.geo.json");
    }

    @Nullable
    public VehicleModel.TransformContext<ViperEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "propeller" -> (bone, vehicle, state) -> bone.setRotY(-Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            case "tailPropeller" -> (bone, vehicle, state) -> bone.setRotX(6.0f * Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            case "BarrelRotationController" -> (bone, vehicle, state) -> bone.setRotZ(-Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getBarrelRot0(), (float)vehicle.getBarrelRot()));
            case "LockOn1" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-LockOn", 3));
            case "LockOn2" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-LockOn", 2));
            case "LockOn3" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-LockOn", 1));
            case "LockOn4" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-LockOn", 0));
            case "WireGuided1" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-WireGuided", 3));
            case "WireGuided2" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-WireGuided", 2));
            case "WireGuided3" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-WireGuided", 1));
            case "WireGuided4" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Hellfire-WireGuided", 0));
            case "Sidewinder1" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Sidewinder", 1));
            case "Sidewinder2" -> (bone, vehicle, state) -> bone.setHidden(vehicle.GetWeaponState("Sidewinder", 0));
            default -> super.collectTransform(boneName);
        };
    }
}

