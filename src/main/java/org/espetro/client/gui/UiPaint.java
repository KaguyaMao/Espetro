package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Immediate-mode paint primitives for the Espetro UI.
 *
 * <p>原版 {@link GuiGraphics} 只有直角 {@code fill}，现代玻璃质感必须自己画：
 * 圆角填充、渐变填充、1px 圆角描边、外阴影、胶囊、圆环、进度条、屏幕暗角与颗粒。
 * 全部按"逐行扫描"实现（半径 ≤ 8 时每帧开销可忽略），并带半径内缩缓存。
 */
public final class UiPaint {

    private UiPaint() {
    }

    /** 颗粒纹理（256×256 RGBA，透明度很低）。 */
    public static final ResourceLocation NOISE =
        new ResourceLocation("espetro", "textures/gui/ui/noise.png");

    private static final int MAX_RADIUS = 24;
    /** 缓存每个半径的逐行内缩量，避免每帧开方。 */
    private static final int[][] INSETS = new int[MAX_RADIUS + 1][];

    private static int[] insets(int radius) {
        int r = Math.max(0, Math.min(MAX_RADIUS, radius));
        int[] cached = INSETS[r];
        if (cached != null) {
            return cached;
        }
        int[] table = new int[r + 1];
        for (int row = 0; row <= r; row++) {
            // 使用像素中心计算圆弧内缩，得到视觉上更平滑的角。
            double dy = r - (row + 0.5D);
            double inner = Math.sqrt(Math.max(0.0D, (double) r * r - dy * dy));
            table[row] = (int) Math.round(r - inner);
        }
        INSETS[r] = table;
        return table;
    }

    /** 某一行在圆角形状内的水平内缩量。 */
    private static int insetAt(int row, int height, int radius, int[] table) {
        int r = Math.max(0, Math.min(MAX_RADIUS, radius));
        if (r == 0) {
            return 0;
        }
        if (row < r) {
            return table[row];
        }
        if (row >= height - r) {
            int mirrored = height - 1 - row;
            return mirrored >= 0 ? table[Math.min(mirrored, r)] : 0;
        }
        return 0;
    }

    // ------------------------------------------------------------------
    // 基础形状
    // ------------------------------------------------------------------

    /** 圆角实心填充。 */
    public static void roundRect(GuiGraphics graphics, int x, int y, int width, int height,
                                 int radius, int color) {
        if (!UiTheme.visible(color) || width <= 0 || height <= 0) {
            return;
        }
        int r = clampRadius(radius, width, height);
        int[] table = insets(r);
        for (int row = 0; row < height; row++) {
            int inset = insetAt(row, height, r, table);
            int left = x + inset;
            int right = x + width - inset;
            if (right > left) {
                graphics.fill(left, y + row, right, y + row + 1, color);
            }
        }
    }

    /** 圆角竖向渐变填充（顶部 → 底部）。 */
    public static void roundRectGradient(GuiGraphics graphics, int x, int y, int width, int height,
                                         int radius, int topColor, int bottomColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (!UiTheme.visible(topColor) && !UiTheme.visible(bottomColor)) {
            return;
        }
        int r = clampRadius(radius, width, height);
        int[] table = insets(r);
        for (int row = 0; row < height; row++) {
            float t = height <= 1 ? 0f : row / (float) (height - 1);
            int color = UiTheme.lerp(topColor, bottomColor, t);
            if (!UiTheme.visible(color)) {
                continue;
            }
            int inset = insetAt(row, height, r, table);
            int left = x + inset;
            int right = x + width - inset;
            if (right > left) {
                graphics.fill(left, y + row, right, y + row + 1, color);
            }
        }
    }

    /** 1px 圆角描边。 */
    public static void strokeRoundRect(GuiGraphics graphics, int x, int y, int width, int height,
                                       int radius, int color) {
        if (!UiTheme.visible(color) || width <= 0 || height <= 0) {
            return;
        }
        int r = clampRadius(radius, width, height);
        int[] table = insets(r);
        for (int row = 0; row < height; row++) {
            int inset = insetAt(row, height, r, table);
            int left = x + inset;
            int right = x + width - inset;
            if (right <= left) {
                continue;
            }
            if (row < r || row >= height - r) {
                // 圆弧区：整行宽度就是这一行上的轮廓。
                graphics.fill(left, y + row, right, y + row + 1, color);
            } else {
                graphics.fill(left, y + row, left + 1, y + row + 1, color);
                graphics.fill(right - 1, y + row, right, y + row + 1, color);
            }
        }
    }

    /** 圆形实心（轮盘中心、图标底）。 */
    public static void circle(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        if (!UiTheme.visible(color) || radius <= 0) {
            return;
        }
        for (int dy = -radius; dy <= radius; dy++) {
            int half = (int) Math.round(Math.sqrt((double) radius * radius - (double) dy * dy));
            if (half <= 0) {
                continue;
            }
            graphics.fill(centerX - half, centerY + dy, centerX + half, centerY + dy + 1, color);
        }
    }

    /** 1px 圆环。 */
    public static void ring(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        if (!UiTheme.visible(color) || radius <= 0) {
            return;
        }
        for (int dy = -radius; dy <= radius; dy++) {
            int outer = (int) Math.round(Math.sqrt((double) radius * radius - (double) dy * dy));
            int innerRadius = radius - 1;
            int inner = innerRadius <= 0 ? -1
                : (int) Math.round(Math.sqrt(Math.max(0.0D,
                    (double) innerRadius * innerRadius - (double) dy * dy)));
            if (outer <= 0) {
                continue;
            }
            int y = centerY + dy;
            if (inner <= 0) {
                graphics.fill(centerX - outer, y, centerX + outer, y + 1, color);
            } else {
                graphics.fill(centerX - outer, y, centerX - inner, y + 1, color);
                graphics.fill(centerX + inner, y, centerX + outer, y + 1, color);
            }
        }
    }

    // ------------------------------------------------------------------
    // 组合部件
    // ------------------------------------------------------------------

    /** 柔和外阴影：三层圆角叠加，向下偏移。 */
    public static void shadow(GuiGraphics graphics, int x, int y, int width, int height, int radius) {
        int r = Math.max(0, radius);
        roundRect(graphics, x - 2, y + 1, width + 4, height + 3, r + 2, UiTheme.SHADOW_3);
        roundRect(graphics, x - 1, y + 1, width + 2, height + 2, r + 1, UiTheme.SHADOW_2);
        roundRect(graphics, x, y + 1, width, height + 1, r, UiTheme.SHADOW_1);
    }

    /**
     * 标准玻璃卡片：外阴影 + 渐变底 + 发丝描边 + 顶部内高光 + 底部压暗。
     */
    public static void card(GuiGraphics graphics, int x, int y, int width, int height) {
        card(graphics, x, y, width, height, UiTheme.RADIUS_CARD, UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
    }

    /** 自定义底色/圆角的卡片。 */
    public static void card(GuiGraphics graphics, int x, int y, int width, int height,
                            int radius, int topColor, int bottomColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        shadow(graphics, x, y, width, height, radius);
        roundRectGradient(graphics, x, y, width, height, radius, topColor, bottomColor);
        strokeRoundRect(graphics, x, y, width, height, radius, UiTheme.HAIRLINE);
        // 顶部内高光：贴着上边缘的一条 1px 亮线（左右各收掉圆角）。
        hairline(graphics, x + radius, y, Math.max(0, width - radius * 2), UiTheme.SHEEN);
        // 底部压暗：让卡片从背景中"沉"下去。
        hairline(graphics, x + radius, y + height - 1, Math.max(0, width - radius * 2),
            UiTheme.HAIRLINE_DARK);
    }

    /** 内凹容器（列表底、进度槽、输入框）。 */
    public static void sunken(GuiGraphics graphics, int x, int y, int width, int height, int radius) {
        roundRect(graphics, x, y, width, height, radius, UiTheme.SUNKEN);
        strokeRoundRect(graphics, x, y, width, height, radius, UiTheme.withAlpha(0xFF000000, 0x50));
        hairline(graphics, x + radius, y + 1, Math.max(0, width - radius * 2), 0x0A000000);
    }

    /** 1px 水平细线（两端可渐隐）。 */
    public static void hairline(GuiGraphics graphics, int x, int y, int width, int color) {
        if (!UiTheme.visible(color) || width <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + 1, color);
    }

    /** 水平分割线：中间实、两端渐隐。 */
    public static void divider(GuiGraphics graphics, int x, int y, int width) {
        if (width <= 0) {
            return;
        }
        int inset = Math.min(12, width / 4);
        graphics.fill(x + inset, y, x + width - inset, y + 1, UiTheme.HAIRLINE);
        graphics.fill(x + inset, y, x + inset * 2, y + 1, UiTheme.withAlpha(UiTheme.HAIRLINE, 0x08));
        graphics.fill(x + width - inset * 2, y, x + width - inset, y + 1,
            UiTheme.withAlpha(UiTheme.HAIRLINE, 0x08));
    }

    /** 竖直分割线。 */
    public static void dividerV(GuiGraphics graphics, int x, int y, int height) {
        if (height <= 0) {
            return;
        }
        int inset = Math.min(10, height / 4);
        graphics.fill(x, y + inset, x + 1, y + height - inset, UiTheme.HAIRLINE);
    }

    /** 强调色条：卡片左侧 2px 竖条，上下渐隐。 */
    public static void accentBarLeft(GuiGraphics graphics, int x, int y, int height, int accent) {
        if (height <= 0) {
            return;
        }
        int inset = Math.min(4, height / 3);
        graphics.fill(x, y + inset, x + 2, y + height - inset, accent);
        graphics.fill(x, y, x + 2, y + inset, UiTheme.alpha(accent, 0.35f));
        graphics.fill(x, y + height - inset, x + 2, y + height, UiTheme.alpha(accent, 0.35f));
    }

    /** 强调色条：卡片底部 1px 横线，左右渐隐。 */
    public static void accentBarBottom(GuiGraphics graphics, int x, int y, int width, int accent) {
        if (width <= 0) {
            return;
        }
        int inset = Math.min(16, width / 4);
        graphics.fill(x + inset, y, x + width - inset, y + 1, accent);
        graphics.fill(x + inset / 2, y, x + inset, y + 1, UiTheme.alpha(accent, 0.4f));
        graphics.fill(x + width - inset, y, x + width - inset / 2, y + 1, UiTheme.alpha(accent, 0.4f));
    }

    /** 胶囊底（圆角 = 高度一半）。 */
    public static void pill(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        roundRect(graphics, x, y, width, height, Math.max(1, height / 2), color);
    }

    /** 胶囊描边。 */
    public static void pillOutline(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        strokeRoundRect(graphics, x, y, width, height, Math.max(1, height / 2), color);
    }

    /**
     * 进度条：内凹槽 + 渐变填充 + 顶部高光。
     *
     * @param progress 0..1
     */
    public static void progress(GuiGraphics graphics, int x, int y, int width, int height,
                                float progress, int fillColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int radius = Math.max(1, height / 2);
        sunken(graphics, x, y, width, height, radius);
        float clamped = Math.max(0f, Math.min(1f, progress));
        int filled = Math.round(width * clamped);
        if (filled <= 0) {
            return;
        }
        int top = UiTheme.lerp(fillColor, 0xFFFFFFFF, 0.18f);
        int bottom = UiTheme.lerp(fillColor, 0xFF000000, 0.22f);
        roundRectGradient(graphics, x, y, filled, height, radius, top, bottom);
        if (filled > radius * 2 + 2 && height >= 4) {
            hairline(graphics, x + radius, y + 1, filled - radius * 2, 0x22FFFFFF);
        }
    }

    /** 发光外圈：选中态使用，两层低 alpha 圆角叠加。 */
    public static void glow(GuiGraphics graphics, int x, int y, int width, int height,
                            int radius, int accent) {
        strokeRoundRect(graphics, x - 1, y - 1, width + 2, height + 2, radius + 1,
            UiTheme.alpha(accent, 0.30f));
        strokeRoundRect(graphics, x - 2, y - 2, width + 4, height + 4, radius + 2,
            UiTheme.alpha(accent, 0.12f));
    }

    // ------------------------------------------------------------------
    // 屏幕级背景
    // ------------------------------------------------------------------

    /** 全屏渐变底（深色玻璃）。 */
    public static void backdrop(GuiGraphics graphics, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.fillGradient(0, 0, width, height, UiTheme.BACKDROP_TOP, UiTheme.BACKDROP_BOTTOM);
    }

    /** 屏幕暗角：四边向内的黑色渐变，让中央内容更聚焦。 */
    public static void vignette(GuiGraphics graphics, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int bandX = Math.max(24, width / 5);
        int bandY = Math.max(20, height / 6);
        // 左
        for (int i = 0; i < bandX; i += 2) {
            float t = 1f - i / (float) bandX;
            graphics.fill(i, 0, Math.min(i + 2, width), height, UiTheme.withAlpha(0xFF000000,
                Math.round(26 * t)));
        }
        // 右
        for (int i = 0; i < bandX; i += 2) {
            float t = 1f - i / (float) bandX;
            int x = width - i - 2;
            if (x < 0) {
                break;
            }
            graphics.fill(x, 0, x + 2, height, UiTheme.withAlpha(0xFF000000, Math.round(26 * t)));
        }
        // 上
        for (int i = 0; i < bandY; i += 2) {
            float t = 1f - i / (float) bandY;
            graphics.fill(0, i, width, Math.min(i + 2, height),
                UiTheme.withAlpha(0xFF000000, Math.round(30 * t)));
        }
        // 下
        for (int i = 0; i < bandY; i += 2) {
            float t = 1f - i / (float) bandY;
            int y = height - i - 2;
            if (y < 0) {
                break;
            }
            graphics.fill(0, y, width, y + 2, UiTheme.withAlpha(0xFF000000, Math.round(30 * t)));
        }
    }

    /** 全屏颗粒（单次拉伸 blit，成本固定）。 */
    public static void grain(GuiGraphics graphics, int width, int height, float alpha) {
        if (width <= 0 || height <= 0 || alpha <= 0.01f) {
            return;
        }
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        graphics.setColor(1f, 1f, 1f, a / 255f);
        graphics.blit(NOISE, 0, 0, width, height, 0f, 0f, 256, 256, 256, 256);
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    /** 阶段界面统一底：渐变 + 暗角 + 颗粒。 */
    public static void screenBackdrop(GuiGraphics graphics, int width, int height) {
        backdrop(graphics, width, height);
        vignette(graphics, width, height);
        grain(graphics, width, height, 0.05f);
    }

    // ------------------------------------------------------------------
    // 文本
    // ------------------------------------------------------------------

    /** 带阴影的文本（现代 UI 的"重量感"来自阴影而非粗体）。 */
    public static void text(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, Component.literal(value), x, y, color, false);
    }

    /** 带投影的文本。 */
    public static void textShadowed(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, Component.literal(value), x, y, color, true);
    }

    /** 缩放文本（左上角锚点）。 */
    public static void textScaled(GuiGraphics graphics, String value, int x, int y, int color,
                                  float scale, boolean shadow) {
        if (scale == 1f) {
            graphics.drawString(Minecraft.getInstance().font, Component.literal(value), x, y, color, shadow);
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1f);
        graphics.drawString(Minecraft.getInstance().font, Component.literal(value),
            Math.round(x / scale), Math.round(y / scale), color, shadow);
        graphics.pose().popPose();
    }

    /** 居中缩放文本（相对给定矩形）。 */
    public static void textCentered(GuiGraphics graphics, String value, int x, int y, int width,
                                    int color, float scale, boolean shadow) {
        int textWidth = Math.round(Minecraft.getInstance().font.width(value) * scale);
        textScaled(graphics, value, x + (width - textWidth) / 2, y, color, scale, shadow);
    }

    /** 带字距的文本（仅用于全大写拉丁微标签；中日韩字符请用普通文本）。 */
    public static void textSpaced(GuiGraphics graphics, String value, int x, int y, int color,
                                  float scale, int spacing, boolean shadow) {
        var font = Minecraft.getInstance().font;
        float cursor = x;
        for (int i = 0; i < value.length(); i++) {
            String ch = String.valueOf(value.charAt(i));
            int advance = font.width(ch) + spacing;
            if (scale == 1f) {
                graphics.drawString(font, Component.literal(ch), Math.round(cursor), y, color, shadow);
            } else {
                graphics.pose().pushPose();
                graphics.pose().scale(scale, scale, 1f);
                graphics.drawString(font, Component.literal(ch),
                    Math.round(cursor / scale), Math.round(y / scale), color, shadow);
                graphics.pose().popPose();
            }
            cursor += advance * scale;
        }
    }

    /** 微标签宽度（带字距）。 */
    public static int spacedWidth(String value, int spacing) {
        var font = Minecraft.getInstance().font;
        if (value == null || value.isEmpty()) {
            return 0;
        }
        int total = spacing * (value.length() - 1);
        for (int i = 0; i < value.length(); i++) {
            total += font.width(String.valueOf(value.charAt(i)));
        }
        return total;
    }

    private static int clampRadius(int radius, int width, int height) {
        int max = Math.max(0, Math.min(width, height) / 2);
        return Math.max(0, Math.min(Math.min(MAX_RADIUS, radius), max));
    }
}
