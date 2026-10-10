/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.client.model.CentauroModel;
import tech.vvp.vvp.entity.vehicle.CentauroEntity;

public class CentauroRenderer
extends VehicleRenderer<CentauroEntity> {
    public CentauroRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new CentauroModel());
    }

    public void render(CentauroEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.m_85836_();
        poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f));
        super.render((VehicleEntity)entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.m_85849_();
    }

    public ResourceLocation getTextureLocation(CentauroEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return camoType >= 0 && camoType < textures.length ? textures[camoType] : textures[0];
    }
}

