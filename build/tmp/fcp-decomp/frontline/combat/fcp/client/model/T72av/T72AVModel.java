/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 */
package frontline.combat.fcp.client.model.T72av;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.CannonRecoilTransforms;
import frontline.combat.fcp.client.model.Util.ModelBoneTransforms;
import frontline.combat.fcp.entity.vehicle.T72av.T72AVEntity;
import net.minecraft.resources.ResourceLocation;

public class T72AVModel
extends FCPVehicleModel<T72AVEntity> {
    private static final String CANNON_WEAPON = "Cannon";

    public ResourceLocation getModelResource(T72AVEntity animatable) {
        return new ResourceLocation("fcp", "geo/t72av.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    private VehicleModel.TransformContext<T72AVEntity> barrelRecoil(int barrelIndex) {
        return (bone, vehicle, state) -> {
            ModelBoneTransforms.clearRecoilOffsets(bone);
            if (vehicle.getCannonRecoilTime() <= 0) {
                return;
            }
            if (!CANNON_WEAPON.equals(vehicle.getGunName(1))) {
                return;
            }
            CannonRecoilTransforms.apply(bone, vehicle, CannonRecoilTransforms.Profile.HEAVY);
        };
    }
}

