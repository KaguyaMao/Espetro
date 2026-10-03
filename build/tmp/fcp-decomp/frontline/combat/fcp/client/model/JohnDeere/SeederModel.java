/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.JohnDeere;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.JohnDeere.SeederEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SeederModel
extends FCPVehicleModel<SeederEntity> {
    public ResourceLocation getModelResource(SeederEntity animatable) {
        return new ResourceLocation("fcp", "geo/seeder.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<SeederEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext turn = WheelRotationTransforms.matchAnyTurn(boneName, 0.6, 30.0f, "WheelL0Turn", "WheelR0Turn", "WheelL1Turn", "WheelR1Turn");
        if (turn != null) {
            return turn;
        }
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.6, "WheelL0", "WheelR0", "WheelL1", "WheelR1", "WheelL2", "WheelR2", "RollerL0", "RollerR0", "RollerL1", "RollerR1", "RollerL2", "RollerR2", "RollerL3", "RollerR3", "RollerL4", "RollerR4", "RollerL5", "RollerR5", "RollerL6", "RollerR6");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }
}

