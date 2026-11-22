package org.espetro.client.gui.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.gui.EspetroIcons;

/**
 * HUD 面板的通用绘制原语与配色。补给类 HUD（FOB / 前哨 / 载具）共用同一套外观，
 * 新增 HUD 时优先组合这里的方法，而不是复制 fill/blit 代码。
 */
public final class HudDraw {

    /** 面板底色。 */
    public static final int PANEL_BACKGROUND = 0xD0111111;
    /** 面板 1px 描边。 */
    public static final int PANEL_BORDER = 0xB05E646C;
    /** 进度条底色。 */
    public static final int BAR_BACKGROUND = 0xFF333333;
    /** 电台 / 设施血量。 */
    public static final int HEALTH = 0xFF4A9BFF;
    /** 弹药。 */
    public static final int AMMO = 0xFFCC4444;
    /** 建材。 */
    public static final int CONSTRUCTION = 0xFFD6B300;
    public static final int TEXT = 0xFFFFFFFF;

    /** 图标与数值之间的间距。 */
    public static final int ICON_TEXT_GAP = 3;

    private HudDraw() {
    }

    /** 实心面板 + 1px 描边。 */
    public static void panel(GuiGraphics graphics, int x, int y, int width, int height,
                             int background, int border) {
        graphics.fill(x, y, x + width, y + height, background);
        graphics.renderOutline(x, y, width, height, border);
    }

    /** 绘制一个 128×128 源纹理的方形图标。 */
    public static void icon(GuiGraphics graphics, ResourceLocation icon, int x, int y, int size) {
        int tex = EspetroIcons.ICON_TEXTURE_SIZE;
        graphics.blit(icon, x, y, size, size, 0.0F, 0.0F, tex, tex, tex, tex);
    }

    /** 图标 + 右侧数值（文字基线由调用方给出），返回整组宽度。 */
    public static int iconValue(GuiGraphics graphics, Font font, ResourceLocation icon, int x, int y,
                                int iconSize, String value, int textY, int color, boolean shadow) {
        icon(graphics, icon, x, y, iconSize);
        graphics.drawString(font, value, x + iconSize + ICON_TEXT_GAP, textY, color, shadow);
        return iconValueWidth(font, iconSize, value);
    }

    public static int iconValueWidth(Font font, int iconSize, String value) {
        return iconSize + ICON_TEXT_GAP + font.width(value);
    }

    /** 进度条：底条 + 从左侧起 {@code filledWidth} 像素的填充。 */
    public static void bar(GuiGraphics graphics, int x, int y, int width, int height,
                           int filledWidth, int background, int fill) {
        graphics.fill(x, y, x + width, y + height, background);
        if (filledWidth > 0) {
            graphics.fill(x, y, x + Math.min(filledWidth, width), y + height, fill);
        }
    }

    /** 按比例换算宽度，向下取整（{@code value} 夹在 0..max）。 */
    public static int floorWidth(int value, int max, int width) {
        if (value <= 0 || max <= 0 || width <= 0) {
            return 0;
        }
        return (int) ((long) Math.min(value, max) * width / max);
    }

    /** 按比例换算宽度，四舍五入（{@code value} 夹在 0..max）。 */
    public static int roundedWidth(int value, int max, int width) {
        if (value <= 0 || max <= 0 || width <= 0) {
            return 0;
        }
        return (int) Math.round((double) Math.min(value, max) * width / max);
    }
}
