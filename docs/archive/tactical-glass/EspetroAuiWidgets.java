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
 * 阶段界面 / 菜单共用的即时绘制控件库（"Tactical Glass" 风格）。
 *
 * <p>公开 API 与旧版保持一致（颜色常量、{@code panel/button/text/...} 工厂、
 * {@link PhaseHeader}），因此所有既有界面无需改动即可换肤；内部绘制全部改走
 * {@link UiPaint}：圆角、渐变、发丝描边、柔和阴影、微标签。
 */
final class EspetroAuiWidgets {

    // ---- 语义色（保持旧名，数值换成新色板） ----
    /** 全屏静态底：不透明，避免世界/失明层穿透或闪烁。 */
    static final int BACKDROP = UiTheme.BACKDROP_TOP;
    static final int PANEL = 0x00191C1E;
    static final int PANEL_SOFT = 0x00212527;
    static final int BORDER = 0x665B6260;
    static final int BORDER_ACTIVE = UiTheme.ACCENT;
    static final int TEXT = UiTheme.TEXT;
    static final int MUTED = UiTheme.TEXT_SECOND;
    static final int DIM = UiTheme.TEXT_THIRD;
    static final int GOLD = UiTheme.GOLD;
    static final int PURPLE = UiTheme.PURPLE;
    static final int SQUAD_BLUE = UiTheme.SQUAD;
    static final int ATTACK = UiTheme.ATTACK;
    static final int DEFEND = UiTheme.DEFEND;
    static final int POSITIVE = UiTheme.POSITIVE;
    static final int WARNING = UiTheme.WARNING;
    static final int NEGATIVE = UiTheme.NEGATIVE;
    static final int PHASE_HEADER_HEIGHT = 44;

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
     * 阶段 GUI 全屏底：深色玻璃渐变 + 暗角 + 颗粒。
     * 底层仍是不透明填充，语义与旧版"纯黑底"一致（遮住世界）。
     */
    static void drawScreenShade(GuiGraphics graphics, int width, int height) {
        if (graphics == null || width <= 0 || height <= 0) {
            return;
        }
        graphics.fill(0, 0, width, height, UiTheme.BACKDROP_TOP);
        graphics.fillGradient(0, 0, width, height, 0x00000000, UiTheme.accentWash(0.06f));
        UiPaint.vignette(graphics, width, height);
        UiPaint.grain(graphics, width, height, 0.05f);
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
        return new Panel(x, y, width, height, UiTheme.SUNKEN, 0x66000000,
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
    static ActionButton primaryButton(int x, int y, int width, int height, String label, Runnable action) {
        return new ActionButton(x, y, width, height, label, action)
            .setColors(UiTheme.ACCENT_SOFT, UiTheme.withAlpha(UiTheme.ACCENT, 0x3C),
                UiTheme.withAlpha(UiTheme.ACCENT, 0x4A))
            .setBorderColor(UiTheme.ACCENT_LINE)
            .setAccentColor(UiTheme.ACCENT);
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
        int width = Math.max(1, screenWidth - 16);
        Text titleText = centeredText(8, 5, width, title == null ? "" : title, TEXT);
        titleText.setTextScale(UiTheme.TYPE_TITLE).setShadow(true);
        Text statusText = centeredText(8, 20, width, status == null ? "" : status, MUTED);
        statusText.setTextScale(0.95f);
        Text detailText = centeredText(8, 33, width, detail == null ? "" : detail, DIM);
        detailText.setTextScale(UiTheme.TYPE_MICRO);
        root.addChild(titleText);
        root.addChild(statusText);
        root.addChild(detailText);
        return new PhaseHeader(titleText, statusText, detailText);
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
        if (scale == 1f) {
            graphics.drawString(Minecraft.getInstance().font, Component.literal(value), x, y, color, shadow);
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(Minecraft.getInstance().font, Component.literal(value),
            Math.round(x / scale), Math.round(y / scale), color, shadow);
        graphics.pose().popPose();
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

            graphics.drawWordWrap(Minecraft.getInstance().font, Component.literal(value),
                x + getX(), y + getY(), getWidth(), color);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class ActionButton extends GuiElement {
        private final Runnable action;
        private String label;
        private boolean enabled = true;
        private boolean selected = false;
        // 默认透明底 + 边框区分 hover/selected（避免大面积半透明当"背景墙"）。
        private int normalColor = 0x00000000;
        private int hoverColor = 0xCC1E2A38;
        private int selectedColor = 0xCC16303F;
        private int disabledColor = 0x00000000;
        private int borderColor = 0x66707C8C;
        private int textColor = TEXT;
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
                UiPaint.hairline(graphics, bx + radius, by, Math.max(0, w - radius * 2),
                    UiTheme.alpha(accentColor, 0.35f));
            } else if (UiTheme.visible(borderColor)) {
                int outline = hasFocus() && enabled ? UiTheme.withAlpha(0xFFFFFFFF, 0x2E) : borderColor;
                UiPaint.strokeRoundRect(graphics, bx, by, w, h, radius, outline);
            }

            int logicalTextWidth = Math.max(8, (int) ((w - 8) / textScale));
            String drawnLabel = trimToWidth(label, logicalTextWidth);
            int labelWidth = Math.round(
                Minecraft.getInstance().font.width(stripFormatting(drawnLabel)) * textScale);
            int textHeight = Math.max(1,
                Math.round(Minecraft.getInstance().font.lineHeight * textScale));
            int color = !enabled ? DIM : selected ? UiTheme.lerp(textColor, accentColor, 0.55f) : textColor;
            drawScaledString(graphics, drawnLabel,
                bx + Math.max(4, (w - labelWidth) / 2),
                by + Math.max(1, (h - textHeight) / 2 + 1),
                color, textScale, true);

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
                graphics.fillGradient(bx, by, bx + half, by + 1,
                    UiTheme.withAlpha(color, 0x18), color);
                graphics.fillGradient(bx + half, by, bx + w, by + 1,
                    color, UiTheme.withAlpha(color, 0x18));
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
            int radius = Math.max(1, h / 2);
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
                    UiTheme.withAlpha(accent, 0x40), 0x00000000);
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
        private int borderColor = 0x00000000;
        private int hoverBorderColor = 0x80FFFFFF;
        /** 选中态边框；非 0 时优先于普通 border，且 hover 时仍保留。 */
        private int selectedBorderColor = 0x00000000;

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
