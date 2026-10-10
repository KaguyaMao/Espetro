/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.espetro.client.gui.TutorialClientController;

public final class TutorialHudOverlay {
    private static final int EXIT_W = 88;
    private static final int EXIT_H = 16;
    private static final int EXIT_MARGIN = 8;
    private static final int CARD_MARGIN = 10;
    private static final int CARD_MAX_W = 320;

    private TutorialHudOverlay() {
    }

    public static void onStepChanged() {
    }

    public static void clear() {
    }

    public static void render(GuiGraphics graphics, Minecraft mc, float partialTick) {
        if (!TutorialClientController.isActive() || mc == null || mc.f_91062_ == null) {
            return;
        }
        Font font = mc.f_91062_;
        int sw = mc.m_91268_().m_85445_();
        int sh = mc.m_91268_().m_85446_();
        String stepId = TutorialClientController.getStepId();
        MutableComponent title = Component.m_237115_("tutorial.step." + stepId + ".title").m_7220_(Component.m_237113_("  " + TutorialClientController.getIndex() + " / " + TutorialClientController.getTotal()));
        MutableComponent body = Component.m_237115_("tutorial.step." + stepId + ".body");
        String footer = "Enter = \u4e0b\u4e00\u6b65" + (TutorialClientController.isAllowSkip() ? "  \u00b7  \u5de6\u4e0b\u89d2\u53ef\u5b8c\u5168\u9000\u51fa" : "");
        int cardW = Math.min(320, Math.max(180, sw - 20));
        String bodyText = body.getString();
        int bodyH = font.m_92920_(bodyText, cardW - 16);
        int cardH = 12 + font.f_92710_ + 6 + bodyH + 8 + font.f_92710_ + 10;
        int cardX = (sw - cardW) / 2;
        int cardY = sh - cardH - 8 - 16 - 6;
        graphics.m_280509_(cardX, cardY, cardX + cardW, cardY + cardH, -871295978);
        graphics.m_280637_(cardX, cardY, cardW, cardH, -1864320934);
        graphics.m_280614_(font, title, cardX + 8, cardY + 6, -2049958, false);
        graphics.m_280554_(font, Component.m_237113_(bodyText), cardX + 8, cardY + 6 + font.f_92710_ + 6, cardW - 16, -1513240);
        graphics.m_280056_(font, footer, cardX + 8, cardY + cardH - font.f_92710_ - 6, -5327681, false);
        int exitX = 8;
        int exitY = sh - 8 - 16;
        boolean hover = TutorialHudOverlay.isOverExit(mc.f_91067_.m_91589_(), mc.f_91067_.m_91594_(), mc);
        graphics.m_280509_(exitX, exitY, exitX + 88, exitY + 16, hover ? -1069535184 : -1609033704);
        graphics.m_280637_(exitX, exitY, 88, 16, hover ? -39322 : -2130745754);
        String exitLabel = "\u9000\u51fa\u6559\u7a0b";
        int tw = font.m_92895_(exitLabel);
        graphics.m_280056_(font, exitLabel, exitX + (88 - tw) / 2, exitY + (16 - font.f_92710_) / 2, -30584, false);
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!TutorialClientController.isActive() || button != 0) {
            return false;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null) {
            return false;
        }
        int sh = mc.m_91268_().m_85446_();
        int exitX = 8;
        int exitY = sh - 8 - 16;
        if (mouseX >= (double)exitX && mouseX < (double)(exitX + 88) && mouseY >= (double)exitY && mouseY < (double)(exitY + 16)) {
            TutorialClientController.requestSkipAll();
            return true;
        }
        return false;
    }

    private static boolean isOverExit(double windowMouseX, double windowMouseY, Minecraft mc) {
        double scale = mc.m_91268_().m_85449_();
        double mx = windowMouseX / scale;
        double my = windowMouseY / scale;
        int sh = mc.m_91268_().m_85446_();
        int exitX = 8;
        int exitY = sh - 8 - 16;
        return mx >= (double)exitX && mx < (double)(exitX + 88) && my >= (double)exitY && my < (double)(exitY + 16);
    }
}

