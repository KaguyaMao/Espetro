/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.BedrockModelRenderTypes
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockBone
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity.projectile;

import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockBone;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.redabysslucia.dragonrise_reforge.entities.projectile.AntiTopWireGuideMissileEntity;
import com.redabysslucia.dragonrise_reforge.resource.model.ProjectileModelReloadListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class AntiTopWireGuideMissileRenderer
extends EntityRenderer<AntiTopWireGuideMissileEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation("dragonrise_reforge", "models/bedrock/projectile/anti_top_wire_guide_missile.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("dragonrise_reforge", "textures/bedrock/projectile/anti_top_wire_guide_missile.png");
    private static final ResourceLocation FLARE_TEXTURE = new ResourceLocation("superbwarfare", "textures/bedrock/projectile/flare.png");

    public AntiTopWireGuideMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(AntiTopWireGuideMissileEntity entity) {
        return TEXTURE;
    }

    public void render(AntiTopWireGuideMissileEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean hasFlare;
        if (entity.f_19797_ <= entity.getHiddenTicks()) {
            return;
        }
        BedrockModel model = (BedrockModel)ProjectileModelReloadListener.INSTANCE.getModel(MODEL);
        if (model == null) {
            return;
        }
        poseStack.m_85836_();
        poseStack.m_252880_(0.0f, entity.m_20206_() / 2.0f, 0.0f);
        Vec3 lookAngle = entity.m_20154_();
        poseStack.m_252781_(Axis.f_252436_.m_252977_((float)VehicleVecUtils.getYRotFromVector((Vec3)lookAngle)));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(-((float)VehicleVecUtils.getXRotFromVector((Vec3)lookAngle)) + 180.0f));
        poseStack.m_252781_(Axis.f_252403_.m_252977_(180.0f));
        BedrockBone flare = model.getBone("flare");
        boolean bl = hasFlare = flare != null;
        if (hasFlare) {
            flare.visible = false;
        }
        model.renderToBuffer(poseStack, buffer, RenderType.m_110452_((ResourceLocation)TEXTURE), BedrockModelRenderTypes.polyMeshCutout((ResourceLocation)TEXTURE), packedLight, OverlayTexture.f_118083_);
        ResourceLocation emissive = entity.getEmissiveTexture();
        if (emissive != null) {
            model.renderToBuffer(poseStack, buffer, RenderType.m_110452_((ResourceLocation)emissive), BedrockModelRenderTypes.polyMeshCutout((ResourceLocation)emissive), packedLight, OverlayTexture.f_118083_);
        }
        if (hasFlare && entity.f_19797_ > entity.getFlareHiddenTicks()) {
            flare.visible = true;
            flare.rotation.rotationZ(2.5f * (float)(Math.random() - 0.5));
            flare.xScale = (float)((2.0 * Math.random() - 1.0) * 0.4 + 1.6);
            flare.yScale = (float)((2.0 * Math.random() - 1.0) * 0.4 + 1.6);
            flare.zScale = (float)((2.0 * Math.random() - 1.0) * 0.4 + 1.6);
            flare.render(poseStack, buffer.m_6299_(RenderType.m_110488_((ResourceLocation)FLARE_TEXTURE)), packedLight, OverlayTexture.f_118083_);
        }
        poseStack.m_85849_();
    }
}

