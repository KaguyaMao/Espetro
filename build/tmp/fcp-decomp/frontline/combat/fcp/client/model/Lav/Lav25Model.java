/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Lav;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.CannonRecoilTransforms;
import frontline.combat.fcp.client.model.Util.ModelBoneTransforms;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Lav.Lav25Entity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Lav25Model
extends FCPVehicleModel<Lav25Entity> {
    private static final String CANNON_WEAPON = "Cannon";

    public ResourceLocation getModelResource(Lav25Entity animatable) {
        return new ResourceLocation("fcp", "geo/lav25.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<Lav25Entity> collectTransform(String boneName) {
        if ("BarrelOccilator".equals(boneName)) {
            return this.barrelRecoil(0);
        }
        VehicleModel.TransformContext turn = WheelRotationTransforms.matchAnyTurn(boneName, 0.51, 30.0f, "WheelL0Turn", "WheelR0Turn", "WheelL1Turn", "WheelR1Turn");
        if (turn != null) {
            return turn;
        }
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.51, "WheelL0", "WheelR0", "WheelL1", "WheelR1");
        if (wheels != null) {
            return wheels;
        }
        return super.collectTransform(boneName);
    }

    private VehicleModel.TransformContext<Lav25Entity> barrelRecoil(int barrelIndex) {
        return (bone, vehicle, state) -> {
            ModelBoneTransforms.clearRecoilOffsets(bone);
            if (vehicle.getCannonRecoilTime() <= 0) {
                return;
            }
            if (!CANNON_WEAPON.equals(vehicle.getGunName(1))) {
                return;
            }
            CannonRecoilTransforms.apply(bone, vehicle, CannonRecoilTransforms.Profile.STANDARD);
        };
    }
}

