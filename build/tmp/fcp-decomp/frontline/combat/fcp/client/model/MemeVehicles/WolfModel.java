/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.MemeVehicles;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.entity.vehicle.MemeVehicles.WolfEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class WolfModel
extends FCPVehicleModel<WolfEntity> {
    public ResourceLocation getModelResource(WolfEntity animatable) {
        return new ResourceLocation("fcp", "geo/wolf.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<WolfEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "leg1", "leg2" -> (bone, vehicle, state) -> {
                float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getPrevWheelRotation(), (float)vehicle.getWheelRotation());
                bone.setRotX((float)Math.toRadians(-wheelRot));
            };
            case "leg0", "leg3" -> (bone, vehicle, state) -> {
                float wheelRot = Mth.m_14179_((float)state.getPartialTick(), (float)(vehicle.getPrevWheelRotation() + 180.0f), (float)(vehicle.getWheelRotation() + 180.0f));
                bone.setRotX((float)Math.toRadians(-wheelRot));
            };
            default -> super.collectTransform(boneName);
        };
    }
}

