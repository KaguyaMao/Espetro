/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package tech.vvp.vvp.client.model;

import net.minecraft.util.Mth;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.M1A2SepIIEntity;

public class M1A2SepIIModel
extends VvpVehicleModel<M1A2SepIIEntity> {
    public static final int TRACK_COUNT = 46;
    private static final int MAX_IDX = 46;
    private static final float[][] KEYFRAMES = new float[][]{{176.95f, 23.4f, 66.31f}, {123.79f, 20.42f, 71.91f}, {70.02f, 14.1f, 72.59f}, {28.47f, 9.75f, 67.85f}, {28.47f, 6.6f, 62.04f}, {28.47f, 3.45f, 56.23f}, {2.96f, 0.87f, 50.2f}, {0.0f, 0.85f, 43.59f}, {0.0f, 0.85f, 36.98f}, {0.0f, 0.85f, 30.37f}, {0.0f, 0.85f, 23.76f}, {0.0f, 0.85f, 17.15f}, {0.0f, 0.85f, 10.54f}, {0.0f, 0.85f, 3.93f}, {0.0f, 0.85f, -2.68f}, {0.0f, 0.85f, -9.29f}, {0.0f, 0.85f, -15.91f}, {0.0f, 0.85f, -22.52f}, {0.0f, 0.85f, -29.13f}, {0.0f, 0.85f, -35.74f}, {0.0f, 0.85f, -42.35f}, {-25.82f, 2.49f, -48.64f}, {-25.82f, 5.37f, -54.59f}, {-25.82f, 8.25f, -60.54f}, {-38.25f, 11.26f, -66.42f}, {-92.96f, 17.03f, -69.05f}, {-147.67f, 22.45f, -65.74f}, {-180.0f, 23.4f, -59.3f}, {-180.0f, 23.4f, -52.69f}, {-180.0f, 23.4f, -46.08f}, {-180.0f, 23.4f, -39.47f}, {-180.0f, 23.4f, -32.86f}, {-180.0f, 23.4f, -26.25f}, {-180.0f, 23.4f, -19.63f}, {-180.0f, 23.4f, -13.02f}, {-180.0f, 23.4f, -6.41f}, {-180.0f, 23.4f, 0.2f}, {-180.0f, 23.4f, 6.81f}, {-180.0f, 23.4f, 13.42f}, {-180.0f, 23.4f, 20.03f}, {-180.0f, 23.4f, 26.64f}, {-180.0f, 23.4f, 33.25f}, {-180.0f, 23.4f, 39.86f}, {-180.0f, 23.4f, 46.47f}, {-180.0f, 23.4f, 53.09f}, {-180.0f, 23.4f, 59.7f}, {176.95f, 23.4f, 66.31f}};
    private static final float START_Y = 23.4f;
    private static final float START_Z = 66.31f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 92;
        float normalized = t / (float)wrapRange * 46.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)46);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)46);
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
        return this.getKeyframeValue(t, 1) - 23.4f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 66.31f;
    }
}

