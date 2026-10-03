/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance
 *  com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.util.Mth
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance;
import com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.entities.M3A3Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class M3A3Renderer
extends GeoVehicleRenderer<M3A3Entity> {
    private static final float[] MOVE_Y = new float[]{18.4688f, 18.3352f, 17.9403f, 17.302f, 16.4488f, 15.4191f, 14.2592f, 13.0213f, 11.7608f, 10.5346f, 9.3976f, 8.4009f, 7.5894f, 6.9956f, 6.4825f, 5.9693f, 5.4561f, 4.943f, 4.4298f, 3.9167f, 3.4035f, 2.8903f, 2.3772f, 1.864f, 1.3508f, 0.91f, 0.7206f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7147f, 0.7843f, 1.1076f, 1.6244f, 2.1532f, 2.6821f, 3.2109f, 3.7397f, 4.2685f, 4.7974f, 5.3262f, 5.855f, 6.3838f, 6.9127f, 7.4415f, 7.9703f, 8.592f, 9.4295f, 10.4469f, 11.5983f, 12.8321f, 14.0929f, 15.3238f, 16.4698f, 17.4792f, 18.3067f, 18.9152f, 19.2772f, 19.3774f, 19.3666f, 19.3558f, 19.345f, 19.3341f, 19.3233f, 19.3125f, 19.3017f, 19.2909f, 19.2801f, 19.2692f, 19.2584f, 19.2476f, 19.2368f, 19.226f, 19.2152f, 19.2043f, 19.1935f, 19.1827f, 19.1719f, 19.1611f, 19.1503f, 19.1394f, 19.1286f, 19.1178f, 19.107f, 19.0962f, 19.0854f, 19.0745f, 19.0637f, 19.0529f, 19.0421f, 19.0313f, 19.0205f, 19.0096f, 18.9988f, 18.988f, 18.9772f, 18.9664f, 18.9556f, 18.9447f, 18.9339f, 18.9231f, 18.9123f, 18.9015f, 18.8907f, 18.8798f, 18.869f, 18.8582f, 18.8474f, 18.8366f, 18.8258f, 18.8149f, 18.8041f, 18.7933f, 18.7825f, 18.7717f, 18.7609f, 18.75f, 18.7392f, 18.7284f, 18.7176f, 18.7068f, 18.696f, 18.6851f, 18.6743f, 18.6635f, 18.6527f, 18.6419f, 18.6311f, 18.6202f, 18.6094f, 18.5986f, 18.5878f, 18.577f, 18.5662f, 18.5553f, 18.5445f, 18.5337f, 18.5229f, 18.5121f, 18.5013f, 18.4904f, 18.4796f, 18.4688f};
    private static final float[] MOVE_Z = new float[]{53.4636f, 54.7172f, 55.9145f, 57.0017f, 57.9299f, 58.6574f, 59.1516f, 59.3901f, 59.3624f, 59.0696f, 58.5249f, 57.7528f, 56.788f, 55.6754f, 54.5212f, 53.3671f, 52.2129f, 51.0587f, 49.9045f, 48.7504f, 47.5962f, 46.442f, 45.2878f, 44.1337f, 42.9795f, 41.7977f, 40.5513f, 39.2882f, 38.0251f, 36.762f, 35.4989f, 34.2358f, 32.9727f, 31.7096f, 30.4464f, 29.1833f, 27.9202f, 26.6571f, 25.394f, 24.1309f, 22.8678f, 21.6047f, 20.3415f, 19.0784f, 17.8153f, 16.5522f, 15.2891f, 14.026f, 12.7629f, 11.4997f, 10.2366f, 8.9735f, 7.7104f, 6.4473f, 5.1842f, 3.9211f, 2.658f, 1.3948f, 0.1317f, -1.1314f, -2.3945f, -3.6576f, -4.9207f, -6.1838f, -7.447f, -8.7101f, -9.9732f, -11.2363f, -12.4994f, -13.7625f, -15.0256f, -16.2888f, -17.5519f, -18.815f, -20.0781f, -21.3412f, -22.6043f, -23.8674f, -25.1305f, -26.3937f, -27.6568f, -28.9199f, -30.183f, -31.4461f, -32.7092f, -33.9723f, -35.2355f, -36.4986f, -37.7617f, -39.0212f, -40.2398f, -41.3921f, -42.5392f, -43.6863f, -44.8334f, -45.9805f, -47.1276f, -48.2746f, -49.4217f, -50.5688f, -51.7159f, -52.863f, -54.01f, -55.1571f, -56.2541f, -57.1965f, -57.9411f, -58.4545f, -58.7138f, -58.7071f, -58.4348f, -57.9092f, -57.1539f, -56.2027f, -55.0985f, -53.8909f, -52.6341f, -51.371f, -50.1079f, -48.8449f, -47.5818f, -46.3187f, -45.0556f, -43.7926f, -42.5295f, -41.2664f, -40.0034f, -38.7403f, -37.4772f, -36.2142f, -34.9511f, -33.688f, -32.425f, -31.1619f, -29.8988f, -28.6358f, -27.3727f, -26.1096f, -24.8466f, -23.5835f, -22.3204f, -21.0574f, -19.7943f, -18.5312f, -17.2682f, -16.0051f, -14.742f, -13.479f, -12.2159f, -10.9528f, -9.6898f, -8.4267f, -7.1636f, -5.9006f, -4.6375f, -3.3744f, -2.1114f, -0.8483f, 0.4148f, 1.6778f, 2.9409f, 4.204f, 5.467f, 6.7301f, 7.9932f, 9.2562f, 10.5193f, 11.7824f, 13.0454f, 14.3085f, 15.5716f, 16.8346f, 18.0977f, 19.3608f, 20.6239f, 21.8869f, 23.15f, 24.4131f, 25.6761f, 26.9392f, 28.2023f, 29.4653f, 30.7284f, 31.9915f, 33.2545f, 34.5176f, 35.7807f, 37.0437f, 38.3068f, 39.5699f, 40.8329f, 42.096f, 43.3591f, 44.6221f, 45.8852f, 47.1483f, 48.4113f, 49.6744f, 50.9375f, 52.2005f, 53.4636f};
    private static final float[] ROT_X = new float[]{0.0f, -12.1681f, -24.3362f, -36.5043f, -48.6725f, -60.8406f, -73.0087f, -85.1768f, -97.3449f, -109.513f, -121.6811f, -133.8493f, -146.0174f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -156.0294f, -165.2765f, -177.4446f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -180.0f, -188.7751f, -200.9432f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -204.7506f, -215.5466f, -227.7147f, -239.8829f, -252.051f, -264.2191f, -276.3872f, -288.5553f, -300.7234f, -312.8916f, -325.0597f, -337.2278f, -349.3959f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f, -360.4907f};
    private static final float[] TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};
    private static final float[] ROT_TIMES = new float[]{0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f, 5.5f, 6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 10.5f, 11.0f, 11.5f, 12.0f, 12.5f, 13.0f, 13.5f, 14.0f, 14.5f, 15.0f, 15.5f, 16.0f, 16.5f, 17.0f, 17.5f, 18.0f, 18.5f, 19.0f, 19.5f, 20.0f, 20.5f, 21.0f, 21.5f, 22.0f, 22.5f, 23.0f, 23.5f, 24.0f, 24.5f, 25.0f, 25.5f, 26.0f, 26.5f, 27.0f, 27.5f, 28.0f, 28.5f, 29.0f, 29.5f, 30.0f, 30.5f, 31.0f, 31.5f, 32.0f, 32.5f, 33.0f, 33.5f, 34.0f, 34.5f, 35.0f, 35.5f, 36.0f, 36.5f, 37.0f, 37.5f, 38.0f, 38.5f, 39.0f, 39.5f, 40.0f, 40.5f, 41.0f, 41.5f, 42.0f, 42.5f, 43.0f, 43.5f, 44.0f, 44.5f, 45.0f, 45.5f, 46.0f, 46.5f, 47.0f, 47.5f, 48.0f, 48.5f, 49.0f, 49.5f, 50.0f, 50.5f, 51.0f, 51.5f, 52.0f, 52.5f, 53.0f, 53.5f, 54.0f, 54.5f, 55.0f, 55.5f, 56.0f, 56.5f, 57.0f, 57.5f, 58.0f, 58.5f, 59.0f, 59.5f, 60.0f, 60.5f, 61.0f, 61.5f, 62.0f, 62.5f, 63.0f, 63.5f, 64.0f, 64.5f, 65.0f, 65.5f, 66.0f, 66.5f, 67.0f, 67.5f, 68.0f, 68.5f, 69.0f, 69.5f, 70.0f, 70.5f, 71.0f, 71.5f, 72.0f, 72.5f, 73.0f, 73.5f, 74.0f, 74.5f, 75.0f, 75.5f, 76.0f, 76.5f, 77.0f, 77.5f, 78.0f, 78.5f, 79.0f, 79.5f, 80.0f, 80.5f, 81.0f, 81.5f, 82.0f, 82.5f, 83.0f, 83.5f, 84.0f, 84.5f, 85.0f, 85.5f, 86.0f, 86.5f, 87.0f, 87.5f, 88.0f, 88.5f, 89.0f, 89.5f, 90.0f, 90.5f, 91.0f, 91.5f, 92.0f, 92.5f, 93.0f, 93.5f, 94.0f, 94.5f, 95.0f, 95.5f, 96.0f, 96.5f, 97.0f, 97.5f, 98.0f, 98.5f, 99.0f, 99.5f, 100.0f};

    public M3A3Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    public void transformCustomModelPart(M3A3Entity entity, VehicleModelInstance instance, PoseStack poseStack, float entityYaw, float partialTicks) {
        BoneState bone;
        super.transformCustomModelPart((VehicleEntity)entity, instance, poseStack, entityYaw, partialTicks);
        int state = entity.getMissileState();
        int timer = entity.getDeployTimer();
        float progress = state == 1 ? 1.0f - (float)timer / 30.0f : (state == 2 ? 1.0f : (state == 3 ? (float)timer / 30.0f : 0.0f));
        progress = Mth.m_14036_((float)progress, (float)0.0f, (float)1.0f);
        BoneState missile = instance.getBone("missile");
        if (missile != null) {
            missile.rotation.set((Quaternionfc)new Quaternionf().rotationZ((float)Math.toRadians(90.0f * (1.0f - progress))));
        }
        if ((bone = instance.getBone("bone")) != null) {
            bone.rotation.set((Quaternionfc)new Quaternionf().rotationZ((float)Math.toRadians(-45.0f * (1.0f - progress))));
        }
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
        return M3A3Renderer.sample(TIMES, MOVE_Y, t);
    }

    public float getBoneMoveZ(float t) {
        return M3A3Renderer.sample(TIMES, MOVE_Z, t);
    }

    public float getBoneRotX(float t) {
        return M3A3Renderer.sample(ROT_TIMES, ROT_X, t);
    }

    public float getTrackDistance() {
        return 1.7857143f;
    }
}

