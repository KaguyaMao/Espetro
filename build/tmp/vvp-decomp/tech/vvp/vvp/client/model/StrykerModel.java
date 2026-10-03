/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.client.model.util.CannonRecoilTransforms;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;

public class StrykerModel
extends VvpVehicleModel<StrykerEntity> {
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<StrykerEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.match(boneName, "Otkat");
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }
}

