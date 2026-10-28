package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.OutpostSupplySyncPacket;
import org.espetro.client.aui.GuiElement;

/** 靠近前哨基地时的 Radio 血量、弹药/建材存量与兵站状态 HUD。 */
public final class OutpostSupplyHud {

    private static final int HUD_X = 8;
    private static final int HUD_Y = 34;
    private static final int PANEL_WIDTH = 158;
    private static final int PANEL_HEIGHT = 52;

    private static final ResourceLocation FOB_STATUS_ICON =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/fob_status.png");
    private static final ResourceLocation AMMO_ICON =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation CONSTRUCTION_ICON =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/construction_supply.png");

    private static final int TEXTURE_SIZE = 128;
    private static final int PAD = 6;
    private static final int STATUS_ICON_SIZE = 12;
    private static final int SUPPLY_ICON_SIZE = 10;
    private static final int STATUS_PILL_HEIGHT = 11;

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

            // 圆角玻璃卡：阴影 + 渐变底 + 发丝描边。
            UiPaint.card(graphics, bx, by, bw, bh);

            // 左上角 fob_status 图标 + 电台血量进度条（内凹槽 + 渐变填充）。
            int iconX = bx + PAD;
            int iconY = by + PAD;
            graphics.fill(iconX, iconY, iconX + STATUS_ICON_SIZE, iconY + STATUS_ICON_SIZE,
                UiTheme.ICON_PLATE);
            graphics.blit(FOB_STATUS_ICON, iconX, iconY,
                STATUS_ICON_SIZE, STATUS_ICON_SIZE, 0f, 0f,
                TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);

            int barX = iconX + STATUS_ICON_SIZE + 5;
            int barY = by + 9;
            int barW = bw - (barX - bx) - PAD;
            float health = radioMaxHealth <= 0 ? 0f
                : Math.min(radioHealth, radioMaxHealth) / (float) radioMaxHealth;
            UiPaint.progress(graphics, barX, barY, barW, 6, health, UiTheme.ACCENT);

            // 分割线：状态条与存量行之间。
            UiPaint.hairline(graphics, bx + PAD, by + 22, bw - PAD * 2, UiTheme.HAIRLINE);

            // 弹药 / 建材存量行（数值用标题字号 + 投影）。
            int supplyY = by + 24;
            graphics.fill(bx + PAD, supplyY, bx + PAD + SUPPLY_ICON_SIZE,
                supplyY + SUPPLY_ICON_SIZE, UiTheme.ICON_PLATE);
            graphics.blit(AMMO_ICON, bx + PAD, supplyY,
                SUPPLY_ICON_SIZE, SUPPLY_ICON_SIZE, 0f, 0f,
                TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
            UiPaint.textScaled(graphics, Integer.toString(ammunition),
                bx + PAD + SUPPLY_ICON_SIZE + 4, supplyY, UiTheme.TEXT,
                UiTheme.TYPE_HEADING, true);

            int constructionIconX = bx + 78;
            graphics.fill(constructionIconX, supplyY, constructionIconX + SUPPLY_ICON_SIZE,
                supplyY + SUPPLY_ICON_SIZE, UiTheme.ICON_PLATE);
            graphics.blit(CONSTRUCTION_ICON, constructionIconX, supplyY,
                SUPPLY_ICON_SIZE, SUPPLY_ICON_SIZE, 0f, 0f,
                TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
            UiPaint.textScaled(graphics, Integer.toString(construction),
                constructionIconX + SUPPLY_ICON_SIZE + 4, supplyY, UiTheme.TEXT,
                UiTheme.TYPE_HEADING, true);

            // 最底下一行：兵站状态胶囊（绿=已启用，红=未启用）。
            String status = habEnabled ? "重生功能已启用" : "兵站未启用";
            int accent = habEnabled ? UiTheme.POSITIVE : UiTheme.NEGATIVE;
            int pillX = bx + PAD;
            int pillY = by + 38;
            int pillW = Minecraft.getInstance().font.width(status) + 12;
            UiPaint.pill(graphics, pillX, pillY, pillW, STATUS_PILL_HEIGHT,
                UiTheme.alpha(accent, 0.16f));
            UiPaint.pillOutline(graphics, pillX, pillY, pillW, STATUS_PILL_HEIGHT,
                UiTheme.alpha(accent, 0.5f));
            UiPaint.text(graphics, status, pillX + 6, pillY + 1, accent);

            super.draw(graphics, parentX, parentY, drawWidth, drawHeight,
                mouseX, mouseY, partialTick);
        }

        private void dispose() {
            // Direct-draw HUD keeps no textures to release.
        }
    }
}
