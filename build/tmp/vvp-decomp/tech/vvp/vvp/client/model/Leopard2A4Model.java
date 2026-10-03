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
import tech.vvp.vvp.entity.vehicle.Leopard2A4Entity;

public class Leopard2A4Model
extends VvpVehicleModel<Leopard2A4Entity> {
    private static final int TRACK_COUNT = 55;
    private static final int MAX_IDX = 55;
    private static final float[][] KEYFRAMES = new float[][]{{176.79f, 22.5f, 67.75f}, {133.14f, 20.51f, 72.21f}, {83.82f, 15.86f, 73.71f}, {36.97f, 11.64f, 71.26f}, {39.45f, 8.47f, 67.35f}, {39.45f, 5.27f, 63.46f}, {39.45f, 2.07f, 59.58f}, {0.57f, 0.5f, 54.92f}, {0.0f, 0.5f, 49.89f}, {0.0f, 0.5f, 44.86f}, {0.0f, 0.5f, 39.83f}, {0.0f, 0.5f, 34.79f}, {0.0f, 0.5f, 29.76f}, {0.0f, 0.5f, 24.73f}, {0.0f, 0.5f, 19.7f}, {0.0f, 0.5f, 14.66f}, {0.0f, 0.5f, 9.63f}, {0.0f, 0.5f, 4.6f}, {0.0f, 0.5f, -0.44f}, {0.0f, 0.5f, -5.47f}, {0.0f, 0.5f, -10.5f}, {0.0f, 0.5f, -15.53f}, {0.0f, 0.5f, -20.57f}, {0.0f, 0.5f, -25.6f}, {0.0f, 0.5f, -30.63f}, {-22.29f, 1.1f, -35.58f}, {-35.92f, 3.95f, -39.72f}, {-35.92f, 6.91f, -43.79f}, {-35.92f, 9.86f, -47.87f}, {-36.99f, 12.79f, -51.96f}, {-85.47f, 17.11f, -54.23f}, {-133.95f, 21.69f, -52.54f}, {-179.76f, 23.5f, -48.0f}, {179.51f, 23.45f, -42.96f}, {179.51f, 23.41f, -37.93f}, {179.51f, 23.37f, -32.9f}, {179.51f, 23.32f, -27.87f}, {179.51f, 23.28f, -22.83f}, {179.51f, 23.24f, -17.8f}, {179.51f, 23.19f, -12.77f}, {179.51f, 23.15f, -7.74f}, {179.51f, 23.11f, -2.7f}, {179.51f, 23.06f, 2.33f}, {179.51f, 23.02f, 7.36f}, {179.51f, 22.98f, 12.39f}, {179.51f, 22.93f, 17.43f}, {179.51f, 22.89f, 22.46f}, {179.51f, 22.85f, 27.49f}, {179.51f, 22.8f, 32.52f}, {179.51f, 22.76f, 37.56f}, {179.51f, 22.72f, 42.59f}, {179.51f, 22.67f, 47.62f}, {179.51f, 22.63f, 52.65f}, {179.51f, 22.59f, 57.69f}, {179.51f, 22.54f, 62.72f}, {176.79f, 22.5f, 67.75f}};
    private static final float START_Y = 22.5f;
    private static final float START_Z = 67.75f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<Leopard2A4Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.matchBarrel(boneName);
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 110;
        float normalized = t / (float)wrapRange * 55.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)55);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)55);
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
        return this.getKeyframeValue(t, 1) - 22.5f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 67.75f;
    }
}

