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
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity.special;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.model.BedrockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.redabysslucia.dragonrise_reforge.entities.special.ClusterChargeEntity;
import com.redabysslucia.dragonrise_reforge.resource.model.ProjectileModelReloadListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ClusterChargeRenderer
extends EntityRenderer<ClusterChargeEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation("dragonrise_reforge", "models/bedrock/projectile/cluster_charge.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("dragonrise_reforge", "textures/bedrock/projectile/cluster_charge.png");

    public ClusterChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(ClusterChargeEntity entity) {
        return TEXTURE;
    }

    public void render(ClusterChargeEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BedrockModel model = (BedrockModel)ProjectileModelReloadListener.INSTANCE.getModel(MODEL);
        if (model == null) {
            return;
        }
        poseStack.m_85836_();
        Direction direction = entity.m_6350_();
        if (direction == Direction.UP) {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(-90.0f));
        } else if (direction == Direction.DOWN) {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0f));
        } else {
            poseStack.m_252781_(Axis.f_252529_.m_252977_(180.0f));
            poseStack.m_252781_(Axis.f_252392_.m_252977_(direction.m_122435_() + 180.0f));
            if (direction == Direction.EAST || direction == Direction.WEST) {
                poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f));
            }
        }
        if (!entity.isFacingLeft()) {
            poseStack.m_252781_(Axis.f_252403_.m_252977_(180.0f));
        }
        model.renderToBuffer(poseStack, buffer, RenderType.m_110452_((ResourceLocation)TEXTURE), BedrockModelRenderTypes.polyMeshCutout((ResourceLocation)TEXTURE), packedLight, OverlayTexture.f_118083_);
        poseStack.m_85849_();
    }
}

