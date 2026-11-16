package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

/**
 * Immediate-mode primitives for Espetro's minimal translucent glass UI.
 *
 * <p>Colors come from {@link UiTheme}. Legacy decorative entry points retain their
 * signatures but draw nothing; functional geometry and text retain their behavior.
 * Glass surfaces use the shared blur bridge before foreground content is drawn.
 */
public final class UiPaint {

    private UiPaint() {
    }

    /** 颗粒纹理（256×256 RGBA，透明度很低；纸的纤维感）。 */
    public static final ResourceLocation NOISE =
        new ResourceLocation("espetro", "textures/gui/ui/noise.png");

    private static final int MAX_RADIUS = 24;
    /** 缓存每个切角尺寸的逐行内缩量（45° 直线切角，不是圆弧）。 */
    private static final int[][] INSETS = new int[MAX_RADIUS + 1][];

    private static int[] insets(int radius) {
        int r = Math.max(0, Math.min(MAX_RADIUS, radius));
        int[] cached = INSETS[r];
        if (cached != null) {
            return cached;
        }
        // 45° 切角：第 row 行内缩 r - (row + 1)，形成一条斜边。
        int[] table = new int[r + 1];
        for (int row = 0; row <= r; row++) {
            table[row] = r - row;
        }
        INSETS[r] = table;
        return table;
    }

    /** 某一行在切角形状内的水平内缩量。 */
    private static int insetAt(int row, int height, int radius, int[] table) {
        int r = Math.max(0, Math.min(MAX_RADIUS, radius));
        if (r == 0) {
            return 0;
        }
        if (row < r) {
            return Math.max(0, table[row] - 1);
        }
        if (row >= height - r) {
            int mirrored = height - 1 - row;
            return mirrored >= 0 && mirrored < table.length ? Math.max(0, table[mirrored] - 1) : 0;
        }
        return 0;
    }

    // ------------------------------------------------------------------
    // 基础形状
    // ------------------------------------------------------------------

    /** 直角（或 45° 切角，radius > 0）实心填充。 */
    public static void roundRect(GuiGraphics graphics, int x, int y, int width, int height,
                                 int radius, int color) {
        if (!UiTheme.visible(color) || width <= 0 || height <= 0) {
            return;
        }
        int r = clampRadius(radius, width, height);
        if (r == 0) {
            graphics.fill(x, y, x + width, y + height, color);
            return;
        }
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

    /** 竖向渐变实心填充（纸面微差，或墨块的从深到更深）。 */
    public static void roundRectGradient(GuiGraphics graphics, int x, int y, int width, int height,
                                         int radius, int topColor, int bottomColor) {
        roundRect(graphics, x, y, width, height, radius, topColor);
    }

    /** 1px 描边（直角或切角）。 */
    public static void strokeRoundRect(GuiGraphics graphics, int x, int y, int width, int height,
                                       int radius, int color) {
        if (!UiTheme.visible(color) || width <= 0 || height <= 0) {
            return;
        }
        int r = clampRadius(radius, width, height);
        if (r == 0) {
            graphics.fill(x, y, x + width, y + 1, color);
            graphics.fill(x, y + height - 1, x + width, y + height, color);
            graphics.fill(x, y + 1, x + 1, y + height - 1, color);
            graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
            return;
        }
        int[] table = insets(r);
        for (int row = 0; row < height; row++) {
            int inset = insetAt(row, height, r, table);
            int left = x + inset;
            int right = x + width - inset;
            if (right <= left) {
                continue;
            }
            if (row < r || row >= height - r) {
                graphics.fill(left, y + row, right, y + row + 1, color);
            } else {
                graphics.fill(left, y + row, left + 1, y + row + 1, color);
                graphics.fill(right - 1, y + row, right, y + row + 1, color);
            }
        }
    }

    /** 圆形实心（轮盘中心、状态点、图标底）。 */
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

    /** 1px 矩形框（最常用的"技术图纸"框）。 */
    public static void frame(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        if (!UiTheme.visible(color) || width <= 0 || height <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        if (height > 2) {
            graphics.fill(x, y + 1, x + 1, y + height - 1, color);
            graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
        }
    }

    /** 四角 L 形角标：画在矩形外侧，强调"这是一个被框定的区域"。 */
    public static void brackets(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        // Retained for existing callers; this skin does not draw corner brackets.
    }

    public static void brackets(GuiGraphics graphics, int x, int y, int width, int height,
                                int length, int color) {
        // Retained for existing callers; this skin does not draw corner brackets.
    }

    /** 水平刻度尺（每 step 一格，可指定短线长度）。 */
    public static void ticksH(GuiGraphics graphics, int x, int y, int width, int step, int length,
                              int color) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    /** 竖直刻度尺。 */
    public static void ticksV(GuiGraphics graphics, int x, int y, int height, int step, int length,
                              int color) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    /** 图纸网格：minor 每格、major 每 5 格一条更实的线。 */
    public static void grid(GuiGraphics graphics, int x, int y, int width, int height, int step) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    /** 45° 斜线填充（"禁用 / 不可用"区域的工业语汇）。 */
    public static void hatch(GuiGraphics graphics, int x, int y, int width, int height,
                             int step, int color) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    // ------------------------------------------------------------------
    // 组合部件
    // ------------------------------------------------------------------

    /** 硬投影：向下 2px 的两层直角块，不做模糊。 */
    public static void shadow(GuiGraphics graphics, int x, int y, int width, int height, int radius) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    /** 标准纸卡片：硬投影 + 纸面 + 1px 发丝框 + 顶部高光。 */
    public static void card(GuiGraphics graphics, int x, int y, int width, int height) {
        card(graphics, x, y, width, height, UiTheme.RADIUS_CARD, UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
    }

    /** Glass for a HUD region, or a translucent plate over the screen's shared blur. */
    public static void glassPanel(GuiGraphics graphics, int x, int y, int width, int height, int tint) {
        if (graphics == null || width <= 0 || height <= 0 || !UiTheme.visible(tint)) return;
        int color = UiTheme.glassSurface(tint);
        if (!FrostedBackdrop.isScreenLayerActive()
            && FancyMenuGlass.draw(graphics, x, y, width, height,
                UiTheme.GLASS_BLUR_RADIUS, color, 0.0F)) {
            return;
        }
        graphics.fill(x, y, x + width, y + height, color);
    }

    /** 自定义底色/切角的卡片。 */
    public static void card(GuiGraphics graphics, int x, int y, int width, int height,
                            int radius, int topColor, int bottomColor) {
        glassPanel(graphics, x, y, width, height, topColor);
    }

    /** 带强调条的卡片：左侧实心信号黄竖条 + 右上角标。 */
    public static void accentCard(GuiGraphics graphics, int x, int y, int width, int height,
                                  int accent) {
        card(graphics, x, y, width, height);
    }

    /** 内凹容器（列表底、输入框、进度槽、表格体）。 */
    public static void sunken(GuiGraphics graphics, int x, int y, int width, int height, int radius) {
        glassPanel(graphics, x, y, width, height, UiTheme.SUNKEN);
    }

    /** 1px 水平细线。 */
    public static void hairline(GuiGraphics graphics, int x, int y, int width, int color) {
        if (!UiTheme.visible(color) || width <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + 1, color);
    }

    /** 1px 竖直细线。 */
    public static void hairlineV(GuiGraphics graphics, int x, int y, int height, int color) {
        if (!UiTheme.visible(color) || height <= 0) {
            return;
        }
        graphics.fill(x, y, x + 1, y + height, color);
    }

    /** 水平分割线：一条实线 + 一段末端刻度，编辑风的"分栏"。 */
    public static void divider(GuiGraphics graphics, int x, int y, int width) {
        hairline(graphics, x, y, width, UiTheme.HAIRLINE);
    }

    /** 竖直分割线。 */
    public static void dividerV(GuiGraphics graphics, int x, int y, int height) {
        if (height <= 0) {
            return;
        }
        graphics.fill(x, y, x + 1, y + height, UiTheme.HAIRLINE);
    }

    /** 强调条：卡片左侧竖条。 */
    public static void accentBarLeft(GuiGraphics graphics, int x, int y, int height, int accent) {
        if (height <= 0) {
            return;
        }
        graphics.fill(x, y, x + UiTheme.ACCENT_BAR, y + height, accent);
    }

    /** 强调条：卡片底部横线。 */
    public static void accentBarBottom(GuiGraphics graphics, int x, int y, int width, int accent) {
        if (width <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + 2, accent);
    }

    /** 标签底（直角实心块，或描边框）。 */
    public static void pill(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        roundRect(graphics, x, y, width, height, UiTheme.RADIUS_CONTROL, color);
    }

    /** 标签描边。 */
    public static void pillOutline(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        roundRect(graphics, x, y, width, height, UiTheme.RADIUS_CONTROL, UiTheme.alpha(color, 0.12f));
    }

    /**
     * 进度条：内凹槽 + 实心信号色 + 每 10% 一格刻度 + 右端 2px 端头。
     *
     * @param progress 0..1
     */
    public static void progress(GuiGraphics graphics, int x, int y, int width, int height,
                                float progress, int fillColor) {
        if (width <= 0 || height <= 0) return;
        roundRect(graphics, x, y, width, height, UiTheme.RADIUS_INNER, UiTheme.SUNKEN);
        int filled = Math.round(width * Math.max(0f, Math.min(1f, progress)));
        if (filled > 0) {
            graphics.fill(x, y, x + filled, y + height, fillColor);
        }
    }

    /** 选中框：1px 信号黄实线 + 外一圈更淡的黄，替代旧版的"发光"。 */
    public static void glow(GuiGraphics graphics, int x, int y, int width, int height,
                            int radius, int accent) {
        roundRect(graphics, x, y, width, height, radius, UiTheme.alpha(accent, 0.12f));
    }

    // ------------------------------------------------------------------
    // 屏幕级背景
    // ------------------------------------------------------------------

    /** 全屏纸底：纸色渐变 + 极淡网格。 */
    public static void backdrop(GuiGraphics graphics, int width, int height) {
        FrostedBackdrop.draw(graphics, width, height);
    }

    /** 屏幕暗角：四边向内轻微压暗（编辑风的"纸张边缘阴影"，很轻）。 */
    public static void vignette(GuiGraphics graphics, int width, int height) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    /** 全屏颗粒（单次拉伸 blit，成本固定）。 */
    public static void grain(GuiGraphics graphics, int width, int height, float alpha) {
        // Decorative industrial chrome is intentionally absent in the minimal glass skin.
    }

    /** 阶段界面统一底：纸底 + 网格 + 边缘压暗 + 纸纤维。 */
    public static void screenBackdrop(GuiGraphics graphics, int width, int height) {
        backdrop(graphics, width, height);
    }

    // ------------------------------------------------------------------
    // 文本
    // ------------------------------------------------------------------

    /**
     * 文案色码重映射钩子。暗色系下 Minecraft 的原生色码（{@code §f} 白、{@code §7} 灰、
     * {@code §e} 黄、{@code §c} 红…）本身就适合深底，因此当前是恒等映射；保留这个入口，
     * 是为了将来若再换浅色主题时，只需在这里改一张映射表而不用逐屏改文案。
     */
    public static String retone(String value) {
        return value == null ? "" : value.replaceAll("(?i)\u00a7[0-9a-f]", "");
    }

    /** Resolve legacy colors into the active palette; preserve emphasis and resets. */
    public static Component themed(String value, int baseColor) {
        baseColor = UiTheme.readableText(baseColor);
        MutableComponent result = Component.empty();
        Style style = Style.EMPTY.withColor(baseColor & 0x00FFFFFF);
        String text = value == null ? "" : value;
        int start = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) != '\u00a7') continue;
            ChatFormatting format = ChatFormatting.getByCode(text.charAt(i + 1));
            if (format == null) continue;
            if (i > start) result.append(Component.literal(text.substring(start, i)).setStyle(style));
            if (format.isColor()) {
                style = Style.EMPTY.withColor(legacyColor(Character.toLowerCase(text.charAt(i + 1)), baseColor)
                    & 0x00FFFFFF);
            } else if (format == ChatFormatting.RESET) {
                style = Style.EMPTY.withColor(baseColor & 0x00FFFFFF);
            } else {
                style = style.applyFormat(format);
            }
            i++;
            start = i + 1;
        }
        if (start < text.length()) result.append(Component.literal(text.substring(start)).setStyle(style));
        return result;
    }

    private static int legacyColor(char code, int baseColor) {
        return switch (code) {
            case '0', '8' -> UiTheme.TEXT_THIRD;
            case '1', '3', '9', 'b' -> UiTheme.DEFEND;
            case '2', 'a' -> UiTheme.POSITIVE;
            case '4', 'c' -> UiTheme.NEGATIVE;
            case '5', 'd' -> UiTheme.PURPLE;
            case '6', 'e' -> UiTheme.GOLD;
            case '7' -> UiTheme.TEXT_SECOND;
            default -> baseColor;
        };
    }

    /** Tint monochrome texture masks with a palette color. Reset to white after blitting. */
    public static void tint(GuiGraphics graphics, int color) {
        graphics.setColor(((color >>> 16) & 0xFF) / 255f, ((color >>> 8) & 0xFF) / 255f,
            (color & 0xFF) / 255f, ((color >>> 24) & 0xFF) / 255f);
    }

    /** 普通文本（纸上不用阴影，保持印刷的干净边缘）。 */
    public static void text(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, themed(value, color), x, y, color, false);
    }

    /** 带投影的文本（仅用于压在墨块/图片上的文字）。 */
    public static void textShadowed(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, themed(value, color), x, y, color, true);
    }

    /** 缩放文本（左上角锚点）。 */
    public static void textScaled(GuiGraphics graphics, String value, int x, int y, int color,
                                  float scale, boolean shadow) {
        Component text = themed(value, color);
        if (scale == 1f) {
            graphics.drawString(Minecraft.getInstance().font, text, x, y, color, shadow);
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1f);
        graphics.drawString(Minecraft.getInstance().font, text,
            Math.round(x / scale), Math.round(y / scale), color, shadow);
        graphics.pose().popPose();
    }

    /** 居中缩放文本（相对给定矩形）。 */
    public static void textCentered(GuiGraphics graphics, String value, int x, int y, int width,
                                    int color, float scale, boolean shadow) {
        int textWidth = Math.round(Minecraft.getInstance().font.width(themed(value, color)) * scale);
        textScaled(graphics, value, x + (width - textWidth) / 2, y, color, scale, shadow);
    }

    /** 右对齐文本。 */
    public static void textRight(GuiGraphics graphics, String value, int rightX, int y, int color,
                                 float scale) {
        int textWidth = Math.round(Minecraft.getInstance().font.width(themed(value, color)) * scale);
        textScaled(graphics, value, rightX - textWidth, y, color, scale, false);
    }

    /** 带字距的文本（全大写拉丁微标签；中日韩字符请用普通文本）。 */
    public static void textSpaced(GuiGraphics graphics, String value, int x, int y, int color,
                                  float scale, int spacing, boolean shadow) {
        var font = Minecraft.getInstance().font;
        float[] cursor = {x};
        themed(value, color).visit((style, part) -> {
            for (int offset = 0; offset < part.length();) {
                int codePoint = part.codePointAt(offset);
                offset += Character.charCount(codePoint);
                Component glyph = Component.literal(new String(Character.toChars(codePoint))).setStyle(style);
                if (scale == 1f) {
                    graphics.drawString(font, glyph, Math.round(cursor[0]), y, color, shadow);
                } else {
                    graphics.pose().pushPose();
                    graphics.pose().scale(scale, scale, 1f);
                    graphics.drawString(font, glyph,
                        Math.round(cursor[0] / scale), Math.round(y / scale), color, shadow);
                    graphics.pose().popPose();
                }
                cursor[0] += (font.width(glyph) + spacing) * scale;
            }
            return java.util.Optional.empty();
        }, Style.EMPTY);
    }

    /** 微标签：小号 + 字距 + 可选的左侧 2px 信号色方块。 */
    public static void label(GuiGraphics graphics, String value, int x, int y, int color) {
        textSpaced(graphics, value, x, y, color, UiTheme.TYPE_MICRO, UiTheme.LETTERSPACING_MICRO, false);
    }

    /** 微标签宽度（带字距）。 */
    public static int spacedWidth(String value, int spacing) {
        var font = Minecraft.getInstance().font;
        if (value == null || value.isEmpty()) {
            return 0;
        }
        String plain = value.replaceAll("(?i)\u00a7[0-9a-fk-or]", "");
        int count = plain.codePointCount(0, plain.length());
        return font.width(themed(value, UiTheme.TEXT)) + spacing * Math.max(0, count - 1);
    }

    private static int clampRadius(int radius, int width, int height) {
        int max = Math.max(0, Math.min(width, height) / 2);
        return Math.max(0, Math.min(Math.min(MAX_RADIUS, radius), max));
    }
}
