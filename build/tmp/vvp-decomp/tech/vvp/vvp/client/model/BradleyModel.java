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
import tech.vvp.vvp.entity.vehicle.BradleyEntity;

public class BradleyModel
extends VvpVehicleModel<BradleyEntity> {
    private static final int TRACK_COUNT = 63;
    private static final int MAX_IDX = 63;
    private static final float[][] KEYFRAMES = new float[][]{{175.55f, 17.49f, 45.29f}, {123.17f, 15.66f, 48.65f}, {63.86f, 11.86f, 48.95f}, {37.4f, 9.32f, 46.02f}, {37.4f, 6.91f, 42.85f}, {37.4f, 4.49f, 39.69f}, {37.4f, 2.07f, 36.53f}, {0.53f, 0.7f, 32.95f}, {0.0f, 0.7f, 28.97f}, {0.0f, 0.7f, 24.99f}, {0.0f, 0.7f, 21.01f}, {0.0f, 0.7f, 17.03f}, {0.0f, 0.7f, 13.05f}, {0.0f, 0.7f, 9.07f}, {0.0f, 0.7f, 5.09f}, {0.0f, 0.7f, 1.11f}, {0.0f, 0.7f, -2.87f}, {0.0f, 0.7f, -6.85f}, {0.0f, 0.7f, -10.83f}, {0.0f, 0.7f, -14.81f}, {0.0f, 0.7f, -18.79f}, {0.0f, 0.7f, -22.77f}, {0.0f, 0.7f, -26.75f}, {0.0f, 0.7f, -30.73f}, {0.0f, 0.7f, -34.71f}, {0.0f, 0.7f, -38.69f}, {0.0f, 0.7f, -42.67f}, {-15.23f, 0.97f, -46.63f}, {-24.82f, 2.59f, -50.26f}, {-24.82f, 4.26f, -53.88f}, {-24.82f, 5.93f, -57.49f}, {-24.82f, 7.6f, -61.1f}, {-29.17f, 9.27f, -64.71f}, {-63.79f, 12.09f, -67.41f}, {-103.35f, 15.97f, -67.81f}, {-142.91f, 19.28f, -65.72f}, {-177.0f, 20.6f, -62.05f}, {179.64f, 20.58f, -58.07f}, {179.64f, 20.55f, -54.09f}, {179.64f, 20.53f, -50.11f}, {179.64f, 20.5f, -46.13f}, {179.64f, 20.48f, -42.15f}, {179.64f, 20.45f, -38.17f}, {179.64f, 20.43f, -34.19f}, {179.73f, 20.4f, -30.21f}, {-180.0f, 20.4f, -26.23f}, {-180.0f, 20.4f, -22.25f}, {-180.0f, 20.4f, -18.27f}, {-180.0f, 20.4f, -14.29f}, {-180.0f, 20.4f, -10.31f}, {-180.0f, 20.4f, -6.33f}, {179.63f, 20.4f, -2.35f}, {178.52f, 20.3f, 1.63f}, {178.52f, 20.2f, 5.61f}, {178.52f, 20.1f, 9.59f}, {178.52f, 19.99f, 13.57f}, {178.52f, 19.89f, 17.55f}, {175.54f, 19.76f, 21.52f}, {174.54f, 19.38f, 25.49f}, {174.54f, 19.0f, 29.45f}, {174.54f, 18.63f, 33.41f}, {174.54f, 18.25f, 37.37f}, {174.54f, 17.87f, 41.33f}, {175.55f, 17.49f, 45.29f}};
    private static final float START_Y = 17.49f;
    private static final float START_Z = 45.29f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<BradleyEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.matchBarrel(boneName);
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 126;
        float normalized = t / (float)wrapRange * 63.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)63);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)63);
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
        return this.getKeyframeValue(t, 1) - 17.49f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 45.29f;
    }
}

