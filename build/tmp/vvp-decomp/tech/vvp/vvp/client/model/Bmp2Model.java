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
import tech.vvp.vvp.entity.vehicle.Bmp2Entity;

public class Bmp2Model
extends VvpVehicleModel<Bmp2Entity> {
    private static final int TRACK_COUNT = 62;
    private static final int MAX_IDX = 62;
    private static final float[][] KEYFRAMES = new float[][]{{176.06f, 21.18f, 53.69f}, {143.44f, 20.06f, 57.17f}, {109.31f, 17.13f, 59.35f}, {75.19f, 13.48f, 59.44f}, {35.61f, 10.46f, 57.39f}, {28.97f, 8.61f, 54.18f}, {28.97f, 6.81f, 50.93f}, {28.97f, 5.02f, 47.68f}, {28.97f, 3.22f, 44.43f}, {7.23f, 1.74f, 41.06f}, {0.0f, 1.68f, 37.35f}, {0.0f, 1.68f, 33.64f}, {0.0f, 1.68f, 29.92f}, {0.0f, 1.68f, 26.21f}, {0.0f, 1.68f, 22.5f}, {0.0f, 1.68f, 18.79f}, {0.0f, 1.68f, 15.08f}, {0.0f, 1.68f, 11.36f}, {0.0f, 1.68f, 7.65f}, {0.0f, 1.68f, 3.94f}, {0.0f, 1.68f, 0.23f}, {0.0f, 1.68f, -3.49f}, {0.0f, 1.68f, -7.2f}, {0.0f, 1.68f, -10.91f}, {0.0f, 1.68f, -14.62f}, {0.0f, 1.68f, -18.34f}, {0.0f, 1.68f, -22.05f}, {0.0f, 1.68f, -25.76f}, {-20.81f, 2.01f, -29.43f}, {-36.24f, 4.08f, -32.5f}, {-36.24f, 6.27f, -35.5f}, {-36.24f, 8.46f, -38.49f}, {-36.24f, 10.66f, -41.49f}, {-34.73f, 12.85f, -44.48f}, {-74.4f, 15.63f, -46.78f}, {-128.43f, 19.12f, -46.0f}, {-175.02f, 20.62f, -42.74f}, {-175.73f, 20.89f, -39.04f}, {-175.68f, 21.17f, -35.33f}, {-175.68f, 21.45f, -31.63f}, {-175.68f, 21.73f, -27.93f}, {-175.68f, 22.01f, -24.23f}, {-176.76f, 22.29f, -20.53f}, {-180.0f, 22.3f, -16.81f}, {-180.0f, 22.3f, -13.1f}, {-180.0f, 22.3f, -9.39f}, {-180.0f, 22.3f, -5.68f}, {-180.0f, 22.3f, -1.96f}, {-180.0f, 22.3f, 1.75f}, {-180.0f, 22.3f, 5.46f}, {-180.0f, 22.3f, 9.17f}, {-180.0f, 22.3f, 12.89f}, {-180.0f, 22.3f, 16.6f}, {-180.0f, 22.3f, 20.31f}, {-180.0f, 22.3f, 24.02f}, {-180.0f, 22.3f, 27.74f}, {-180.0f, 22.3f, 31.45f}, {179.11f, 22.3f, 35.16f}, {176.44f, 22.1f, 38.87f}, {176.44f, 21.87f, 42.57f}, {176.44f, 21.64f, 46.28f}, {176.44f, 21.41f, 49.98f}, {176.06f, 21.18f, 53.69f}};
    private static final float START_Y = 21.18f;
    private static final float START_Z = 53.69f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<Bmp2Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.match(boneName, "dulo_bmp2");
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 124;
        float normalized = t / (float)wrapRange * 62.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)62);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)62);
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
        return this.getKeyframeValue(t, 1) - 21.18f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 53.69f;
    }
}

