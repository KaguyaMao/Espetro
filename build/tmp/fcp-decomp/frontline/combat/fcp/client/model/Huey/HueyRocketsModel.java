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
package frontline.combat.fcp.client.model.Huey;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.entity.vehicle.Huey.HueyRocketsEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class HueyRocketsModel
extends VehicleModel<HueyRocketsEntity> {
    public ResourceLocation getModelResource(HueyRocketsEntity animatable) {
        return new ResourceLocation("fcp", "geo/huey_rockets.geo.json");
    }

    @Nullable
    public VehicleModel.TransformContext<HueyRocketsEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "propeller" -> (bone, vehicle, state) -> bone.setRotY(-Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            case "tailPropeller" -> (bone, vehicle, state) -> bone.setRotX(6.0f * Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPropellerRotO(), (float)vehicle.getPropellerRot()));
            default -> super.collectTransform(boneName);
        };
    }
}

