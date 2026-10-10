/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.client.RenderHelper
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
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
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.redabysslucia.dragonrise_reforge.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.entities.atmg.HJ8Entity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class HJ8Overlay
implements IGuiOverlay {
    public static final String ID = "superbwarfare_hj8";
    private static final ResourceLocation SPYGLASS = Mod.loc((String)"textures/overlay/spyglass/spyglass.png");
    private static float scopeScale = 1.0f;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        PoseStack poseStack = guiGraphics.m_280168_();
        LocalPlayer player = gui.getMinecraft().f_91074_;
        if (player == null) {
            return;
        }
        if (player.m_20202_() instanceof HJ8Entity && (ClientEventHandler.zoomVehicle || mc.f_91066_.m_92176_() == CameraType.FIRST_PERSON)) {
            poseStack.m_85836_();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::m_172817_);
            RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            float deltaFrame = Minecraft.m_91087_().m_91297_();
            scopeScale = (float)Mth.m_14139_((double)(0.5f * deltaFrame), (double)scopeScale, (double)((double)1.35f + (double)0.2f * ClientEventHandler.firePosTimer));
            float f = Math.min(screenWidth, screenHeight);
            float f1 = Math.min((float)screenWidth / f, (float)screenHeight / f) * scopeScale;
            float i = Mth.m_14143_((float)(f * f1));
            float j = Mth.m_14143_((float)(f * f1));
            float k = ((float)screenWidth - i) / 2.0f;
            float l = ((float)screenHeight - j) / 2.0f;
            float w = i * 21.0f / 9.0f;
            RenderHelper.preciseBlit((GuiGraphics)guiGraphics, (ResourceLocation)SPYGLASS, (float)(k - 2.0f * w / 7.0f), (float)l, (float)0.0f, (float)0.0f, (float)w, (float)j, (float)w, (float)j);
            poseStack.m_85849_();
        } else {
            scopeScale = 1.0f;
        }
    }
}

