/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.RenderHelper
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.redabysslucia.dragonrise_reforge.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.entities.utils.IVehicleBackground;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class VehicleBackgroundOverlay
implements IGuiOverlay {
    public static final String ID = "dragonrise_reforge_vehicle_background";
    private float scopeScale = 1.0f;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        IVehicleBackground vehicle;
        Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        if (mc.f_91066_.m_92176_() != CameraType.FIRST_PERSON) {
            return;
        }
        if (player.m_5833_()) {
            return;
        }
        Entity entity = player.m_20202_();
        if (entity instanceof IVehicleBackground && (vehicle = (IVehicleBackground)entity).shouldRenderBackground()) {
            ResourceLocation texture = vehicle.getBackgroundTexture();
            if (texture == null) {
                return;
            }
            PoseStack poseStack = guiGraphics.m_280168_();
            poseStack.m_85836_();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::m_172817_);
            RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.scopeScale = Mth.m_14179_((float)(0.5f * partialTick), (float)this.scopeScale, (float)1.35f);
            float f = Math.min(screenWidth, screenHeight);
            float f1 = Math.min((float)screenWidth / f, (float)screenHeight / f) * this.scopeScale;
            float i = Mth.m_14143_((float)(f * f1));
            float j = Mth.m_14143_((float)(f * f1));
            float k = ((float)screenWidth - i) / 2.0f;
            float l = ((float)screenHeight - j) / 2.0f;
            float w = i * 21.0f / 9.0f;
            RenderHelper.preciseBlit((GuiGraphics)guiGraphics, (ResourceLocation)texture, (float)(k - 2.0f * w / 7.0f), (float)l, (float)0.0f, (float)0.0f, (float)w, (float)j, (float)w, (float)j);
            poseStack.m_85849_();
        } else {
            this.scopeScale = 1.0f;
        }
    }
}

