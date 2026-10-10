/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package tech.vvp.vvp.client.model;

import net.minecraft.util.Mth;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.CV90Entity;

public class CV90Model
extends VvpVehicleModel<CV90Entity> {
    public static final int TRACK_COUNT = 71;
    private static final int MAX_IDX = 71;
    private static final float[][] KEYFRAMES = new float[][]{{178.43f, 14.5f, 42.0f}, {143.02f, 13.55f, 44.76f}, {105.04f, 11.11f, 46.36f}, {67.06f, 8.2f, 46.12f}, {29.08f, 6.05f, 44.15f}, {24.81f, 4.85f, 41.43f}, {24.81f, 3.6f, 38.73f}, {24.81f, 2.35f, 36.03f}, {24.81f, 1.1f, 33.33f}, {0.0f, 0.5f, 30.46f}, {0.0f, 0.5f, 27.49f}, {0.0f, 0.5f, 24.51f}, {0.0f, 0.5f, 21.54f}, {0.0f, 0.5f, 18.56f}, {0.0f, 0.5f, 15.59f}, {0.0f, 0.5f, 12.62f}, {0.0f, 0.5f, 9.64f}, {0.0f, 0.5f, 6.67f}, {0.0f, 0.5f, 3.69f}, {0.0f, 0.5f, 0.72f}, {0.0f, 0.5f, -2.25f}, {0.0f, 0.5f, -5.23f}, {0.0f, 0.5f, -8.2f}, {0.0f, 0.5f, -11.18f}, {0.0f, 0.5f, -14.15f}, {0.0f, 0.5f, -17.13f}, {0.0f, 0.5f, -20.1f}, {0.0f, 0.5f, -23.07f}, {0.0f, 0.5f, -26.05f}, {0.0f, 0.5f, -29.02f}, {0.0f, 0.5f, -32.0f}, {-0.62f, 0.5f, -34.97f}, {-28.74f, 1.32f, -37.79f}, {-28.74f, 2.75f, -40.39f}, {-28.74f, 4.18f, -43.0f}, {-28.74f, 5.61f, -45.61f}, {-28.74f, 7.04f, -48.22f}, {-37.69f, 8.48f, -50.81f}, {-75.51f, 10.95f, -52.38f}, {-115.31f, 13.86f, -52.1f}, {-153.13f, 15.98f, -50.1f}, {179.85f, 16.48f, -47.21f}, {178.76f, 16.42f, -44.23f}, {178.73f, 16.35f, -41.26f}, {178.73f, 16.28f, -38.29f}, {178.73f, 16.22f, -35.31f}, {178.73f, 16.15f, -32.34f}, {178.73f, 16.09f, -29.37f}, {178.73f, 16.02f, -26.39f}, {178.73f, 15.95f, -23.42f}, {178.73f, 15.89f, -20.44f}, {178.73f, 15.82f, -17.47f}, {178.73f, 15.76f, -14.5f}, {178.73f, 15.69f, -11.52f}, {178.73f, 15.62f, -8.55f}, {178.73f, 15.56f, -5.58f}, {178.73f, 15.49f, -2.6f}, {178.73f, 15.43f, 0.37f}, {178.73f, 15.36f, 3.34f}, {178.73f, 15.29f, 6.32f}, {178.73f, 15.23f, 9.29f}, {178.73f, 15.16f, 12.26f}, {178.73f, 15.09f, 15.24f}, {178.73f, 15.03f, 18.21f}, {178.73f, 14.96f, 21.19f}, {178.73f, 14.9f, 24.16f}, {178.73f, 14.83f, 27.13f}, {178.73f, 14.76f, 30.11f}, {178.73f, 14.7f, 33.08f}, {178.73f, 14.63f, 36.05f}, {178.73f, 14.57f, 39.03f}, {178.43f, 14.5f, 42.0f}};
    private static final float START_Y = 14.5f;
    private static final float START_Z = 42.0f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 142;
        float normalized = t / (float)wrapRange * 71.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)71);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)71);
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
        return this.getKeyframeValue(t, 1) - 14.5f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 42.0f;
    }
}

