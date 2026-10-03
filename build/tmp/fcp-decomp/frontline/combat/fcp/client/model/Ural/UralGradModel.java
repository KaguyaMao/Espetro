/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Ural;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Ural.UralGradEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class UralGradModel
extends VehicleModel<UralGradEntity> {
    public ResourceLocation getModelResource(UralGradEntity animatable) {
        return new ResourceLocation("fcp", "geo/ural_grad.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<UralGradEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext turn = WheelRotationTransforms.matchAnyTurn(boneName, 0.6, 30.0f, "WheelL0Turn", "WheelR0Turn", "WheelL1Turn", "WheelR1Turn");
        if (turn != null) {
            return turn;
        }
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.6, "WheelL0", "WheelR0", "WheelL1", "WheelR1");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }
}

