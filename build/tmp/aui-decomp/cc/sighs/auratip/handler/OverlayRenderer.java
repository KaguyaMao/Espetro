/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package cc.sighs.auratip.handler;

import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.client.render.TipOverlay;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="auratip", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class OverlayRenderer {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
            return;
        }
        GuiGraphics gg = event.getGuiGraphics();
        PoseStack pose = gg.m_280168_();
        pose.m_85836_();
        pose.m_252880_(0.0f, 0.0f, 500.0f);
        int width = event.getWindow().m_85445_();
        int height = event.getWindow().m_85446_();
        double mouseX = minecraft.f_91067_.m_91589_() * (double)width / (double)event.getWindow().m_85443_();
        double mouseY = minecraft.f_91067_.m_91594_() * (double)height / (double)event.getWindow().m_85444_();
        if (TipOverlay.INSTANCE.isActive()) {
            TipOverlay.INSTANCE.render(event.getGuiGraphics(), event.getPartialTick(), (int)mouseX, (int)mouseY, width, height);
        }
        if (RadialMenuOverlay.INSTANCE.isActive()) {
            RadialMenuOverlay.INSTANCE.render(event.getGuiGraphics(), event.getPartialTick(), (int)mouseX, (int)mouseY, width, height);
        }
        pose.m_85849_();
    }
}

