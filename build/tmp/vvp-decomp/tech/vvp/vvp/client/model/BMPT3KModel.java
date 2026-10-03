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
import tech.vvp.vvp.entity.vehicle.BMPT3KEntity;

public class BMPT3KModel
extends VvpVehicleModel<BMPT3KEntity> {
    private static final int TRACK_COUNT = 53;
    private static final int MAX_IDX = 53;
    private static final float[][] KEYFRAMES = new float[][]{{176.78f, 21.97f, 51.55f}, {128.03f, 19.86f, 55.87f}, {78.54f, 15.17f, 56.88f}, {31.65f, 11.5f, 53.78f}, {31.65f, 8.89f, 49.54f}, {31.65f, 6.28f, 45.31f}, {33.51f, 3.67f, 41.08f}, {6.9f, 1.72f, 36.59f}, {0.0f, 1.69f, 31.62f}, {0.0f, 1.69f, 26.65f}, {0.0f, 1.69f, 21.67f}, {0.0f, 1.69f, 16.7f}, {0.0f, 1.69f, 11.73f}, {0.0f, 1.69f, 6.75f}, {0.0f, 1.69f, 1.78f}, {0.0f, 1.69f, -3.19f}, {0.0f, 1.69f, -8.16f}, {0.0f, 1.69f, -13.14f}, {0.0f, 1.69f, -18.11f}, {0.0f, 1.69f, -23.08f}, {0.0f, 1.69f, -28.06f}, {0.0f, 1.69f, -33.03f}, {0.0f, 1.69f, -38.0f}, {0.0f, 1.69f, -42.97f}, {-0.57f, 1.69f, -47.95f}, {-34.42f, 3.17f, -52.6f}, {-43.88f, 6.6f, -56.2f}, {-43.88f, 10.05f, -59.78f}, {-43.88f, 13.49f, -63.37f}, {-92.83f, 17.95f, -65.12f}, {-147.62f, 22.07f, -62.65f}, {-178.31f, 22.94f, -57.85f}, {179.48f, 22.91f, -52.87f}, {179.48f, 22.86f, -47.9f}, {179.48f, 22.82f, -42.93f}, {179.48f, 22.77f, -37.96f}, {179.48f, 22.73f, -32.98f}, {179.48f, 22.68f, -28.01f}, {179.48f, 22.64f, -23.04f}, {179.48f, 22.6f, -18.07f}, {179.48f, 22.55f, -13.09f}, {179.48f, 22.51f, -8.12f}, {179.48f, 22.46f, -3.15f}, {179.48f, 22.42f, 1.82f}, {179.48f, 22.37f, 6.8f}, {179.48f, 22.33f, 11.77f}, {179.48f, 22.28f, 16.74f}, {179.48f, 22.24f, 21.72f}, {179.48f, 22.19f, 26.69f}, {179.48f, 22.15f, 31.66f}, {179.48f, 22.1f, 36.63f}, {179.48f, 22.06f, 41.61f}, {179.48f, 22.01f, 46.58f}, {176.78f, 21.97f, 51.55f}};
    private static final float START_Y = 21.97f;
    private static final float START_Z = 51.55f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<BMPT3KEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.matchBarrel(boneName);
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 106;
        float normalized = t / (float)wrapRange * 53.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)53);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)53);
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
        return this.getKeyframeValue(t, 1) - 21.97f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 51.55f;
    }
}

