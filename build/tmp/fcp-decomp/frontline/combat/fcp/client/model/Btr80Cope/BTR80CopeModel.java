/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Btr80Cope;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Btr80Cope.BTR80CopeEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BTR80CopeModel
extends FCPVehicleModel<BTR80CopeEntity> {
    public ResourceLocation getModelResource(BTR80CopeEntity animatable) {
        return new ResourceLocation("fcp", "geo/btr80_cope.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<BTR80CopeEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext turn = WheelRotationTransforms.matchAnyTurn(boneName, 0.6, 30.0f, "WheelTurnL1", "WheelTurnL2", "WheelTurnR1", "WheelTurnR2");
        if (turn != null) {
            return turn;
        }
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.6, "WheelL3", "WheelL4", "WheelR3", "WheelR2");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }
}

