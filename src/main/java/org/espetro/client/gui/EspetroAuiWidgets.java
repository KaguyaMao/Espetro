package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.aui.GuiRect;
import org.espetro.team.TeamDisplayNames;

import java.util.regex.Pattern;

/**
 * 阶段界面 / 菜单共用的即时绘制控件库（"END-02" 亮暗工业风格）。
 *
 * <p>公开 API 与旧版保持一致（颜色常量、{@code panel/button/text/...} 工厂、
 * {@link PhaseHeader}），因此所有既有界面无需改动即可换肤；内部绘制全部改走
 * {@link UiPaint}：直角、实色、发丝描边、硬投影、微标签。
 */
final class EspetroAuiWidgets {

    // ---- 语义色（保持旧名，数值换成新色板） ----
    /** 全屏静态底：不透明，避免世界/失明层穿透或闪烁。 */
    static int BACKDROP = UiTheme.BACKDROP_TOP;
    static int PANEL = UiTheme.CARD_TOP;
    static int PANEL_SOFT = UiTheme.SURFACE_2;
    static int BORDER = UiTheme.HAIRLINE;
    static int BORDER_ACTIVE = UiTheme.ACCENT;
    static int TEXT = UiTheme.TEXT;
    static int MUTED = UiTheme.TEXT_SECOND;
    static int DIM = UiTheme.TEXT_THIRD;
    static int GOLD = UiTheme.GOLD;
    static int PURPLE = UiTheme.PURPLE;
    static int SQUAD_BLUE = UiTheme.SQUAD;
    static int ATTACK = UiTheme.ATTACK;
    static int DEFEND = UiTheme.DEFEND;
    static int POSITIVE = UiTheme.POSITIVE;
    static int WARNING = UiTheme.WARNING;
    static int NEGATIVE = UiTheme.NEGATIVE;
    static final int PHASE_HEADER_HEIGHT = 44;


    static { UiTheme.addChangeListener(EspetroAuiWidgets::refreshThemeAliases); }

    private static void refreshThemeAliases() {
        BACKDROP = UiTheme.BACKDROP_TOP;
        PANEL = UiTheme.CARD_TOP;
        PANEL_SOFT = UiTheme.SURFACE_2;
        BORDER = UiTheme.HAIRLINE;
        BORDER_ACTIVE = UiTheme.ACCENT;
        TEXT = UiTheme.TEXT;
        MUTED = UiTheme.TEXT_SECOND;
        DIM = UiTheme.TEXT_THIRD;
        GOLD = UiTheme.GOLD;
        PURPLE = UiTheme.PURPLE;
        SQUAD_BLUE = UiTheme.SQUAD;
        ATTACK = UiTheme.ATTACK;
        DEFEND = UiTheme.DEFEND;
        POSITIVE = UiTheme.POSITIVE;
        WARNING = UiTheme.WARNING;
        NEGATIVE = UiTheme.NEGATIVE;
    }

    private static final Pattern FORMAT_CODE = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private EspetroAuiWidgets() {
    }

    static String stripFormatting(String text) {
        return text == null ? "" : FORMAT_CODE.matcher(text).replaceAll("");
    }

    static GuiRect rect(int x, int y, int width, int height, int color) {
        return new GuiRect(x, y, width, height, color);
    }

    /**
     * 阶段 GUI 全屏底：纸色渐变 + 图纸网格 + 边缘压暗 + 纸纤维。
     * 底层不透明度足够高（遮住世界），但保留一点点场景透出，避免"纯色板"的死板。
     */
    static void drawScreenShade(GuiGraphics graphics, int width, int height) {
        if (graphics == null || width <= 0 || height <= 0) {
            return;
        }
        UiPaint.backdrop(graphics, width, height);
        UiPaint.vignette(graphics, width, height);
        UiPaint.grain(graphics, width, height, 0.035f);
        drawChrome(graphics, width, height);
    }

    /**
     * 屏幕外框（工业图纸的"图框"）：细线边框 + 四角 L 形角标 + 边缘刻度 +
     * 左上信号方块与编号、右下版本号。所有阶段界面共用，保证风格是"一张图纸"。
     */
    static void drawChrome(GuiGraphics graphics, int width, int height) {
        int inset = 5;
        int frameColor = UiTheme.withAlpha(UiTheme.TEXT, 0x3A);
        UiPaint.frame(graphics, inset, inset, width - inset * 2, height - inset * 2, frameColor);
        UiPaint.brackets(graphics, inset, inset, width - inset * 2, height - inset * 2, 18,
            UiTheme.withAlpha(UiTheme.TEXT, 0x66));

        // 左上：信号方块 + 编号
        graphics.fill(inset + 5, inset + 5, inset + 12, inset + 12, UiTheme.ACCENT);
        UiPaint.label(graphics, "ESPETRO OPS", inset + 18, inset + 5, UiTheme.TEXT_THIRD);
        // 右上：界面代号
        String code = "END-02 / " + UiTheme.mode().name();
        int codeWidth = UiPaint.spacedWidth(code, UiTheme.LETTERSPACING_MICRO);
        UiPaint.label(graphics, code, width - inset - 8 - codeWidth, inset + 5, UiTheme.TEXT_THIRD);

        // 左右边缘刻度尺（竖向），像图纸的标尺
        int tickTop = inset + 26;
        int tickBottom = height - inset - 26;
        for (int y = tickTop; y < tickBottom; y += 14) {
            int len = ((y - tickTop) / 14) % 5 == 0 ? 7 : 4;
            graphics.fill(inset + 3, y, inset + 3 + len, y + 1, UiTheme.withAlpha(UiTheme.TICK, 0x66));
            graphics.fill(width - inset - 3 - len, y, width - inset - 3, y + 1,
                UiTheme.withAlpha(UiTheme.TICK, 0x66));
        }

        // 左下角：通道号；右下角：版本
        UiPaint.label(graphics, "CH 07 // SECURE", inset + 8, height - inset - 12, UiTheme.TEXT_THIRD);
        String version = versionLabel();
        int versionWidth = UiPaint.spacedWidth(version, UiTheme.LETTERSPACING_MICRO);
        UiPaint.label(graphics, version, width - inset - 8 - versionWidth, height - inset - 12,
            UiTheme.TEXT_THIRD);
    }

    private static String versionLabel() {
        return "v" + net.minecraftforge.fml.ModList.get()
            .getModContainerById("espetro")
            .map(container -> container.getModInfo().getVersion().toString())
            .orElse("dev");
    }

    // ------------------------------------------------------------------
    // 工厂
    // ------------------------------------------------------------------

    static Panel panel(int x, int y, int width, int height) {
        return new Panel(x, y, width, height, PANEL, BORDER, UiTheme.RADIUS_CARD, true);
    }

    static Panel panel(int x, int y, int width, int height, int color, int borderColor) {
        return new Panel(x, y, width, height, color, borderColor, UiTheme.RADIUS_CARD, false);
    }

    /** 标准玻璃卡片（渐变底 + 发丝边 + 外阴影）。 */
    static Panel card(int x, int y, int width, int height) {
        return new Panel(x, y, width, height, 0, 0, UiTheme.RADIUS_CARD, true)
            .setGlass(true);
    }

    /** 左侧带强调色条的卡片。 */
    static Panel cardAccent(int x, int y, int width, int height, int accent) {
        return card(x, y, width, height).setAccentColor(accent);
    }

    /** 内凹容器（列表底、输入槽）。 */
    static Panel sunken(int x, int y, int width, int height) {
        return new Panel(x, y, width, height, UiTheme.SUNKEN, UiTheme.HAIRLINE,
            UiTheme.RADIUS_INNER, false).setSunken(true);
    }

    /** 极简细线（不做元素，直接画）。 */
    static void divider(GuiGraphics graphics, int x, int y, int width) {
        UiPaint.divider(graphics, x, y, width);
    }

    /** 可加入元素树的分割线（两端渐隐）。 */
    static Divider divider(int x, int y, int width) {
        return new Divider(x, y, width, UiTheme.HAIRLINE);
    }

    static void hairline(GuiGraphics graphics, int x, int y, int width, int color) {
        UiPaint.hairline(graphics, x, y, width, color);
    }

    static Text text(int x, int y, String value, int color) {
        return new Text(x, y, 0, value, color, false);
    }

    static Text text(int x, int y, int width, String value, int color) {
        return new Text(x, y, width, value, color, false);
    }

    static Text centeredText(int x, int y, int width, String value, int color) {
        return new Text(x, y, width, value, color, true);
    }

    /** 微标签：小号、带字距、第三级文字色。 */
    static Text microLabel(int x, int y, int width, String value) {
        return new Text(x, y, width, value, UiTheme.TEXT_THIRD, false)
            .setTextScale(UiTheme.TYPE_MICRO)
            .setSpacing(UiTheme.LETTERSPACING_MICRO);
    }

    static TextBlock textBlock(int x, int y, int width, String value, int color) {
        return new TextBlock(x, y, width, value, color);
    }

    static ActionButton button(int x, int y, int width, int height, String label, Runnable action) {
        return new ActionButton(x, y, width, height, label, action);
    }

    static ActionButton textButton(int x, int y, String label, Runnable action) {
        return button(x, y, textButtonWidth(label), 14, label, action);
    }

    /** 主行动按钮：强调色底 + 强调描边。 */
    /** 主行动按钮：墨色实心块 + 纸色字，悬停转为信号黄块。 */
    static ActionButton primaryButton(int x, int y, int width, int height, String label, Runnable action) {
        return new ActionButton(x, y, width, height, label, action)
            .setColors(UiTheme.INK_FILL, UiTheme.ACCENT, UiTheme.ACCENT)
            .setBorderColor(UiTheme.INK_FILL)
            .setAccentColor(UiTheme.ACCENT)
            .setTextColor(UiTheme.ON_INK)
            .setHoverTextColor(UiTheme.TEXT_ON_ACCENT);
    }

    static int textButtonWidth(String label) {
        return Minecraft.getInstance().font.width(stripFormatting(label)) + 16;
    }

    /** 胶囊标签。 */
    static Chip chip(int x, int y, String label, int accent) {
        return new Chip(x, y, label, accent);
    }

    /** 数值块：上方微标签、下方数值。 */
    static Stat stat(int x, int y, int width, String label, String value, int valueColor) {
        return new Stat(x, y, width, label, value, valueColor);
    }

    /** 进度条。 */
    static Bar bar(int x, int y, int width, int height, int fillColor) {
        return new Bar(x, y, width, height, fillColor);
    }

    /** 分区标题：微标签 + 右侧渐隐细线。 */
    static SectionLabel sectionLabel(int x, int y, int width, String label, int accent) {
        return new SectionLabel(x, y, width, label, accent);
    }

    static int teamColor(String team) {
        return "ATTACK".equals(team) ? ATTACK : DEFEND;
    }

    static String teamName(String team) {
        return TeamDisplayNames.displayName(team);
    }

    static String teamPrefix(String team) {
        return TeamDisplayNames.prefix(team);
    }

    // ------------------------------------------------------------------
    // 页头
    // ------------------------------------------------------------------

    /** Adds a three-line phase/status header at the very top of a screen. */
    static int addPhaseHeader(GuiElement root, int screenWidth, String title,
                              String status, String detail, int accentColor) {
        addMutablePhaseHeader(root, screenWidth, title, status, detail, accentColor);
        return PHASE_HEADER_HEIGHT;
    }

    /**
     * Adds a phase header whose strings can be refreshed in place.
     * 版式：标题（大号）→ 状态（次级）→ 说明（微标签），全部居中，无底框。
     */
    static PhaseHeader addMutablePhaseHeader(GuiElement root, int screenWidth, String title,
                                             String status, String detail, int accentColor) {
        int width = Math.max(1, screenWidth - 40);
        root.addChild(new HeaderRule(0, 0, Math.max(1, screenWidth), PHASE_HEADER_HEIGHT, accentColor));
        Text titleText = text(20, 16, width, title == null ? "" : title, TEXT);
        titleText.setTextScale(UiTheme.TYPE_TITLE).setShadow(false);
        Text statusText = text(20, 7, width, status == null ? "" : status, MUTED)
            .setAlignRight(true);
        statusText.setTextScale(UiTheme.TYPE_MICRO).setSpacing(UiTheme.LETTERSPACING_MICRO);
        Text detailText = text(20, 34, width, detail == null ? "" : detail, DIM);
        detailText.setTextScale(UiTheme.TYPE_MICRO).setSpacing(UiTheme.LETTERSPACING_MICRO);
        root.addChild(titleText);
        root.addChild(statusText);
        root.addChild(detailText);
        return new PhaseHeader(titleText, statusText, detailText);
    }

    /** 页头底饰：左侧信号黄方块 + 顶部细线 + 底部通栏发丝线。纯装饰，不参与命中测试。 */
    static final class HeaderRule extends GuiElement {
        private final int accent;

        HeaderRule(int x, int y, int width, int height, int accent) {
            super(x, y, width, height);
            this.accent = accent == 0 ? UiTheme.ACCENT : accent;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            int px = x + getX();
            int py = y + getY();
            graphics.fill(px + 8, py + 8, px + 14, py + 14, accent);
            UiPaint.hairline(graphics, px + 8, py + getHeight() - 3,
                Math.max(1, getWidth() - 16), UiTheme.HAIRLINE);
        }
    }

    static final class PhaseHeader {
        private final Text title;
        private final Text status;
        private final Text detail;
        private String lastTitle;
        private String lastStatus;
        private String lastDetail;

        private PhaseHeader(Text title, Text status, Text detail) {
            this.title = title;
            this.status = status;
            this.detail = detail;
        }

        void setTitle(String value) {
            String next = value == null ? "" : value;
            if (java.util.Objects.equals(lastTitle, next)) {
                return;
            }
            lastTitle = next;
            title.setText(next);
        }

        void setStatus(String value) {
            String next = value == null ? "" : value;
            if (java.util.Objects.equals(lastStatus, next)) {
                return;
            }
            lastStatus = next;
            status.setText(next);
        }

        void setDetail(String value) {
            String next = value == null ? "" : value;
            if (java.util.Objects.equals(lastDetail, next)) {
                return;
            }
            lastDetail = next;
            detail.setText(next);
        }
    }

    static String trimToWidth(String value, int maxWidth) {
        if (value == null) {
            return "";
        }
        String plain = stripFormatting(value);
        if (Minecraft.getInstance().font.width(plain) <= maxWidth) {
            return value;
        }
        String suffix = "...";
        int suffixWidth = Minecraft.getInstance().font.width(suffix);
        return Minecraft.getInstance().font.plainSubstrByWidth(
            plain, Math.max(0, maxWidth - suffixWidth)) + suffix;
    }

    static void drawScaledString(GuiGraphics graphics, String value, int x, int y,
                                 int color, float scale) {
        drawScaledString(graphics, value, x, y, color, scale, false);
    }

    static void drawScaledString(GuiGraphics graphics, String value, int x, int y,
                                 int color, float scale, boolean shadow) {
        UiPaint.textScaled(graphics, value, x, y, color, scale, shadow);
    }

    // ------------------------------------------------------------------
    // 控件
    // ------------------------------------------------------------------

    static class Panel extends GuiElement {
        private int color;
        private int borderColor;
        private int radius;
        private final boolean createdWithDefaultBorder;
        private int accentColor = 0;
        private boolean glass;
        private boolean sunken;

        Panel(int x, int y, int width, int height, int color, int borderColor, int radius,
              boolean createdWithDefaultBorder) {
            super(x, y, width, height);
            this.color = color;
            this.borderColor = borderColor;
            this.radius = radius;
            this.createdWithDefaultBorder = createdWithDefaultBorder;
        }

        Panel setColor(int color) {
            this.color = color;
            return this;
        }

        Panel setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        Panel setRadius(int radius) {
            this.radius = Math.max(0, radius);
            return this;
        }

        Panel setAccentColor(int accentColor) {
            this.accentColor = accentColor;
            return this;
        }

        Panel setGlass(boolean glass) {
            this.glass = glass;
            if (glass) {
                this.color = 0;
                this.borderColor = 0;
            }
            return this;
        }

        Panel setSunken(boolean sunken) {
            this.sunken = sunken;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }

            int bx = x + getX();
            int by = y + getY();
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
                return;
            }

            if (glass) {
                UiPaint.card(graphics, bx, by, w, h, radius, UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
            } else {
                if (sunken) {
                    UiPaint.sunken(graphics, bx, by, w, h, radius);
                } else {
                    if (UiTheme.visible(color)) {
                        if (color == PANEL && createdWithDefaultBorder) {
                            UiPaint.card(graphics, bx, by, w, h, radius,
                                UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
                        } else {
                            UiPaint.roundRect(graphics, bx, by, w, h, radius, color);
                        }
                    }
                    if (UiTheme.visible(borderColor)) {
                        UiPaint.strokeRoundRect(graphics, bx, by, w, h, radius, borderColor);
                    }
                }
            }
            if (UiTheme.visible(accentColor)) {
                UiPaint.accentBarLeft(graphics, bx + 1, by + 2, Math.max(0, h - 4), accentColor);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class Text extends GuiElement {
        private String value;
        private int color;
        private final boolean centered;
        private final boolean fixedWidth;
        private float textScale = 1.0f;
        private int spacing = 0;
        private boolean shadow = false;
        private boolean alignRight = false;

        Text(int x, int y, int width, String value, int color, boolean centered) {
            super(x, y, width > 0 ? width : Minecraft.getInstance().font.width(stripFormatting(value)),
                Math.max(1, Minecraft.getInstance().font.lineHeight));
            this.value = value == null ? "" : value;
            this.color = color;
            this.centered = centered;
            this.fixedWidth = width > 0;
        }

        void setText(String value) {
            String next = value == null ? "" : value;
            if (next.equals(this.value)) {
                return; // 等值短路：避免无谓的宽度重算与视觉抖动
            }
            this.value = next;
            if (!fixedWidth) {
                setWidth(Math.max(1, Math.round(textWidth() * textScale)));
            }
        }

        void setColor(int color) {
            this.color = color;
        }

        Text setSpacing(int spacing) {
            this.spacing = Math.max(0, spacing);
            return this;
        }

        Text setShadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        Text setAlignRight(boolean alignRight) {
            this.alignRight = alignRight;
            return this;
        }

        Text setTextScale(float scale) {
            this.textScale = Math.max(0.5f, Math.min(2.0f, scale));
            setHeight(Math.max(1, Math.round(Minecraft.getInstance().font.lineHeight * textScale)));
            if (!fixedWidth) {
                setWidth(Math.max(1, Math.round(textWidth() * textScale)));
            }
            return this;
        }

        private int textWidth() {
            return spacing > 0
                ? UiPaint.spacedWidth(stripFormatting(value), spacing)
                : Minecraft.getInstance().font.width(stripFormatting(value));
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }

            String drawnValue = fixedWidth
                ? trimToWidth(value, Math.max(8, (int) (getWidth() / textScale)))
                : value;
            int drawnWidth = spacing > 0
                ? Math.round(UiPaint.spacedWidth(stripFormatting(drawnValue), spacing) * textScale)
                : Math.round(Minecraft.getInstance().font.width(stripFormatting(drawnValue)) * textScale);
            int tx = x + getX();
            if (centered) {
                tx += Math.max(0, (getWidth() - drawnWidth) / 2);
            } else if (alignRight && fixedWidth) {
                tx += Math.max(0, getWidth() - drawnWidth);
            }
            if (spacing > 0) {
                UiPaint.textSpaced(graphics, drawnValue, tx, y + getY(), color, textScale, spacing, shadow);
            } else {
                drawScaledString(graphics, drawnValue, tx, y + getY(), color, textScale, shadow);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class TextBlock extends GuiElement {
        private String value;
        private int color;

        TextBlock(int x, int y, int width, String value, int color) {
            super(x, y, width, Math.max(1, Minecraft.getInstance().font.lineHeight));
            this.value = value == null ? "" : value;
            this.color = color;
            updateHeight();
        }

        void setText(String value) {
            String next = value == null ? "" : value;
            if (next.equals(this.value)) {
                return;
            }
            this.value = next;
            updateHeight();
        }

        void setColor(int color) {
            this.color = color;
        }

        private void updateHeight() {
            setHeight(Math.max(1,
                Minecraft.getInstance().font.wordWrapHeight(stripFormatting(value), getWidth())));
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }

            graphics.drawWordWrap(Minecraft.getInstance().font, UiPaint.themed(value, color),
                x + getX(), y + getY(), getWidth(), color);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class ActionButton extends GuiElement {
        private final Runnable action;
        private String label;
        private boolean enabled = true;
        private boolean selected = false;
        // 常态是"黑色半透明平底"：透明底压在地图上会像磨砂玻璃，用户要求去掉。
        private int normalColor = UiTheme.COLUMN_SCRIM;
        private int hoverColor = UiTheme.SURFACE_3;
        private int selectedColor = UiTheme.ACCENT;
        private int disabledColor = UiTheme.SUNKEN;
        private int borderColor = UiTheme.HAIRLINE_STRONG;
        private int textColor = TEXT;
        private int hoverTextColor = TEXT;
        private float textScale = 1.0f;
        private int accentColor = UiTheme.ACCENT;

        ActionButton(int x, int y, int width, int height, String label, Runnable action) {
            super(x, y, width, height);
            this.label = label == null ? "" : label;
            this.action = action;
        }

        ActionButton setLabel(String label) {
            String next = label == null ? "" : label;
            if (!next.equals(this.label)) {
                this.label = next;
            }
            return this;
        }

        ActionButton setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        ActionButton setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        ActionButton setColors(int normalColor, int hoverColor, int selectedColor) {
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.selectedColor = selectedColor;
            return this;
        }

        ActionButton setDisabledColor(int disabledColor) {
            this.disabledColor = disabledColor;
            return this;
        }

        ActionButton setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        ActionButton setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        ActionButton setHoverTextColor(int hoverTextColor) {
            this.hoverTextColor = hoverTextColor;
            return this;
        }

        ActionButton setAccentColor(int accentColor) {
            this.accentColor = accentColor;
            return this;
        }

        ActionButton setTextScale(float scale) {
            this.textScale = Math.max(0.5f, Math.min(1.0f, scale));
            return this;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button != 0 || !enabled || !isVisible() || !hasFocus()) {
                return false;
            }

            if (action != null) {
                action.run();
            }
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }

            int bx = x + getX();
            int by = y + getY();
            int w = getWidth();
            int h = getHeight();
            int radius = Math.min(UiTheme.RADIUS_CONTROL, Math.max(1, h / 2));

            int fillColor = !enabled ? disabledColor : selected ? selectedColor : hasFocus() ? hoverColor : normalColor;
            if (UiTheme.visible(fillColor)) {
                UiPaint.roundRect(graphics, bx, by, w, h, radius, fillColor);
            }
            if (selected) {
                UiPaint.strokeRoundRect(graphics, bx, by, w, h, radius, UiTheme.alpha(accentColor, 0.85f));
                graphics.fill(bx, by, bx + 2, by + h, UiTheme.INK_FILL);
            } else if (UiTheme.visible(borderColor)) {
                int outline = hasFocus() && enabled ? UiTheme.ACCENT_LINE : borderColor;
                UiPaint.strokeRoundRect(graphics, bx, by, w, h, radius, outline);
            }
            // 悬停：底边 2px 信号黄，工业风格的"当前指向"。
            if (enabled && hasFocus() && !selected) {
                graphics.fill(bx, by + h - 2, bx + w, by + h, UiTheme.ACCENT);
            }

            int logicalTextWidth = Math.max(8, (int) ((w - 8) / textScale));
            String drawnLabel = trimToWidth(label, logicalTextWidth);
            int labelWidth = Math.round(
                Minecraft.getInstance().font.width(stripFormatting(drawnLabel)) * textScale);
            int textHeight = Math.max(1,
                Math.round(Minecraft.getInstance().font.lineHeight * textScale));
            int color = !enabled ? UiTheme.TEXT_DISABLED
                : selected ? UiTheme.TEXT_ON_ACCENT
                : hasFocus() && UiTheme.visible(hoverColor) ? hoverTextColor : textColor;
            drawScaledString(graphics, drawnLabel,
                bx + Math.max(4, (w - labelWidth) / 2),
                by + Math.max(1, (h - textHeight) / 2 + 1),
                color, textScale, false);

            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }

        private static String trimToWidth(String value, int maxWidth) {
            if (Minecraft.getInstance().font.width(stripFormatting(value)) <= maxWidth) {
                return value;
            }

            String plain = stripFormatting(value);
            String suffix = "...";
            int suffixWidth = Minecraft.getInstance().font.width(suffix);
            return Minecraft.getInstance().font.plainSubstrByWidth(plain, Math.max(0, maxWidth - suffixWidth)) + suffix;
        }
    }

    /** 分割线元素：中间实、两端渐隐。 */
    static class Divider extends GuiElement {
        private int color;

        Divider(int x, int y, int width, int color) {
            super(x, y, width, 3);
            this.color = color;
        }

        Divider setColor(int color) {
            this.color = color;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            int bx = x + getX();
            int by = y + getY() + 1;
            int w = getWidth();
            if (w > 0 && UiTheme.visible(color)) {
                int half = Math.max(1, w / 2);
                UiPaint.hairline(graphics, bx, by, w, color);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    /** 胶囊：主色描边 + 8% 底色。 */
    static class Chip extends GuiElement {
        private String label;
        private int accent;
        private float textScale = 0.85f;
        private boolean solid;

        Chip(int x, int y, String label, int accent) {
            super(x, y, Math.max(24, staticChipWidth(label, 0.85f)), 13);
            this.label = label == null ? "" : label;
            this.accent = accent == 0 ? UiTheme.TEXT_THIRD : accent;
        }

        private static int staticChipWidth(String value, float scale) {
            return Math.round(Minecraft.getInstance().font.width(stripFormatting(value)) * scale) + 12;
        }

        Chip setLabel(String label) {
            this.label = label == null ? "" : label;
            setWidth(Math.max(24, chipWidth(this.label)));
            return this;
        }

        Chip setAccent(int accent) {
            this.accent = accent;
            return this;
        }

        Chip setSolid(boolean solid) {
            this.solid = solid;
            return this;
        }

        Chip setTextScale(float scale) {
            this.textScale = Math.max(0.5f, Math.min(1.25f, scale));
            setWidth(Math.max(20, chipWidth(label)));
            return this;
        }

        private int chipWidth(String value) {
            return staticChipWidth(value, textScale);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            int bx = x + getX();
            int by = y + getY();
            int w = getWidth();
            int h = getHeight();
            int radius = UiTheme.RADIUS_CONTROL;
            UiPaint.roundRect(graphics, bx, by, w, h, radius,
                solid ? UiTheme.alpha(accent, 0.85f) : UiTheme.alpha(accent, 0.14f));
            UiPaint.strokeRoundRect(graphics, bx, by, w, h, radius, UiTheme.alpha(accent, 0.55f));
            int textColor = solid ? UiTheme.contrastText(accent) : accent;
            int textHeight = Math.max(1, Math.round(Minecraft.getInstance().font.lineHeight * textScale));
            int tw = Math.round(Minecraft.getInstance().font.width(stripFormatting(label)) * textScale);
            drawScaledString(graphics, label, bx + (w - tw) / 2, by + (h - textHeight) / 2,
                textColor, textScale, false);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    /** 数值块：微标签 + 数值（数值左对齐，标签在下）。 */
    static class Stat extends GuiElement {
        private String label;
        private String value;
        private int valueColor;

        Stat(int x, int y, int width, String label, String value, int valueColor) {
            super(x, y, width, 24);
            this.label = label == null ? "" : label;
            this.value = value == null ? "" : value;
            this.valueColor = valueColor == 0 ? TEXT : valueColor;
        }

        Stat setValue(String value) {
            this.value = value == null ? "" : value;
            return this;
        }

        Stat setValueColor(int color) {
            this.valueColor = color;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            int bx = x + getX();
            int by = y + getY();
            UiPaint.textSpaced(graphics, label, bx, by, UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO,
                UiTheme.LETTERSPACING_MICRO, false);
            UiPaint.textScaled(graphics, value, bx, by + 10, valueColor, UiTheme.TYPE_HEADING, true);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    /** 进度条元素。 */
    static class Bar extends GuiElement {
        private float progress;
        private int fillColor;

        Bar(int x, int y, int width, int height, int fillColor) {
            super(x, y, width, Math.max(3, height));
            this.fillColor = fillColor;
        }

        Bar setProgress(float progress) {
            this.progress = Math.max(0f, Math.min(1f, progress));
            return this;
        }

        Bar setFillColor(int fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            UiPaint.progress(graphics, x + getX(), y + getY(), getWidth(), getHeight(), progress, fillColor);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    /** 分区标题：微标签 + 向右延伸的渐隐细线。 */
    static class SectionLabel extends GuiElement {
        private String label;
        private int accent;

        SectionLabel(int x, int y, int width, String label, int accent) {
            super(x, y, width, 12);
            this.label = label == null ? "" : label;
            this.accent = accent == 0 ? UiTheme.TEXT_THIRD : accent;
        }

        SectionLabel setLabel(String label) {
            this.label = label == null ? "" : label;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            int bx = x + getX();
            int by = y + getY();
            int textWidth = UiPaint.spacedWidth(label, UiTheme.LETTERSPACING_MICRO);
            if (UiTheme.visible(accent)) {
                graphics.fill(bx, by + 2, bx + 2, by + 9, accent);
            }
            UiPaint.textSpaced(graphics, label, bx + 7, by + 1, accent, UiTheme.TYPE_MICRO,
                UiTheme.LETTERSPACING_MICRO, false);
            int lineStart = bx + 7 + textWidth + 8;
            int lineEnd = bx + getWidth();
            if (lineEnd - lineStart > 8) {
                graphics.fillGradient(lineStart, by + 5, lineEnd, by + 6,
                    UiTheme.withAlpha(accent, 0x40), UiTheme.TRANSPARENT);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    /**
     * 渲染一张纹理图片
     */
    static class TextureImage extends GuiElement {
        private final ResourceLocation texture;
        private float alpha = 1f;

        TextureImage(int x, int y, int width, int height, ResourceLocation texture) {
            super(x, y, width, height);
            this.texture = texture;
        }

        TextureImage setAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;

            graphics.setColor(1f, 1f, 1f, alpha);
            graphics.blit(texture, x + getX(), y + getY(), 0, 0, getWidth(), getHeight(),
                getWidth(), getHeight());
            graphics.setColor(1f, 1f, 1f, 1f);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    /**
     * 可点击的图片按钮（带 hover 高亮边框，等比例缩放）
     */
    static class ImageButton extends GuiElement {
        private final ResourceLocation texture;
        private final Runnable action;
        private final int texW, texH;
        private int borderColor = UiTheme.TRANSPARENT;
        private int hoverBorderColor = UiTheme.ACCENT_LINE;
        /** 选中态边框；非 0 时优先于普通 border，且 hover 时仍保留。 */
        private int selectedBorderColor = UiTheme.TRANSPARENT;

        ImageButton(int x, int y, int maxWidth, int maxHeight, ResourceLocation texture, Runnable action) {
            super(x, y, maxWidth, maxHeight);
            this.texture = texture;
            this.action = action;
            // 无法在构造时获取纹理尺寸（可能尚未加载），用 blit 方式自动等比
            this.texW = maxWidth;
            this.texH = maxHeight;
        }

        ImageButton(int x, int y, int maxWidth, int maxHeight, int texW, int texH,
                    ResourceLocation texture, Runnable action) {
            super(x, y, maxWidth, maxHeight);
            this.texture = texture;
            this.action = action;
            this.texW = texW;
            this.texH = texH;
        }

        ImageButton setBorderColor(int color) { this.borderColor = color; return this; }
        ImageButton setHoverBorderColor(int color) { this.hoverBorderColor = color; return this; }
        ImageButton setSelectedBorderColor(int color) { this.selectedBorderColor = color; return this; }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button != 0 || !isVisible() || !hasFocus()) return false;
            if (action != null) action.run();
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;

            int bx = x + getX(), by = y + getY();
            int maxW = getWidth(), maxH = getHeight();

            // 等比例缩放：按最大边适配
            float scale = Math.min((float) maxW / texW, (float) maxH / texH);
            int drawW = (int) (texW * scale);
            int drawH = (int) (texH * scale);
            // 居中
            int drawX = bx + (maxW - drawW) / 2;
            int drawY = by + (maxH - drawH) / 2;

            // 选中态用圆角发光底，替代旧的方形描边。
            if (hasAlpha(selectedBorderColor)) {
                int pad = 3;
                UiPaint.roundRect(graphics, drawX - pad, drawY - pad, drawW + pad * 2, drawH + pad * 2,
                    UiTheme.RADIUS_CONTROL, UiTheme.alpha(selectedBorderColor, 0.16f));
                UiPaint.strokeRoundRect(graphics, drawX - pad, drawY - pad, drawW + pad * 2,
                    drawH + pad * 2, UiTheme.RADIUS_CONTROL,
                    hasFocus() && hasAlpha(hoverBorderColor) ? hoverBorderColor : selectedBorderColor);
            } else {
                int outlineColor = hasFocus() ? hoverBorderColor : borderColor;
                if (hasAlpha(outlineColor)) {
                    UiPaint.strokeRoundRect(graphics, drawX - 2, drawY - 2, drawW + 4, drawH + 4,
                        UiTheme.RADIUS_CONTROL, outlineColor);
                }
            }

            // 目标区域与源纹理尺寸必须分别传入。
            graphics.blit(texture, drawX, drawY, drawW, drawH,
                0f, 0f, texW, texH, texW, texH);

            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    private static boolean hasAlpha(int color) {
        return (color & 0xFF000000) != 0;
    }
}
