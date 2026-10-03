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
import tech.vvp.vvp.entity.vehicle.T72B3MEntity;

public class T72B3MModel
extends VvpVehicleModel<T72B3MEntity> {
    private static final int TRACK_COUNT = 90;
    private static final int MAX_IDX = 90;
    private static final float[][] KEYFRAMES = new float[][]{{177.18f, 22.0f, 48.75f}, {157.56f, 21.4f, 51.67f}, {132.62f, 19.71f, 54.14f}, {112.67f, 17.2f, 55.74f}, {87.74f, 14.25f, 56.24f}, {62.8f, 11.35f, 55.55f}, {42.86f, 8.94f, 53.78f}, {21.04f, 7.41f, 51.21f}, {20.41f, 6.37f, 48.4f}, {20.41f, 5.32f, 45.58f}, {20.41f, 4.27f, 42.76f}, {20.41f, 3.22f, 39.94f}, {20.41f, 2.17f, 37.13f}, {10.21f, 1.25f, 34.27f}, {0.0f, 1.14f, 31.27f}, {0.0f, 1.14f, 28.26f}, {0.0f, 1.14f, 25.26f}, {0.0f, 1.14f, 22.25f}, {0.0f, 1.14f, 19.25f}, {0.0f, 1.14f, 16.24f}, {0.0f, 1.14f, 13.23f}, {0.0f, 1.14f, 10.23f}, {0.0f, 1.14f, 7.22f}, {0.0f, 1.14f, 4.21f}, {0.0f, 1.14f, 1.21f}, {0.0f, 1.14f, -1.8f}, {0.0f, 1.14f, -4.81f}, {0.0f, 1.14f, -7.81f}, {0.0f, 1.14f, -10.82f}, {0.0f, 1.14f, -13.82f}, {0.0f, 1.14f, -16.83f}, {0.0f, 1.14f, -19.84f}, {0.0f, 1.14f, -22.84f}, {0.0f, 1.14f, -25.85f}, {0.0f, 1.14f, -28.86f}, {0.0f, 1.14f, -31.86f}, {0.0f, 1.14f, -34.87f}, {0.0f, 1.14f, -37.88f}, {0.0f, 1.14f, -40.88f}, {0.0f, 1.14f, -43.89f}, {0.0f, 1.14f, -46.9f}, {-0.55f, 1.14f, -49.9f}, {-32.03f, 1.75f, -52.82f}, {-36.06f, 3.52f, -55.25f}, {-36.06f, 5.29f, -57.68f}, {-36.06f, 7.06f, -60.11f}, {-36.06f, 8.83f, -62.54f}, {-36.06f, 10.6f, -64.97f}, {-39.32f, 12.24f, -67.48f}, {-68.93f, 14.67f, -69.18f}, {-103.49f, 17.63f, -69.38f}, {-133.11f, 20.26f, -68.0f}, {-162.72f, 21.8f, -65.47f}, {-178.82f, 21.95f, -62.48f}, {178.89f, 21.9f, -59.47f}, {178.75f, 21.84f, -56.47f}, {178.75f, 21.77f, -53.46f}, {178.75f, 21.71f, -50.45f}, {178.75f, 21.64f, -47.45f}, {178.75f, 21.57f, -44.44f}, {179.07f, 21.51f, -41.44f}, {179.69f, 21.5f, -38.43f}, {-180.0f, 21.5f, -35.42f}, {-180.0f, 21.5f, -32.42f}, {-180.0f, 21.5f, -29.41f}, {-180.0f, 21.5f, -26.4f}, {-180.0f, 21.5f, -23.4f}, {-180.0f, 21.5f, -20.39f}, {-180.0f, 21.5f, -17.39f}, {-180.0f, 21.5f, -14.38f}, {-180.0f, 21.5f, -11.37f}, {-180.0f, 21.5f, -8.37f}, {-180.0f, 21.5f, -5.36f}, {-180.0f, 21.5f, -2.35f}, {-180.0f, 21.5f, 0.65f}, {-180.0f, 21.5f, 3.66f}, {-180.0f, 21.5f, 6.67f}, {-180.0f, 21.5f, 9.67f}, {-180.0f, 21.5f, 12.68f}, {-180.0f, 21.5f, 15.68f}, {-180.0f, 21.5f, 18.69f}, {-179.7f, 21.5f, 21.7f}, {-179.11f, 21.5f, 24.7f}, {-178.82f, 21.57f, 27.71f}, {-178.82f, 21.63f, 30.72f}, {-178.82f, 21.69f, 33.72f}, {-178.82f, 21.75f, 36.73f}, {-178.82f, 21.81f, 39.73f}, {-178.82f, 21.88f, 42.74f}, {-178.82f, 21.94f, 45.74f}, {177.18f, 22.0f, 48.75f}};
    private static final float START_Y = 22.0f;
    private static final float START_Z = 48.75f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<T72B3MEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.matchBarrel(boneName);
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 180;
        float normalized = t / (float)wrapRange * 90.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)90);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)90);
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
        return this.getKeyframeValue(t, 1) - 22.0f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 48.75f;
    }
}

