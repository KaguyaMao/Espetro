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
package frontline.combat.fcp.client.model.MemeVehicles;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.entity.vehicle.MemeVehicles.LaHumveeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class LaHumveeModel
extends VehicleModel<LaHumveeEntity> {
    public ResourceLocation getModelResource(LaHumveeEntity animatable) {
        return new ResourceLocation("fcp", "geo/humvee_mk19.geo.json");
    }

    @Nullable
    public VehicleModel.TransformContext<LaHumveeEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "turret" -> (bone, vehicle, state) -> bone.setRotY(-Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            case "extrawheel" -> (bone, vehicle, state) -> bone.setRotX(6.0f * Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            default -> super.collectTransform(boneName);
        };
    }
}

