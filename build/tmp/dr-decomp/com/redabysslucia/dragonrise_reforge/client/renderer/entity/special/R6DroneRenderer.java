/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.BedrockModelRenderTypes
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity.special;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.redabysslucia.dragonrise_reforge.entities.special.AttackDroneEntity;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import com.redabysslucia.dragonrise_reforge.resource.model.EntityModelReloadListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class R6DroneRenderer
extends EntityRenderer<R6DroneEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation("dragonrise_reforge", "models/bedrock/entity/r6_drone.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("dragonrise_reforge", "textures/entity/r6_drone.png");
    private static final ResourceLocation ATTACK_MODEL = new ResourceLocation("dragonrise_reforge", "models/bedrock/entity/attack_drone.geo.json");
    private static final ResourceLocation ATTACK_TEXTURE = new ResourceLocation("dragonrise_reforge", "textures/entity/attack_drone.png");

    public R6DroneRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static boolean isAttack(R6DroneEntity entity) {
        return entity instanceof AttackDroneEntity;
    }

    public ResourceLocation getTextureLocation(R6DroneEntity entity) {
        return R6DroneRenderer.isAttack(entity) ? ATTACK_TEXTURE : TEXTURE;
    }

    public void render(R6DroneEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean attack = R6DroneRenderer.isAttack(entity);
        ResourceLocation modelLoc = attack ? ATTACK_MODEL : MODEL;
        ResourceLocation texture = attack ? ATTACK_TEXTURE : TEXTURE;
        BedrockModel model = (BedrockModel)EntityModelReloadListener.INSTANCE.getModel(modelLoc);
        if (model == null) {
            return;
        }
        poseStack.m_85836_();
        Vec3 smooth = entity.getSmoothPositionOrNull();
        Vec3 spline = smooth != null ? smooth : entity.getRenderPosition(partialTick);
        double lx = Mth.m_14139_((double)partialTick, (double)entity.f_19790_, (double)entity.m_20185_());
        double ly = Mth.m_14139_((double)partialTick, (double)entity.f_19791_, (double)entity.m_20186_());
        double lz = Mth.m_14139_((double)partialTick, (double)entity.f_19792_, (double)entity.m_20189_());
        poseStack.m_85837_(spline.f_82479_ - lx, spline.f_82480_ - ly, spline.f_82481_ - lz);
        float bodyYaw = entity.getRenderYaw(partialTick);
        float bodyXRot = entity.getRenderPitch(partialTick);
        poseStack.m_252781_(Axis.f_252436_.m_252977_(-bodyYaw));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(-bodyXRot));
        poseStack.m_85837_(0.0, -0.06, 0.0);
        model.renderToBuffer(poseStack, buffer, RenderType.m_110452_((ResourceLocation)texture), BedrockModelRenderTypes.polyMeshCutout((ResourceLocation)texture), packedLight, OverlayTexture.f_118083_);
        poseStack.m_85849_();
    }
}

