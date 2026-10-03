/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoEntityRenderer
 */
package frontline.combat.fcp.client.renderer.Projectile.Malyutka;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frontline.combat.fcp.client.model.Projectile.Malyutka.MalyutkaModel;
import frontline.combat.fcp.entity.projectile.Malyutka.MalyutkaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MalyutkaRenderer
extends GeoEntityRenderer<MalyutkaEntity> {
    public MalyutkaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new MalyutkaModel());
    }

    public RenderType getRenderType(MalyutkaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_((ResourceLocation)this.m_5478_((Entity)animatable));
    }

    public void render(MalyutkaEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.m_85836_();
        poseStack.m_252781_(Axis.f_252436_.m_252977_(-Mth.m_14179_((float)partialTicks, (float)entityIn.f_19859_, (float)entityIn.m_146908_())));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(Mth.m_14179_((float)partialTicks, (float)entityIn.f_19860_, (float)entityIn.m_146909_())));
        super.m_7392_((Entity)entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.m_85849_();
    }
}

