/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.network.OutpostSupplySyncPacket;

public final class OutpostSupplyHud {
    private static final int HUD_X = 8;
    private static final int HUD_Y = 34;
    private static final int PANEL_WIDTH = 158;
    private static final int PANEL_HEIGHT = 52;
    private static final ResourceLocation FOB_STATUS_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/fob_status.png");
    private static final ResourceLocation AMMO_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation CONSTRUCTION_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/construction_supply.png");
    private static final int PANEL_BACKGROUND = -804187887;
    private static final int PANEL_BORDER = -1335991188;
    private static final int BAR_BACKGROUND = -13421773;
    private static final int BAR_HEALTH = -11887617;
    private static final int HAB_ENABLED_COLOR = -11162881;
    private static final int HAB_DISABLED_COLOR = -43691;
    private static boolean inRange;
    private static int radioHealth;
    private static int radioMaxHealth;
    private static int ammunition;
    private static int construction;
    private static boolean habEnabled;
    private static OutpostSupplyElement activeElement;

    private OutpostSupplyHud() {
    }

    public static void update(OutpostSupplySyncPacket packet) {
        if (packet == null || !packet.isInRange()) {
            OutpostSupplyHud.clear();
            return;
        }
        inRange = true;
        radioHealth = Math.max(0, packet.getRadioHealth());
        radioMaxHealth = Math.max(1, packet.getRadioMaxHealth());
        ammunition = Math.max(0, packet.getAmmunition());
        construction = Math.max(0, packet.getConstruction());
        habEnabled = packet.isHabEnabled();
        OutpostSupplyHud.applyState();
    }

    public static void clear() {
        if (!inRange) {
            return;
        }
        inRange = false;
        OutpostSupplyHud.applyState();
    }

    static GuiElement createElement() {
        if (activeElement != null) {
            activeElement.dispose();
        }
        activeElement = new OutpostSupplyElement();
        activeElement.applyState();
        return activeElement;
    }

    private static void applyState() {
        if (activeElement != null) {
            activeElement.applyState();
        }
    }

    static {
        radioMaxHealth = 1;
    }

    private static final class OutpostSupplyElement
    extends GuiElement {
        private OutpostSupplyElement() {
            super(8, 34, 158, 52);
        }

        private void applyState() {
            this.setVisible(inRange);
        }

        @Override
        public void draw(GuiGraphics graphics, int parentX, int parentY, int drawWidth, int drawHeight, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            int bx = parentX + this.getX();
            int by = parentY + this.getY();
            int bw = this.getWidth();
            int bh = this.getHeight();
            graphics.m_280509_(bx, by, bx + bw, by + bh, -804187887);
            graphics.m_280637_(bx, by, bw, bh, -1335991188);
            int iconSize = 12;
            int iconX = bx + 6;
            int iconY = by + 6;
            graphics.m_280411_(FOB_STATUS_ICON, iconX, iconY, iconSize, iconSize, 0.0f, 0.0f, 128, 128, 128, 128);
            int barX = iconX + iconSize + 4;
            int barY = by + 9;
            int barW = bw - (barX - bx) - 6;
            int barH = 6;
            graphics.m_280509_(barX, barY, barX + barW, barY + barH, -13421773);
            int healthW = Math.max(0, (int)Math.round((double)Math.min(radioHealth, radioMaxHealth) * (double)barW / (double)radioMaxHealth));
            if (healthW > 0) {
                graphics.m_280509_(barX, barY, barX + healthW, barY + barH, -11887617);
            }
            int supplyY = by + 24;
            int supplyIconSize = 10;
            graphics.m_280411_(AMMO_ICON, bx + 6, supplyY, supplyIconSize, supplyIconSize, 0.0f, 0.0f, 128, 128, 128, 128);
            graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(String.valueOf(ammunition)), bx + 6 + supplyIconSize + 3, supplyY + 1, -1, false);
            int constructionIconX = bx + 78;
            graphics.m_280411_(CONSTRUCTION_ICON, constructionIconX, supplyY, supplyIconSize, supplyIconSize, 0.0f, 0.0f, 128, 128, 128, 128);
            graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(String.valueOf(construction)), constructionIconX + supplyIconSize + 3, supplyY + 1, -1, false);
            int statusY = by + 40;
            if (habEnabled) {
                graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_("\u91cd\u751f\u529f\u80fd\u5df2\u542f\u7528"), bx + 6, statusY, -11162881, false);
            } else {
                graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_("\u5175\u7ad9\u672a\u542f\u7528"), bx + 6, statusY, -43691, false);
            }
            super.draw(graphics, parentX, parentY, drawWidth, drawHeight, mouseX, mouseY, partialTick);
        }

        private void dispose() {
        }
    }
}

