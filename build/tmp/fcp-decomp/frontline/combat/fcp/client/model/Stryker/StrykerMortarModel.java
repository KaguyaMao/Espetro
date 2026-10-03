/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Stryker;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMortarEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class StrykerMortarModel
extends VehicleModel<StrykerMortarEntity> {
    public ResourceLocation getModelResource(StrykerMortarEntity a) {
        return new ResourceLocation("fcp", "geo/stryker_mortar.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<StrykerMortarEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.6, "wheRR", "wheRR2", "wheRR3", "wheRR4", "wheRR5", "wheRR6", "wheRR7", "wheRR8");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }
}

