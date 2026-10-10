/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package tech.vvp.vvp.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

@OnlyIn(value=Dist.CLIENT)
public class HimarsOperatorOverlay
implements IGuiOverlay {
    public static final String ID = "vvp_himars_operator";
    private static final String GMLRS_WEAPON = "GMLRS";
    private static final float PROJECTILE_VELOCITY = 20.0f;
    private static final float GRAVITY = 0.08f;
    private static final int COLOR_BG = -804254198;
    private static final int COLOR_BORDER = -10727360;
    private static final int COLOR_LABEL = -7570846;
    private static final int COLOR_VALUE = -2570072;
    private static final int COLOR_OK = -5193616;
    private static final int COLOR_WARN = -3108800;
    private static final int COLOR_ALERT = -3387312;

    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
    }

    private static void drawPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.m_280509_(x, y, x + w, y + h, -804254198);
        graphics.m_280509_(x, y, x + w, y + 1, -10727360);
        graphics.m_280509_(x, y + h - 1, x + w, y + h, -10727360);
        graphics.m_280509_(x, y, x + 1, y + h, -10727360);
        graphics.m_280509_(x + w - 1, y, x + w, y + h, -10727360);
    }

    private static void drawPair(Minecraft mc, GuiGraphics graphics, int x, int y, int panelW, String leftLabel, String leftValue, String rightLabel, String rightValue, int leftValueColor, int rightValueColor) {
        int mid = x + (panelW - 12) / 2;
        HimarsOperatorOverlay.drawField(mc, graphics, x, y, mid - 4, leftLabel, leftValue, leftValueColor);
        HimarsOperatorOverlay.drawField(mc, graphics, mid, y, x + panelW - 12, rightLabel, rightValue, rightValueColor);
    }

    private static void drawField(Minecraft mc, GuiGraphics graphics, int x, int y, int rightEdge, String label, String value, int valueColor) {
        graphics.m_280614_(mc.f_91062_, (Component)Component.m_237113_((String)(label + " ")), x, y, -7570846, false);
        int labelW = mc.f_91062_.m_92895_(label + " ");
        HimarsOperatorOverlay.drawRight(mc, graphics, (Component)Component.m_237113_((String)value), x + labelW, y, rightEdge - x - labelW, valueColor);
    }

    private static void drawRight(Minecraft mc, GuiGraphics graphics, Component text, int x, int y, int blockW, int color) {
        int width = mc.f_91062_.m_92852_((FormattedText)text);
        graphics.m_280614_(mc.f_91062_, text, x + blockW - width, y, color, false);
    }

    private static String resolveStatusText(int ammo, boolean reloading, boolean moving, HimarsEntity himars) {
        if (himars.isFdcSlewing()) {
            return "SLEW";
        }
        if (himars.isFdcTargetDesignated()) {
            return "TGT";
        }
        if (moving) {
            return "MOVE";
        }
        if (reloading) {
            return "LOAD";
        }
        if (ammo <= 0) {
            return "EMPTY";
        }
        return "READY";
    }

    private static int resolveStatusColor(int ammo, boolean reloading, boolean moving, HimarsEntity himars) {
        if (himars.isFdcSlewing() || himars.isFdcTargetDesignated()) {
            return -5193616;
        }
        if (moving || reloading) {
            return -3108800;
        }
        if (ammo <= 0) {
            return -3387312;
        }
        return -5193616;
    }
}

