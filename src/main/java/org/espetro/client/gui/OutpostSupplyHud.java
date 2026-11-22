package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.OutpostSupplySyncPacket;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.hud.HudDraw;

/** 靠近前哨基地时的 Radio 血量、弹药/建材存量与兵站状态 HUD。 */
public final class OutpostSupplyHud {

    private static final int HUD_X = 8;
    private static final int HUD_Y = 34;
    private static final int PANEL_WIDTH = 158;
    private static final int PANEL_HEIGHT = 52;

    private static final ResourceLocation FOB_STATUS_ICON = EspetroIcons.FOB_STATUS;
    private static final ResourceLocation AMMO_ICON = EspetroIcons.AMMO_SUPPLY;
    private static final ResourceLocation CONSTRUCTION_ICON = EspetroIcons.CONSTRUCTION_SUPPLY;

    private static final int PANEL_BACKGROUND = HudDraw.PANEL_BACKGROUND;
    private static final int PANEL_BORDER = HudDraw.PANEL_BORDER;
    private static final int BAR_BACKGROUND = HudDraw.BAR_BACKGROUND;
    private static final int BAR_HEALTH = HudDraw.HEALTH;
    private static final int HAB_ENABLED_COLOR = 0xFF55AAFF;
    private static final int HAB_DISABLED_COLOR = 0xFFFF5555;

    private static boolean inRange;
    private static int radioHealth;
    private static int radioMaxHealth = 1;
    private static int ammunition;
    private static int construction;
    private static boolean habEnabled;
    private static OutpostSupplyElement activeElement;

    private OutpostSupplyHud() {
    }

    public static void update(OutpostSupplySyncPacket packet) {
        if (packet == null || !packet.isInRange()) {
            clear();
            return;
        }
        inRange = true;
        radioHealth = Math.max(0, packet.getRadioHealth());
        radioMaxHealth = Math.max(1, packet.getRadioMaxHealth());
        ammunition = Math.max(0, packet.getAmmunition());
        construction = Math.max(0, packet.getConstruction());
        habEnabled = packet.isHabEnabled();
        applyState();
    }

    public static void clear() {
        if (!inRange) return;
        inRange = false;
        applyState();
    }

    static GuiElement createElement() {
        if (activeElement != null) activeElement.dispose();
        activeElement = new OutpostSupplyElement();
        activeElement.applyState();
        return activeElement;
    }

    private static void applyState() {
        if (activeElement != null) activeElement.applyState();
    }

    private static final class OutpostSupplyElement extends GuiElement {

        private OutpostSupplyElement() {
            super(HUD_X, HUD_Y, PANEL_WIDTH, PANEL_HEIGHT);
        }

        private void applyState() {
            setVisible(inRange);
        }

        @Override
        public void draw(GuiGraphics graphics, int parentX, int parentY, int drawWidth,
                         int drawHeight, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            int bx = parentX + getX();
            int by = parentY + getY();
            int bw = getWidth();
            int bh = getHeight();

            HudDraw.panel(graphics, bx, by, bw, bh, PANEL_BACKGROUND, PANEL_BORDER);

            // 左上角 fob_status 图标 + 电台血量蓝条
            int iconSize = 12;
            int iconX = bx + 6;
            int iconY = by + 6;
            HudDraw.icon(graphics, FOB_STATUS_ICON, iconX, iconY, iconSize);

            int barX = iconX + iconSize + 4;
            int barY = by + 9;
            int barW = bw - (barX - bx) - 6;
            int barH = 6;
            HudDraw.bar(graphics, barX, barY, barW, barH,
                HudDraw.roundedWidth(radioHealth, radioMaxHealth, barW), BAR_BACKGROUND, BAR_HEALTH);

            // 弹药 / 建材存量行
            var font = Minecraft.getInstance().font;
            int supplyY = by + 24;
            int supplyIconSize = 10;
            HudDraw.iconValue(graphics, font, AMMO_ICON, bx + 6, supplyY, supplyIconSize,
                String.valueOf(ammunition), supplyY + 1, HudDraw.TEXT, false);
            HudDraw.iconValue(graphics, font, CONSTRUCTION_ICON, bx + 78, supplyY, supplyIconSize,
                String.valueOf(construction), supplyY + 1, HudDraw.TEXT, false);

            // 最底下一行：兵站是否启用
            int statusY = by + 40;
            if (habEnabled) {
                graphics.drawString(Minecraft.getInstance().font,
                    Component.literal("重生功能已启用"),
                    bx + 6, statusY, HAB_ENABLED_COLOR, false);
            } else {
                graphics.drawString(Minecraft.getInstance().font,
                    Component.literal("兵站未启用"),
                    bx + 6, statusY, HAB_DISABLED_COLOR, false);
            }

            super.draw(graphics, parentX, parentY, drawWidth, drawHeight,
                mouseX, mouseY, partialTick);
        }

        private void dispose() {
            // Direct-draw HUD keeps no textures to release.
        }
    }
}
