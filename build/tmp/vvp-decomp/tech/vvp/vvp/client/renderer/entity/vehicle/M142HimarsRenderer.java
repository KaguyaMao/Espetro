/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  software.bernie.geckolib.cache.object.GeoBone
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.util.RenderUtils
 */
package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;
import tech.vvp.vvp.client.firecontrol.FireControlClientEvents;
import tech.vvp.vvp.client.firecontrol.FireControlClientState;
import tech.vvp.vvp.client.model.M142HimarsModel;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

public class M142HimarsRenderer
extends VehicleRenderer<HimarsEntity> {
    public static float debugZOffset = 0.0349f;

    public M142HimarsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new M142HimarsModel());
    }

    public ResourceLocation getTextureLocation(HimarsEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        return textures[0];
    }

    public void renderRecursively(PoseStack poseStack, HimarsEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        ResourceLocation screenTextureId;
        boolean vehicleScreenLit;
        boolean screenLit;
        boolean isMon = bone.getName().equals("MON");
        boolean bl = screenLit = isMon && FireControlClientState.isScreenLit() && FireControlClientState.getBoundHimars() != null && FireControlClientState.getBoundHimars().m_19879_() == animatable.m_19879_();
        if (screenLit) {
            bone.setHidden(true);
        } else if (isMon) {
            bone.setHidden(false);
        }
        float screenBrightness = FireControlClientState.getScreenBrightness();
        boolean bl2 = vehicleScreenLit = screenBrightness > 0.0f && FireControlClientState.getBoundHimars() != null && FireControlClientState.getBoundHimars().m_19879_() == animatable.m_19879_();
        if (vehicleScreenLit) {
            int minLight;
            String name;
            switch (name = bone.getName()) {
                case "Monitor": 
                case "rul3": {
                    int n = 160;
                    break;
                }
                case "DOOR": 
                case "DOOR2": {
                    int n = 128;
                    break;
                }
                case "BOD": {
                    int n = 96;
                    break;
                }
                default: {
                    int n = minLight = -1;
                }
            }
            if (minLight >= 0) {
                int skyLight = packedLight >> 16 & 0xFFFF;
                int ambient = packedLight & 0xFFFF;
                int blockLight = ambient + (int)((float)(minLight - ambient) * screenBrightness);
                packedLight = skyLight << 16 | Math.max(ambient, blockLight);
            }
        }
        super.renderRecursively(poseStack, (VehicleEntity)animatable, bone, renderType, bufferSource, bufferIn, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (screenLit && (screenTextureId = FireControlClientEvents.SCREEN_TEXTURE_ID) != null) {
            poseStack.m_85836_();
            RenderUtils.translateMatrixToBone((PoseStack)poseStack, (CoreGeoBone)bone);
            RenderUtils.translateToPivotPoint((PoseStack)poseStack, (CoreGeoBone)bone);
            if (bone.getRotZ() != 0.0f || bone.getRotY() != 0.0f || bone.getRotX() != 0.0f) {
                poseStack.m_252781_(new Quaternionf().rotationZYX(bone.getRotZ(), bone.getRotY(), bone.getRotX()));
            }
            RenderUtils.scaleMatrixForBone((PoseStack)poseStack, (CoreGeoBone)bone);
            poseStack.m_252781_(Axis.f_252403_.m_252977_(-180.0f));
            poseStack.m_252781_(Axis.f_252529_.m_252977_(-164.0f));
            VertexConsumer screenConsumer = bufferSource.m_6299_(RenderType.m_234338_((ResourceLocation)screenTextureId));
            float minX = -0.28125006f;
            float maxX = minX + 0.5625f;
            float minY = -0.140625f;
            float maxY = minY + 0.35625f;
            float minZ = -0.09062481f;
            float z = minZ - 0.004f + debugZOffset;
            PoseStack.Pose pose = poseStack.m_85850_();
            Matrix4f matrix4f = pose.m_252922_();
            Matrix3f matrix3f = pose.m_252943_();
            int screenAlpha = (int)(screenBrightness * 255.0f);
            screenConsumer.m_252986_(matrix4f, minX, minY, z).m_6122_(255, 255, 255, screenAlpha).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(matrix3f, 0.0f, 0.0f, -1.0f).m_5752_();
            screenConsumer.m_252986_(matrix4f, minX, maxY, z).m_6122_(255, 255, 255, screenAlpha).m_7421_(0.0f, 1.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(matrix3f, 0.0f, 0.0f, -1.0f).m_5752_();
            screenConsumer.m_252986_(matrix4f, maxX, maxY, z).m_6122_(255, 255, 255, screenAlpha).m_7421_(1.0f, 1.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(matrix3f, 0.0f, 0.0f, -1.0f).m_5752_();
            screenConsumer.m_252986_(matrix4f, maxX, minY, z).m_6122_(255, 255, 255, screenAlpha).m_7421_(1.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(matrix3f, 0.0f, 0.0f, -1.0f).m_5752_();
            poseStack.m_85849_();
            bufferSource.m_6299_(RenderType.m_110473_((ResourceLocation)this.getTextureLocation(animatable)));
        }
    }
}

