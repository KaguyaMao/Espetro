/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.ScreenEvent$Render$Post
 *  net.minecraftforge.fml.ModList
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.ModList;
import org.espetro.Espetro;

@OnlyIn(value=Dist.CLIENT)
public final class AuraTipAboveScreen {
    private AuraTipAboveScreen() {
    }

    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (!ModList.get().isLoaded("auratip")) {
            return;
        }
        try {
            Class<?> overlayClz = Class.forName("cc.sighs.auratip.client.render.TipOverlay");
            Object overlay = overlayClz.getField("INSTANCE").get(null);
            if (overlay == null) {
                return;
            }
            if (!((Boolean)overlayClz.getMethod("isActive", new Class[0]).invoke(overlay, new Object[0])).booleanValue()) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc == null || mc.m_91268_() == null || event.getScreen() == null) {
                return;
            }
            int width = mc.m_91268_().m_85445_();
            int height = mc.m_91268_().m_85446_();
            double mouseX = mc.f_91067_.m_91589_() * (double)width / (double)mc.m_91268_().m_85443_();
            double mouseY = mc.f_91067_.m_91594_() * (double)height / (double)mc.m_91268_().m_85444_();
            GuiGraphics graphics = event.getGuiGraphics();
            PoseStack pose = graphics.m_280168_();
            pose.m_85836_();
            pose.m_252880_(0.0f, 0.0f, 1200.0f);
            overlayClz.getMethod("render", GuiGraphics.class, Float.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(overlay, graphics, Float.valueOf(event.getPartialTick()), (int)mouseX, (int)mouseY, width, height);
            pose.m_85849_();
        }
        catch (Throwable t) {
            Espetro.LOGGER.debug("Screen \u4e0a\u53e0\u52a0 AuraTip \u5931\u8d25: {}", (Object)t.toString());
        }
    }
}

