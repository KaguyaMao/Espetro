/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoEntityRenderer
 */
package frontline.combat.fcp.client.renderer.JohnDeere;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frontline.combat.fcp.client.model.JohnDeere.SeederModel;
import frontline.combat.fcp.entity.vehicle.JohnDeere.SeederEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SeederRenderer
extends GeoEntityRenderer<SeederEntity> {
    public SeederRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, (GeoModel)new SeederModel());
    }

    protected void applyRotations(SeederEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        float yaw = Mth.m_14189_((float)partialTick, (float)animatable.f_19859_, (float)animatable.m_146908_());
        poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f - yaw));
    }

    public ResourceLocation getTextureLocation(SeederEntity entity) {
        return entity.getCurrentTexture();
    }
}

