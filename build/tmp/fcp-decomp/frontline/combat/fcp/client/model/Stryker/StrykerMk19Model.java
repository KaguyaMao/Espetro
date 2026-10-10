/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Stryker;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMk19Entity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class StrykerMk19Model
extends FCPVehicleModel<StrykerMk19Entity> {
    public ResourceLocation getModelResource(StrykerMk19Entity a) {
        return new ResourceLocation("fcp", "geo/stryker_mk19.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<StrykerMk19Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.6, "wheRR", "wheRR2", "wheRR3", "wheRR4", "wheRR5", "wheRR6", "wheRR7", "wheRR8");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }
}

