/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.lwjgl.glfw.GLFW
 */
package org.espetro.client.aui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.AuiRadialLayout;
import org.espetro.client.aui.AuiRadialSlot;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public final class AuiRadial {
    private static final double INNER_RADIUS = 44.0;
    private static final double OUTER_RADIUS = 96.0;
    private static final int RING_SEGMENTS = 64;
    private static final ResourceLocation FALLBACK_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/commander_skills/unavailable.png");
    private static final List<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
    private static String pageId = "";
    private static int hovered = -1;
    private static boolean open;
    private static boolean mouseWasDown;

    private AuiRadial() {
    }

    public static boolean isOpen() {
        return open;
    }

    public static String pageId() {
        return AuiRadial.isOpen() ? pageId : "";
    }

    public static boolean isPage(String id) {
        return AuiRadial.isOpen() && id != null && !id.isEmpty() && id.equals(pageId);
    }

    public static int hoveredIndex() {
        return AuiRadial.isOpen() ? hovered : -1;
    }

    public static void show(List<AuiRadialSlot> next) {
        AuiRadial.show(next, "");
    }

    public static void show(List<AuiRadialSlot> next, String nextPage) {
        open = true;
        mouseWasDown = false;
        AuiRadial.replace(next, nextPage);
        AuiRadial.releaseMouse();
    }

    public static void replace(List<AuiRadialSlot> next) {
        AuiRadial.replace(next, pageId);
    }

    public static void replace(List<AuiRadialSlot> next, String nextPage) {
        pageId = nextPage == null ? "" : nextPage;
        slots.clear();
        if (next != null) {
            slots.addAll(next);
        }
        hovered = -1;
    }

    public static void hide() {
        open = false;
        hovered = -1;
        pageId = "";
        mouseWasDown = false;
        slots.clear();
        AuiRadial.grabMouse();
    }

    public static boolean confirmHovered() {
        if (!AuiRadial.isOpen() || hovered < 0 || hovered >= slots.size()) {
            AuiRadial.hide();
            return false;
        }
        slots.get(hovered).action().run();
        return true;
    }

    public static void tickHover(Minecraft minecraft) {
        if (!AuiRadial.isOpen() || minecraft == null || minecraft.m_91268_() == null) {
            return;
        }
        AuiRadial.updateHover();
    }

    public static void tickInput(Minecraft minecraft) {
        boolean down;
        if (!AuiRadial.isOpen() || minecraft == null || minecraft.m_91268_() == null) {
            mouseWasDown = false;
            return;
        }
        AuiRadial.updateHover();
        boolean bl = down = GLFW.glfwGetMouseButton((long)minecraft.m_91268_().m_85439_(), (int)0) == 1;
        if (mouseWasDown && !down) {
            AuiRadial.confirmHovered();
        }
        mouseWasDown = down;
    }

    public static void render(GuiGraphics graphics, float partialTick) {
        String label;
        if (!AuiRadial.isOpen() || graphics == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.m_91268_() == null) {
            return;
        }
        double centerX = (double)mc.m_91268_().m_85445_() * 0.5;
        double centerY = (double)mc.m_91268_().m_85446_() * 0.5;
        float cx = (float)centerX;
        float cy = (float)centerY;
        int count = slots.size();
        AuiRadial.drawRing(graphics, cx, cy);
        for (int index = 0; index < count; ++index) {
            boolean isHovered;
            AuiRadialSlot slot = slots.get(index);
            double x = AuiRadialLayout.slotX(centerX, index, count);
            double y = AuiRadialLayout.slotY(centerY, index, count);
            boolean bl = isHovered = index == hovered;
            if (isHovered) {
                AuiRadial.drawHoverDot(graphics, (float)x, (float)y, 21.0f);
            }
            AuiRadial.drawSlot(graphics, mc, slot, (float)x, (float)y, isHovered);
        }
        if (hovered >= 0 && hovered < count && (label = slots.get(hovered).label().getString()) != null && !label.isEmpty()) {
            graphics.m_280137_(mc.f_91062_, label, (int)centerX, (int)centerY + 122, -1);
        }
    }

    private static void drawRing(GuiGraphics graphics, float cx, float cy) {
        Matrix4f matrix = graphics.m_280168_().m_85850_().m_252922_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::m_172811_);
        BufferBuilder buf = Tesselator.m_85913_().m_85915_();
        buf.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
        for (int i = 0; i <= 64; ++i) {
            double a = Math.PI * 2 * (double)i / 64.0;
            float sin = (float)Math.sin(a);
            float cos = (float)Math.cos(a);
            buf.m_252986_(matrix, cx + cos * 96.0f, cy + sin * 96.0f, 0.0f).m_85950_(0.06f, 0.08f, 0.1f, 0.88f).m_5752_();
            buf.m_252986_(matrix, cx + cos * 44.0f, cy + sin * 44.0f, 0.0f).m_85950_(0.06f, 0.08f, 0.1f, 0.88f).m_5752_();
        }
        BufferUploader.m_231202_(buf.m_231175_());
        RenderSystem.disableBlend();
    }

    private static void drawHoverDot(GuiGraphics graphics, float x, float y, float radius) {
        Matrix4f matrix = graphics.m_280168_().m_85850_().m_252922_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::m_172811_);
        BufferBuilder buf = Tesselator.m_85913_().m_85915_();
        buf.m_166779_(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.f_85815_);
        buf.m_252986_(matrix, x, y, 0.0f).m_85950_(1.0f, 0.84f, 0.31f, 0.4f).m_5752_();
        int segments = 20;
        for (int i = 0; i <= segments; ++i) {
            double a = Math.PI * 2 * (double)i / (double)segments;
            buf.m_252986_(matrix, x + (float)Math.cos(a) * radius, y + (float)Math.sin(a) * radius, 0.0f).m_85950_(1.0f, 0.84f, 0.31f, 0.4f).m_5752_();
        }
        BufferUploader.m_231202_(buf.m_231175_());
        RenderSystem.disableBlend();
    }

    private static void drawSlot(GuiGraphics graphics, Minecraft mc, AuiRadialSlot slot, float x, float y, boolean hoveredSlot) {
        if (slot.item() != null && !slot.item().m_41619_() && slot.texture() == null) {
            float alpha = slot.enabled() ? 1.0f : 0.4f;
            graphics.m_280246_(1.0f, 1.0f, 1.0f, alpha);
            graphics.m_280480_(slot.item(), Math.round(x) - 8, Math.round(y) - 8);
            graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
            return;
        }
        if (slot.glyph() != null && !slot.glyph().isBlank()) {
            String glyph = slot.glyph();
            float size = hoveredSlot ? 2.2f : 1.9f;
            graphics.m_280168_().m_85836_();
            graphics.m_280168_().m_252880_(x, y, 0.0f);
            graphics.m_280168_().m_85841_(size, size, 1.0f);
            graphics.m_280056_(mc.f_91062_, glyph, -mc.f_91062_.m_92895_(glyph) / 2, -mc.f_91062_.f_92710_ / 2, -1, false);
            graphics.m_280168_().m_85849_();
            return;
        }
        ResourceLocation tex = slot.texture() != null ? slot.texture() : FALLBACK_ICON;
        int size = hoveredSlot ? 32 : 28;
        int off = size / 2;
        graphics.m_280246_(1.0f, 1.0f, 1.0f, slot.enabled() ? 1.0f : 0.4f);
        graphics.m_280163_(tex, Math.round(x) - off, Math.round(y) - off, 0.0f, 0.0f, size, size, size, size);
        graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void updateHover() {
        if (!open) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.m_91268_() == null) {
            return;
        }
        double scale = minecraft.m_91268_().m_85449_();
        double mouseX = minecraft.f_91067_.m_91589_() / scale;
        double mouseY = minecraft.f_91067_.m_91594_() / scale;
        double centerX = (double)minecraft.m_91268_().m_85445_() * 0.5;
        double centerY = (double)minecraft.m_91268_().m_85446_() * 0.5;
        hovered = AuiRadialLayout.hitIndex(mouseX, mouseY, centerX, centerY, slots.size());
    }

    private static void releaseMouse() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null && mc.f_91067_ != null) {
            mc.f_91067_.m_91602_();
        }
    }

    private static void grabMouse() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null && mc.f_91067_ != null && mc.f_91080_ == null) {
            mc.f_91067_.m_91601_();
        }
    }
}

