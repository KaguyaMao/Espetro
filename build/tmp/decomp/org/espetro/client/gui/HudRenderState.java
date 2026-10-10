/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

final class HudRenderState {
    private HudRenderState() {
    }

    static void begin(GuiGraphics graphics) {
        if (graphics != null) {
            graphics.m_280262_();
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (graphics != null) {
            graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    static void restore(GuiGraphics graphics) {
        if (graphics != null) {
            graphics.m_280262_();
            graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        }
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);
    }
}

