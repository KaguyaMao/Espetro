package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Immediate-mode paint primitives for the Espetro UI — "END-01 · 纸墨工业".
 *
 * <p>上一版（圆角玻璃）已归档：git tag {@code ui/tactical-glass-v1}，
 * 源码快照 {@code docs/archive/tactical-glass/}。
 *
 * <p>这一版把 {@link GuiGraphics#fill} 组织成"印刷工业"的元件：直角/切角面板、发丝线框、
 * 角标（L 形短线）、刻度尺、图纸网格、实心强调条、硬投影，以及带字距的微标签。
 * 方法名与签名尽量与旧版保持一致，逐屏迁移时调用方不需要改。
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
        if (width <= 0 || height <= 0) {
            return;
        }
        if (!UiTheme.visible(topColor) && !UiTheme.visible(bottomColor)) {
            return;
        }
        int r = clampRadius(radius, width, height);
        if (r == 0) {
            graphics.fillGradient(x, y, x + width, y + height, topColor, bottomColor);
            return;
        }
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
        brackets(graphics, x, y, width, height, UiTheme.TICK_LENGTH, color);
    }

    public static void brackets(GuiGraphics graphics, int x, int y, int width, int height,
                                int length, int color) {
        if (!UiTheme.visible(color) || width <= 2 || height <= 2) {
            return;
        }
        int len = Math.max(2, Math.min(Math.min(length, width / 2), height / 2));
        // 左上
        graphics.fill(x, y, x + len, y + 1, color);
        graphics.fill(x, y, x + 1, y + len, color);
        // 右上
        graphics.fill(x + width - len, y, x + width, y + 1, color);
        graphics.fill(x + width - 1, y, x + width, y + len, color);
        // 左下
        graphics.fill(x, y + height - 1, x + len, y + height, color);
        graphics.fill(x, y + height - len, x + 1, y + height, color);
        // 右下
        graphics.fill(x + width - len, y + height - 1, x + width, y + height, color);
        graphics.fill(x + width - 1, y + height - len, x + width, y + height, color);
    }

    /** 水平刻度尺（每 step 一格，可指定短线长度）。 */
    public static void ticksH(GuiGraphics graphics, int x, int y, int width, int step, int length,
                              int color) {
        if (!UiTheme.visible(color) || width <= 0 || step <= 0) {
            return;
        }
        for (int i = 0; i <= width; i += step) {
            graphics.fill(x + i, y, x + Math.min(i + 1, width), y + Math.max(1, length), color);
        }
    }

    /** 竖直刻度尺。 */
    public static void ticksV(GuiGraphics graphics, int x, int y, int height, int step, int length,
                              int color) {
        if (!UiTheme.visible(color) || height <= 0 || step <= 0) {
            return;
        }
        for (int i = 0; i <= height; i += step) {
            graphics.fill(x, y + i, x + Math.max(1, length), y + Math.min(i + 1, height), color);
        }
    }

    /** 图纸网格：minor 每格、major 每 5 格一条更实的线。 */
    public static void grid(GuiGraphics graphics, int x, int y, int width, int height, int step) {
        if (width <= 0 || height <= 0 || step <= 0) {
            return;
        }
        int i = 0;
        for (int gx = x; gx < x + width; gx += step, i++) {
            int color = (i % 5 == 0) ? UiTheme.GRID_MAJOR : UiTheme.GRID;
            graphics.fill(gx, y, Math.min(gx + 1, x + width), y + height, color);
        }
        i = 0;
        for (int gy = y; gy < y + height; gy += step, i++) {
            int color = (i % 5 == 0) ? UiTheme.GRID_MAJOR : UiTheme.GRID;
            graphics.fill(x, gy, x + width, Math.min(gy + 1, y + height), color);
        }
    }

    /** 45° 斜线填充（"禁用 / 不可用"区域的工业语汇）。 */
    public static void hatch(GuiGraphics graphics, int x, int y, int width, int height,
                             int step, int color) {
        if (!UiTheme.visible(color) || width <= 0 || height <= 0 || step <= 0) {
            return;
        }
        int span = width + height;
        for (int i = -height; i < span; i += step) {
            for (int row = 0; row < height; row++) {
                int px = i + row;
                if (px >= 0 && px < width) {
                    graphics.fill(x + px, y + row, x + px + 1, y + row + 1, color);
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // 组合部件
    // ------------------------------------------------------------------

    /** 硬投影：向下 2px 的两层直角块，不做模糊。 */
    public static void shadow(GuiGraphics graphics, int x, int y, int width, int height, int radius) {
        if (width <= 0 || height <= 0) {
            return;
        }
        roundRect(graphics, x + 1, y + 2, width, height, radius, UiTheme.SHADOW_1);
        roundRect(graphics, x, y + 1, width, height, radius, UiTheme.SHADOW_3);
    }

    /** 标准纸卡片：硬投影 + 纸面 + 1px 发丝框 + 顶部高光。 */
    public static void card(GuiGraphics graphics, int x, int y, int width, int height) {
        card(graphics, x, y, width, height, UiTheme.RADIUS_CARD, UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
    }

    /** 自定义底色/切角的卡片。 */
    public static void card(GuiGraphics graphics, int x, int y, int width, int height,
                            int radius, int topColor, int bottomColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        shadow(graphics, x, y, width, height, radius);
        // 暗色系下卡片是"实色块"，不做玻璃渐变、不留顶部高光：
        // 半透明渐变压在深底上会被看成一层白雾（用户明确要求去掉这种玻璃感）。
        roundRect(graphics, x, y, width, height, radius, topColor);
        strokeRoundRect(graphics, x, y, width, height, radius, UiTheme.HAIRLINE);
        hairline(graphics, x + radius, y + height - 1, Math.max(0, width - radius * 2),
            UiTheme.HAIRLINE_DARK);
    }

    /** 带强调条的卡片：左侧实心信号黄竖条 + 右上角标。 */
    public static void accentCard(GuiGraphics graphics, int x, int y, int width, int height,
                                  int accent) {
        card(graphics, x, y, width, height);
        graphics.fill(x, y + 1, x + UiTheme.ACCENT_BAR, y + height - 1, accent);
        brackets(graphics, x + width - 9, y - 5, 8, 8, 4, UiTheme.alpha(accent, 0.85f));
    }

    /** 内凹容器（列表底、输入框、进度槽、表格体）。 */
    public static void sunken(GuiGraphics graphics, int x, int y, int width, int height, int radius) {
        roundRect(graphics, x, y, width, height, radius, UiTheme.SUNKEN);
        strokeRoundRect(graphics, x, y, width, height, radius, UiTheme.HAIRLINE);
        hairline(graphics, x, y, width, 0x14000000);
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
        if (width <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + 1, UiTheme.HAIRLINE);
        graphics.fill(x, y, x + 1, y + 5, UiTheme.TICK);
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
        strokeRoundRect(graphics, x, y, width, height, UiTheme.RADIUS_CONTROL, color);
    }

    /**
     * 进度条：内凹槽 + 实心信号色 + 每 10% 一格刻度 + 右端 2px 端头。
     *
     * @param progress 0..1
     */
    public static void progress(GuiGraphics graphics, int x, int y, int width, int height,
                                float progress, int fillColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        sunken(graphics, x, y, width, height, UiTheme.RADIUS_INNER);
        float clamped = Math.max(0f, Math.min(1f, progress));
        int filled = Math.round(width * clamped);
        if (filled <= 0) {
            return;
        }
        int innerY = y + 2;
        int innerH = Math.max(1, height - 4);
        graphics.fill(x + 2, innerY, x + Math.max(2, filled - 1), innerY + innerH, fillColor);
        if (filled > 4) {
            graphics.fill(x + Math.max(2, filled - 1), innerY, x + filled, innerY + innerH,
                UiTheme.lerp(fillColor, UiTheme.INK_FILL, 0.45f));
        }
        // 刻度：每 10% 一条 1px 的纸色隔断，让进度"可读数"。
        int step = Math.max(6, width / 10);
        for (int i = step; i < width - 2; i += step) {
            graphics.fill(x + i, innerY, x + i + 1, innerY + innerH, UiTheme.withAlpha(0xFF000000, 0x22));
        }
    }

    /** 选中框：1px 信号黄实线 + 外一圈更淡的黄，替代旧版的"发光"。 */
    public static void glow(GuiGraphics graphics, int x, int y, int width, int height,
                            int radius, int accent) {
        strokeRoundRect(graphics, x, y, width, height, radius, accent);
        strokeRoundRect(graphics, x - 2, y - 2, width + 4, height + 4, radius,
            UiTheme.alpha(accent, 0.22f));
    }

    // ------------------------------------------------------------------
    // 屏幕级背景
    // ------------------------------------------------------------------

    /** 全屏纸底：纸色渐变 + 极淡网格。 */
    public static void backdrop(GuiGraphics graphics, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.fillGradient(0, 0, width, height, UiTheme.BACKDROP_TOP, UiTheme.BACKDROP_BOTTOM);
        grid(graphics, 0, 0, width, height, 24);
    }

    /** 屏幕暗角：四边向内轻微压暗（编辑风的"纸张边缘阴影"，很轻）。 */
    public static void vignette(GuiGraphics graphics, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int bandX = Math.max(20, width / 7);
        int bandY = Math.max(16, height / 8);
        for (int i = 0; i < bandX; i += 2) {
            float t = 1f - i / (float) bandX;
            int a = Math.round(14 * t);
            graphics.fill(i, 0, Math.min(i + 2, width), height, UiTheme.withAlpha(0xFF101110, a));
            int x = width - i - 2;
            if (x >= 0) {
                graphics.fill(x, 0, x + 2, height, UiTheme.withAlpha(0xFF101110, a));
            }
        }
        for (int i = 0; i < bandY; i += 2) {
            float t = 1f - i / (float) bandY;
            int a = Math.round(16 * t);
            graphics.fill(0, i, width, Math.min(i + 2, height), UiTheme.withAlpha(0xFF101110, a));
            int y = height - i - 2;
            if (y >= 0) {
                graphics.fill(0, y, width, y + 2, UiTheme.withAlpha(0xFF101110, a));
            }
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

    /** 阶段界面统一底：纸底 + 网格 + 边缘压暗 + 纸纤维。 */
    public static void screenBackdrop(GuiGraphics graphics, int width, int height) {
        backdrop(graphics, width, height);
        vignette(graphics, width, height);
        grain(graphics, width, height, 0.035f);
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
        return value;
    }

    /** 普通文本（纸上不用阴影，保持印刷的干净边缘）。 */
    public static void text(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, Component.literal(retone(value)), x, y, color, false);
    }

    /** 带投影的文本（仅用于压在墨块/图片上的文字）。 */
    public static void textShadowed(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, Component.literal(retone(value)), x, y, color, true);
    }

    /** 缩放文本（左上角锚点）。 */
    public static void textScaled(GuiGraphics graphics, String value, int x, int y, int color,
                                  float scale, boolean shadow) {
        String text = retone(value);
        if (scale == 1f) {
            graphics.drawString(Minecraft.getInstance().font, Component.literal(text), x, y, color, shadow);
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1f);
        graphics.drawString(Minecraft.getInstance().font, Component.literal(text),
            Math.round(x / scale), Math.round(y / scale), color, shadow);
        graphics.pose().popPose();
    }

    /** 居中缩放文本（相对给定矩形）。 */
    public static void textCentered(GuiGraphics graphics, String value, int x, int y, int width,
                                    int color, float scale, boolean shadow) {
        int textWidth = Math.round(Minecraft.getInstance().font.width(value) * scale);
        textScaled(graphics, value, x + (width - textWidth) / 2, y, color, scale, shadow);
    }

    /** 右对齐文本。 */
    public static void textRight(GuiGraphics graphics, String value, int rightX, int y, int color,
                                 float scale) {
        int textWidth = Math.round(Minecraft.getInstance().font.width(value) * scale);
        textScaled(graphics, value, rightX - textWidth, y, color, scale, false);
    }

    /** 带字距的文本（全大写拉丁微标签；中日韩字符请用普通文本）。 */
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
