/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.joml.Matrix4f
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public final class SeatSwitchHandler {
    private static final int SWITCH_DELAY_TICKS = 100;
    private static final int RING_RADIUS = 28;
    private static final int RING_THICKNESS = 3;
    private static final int RING_SEGMENTS = 64;
    private static int switchTicks;
    private static boolean blocking;
    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.register(SeatSwitchHandler.class);
    }

    @SubscribeEvent
    public static void onClientTickPre(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.f_91074_ == null || mc.f_91080_ != null) {
            switchTicks = 0;
            blocking = false;
            return;
        }
        if (mc.f_91074_.m_20202_() == null || !SeatSwitchHandler.isEspetroVehicle(mc.f_91074_.m_20202_())) {
            switchTicks = 0;
            blocking = false;
            return;
        }
        boolean shiftDown = mc.f_91066_.f_92090_.m_90857_();
        if (!shiftDown) {
            switchTicks = 0;
            blocking = false;
            return;
        }
        if (switchTicks >= 100 && !blocking) {
            return;
        }
        if (blocking) {
            mc.f_91074_.f_108618_.f_108573_ = false;
            if (++switchTicks >= 100) {
                blocking = false;
            }
            return;
        }
        switchTicks = 1;
        blocking = true;
        mc.f_91074_.f_108618_.f_108573_ = false;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        if (switchTicks <= 0 || switchTicks > 100) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        float remainSec = (float)(100 - switchTicks) / 20.0f;
        String text = String.format("\u00a7e\u6362\u5ea7\u5012\u8ba1\u65f6 \u00a7f%.1fs", Float.valueOf(Math.max(0.0f, remainSec)));
        int cx = mc.m_91268_().m_85445_() / 2;
        int cy = mc.m_91268_().m_85446_() / 2;
        graphics.m_280488_(mc.f_91062_, text, cx - mc.f_91062_.m_92895_(text) / 2, cy + 30, 0xFFFFFF);
        SeatSwitchHandler.drawProgressRing(event.getGuiGraphics().m_280168_(), mc, cx, cy, (float)switchTicks / 100.0f);
    }

    private static void drawProgressRing(PoseStack poseStack, Minecraft mc, int cx, int cy, float progress) {
        int r = 28;
        int thick = 3;
        int color = progress > 0.75f ? -12268476 : (progress > 0.5f ? -3364352 : -3390396);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
        float a = color >> 24 & 0xFF;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha = a / 255.0f;
        int filled = (int)(progress * 64.0f);
        for (int i = 0; i <= filled; ++i) {
            float angle = (float)(-1.5707963267948966 + Math.PI * 2 * (double)i / 64.0);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float ix = (float)cx + (float)(r - thick) * cos;
            float iy = (float)cy + (float)(r - thick) * sin;
            float ox = (float)cx + (float)(r + thick) * cos;
            float oy = (float)cy + (float)(r + thick) * sin;
            builder.m_252986_(matrix, ix, iy, 0.0f).m_85950_(red, green, blue, alpha).m_5752_();
            builder.m_252986_(matrix, ox, oy, 0.0f).m_85950_(red, green, blue, alpha).m_5752_();
        }
        BufferUploader.m_231202_(builder.m_231175_());
        RenderSystem.disableBlend();
    }

    private static boolean isEspetroVehicle(Entity vehicle) {
        for (String tag : vehicle.m_19880_()) {
            if (!tag.startsWith("espetro_team_")) continue;
            return true;
        }
        return false;
    }
}

