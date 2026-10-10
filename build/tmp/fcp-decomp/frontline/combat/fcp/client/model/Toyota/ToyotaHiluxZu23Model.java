/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Toyota;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.Util.WheelRotationTransforms;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxZu23Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ToyotaHiluxZu23Model
extends VehicleModel<ToyotaHiluxZu23Entity> {
    private static final float HULL_LURCH = 12.0f;
    private static final float HULL_PITCH = 4.0f;

    public ResourceLocation getModelResource(ToyotaHiluxZu23Entity animatable) {
        return new ResourceLocation("fcp", "geo/toyota_hilux_zu23.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<ToyotaHiluxZu23Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext turn = WheelRotationTransforms.matchAnyTurn(boneName, 0.6, 30.0f, "WheelL0Turn", "WheelR0Turn", "WheelL1Turn", "WheelR1Turn");
        if (turn != null) {
            return turn;
        }
        VehicleModel.TransformContext wheels = WheelRotationTransforms.matchAny(boneName, 0.6, "WheelL0", "WheelR0", "WheelL1", "WheelR1");
        if (wheels != null) {
            return wheels;
        }
        if ("root".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float shake = Mth.m_14179_((float)state.getPartialTick(), (float)((float)vehicle.getRecoilShakeO()), (float)((float)vehicle.getRecoilShake()));
                float a = vehicle.getYawWhileShoot();
                float r = (Mth.m_14154_((float)a) - 90.0f) / 90.0f;
                float r2 = Mth.m_14154_((float)a) <= 90.0f ? a / 90.0f : (a < 0.0f ? -(180.0f + a) / 90.0f : (180.0f - a) / 90.0f);
                float lurch = shake * 12.0f;
                float pitch = shake * 4.0f;
                bone.setPosX(r2 * lurch * 0.5f);
                bone.setPosZ(r * lurch);
                bone.setRotX(r * pitch * ((float)Math.PI / 180));
                bone.setRotZ(r2 * pitch * ((float)Math.PI / 180));
            };
        }
        return super.collectTransform(boneName);
    }
}

