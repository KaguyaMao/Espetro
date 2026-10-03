/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 */
package frontline.combat.fcp.client.model.Aavp;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import frontline.combat.fcp.entity.vehicle.Aavp.AAVPEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AAVPModel
extends VehicleModel<AAVPEntity> {
    private static final int TRACK_COUNT = 50;
    private static final int MAX_IDX = 50;
    private static final float[][] KEYFRAMES = new float[][]{{175.86f, 16.04f, 50.24f}, {128.32f, 14.3f, 53.97f}, {79.11f, 10.34f, 55.04f}, {29.89f, 6.96f, 52.7f}, {26.76f, 5.09f, 48.89f}, {26.76f, 3.18f, 45.1f}, {26.76f, 1.27f, 41.31f}, {0.0f, 0.59f, 37.19f}, {0.0f, 0.59f, 32.94f}, {0.0f, 0.59f, 28.7f}, {0.0f, 0.59f, 24.46f}, {0.0f, 0.59f, 20.21f}, {0.0f, 0.59f, 15.97f}, {0.0f, 0.59f, 11.72f}, {0.0f, 0.59f, 7.48f}, {0.0f, 0.59f, 3.23f}, {0.0f, 0.59f, -1.01f}, {0.0f, 0.59f, -5.25f}, {0.0f, 0.59f, -9.5f}, {0.0f, 0.59f, -13.74f}, {0.0f, 0.59f, -17.99f}, {0.0f, 0.59f, -22.23f}, {-0.59f, 0.59f, -26.48f}, {-32.52f, 1.88f, -30.43f}, {-31.58f, 4.1f, -34.05f}, {-31.58f, 6.32f, -37.66f}, {-45.03f, 8.7f, -41.16f}, {-84.29f, 12.5f, -42.88f}, {-123.56f, 16.56f, -41.95f}, {-157.91f, 19.21f, -38.73f}, {178.45f, 19.55f, -34.54f}, {177.12f, 19.33f, -30.3f}, {177.12f, 19.12f, -26.06f}, {177.12f, 18.91f, -21.83f}, {177.12f, 18.69f, -17.59f}, {177.12f, 18.48f, -13.35f}, {177.12f, 18.27f, -9.11f}, {177.12f, 18.05f, -4.87f}, {177.12f, 17.84f, -0.63f}, {177.12f, 17.63f, 3.61f}, {179.28f, 17.42f, 7.85f}, {-180.0f, 17.42f, 12.09f}, {-180.0f, 17.42f, 16.34f}, {-180.0f, 17.42f, 20.58f}, {-180.0f, 17.42f, 24.82f}, {178.93f, 17.42f, 29.07f}, {176.8f, 17.3f, 33.31f}, {175.73f, 16.99f, 37.54f}, {175.73f, 16.67f, 41.77f}, {175.73f, 16.36f, 46.01f}, {175.86f, 16.04f, 50.24f}};
    private static final float START_Y = 16.04f;
    private static final float START_Z = 50.24f;

    public ResourceLocation getModelResource(AAVPEntity animatable) {
        return new ResourceLocation("fcp", "geo/aavp.geo.json");
    }

    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    private float getKeyframeValue(float t, int component) {
        int wrapRange = 100;
        float normalized = t / (float)wrapRange * 50.0f;
        int idx1 = Mth.m_14045_((int)((int)normalized), (int)0, (int)50);
        int idx2 = Mth.m_14045_((int)(idx1 + 1), (int)0, (int)50);
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
        return this.getKeyframeValue(t, 1) - 16.04f;
    }

    public float getBoneMoveZ(float t) {
        return this.getKeyframeValue(t, 2) - 50.24f;
    }
}

