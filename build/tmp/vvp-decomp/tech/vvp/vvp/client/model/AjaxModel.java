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
import tech.vvp.vvp.entity.vehicle.AjaxEntity;

public class AjaxModel
extends VvpVehicleModel<AjaxEntity> {
    private static final int TRACK_COUNT = 56;
    private static final int MAX_IDX = 56;
    private static final float[][] KEYFRAMES = new float[][]{{176.95f, 18.5f, 47.25f}, {143.37f, 17.21f, 50.96f}, {104.31f, 13.89f, 53.08f}, {65.24f, 9.98f, 52.68f}, {29.22f, 7.14f, 49.96f}, {28.61f, 5.23f, 46.44f}, {28.61f, 3.31f, 42.92f}, {28.01f, 1.39f, 39.4f}, {0.0f, 0.5f, 35.55f}, {0.0f, 0.5f, 31.54f}, {0.0f, 0.5f, 27.53f}, {0.0f, 0.5f, 23.53f}, {0.0f, 0.5f, 19.52f}, {0.0f, 0.5f, 15.51f}, {0.0f, 0.5f, 11.5f}, {0.0f, 0.5f, 7.49f}, {0.0f, 0.5f, 3.48f}, {0.0f, 0.5f, -0.53f}, {0.0f, 0.5f, -4.54f}, {0.0f, 0.5f, -8.54f}, {0.0f, 0.5f, -12.55f}, {0.0f, 0.5f, -16.56f}, {0.0f, 0.5f, -20.57f}, {0.0f, 0.5f, -24.58f}, {0.0f, 0.5f, -28.59f}, {0.0f, 0.5f, -32.6f}, {-7.35f, 0.57f, -36.6f}, {-42.8f, 2.48f, -40.01f}, {-42.8f, 5.21f, -42.95f}, {-42.8f, 7.93f, -45.89f}, {-41.56f, 10.56f, -48.9f}, {-90.13f, 14.1f, -50.5f}, {-143.57f, 17.56f, -48.75f}, {-178.04f, 18.49f, -44.95f}, {-179.93f, 18.5f, -40.94f}, {-180.0f, 18.5f, -36.93f}, {-180.0f, 18.5f, -32.93f}, {-180.0f, 18.5f, -28.92f}, {-180.0f, 18.5f, -24.91f}, {-180.0f, 18.5f, -20.9f}, {-180.0f, 18.5f, -16.89f}, {-180.0f, 18.5f, -12.88f}, {-180.0f, 18.5f, -8.87f}, {-180.0f, 18.5f, -4.87f}, {-180.0f, 18.5f, -0.86f}, {-180.0f, 18.5f, 3.15f}, {-180.0f, 18.5f, 7.16f}, {-180.0f, 18.5f, 11.17f}, {-180.0f, 18.5f, 15.18f}, {-180.0f, 18.5f, 19.19f}, {-180.0f, 18.5f, 23.19f}, {-180.0f, 18.5f, 27.2f}, {-180.0f, 18.5f, 31.21f}, {-180.0f, 18.5f, 35.22f}, {-179.99f, 18.5f, 39.23f}, {-179.99f, 18.5f, 43.24f}, {176.95f, 18.5f, 47.25f}};
    private static final float START_Y = 18.5f;
    private static final float START_Z = 47.25f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Nullable
    public VehicleModel.TransformContext<AjaxEntity> collectTransform(String boneName) {
        VehicleModel.TransformContext recoil = CannonRecoilTransforms.matchBarrelForWeapon(boneName, "Cannon");
        if (recoil != null) {
            return recoil;
        }
        return super.collectTransform(boneName);
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 112;
        float normalized = t / (float)wrapRange * 56.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)56);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)56);
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
        return this.getKeyframeValue(t, 1) - 18.5f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 47.25f;
    }
}

