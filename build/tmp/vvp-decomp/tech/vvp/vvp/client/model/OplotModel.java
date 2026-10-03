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
import tech.vvp.vvp.entity.vehicle.OplotEntity;

public class OplotModel
extends VvpVehicleModel<OplotEntity> {
    private static final int TRACK_COUNT = 61;
    private static final int MAX_IDX = 61;
    private static final float[][] KEYFRAMES = new float[][]{{176.97f, 19.4f, 73.22f}, {138.47f, 18.02f, 76.86f}, {99.37f, 14.57f, 78.68f}, {55.4f, 10.79f, 77.75f}, {20.17f, 8.57f, 74.56f}, {26.89f, 6.77f, 71.0f}, {26.89f, 4.97f, 67.45f}, {26.89f, 3.17f, 63.9f}, {6.93f, 1.54f, 60.3f}, {0.0f, 1.45f, 56.32f}, {0.0f, 1.45f, 52.34f}, {0.0f, 1.45f, 48.36f}, {0.0f, 1.45f, 44.38f}, {0.0f, 1.45f, 40.4f}, {0.0f, 1.45f, 36.42f}, {0.0f, 1.45f, 32.44f}, {0.0f, 1.45f, 28.46f}, {0.0f, 1.45f, 24.48f}, {0.0f, 1.45f, 20.5f}, {0.0f, 1.45f, 16.52f}, {0.0f, 1.45f, 12.54f}, {0.0f, 1.45f, 8.55f}, {0.0f, 1.45f, 4.57f}, {0.0f, 1.45f, 0.59f}, {0.0f, 1.45f, -3.39f}, {0.0f, 1.45f, -7.37f}, {0.0f, 1.45f, -11.35f}, {0.0f, 1.45f, -15.33f}, {-17.24f, 1.65f, -19.29f}, {-33.51f, 3.79f, -22.64f}, {-33.51f, 5.99f, -25.96f}, {-33.51f, 8.19f, -29.27f}, {-37.5f, 10.16f, -32.71f}, {-82.5f, 13.51f, -34.66f}, {-127.5f, 17.25f, -33.65f}, {-173.02f, 19.16f, -30.27f}, {-179.81f, 19.21f, -26.29f}, {-179.89f, 19.22f, -22.31f}, {-179.89f, 19.22f, -18.33f}, {-179.89f, 19.23f, -14.35f}, {-179.89f, 19.24f, -10.37f}, {-179.89f, 19.25f, -6.39f}, {-179.89f, 19.25f, -2.41f}, {-179.89f, 19.26f, 1.57f}, {-179.89f, 19.27f, 5.55f}, {-179.89f, 19.28f, 9.53f}, {-179.89f, 19.28f, 13.51f}, {-179.89f, 19.29f, 17.49f}, {-179.89f, 19.3f, 21.47f}, {-179.89f, 19.31f, 25.45f}, {-179.89f, 19.32f, 29.43f}, {-179.89f, 19.32f, 33.41f}, {-179.89f, 19.33f, 37.39f}, {-179.89f, 19.34f, 41.37f}, {-179.89f, 19.35f, 45.35f}, {-179.89f, 19.35f, 49.33f}, {-179.89f, 19.36f, 53.32f}, {-179.89f, 19.37f, 57.3f}, {-179.89f, 19.38f, 61.28f}, {-179.89f, 19.38f, 65.26f}, {-179.89f, 19.39f, 69.24f}, {176.97f, 19.4f, 73.22f}};
    private static final float START_Y = 19.4f;
    private static final float START_Z = 73.22f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<OplotEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.matchBarrel(boneName);
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
        return this.getKeyframeValue(t, 1) - 19.4f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 73.22f;
    }
}

