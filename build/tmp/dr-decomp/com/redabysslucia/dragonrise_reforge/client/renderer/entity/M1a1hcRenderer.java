/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.util.Mth
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity;

import com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer;
import com.redabysslucia.dragonrise_reforge.entities.M1a1hcEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

public class M1a1hcRenderer
extends GeoVehicleRenderer<M1a1hcEntity> {
    private static final float[] MOVE_Y = new float[]{0.0f, 0.11f, 0.29f, 1.11f, 3.01f, 5.11f, 7.11f, 9.21f, 10.36f, 11.91f, 13.58f, 15.81f, 18.08f, 19.71f, 21.12f, 21.41f, 21.41f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.31f, 21.71f, 21.86f, 21.81f, 21.46f, 20.11f, 18.61f, 16.31f, 14.01f, 12.31f, 9.41f, 7.01f, 4.51f, 2.1135f, 1.09f, 0.4f, -0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] MOVE_Z = new float[]{0.0f, -4.49f, -6.69f, -9.09f, -13.39f, -17.69f, -21.89f, -26.19f, -28.29f, -29.99f, -31.31f, -31.49f, -31.27f, -29.99f, -28.37f, -26.19f, -21.69f, -17.29f, -12.79f, -8.29f, -3.79f, 0.71f, 5.21f, 9.71f, 14.21f, 18.71f, 23.21f, 27.71f, 32.21f, 36.71f, 41.21f, 45.71f, 50.21f, 54.71f, 59.21f, 63.71f, 68.21f, 72.71f, 77.21f, 81.71f, 86.21f, 90.61f, 92.86f, 95.11f, 97.46f, 99.21f, 100.46f, 100.91f, 100.66f, 99.81f, 96.61f, 92.81f, 89.01f, 85.2095f, 83.29f, 81.0f, 78.75f, 76.5f, 72.0f, 67.5f, 63.0f, 58.5f, 54.0f, 49.5f, 45.0f, 40.5f, 36.0f, 31.5f, 27.0f, 22.5f, 18.0f, 13.5f, 9.0f, 4.5f, 0.0f};
    private static final float[] ROT_X = new float[]{360.0f, 357.0f, 339.0f, 334.0f, 334.0f, 315.0f, 270.0f, 225.0f, 181.0f, 177.0f, 180.0f, 180.0f, 182.5f, 186.5f, 176.5f, 138.5f, 92.5f, 54.0f, 33.0f, 33.0f, 13.0f, 0.0f, 0.0f};
    private static final float[] TIMES = new float[]{0.0f, 1.5833f, 2.3333f, 3.1667f, 4.75f, 6.3333f, 7.9167f, 9.5417f, 10.3333f, 11.125f, 11.875f, 12.7083f, 13.4583f, 14.2917f, 15.0417f, 15.875f, 17.4583f, 19.0417f, 20.625f, 22.2083f, 23.7917f, 25.4167f, 27.0f, 28.5833f, 30.1667f, 31.75f, 33.3333f, 34.9167f, 36.5f, 38.0833f, 39.6667f, 41.25f, 42.875f, 44.4583f, 46.0417f, 47.625f, 49.2083f, 50.7917f, 52.375f, 53.9583f, 55.5417f, 57.125f, 57.9167f, 58.75f, 59.5417f, 60.3333f, 61.125f, 61.9167f, 62.7083f, 63.5f, 65.0833f, 66.6667f, 68.25f, 69.8333f, 70.5833f, 71.4167f, 72.2083f, 73.0f, 74.5833f, 76.2083f, 77.7917f, 79.375f, 80.9583f, 82.5417f, 84.125f, 85.7083f, 87.2917f, 88.875f, 90.4583f, 92.0833f, 93.6667f, 95.25f, 96.8333f, 98.4167f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 1.5833f, 3.1667f, 4.75f, 9.5417f, 11.125f, 12.7083f, 14.2917f, 15.875f, 17.4583f, 19.0417f, 53.9583f, 55.5417f, 57.125f, 58.75f, 60.3333f, 61.9167f, 63.5f, 65.0833f, 69.8333f, 71.4167f, 73.0f, 100.0f};

    public M1a1hcRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    private static float sample(float[] times, float[] keys, float t) {
        if (t <= times[0]) {
            return keys[0];
        }
        int n = times.length;
        if (t >= times[n - 1]) {
            return keys[n - 1];
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >> 1;
            if (times[mid] <= t) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float f = (t - times[lo]) / (times[hi] - times[lo]);
        return Mth.m_14179_((float)f, (float)keys[lo], (float)keys[hi]);
    }

    public float getBoneMoveY(float t) {
        return M1a1hcRenderer.sample(TIMES, MOVE_Y, t);
    }

    public float getBoneMoveZ(float t) {
        return M1a1hcRenderer.sample(TIMES, MOVE_Z, t);
    }

    public float getBoneRotX(float t) {
        return M1a1hcRenderer.sample(ROT_TIMES, ROT_X, t);
    }

    public float getTrackDistance() {
        return 1.7857143f;
    }
}

