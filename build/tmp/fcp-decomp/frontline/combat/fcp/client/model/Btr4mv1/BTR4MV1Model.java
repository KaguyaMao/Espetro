/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Btr4mv1;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Btr4mv1.BTR4MV1Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class BTR4MV1Model
extends FCPVehicleModel<BTR4MV1Entity> {
    private static final double WHEEL_RADIUS = 0.5388;

    public ResourceLocation getModelResource(BTR4MV1Entity a) {
        return new ResourceLocation("fcp", "geo/btr4mv1.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<BTR4MV1Entity> collectTransform(String boneName) {
        if ("basnia".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float yRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretYRotO(), (float)vehicle.getTurretYRot());
                bone.setRotY(yRot * ((float)Math.PI / 180));
            };
        }
        if ("GUN".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float xRot = Mth.m_14179_((float)state.getPartialTick(), (float)vehicle.getTurretXRotO(), (float)vehicle.getTurretXRot());
                bone.setRotX(Mth.m_14036_((float)(-xRot), (float)vehicle.getTurretMinPitch(), (float)vehicle.getTurretMaxPitch()) * ((float)Math.PI / 180));
            };
        }
        if ("wheelL".equals(boneName) || "wheelR".equals(boneName)) {
            return (bone, vehicle, state) -> {};
        }
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.5388, "wheelL1", "wheelL2", "wheelL3", "wheelL4", "wheelR1", "wheelR2", "wheelR3", "wheelR4");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }
}

