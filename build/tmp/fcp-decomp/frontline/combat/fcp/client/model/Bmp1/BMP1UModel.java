/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package frontline.combat.fcp.client.model.Bmp1;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.client.model.FCPVehicleModel;
import frontline.combat.fcp.client.model.Util.CannonRecoilTransforms;
import frontline.combat.fcp.client.model.Util.ModelBoneTransforms;
import frontline.combat.fcp.entity.vehicle.Bmp1.BMP1UEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class BMP1UModel
extends FCPVehicleModel<BMP1UEntity> {
    private static final String CANNON_WEAPON = "Cannon";
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{176.22f, 17.36f, 41.51f}, {128.19f, 15.77f, 44.96f}, {78.85f, 12.13f, 45.99f}, {33.07f, 8.97f, 43.9f}, {32.61f, 6.87f, 40.61f}, {32.61f, 4.76f, 37.32f}, {32.61f, 2.65f, 34.03f}, {2.85f, 1.16f, 30.48f}, {0.0f, 1.16f, 26.58f}, {0.0f, 1.16f, 22.67f}, {0.0f, 1.16f, 18.77f}, {0.0f, 1.16f, 14.86f}, {0.0f, 1.16f, 10.95f}, {0.0f, 1.16f, 7.05f}, {0.0f, 1.16f, 3.14f}, {0.0f, 1.16f, -0.77f}, {0.0f, 1.16f, -4.67f}, {0.0f, 1.16f, -8.58f}, {0.0f, 1.16f, -12.49f}, {0.0f, 1.16f, -16.4f}, {0.0f, 1.16f, -20.31f}, {0.0f, 1.16f, -24.21f}, {-21.14f, 1.43f, -28.09f}, {-31.59f, 3.37f, -31.47f}, {-31.59f, 5.42f, -34.79f}, {-31.59f, 7.47f, -38.12f}, {-31.59f, 9.52f, -41.45f}, {-74.57f, 12.41f, -43.92f}, {-118.7f, 16.19f, -43.52f}, {-167.74f, 18.48f, -40.49f}, {178.42f, 18.54f, -36.6f}, {178.26f, 18.42f, -32.69f}, {178.26f, 18.3f, -28.79f}, {178.26f, 18.18f, -24.88f}, {179.56f, 18.07f, -20.98f}, {-180.0f, 18.07f, -17.07f}, {-180.0f, 18.07f, -13.17f}, {-180.0f, 18.07f, -9.26f}, {-180.0f, 18.07f, -5.35f}, {-180.0f, 18.07f, -1.44f}, {-180.0f, 18.07f, 2.46f}, {-180.0f, 18.07f, 6.38f}, {-180.0f, 18.07f, 10.28f}, {-180.0f, 18.07f, 14.19f}, {-180.0f, 18.07f, 18.09f}, {-180.0f, 18.07f, 22.0f}, {179.3f, 18.07f, 25.91f}, {177.21f, 17.93f, 29.81f}, {177.21f, 17.73f, 33.71f}, {177.21f, 17.54f, 37.61f}, {176.22f, 17.36f, 41.51f}};
    private static final float START_Y = 17.36f;
    private static final float START_Z = 41.51f;

    public ResourceLocation getModelResource(BMP1UEntity animatable) {
        return new ResourceLocation("fcp", "geo/bmp1u.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    @Nullable
    public VehicleModel.TransformContext<BMP1UEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "BarrelOccilator" -> this.barrelRecoil(0);
            default -> super.collectTransform(boneName);
        };
    }

    private VehicleModel.TransformContext<BMP1UEntity> barrelRecoil(int barrelIndex) {
        return (bone, vehicle, state) -> {
            ModelBoneTransforms.clearRecoilOffsets(bone);
            if (vehicle.getCannonRecoilTime() <= 0) {
                return;
            }
            if (!CANNON_WEAPON.equals(vehicle.getGunName(1))) {
                return;
            }
            CannonRecoilTransforms.apply(bone, vehicle, CannonRecoilTransforms.Profile.SIDETOSIDE);
        };
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)50);
        float frac = normalized - (float)((int)normalized);
        float p1 = KEYFRAMES[idx1][component];
        float p2 = KEYFRAMES[idx2][component];
        if (component == 0) {
            float diff = p2 - p1;
            if (diff > 180.0f) {
                p2 -= 360.0f;
            } else if (diff < -180.0f) {
                p2 += 360.0f;
            }
        }
        return Mth.m_14179_((float)frac, (float)p1, (float)p2);
    }

    public float getBoneRotX(float t) {
        return this.getKeyframeValue(t, 0);
    }

    public float getBoneMoveY(float t) {
        return this.getKeyframeValue(t, 1) - 17.36f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 41.51f;
    }
}

