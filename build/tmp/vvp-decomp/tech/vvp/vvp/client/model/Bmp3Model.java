/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package tech.vvp.vvp.client.model;

import net.minecraft.util.Mth;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.Bmp3Entity;

public class Bmp3Model
extends VvpVehicleModel<Bmp3Entity> {
    public static final int TRACK_COUNT = 52;
    private static final int MAX_IDX = 52;
    private static final float[][] KEYFRAMES = new float[][]{{179.25f, 20.5f, 58.5f}, {131.77f, 18.5f, 62.97f}, {84.04f, 13.83f, 64.46f}, {35.45f, 9.61f, 61.98f}, {34.82f, 6.73f, 57.84f}, {34.82f, 3.85f, 53.69f}, {24.38f, 1.05f, 49.5f}, {0.0f, 0.5f, 44.54f}, {0.0f, 0.5f, 39.49f}, {0.0f, 0.5f, 34.44f}, {0.0f, 0.5f, 29.4f}, {0.0f, 0.5f, 24.35f}, {0.0f, 0.5f, 19.31f}, {0.0f, 0.5f, 14.26f}, {0.0f, 0.5f, 9.21f}, {0.0f, 0.5f, 4.17f}, {0.0f, 0.5f, -0.88f}, {0.0f, 0.5f, -5.92f}, {0.0f, 0.5f, -10.97f}, {0.0f, 0.5f, -16.02f}, {0.0f, 0.5f, -21.06f}, {0.0f, 0.5f, -26.11f}, {0.0f, 0.5f, -31.15f}, {-20.9f, 0.9f, -36.15f}, {-27.22f, 3.17f, -40.65f}, {-27.22f, 5.48f, -45.14f}, {-27.22f, 7.79f, -49.63f}, {-27.22f, 10.1f, -54.11f}, {-73.65f, 13.85f, -57.27f}, {-122.58f, 18.7f, -56.58f}, {-170.51f, 21.42f, -52.5f}, {179.49f, 21.46f, -47.46f}, {179.48f, 21.42f, -42.42f}, {179.48f, 21.37f, -37.37f}, {179.48f, 21.33f, -32.32f}, {179.48f, 21.28f, -27.28f}, {179.48f, 21.23f, -22.23f}, {179.48f, 21.19f, -17.19f}, {179.48f, 21.14f, -12.14f}, {179.48f, 21.1f, -7.09f}, {179.48f, 21.05f, -2.05f}, {179.48f, 21.0f, 3.0f}, {179.48f, 20.96f, 8.04f}, {179.48f, 20.91f, 13.09f}, {179.48f, 20.87f, 18.13f}, {179.48f, 20.82f, 23.18f}, {179.48f, 20.78f, 28.23f}, {179.48f, 20.73f, 33.27f}, {179.48f, 20.68f, 38.32f}, {179.48f, 20.64f, 43.36f}, {179.48f, 20.59f, 48.41f}, {179.48f, 20.55f, 53.45f}, {179.25f, 20.5f, 58.5f}};
    private static final float START_Y = 20.5f;
    private static final float START_Z = 58.5f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 104;
        float normalized = t / (float)wrapRange * 52.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)52);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)52);
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
        return this.getKeyframeValue(t, 1) - 20.5f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 58.5f;
    }
}

