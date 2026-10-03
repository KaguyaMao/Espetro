/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.client.model.util.CannonRecoilTransforms;
import tech.vvp.vvp.entity.vehicle.PumaEntity;

public class PumaModel
extends VvpVehicleModel<PumaEntity> {
    private static final int TRACK_COUNT = 61;
    private static final int MAX_IDX = 61;
    private static final float[][] KEYFRAMES = new float[][]{{176.89f, 22.52f, 52.45f}, {138.8f, 21.19f, 56.14f}, {100.02f, 17.82f, 58.14f}, {61.24f, 13.95f, 57.54f}, {39.42f, 11.13f, 54.75f}, {39.42f, 8.59f, 51.66f}, {39.42f, 6.05f, 48.57f}, {39.42f, 3.51f, 45.48f}, {7.41f, 1.96f, 41.87f}, {0.0f, 1.93f, 37.87f}, {0.0f, 1.93f, 33.87f}, {0.0f, 1.93f, 29.87f}, {0.0f, 1.93f, 25.87f}, {0.0f, 1.93f, 21.87f}, {0.0f, 1.93f, 17.87f}, {0.0f, 1.93f, 13.87f}, {0.0f, 1.93f, 9.87f}, {0.0f, 1.93f, 5.86f}, {0.0f, 1.93f, 1.86f}, {0.0f, 1.93f, -2.14f}, {0.0f, 1.93f, -6.14f}, {0.0f, 1.93f, -10.14f}, {0.0f, 1.93f, -14.14f}, {0.0f, 1.93f, -18.14f}, {0.0f, 1.93f, -22.14f}, {0.0f, 1.93f, -26.14f}, {0.0f, 1.93f, -30.14f}, {-7.15f, 2.02f, -34.13f}, {-32.43f, 3.71f, -37.72f}, {-32.43f, 5.85f, -41.1f}, {-32.43f, 8.0f, -44.47f}, {-32.43f, 10.14f, -47.85f}, {-32.61f, 12.29f, -51.23f}, {-70.12f, 15.27f, -53.77f}, {-109.19f, 19.19f, -53.84f}, {-148.26f, 22.25f, -51.4f}, {-178.23f, 23.1f, -47.56f}, {179.76f, 23.09f, -43.56f}, {179.66f, 23.07f, -39.56f}, {179.66f, 23.05f, -35.56f}, {179.66f, 23.02f, -31.56f}, {179.66f, 23.0f, -27.56f}, {179.66f, 22.97f, -23.56f}, {179.66f, 22.95f, -19.56f}, {179.66f, 22.93f, -15.56f}, {179.66f, 22.9f, -11.56f}, {179.66f, 22.88f, -7.56f}, {179.66f, 22.86f, -3.56f}, {179.66f, 22.83f, 0.44f}, {179.66f, 22.81f, 4.44f}, {179.66f, 22.78f, 8.44f}, {179.66f, 22.76f, 12.44f}, {179.66f, 22.74f, 16.45f}, {179.66f, 22.71f, 20.45f}, {179.66f, 22.69f, 24.45f}, {179.66f, 22.66f, 28.45f}, {179.66f, 22.64f, 32.45f}, {179.66f, 22.62f, 36.45f}, {179.66f, 22.59f, 40.45f}, {179.66f, 22.57f, 44.45f}, {179.66f, 22.55f, 48.45f}, {176.89f, 22.52f, 52.45f}};
    private static final float START_Y = 22.52f;
    private static final float START_Z = 52.45f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<PumaEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.match(boneName, "dulo", CannonRecoilTransforms.Profile.LIGHT);
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 122;
        float normalized = t / (float)wrapRange * 61.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)61);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)61);
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
        return this.getKeyframeValue(t, 1) - 22.52f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 52.45f;
    }
}

