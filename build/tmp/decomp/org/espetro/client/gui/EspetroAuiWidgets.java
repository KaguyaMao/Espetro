/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.aui.GuiRect;
import org.espetro.team.TeamDisplayNames;

final class EspetroAuiWidgets {
    static final int BACKDROP = -16777216;
    static final int PANEL = 1645598;
    static final int PANEL_SOFT = 2172199;
    static final int BORDER = -10788256;
    static final int BORDER_ACTIVE = -1525668;
    static final int TEXT = -1;
    static final int MUTED = -2828064;
    static final int DIM = -5327681;
    static final int GOLD = -14490;
    static final int PURPLE = -2847489;
    static final int SQUAD_BLUE = -9984001;
    static final int ATTACK = -41386;
    static final int DEFEND = -10514945;
    static final int POSITIVE = -9054838;
    static final int WARNING = -19380;
    static final int NEGATIVE = -39322;
    static final int PHASE_HEADER_HEIGHT = 42;
    private static final Pattern FORMAT_CODE = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private EspetroAuiWidgets() {
    }

    static String stripFormatting(String text) {
        return text == null ? "" : FORMAT_CODE.matcher(text).replaceAll("");
    }

    static GuiRect rect(int x, int y, int width, int height, int color) {
        return new GuiRect(x, y, width, height, color);
    }

    static void drawScreenShade(GuiGraphics graphics, int width, int height) {
        if (graphics == null || width <= 0 || height <= 0) {
            return;
        }
        graphics.m_280509_(0, 0, width, height, -16777216);
    }

    static Panel panel(int x, int y, int width, int height) {
        return new Panel(x, y, width, height, 1645598, -10788256);
    }

    static Panel panel(int x, int y, int width, int height, int color, int borderColor) {
        return new Panel(x, y, width, height, color, borderColor);
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

    static TextBlock textBlock(int x, int y, int width, String value, int color) {
        return new TextBlock(x, y, width, value, color);
    }

    static ActionButton button(int x, int y, int width, int height, String label, Runnable action) {
        return new ActionButton(x, y, width, height, label, action);
    }

    static ActionButton textButton(int x, int y, String label, Runnable action) {
        return EspetroAuiWidgets.button(x, y, EspetroAuiWidgets.textButtonWidth(label), 13, label, action);
    }

    static int textButtonWidth(String label) {
        return Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(label)) + 12;
    }

    static int teamColor(String team) {
        return "ATTACK".equals(team) ? -41386 : -10514945;
    }

    static String teamName(String team) {
        return TeamDisplayNames.displayName(team);
    }

    static String teamPrefix(String team) {
        return TeamDisplayNames.prefix(team);
    }

    static int addPhaseHeader(GuiElement root, int screenWidth, String title, String status, String detail, int accentColor) {
        EspetroAuiWidgets.addMutablePhaseHeader(root, screenWidth, title, status, detail, accentColor);
        return 42;
    }

    static PhaseHeader addMutablePhaseHeader(GuiElement root, int screenWidth, String title, String status, String detail, int accentColor) {
        Text titleText = EspetroAuiWidgets.centeredText(6, 4, Math.max(1, screenWidth - 12), title == null ? "" : title, -1);
        Text statusText = EspetroAuiWidgets.centeredText(6, 16, Math.max(1, screenWidth - 12), status == null ? "" : status, -2828064);
        Text detailText = EspetroAuiWidgets.centeredText(6, 28, Math.max(1, screenWidth - 12), detail == null ? "" : detail, -5327681);
        root.addChild(titleText);
        root.addChild(statusText);
        root.addChild(detailText);
        return new PhaseHeader(titleText, statusText, detailText);
    }

    static String trimToWidth(String value, int maxWidth) {
        if (value == null) {
            return "";
        }
        String plain = EspetroAuiWidgets.stripFormatting(value);
        if (Minecraft.m_91087_().f_91062_.m_92895_(plain) <= maxWidth) {
            return value;
        }
        String suffix = "...";
        int suffixWidth = Minecraft.m_91087_().f_91062_.m_92895_(suffix);
        return Minecraft.m_91087_().f_91062_.m_92834_(plain, Math.max(0, maxWidth - suffixWidth)) + suffix;
    }

    static void drawScaledString(GuiGraphics graphics, String value, int x, int y, int color, float scale) {
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(scale, scale, 1.0f);
        graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(value), Math.round((float)x / scale), Math.round((float)y / scale), color, false);
        graphics.m_280168_().m_85849_();
    }

    private static boolean hasAlpha(int color) {
        return (color & 0xFF000000) != 0;
    }

    static class Panel
    extends GuiElement {
        private int color;
        private int borderColor;

        Panel(int x, int y, int width, int height, int color, int borderColor) {
            super(x, y, width, height);
            this.color = color;
            this.borderColor = borderColor;
        }

        Panel setColor(int color) {
            this.color = color;
            return this;
        }

        Panel setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            if (EspetroAuiWidgets.hasAlpha(this.color)) {
                graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), this.color);
            }
            if (EspetroAuiWidgets.hasAlpha(this.borderColor)) {
                graphics.m_280637_(bx, by, this.getWidth(), this.getHeight(), this.borderColor);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class Text
    extends GuiElement {
        private String value;
        private int color;
        private final boolean centered;
        private final boolean fixedWidth;
        private float textScale = 1.0f;

        Text(int x, int y, int width, String value, int color, boolean centered) {
            super(x, y, width > 0 ? width : Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(value)), Minecraft.m_91087_().f_91062_.f_92710_);
            this.value = value == null ? "" : value;
            this.color = color;
            this.centered = centered;
            this.fixedWidth = width > 0;
        }

        void setText(String value) {
            String next;
            String string = next = value == null ? "" : value;
            if (next.equals(this.value)) {
                return;
            }
            this.value = next;
            if (!this.fixedWidth) {
                this.setWidth(Math.max(1, Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(this.value)) * this.textScale)));
            }
        }

        void setColor(int color) {
            this.color = color;
        }

        Text setTextScale(float scale) {
            this.textScale = Math.max(0.5f, Math.min(1.0f, scale));
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            this.setHeight(Math.max(1, Math.round(9.0f * this.textScale)));
            if (!this.fixedWidth) {
                this.setWidth(Math.max(1, Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(this.value)) * this.textScale)));
            }
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            String drawnValue = this.fixedWidth ? EspetroAuiWidgets.trimToWidth(this.value, Math.max(8, (int)((float)this.getWidth() / this.textScale))) : this.value;
            int drawnWidth = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(drawnValue)) * this.textScale);
            int tx = x + this.getX();
            if (this.centered) {
                tx += Math.max(0, (this.getWidth() - drawnWidth) / 2);
            }
            EspetroAuiWidgets.drawScaledString(graphics, drawnValue, tx, y + this.getY(), this.color, this.textScale);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class TextBlock
    extends GuiElement {
        private String value;
        private int color;

        TextBlock(int x, int y, int width, String value, int color) {
            super(x, y, width, Minecraft.m_91087_().f_91062_.f_92710_);
            this.value = value == null ? "" : value;
            this.color = color;
            this.updateHeight();
        }

        void setText(String value) {
            String next;
            String string = next = value == null ? "" : value;
            if (next.equals(this.value)) {
                return;
            }
            this.value = next;
            this.updateHeight();
        }

        void setColor(int color) {
            this.color = color;
        }

        private void updateHeight() {
            this.setHeight(Minecraft.m_91087_().f_91062_.m_92920_(EspetroAuiWidgets.stripFormatting(this.value), this.getWidth()));
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            graphics.m_280554_(Minecraft.m_91087_().f_91062_, Component.m_237113_(this.value), x + this.getX(), y + this.getY(), this.getWidth(), this.color);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class ActionButton
    extends GuiElement {
        private final Runnable action;
        private String label;
        private boolean enabled = true;
        private boolean selected = false;
        private int normalColor = 0;
        private int hoverColor = 1076311354;
        private int selectedColor = 1077556256;
        private int disabledColor = 0;
        private int borderColor = -2141494688;
        private int textColor = -1;
        private float textScale = 1.0f;

        ActionButton(int x, int y, int width, int height, String label, Runnable action) {
            super(x, y, width, height);
            this.label = label == null ? "" : label;
            this.action = action;
        }

        ActionButton setLabel(String label) {
            String next;
            String string = next = label == null ? "" : label;
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

        ActionButton setTextScale(float scale) {
            this.textScale = Math.max(0.5f, Math.min(1.0f, scale));
            return this;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (!(button == 0 && this.enabled && this.isVisible() && this.hasFocus())) {
                return false;
            }
            if (this.action != null) {
                this.action.run();
            }
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            int outline;
            int fillColor;
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            int n = !this.enabled ? this.disabledColor : (this.selected ? this.selectedColor : (fillColor = this.hasFocus() ? this.hoverColor : this.normalColor));
            int n2 = this.selected ? -1863796644 : (outline = this.hasFocus() && this.enabled ? 1623378133 : this.borderColor);
            if (EspetroAuiWidgets.hasAlpha(fillColor)) {
                graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), fillColor);
            }
            if (EspetroAuiWidgets.hasAlpha(outline)) {
                graphics.m_280637_(bx, by, this.getWidth(), this.getHeight(), outline);
            }
            int logicalTextWidth = Math.max(8, (int)((float)(this.getWidth() - 6) / this.textScale));
            String drawnLabel = ActionButton.trimToWidth(this.label, logicalTextWidth);
            int labelWidth = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(drawnLabel)) * this.textScale);
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            int textHeight = Math.max(1, Math.round(9.0f * this.textScale));
            int color = this.enabled ? this.textColor : -5327681;
            EspetroAuiWidgets.drawScaledString(graphics, drawnLabel, bx + Math.max(3, (this.getWidth() - labelWidth) / 2), by + Math.max(1, (this.getHeight() - textHeight) / 2), color, this.textScale);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }

        private static String trimToWidth(String value, int maxWidth) {
            if (Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(value)) <= maxWidth) {
                return value;
            }
            String plain = EspetroAuiWidgets.stripFormatting(value);
            String suffix = "...";
            int suffixWidth = Minecraft.m_91087_().f_91062_.m_92895_(suffix);
            return Minecraft.m_91087_().f_91062_.m_92834_(plain, Math.max(0, maxWidth - suffixWidth)) + suffix;
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
            String next;
            String string = next = value == null ? "" : value;
            if (Objects.equals(this.lastTitle, next)) {
                return;
            }
            this.lastTitle = next;
            this.title.setText(next);
        }

        void setStatus(String value) {
            String next;
            String string = next = value == null ? "" : value;
            if (Objects.equals(this.lastStatus, next)) {
                return;
            }
            this.lastStatus = next;
            this.status.setText(next);
        }

        void setDetail(String value) {
            String next;
            String string = next = value == null ? "" : value;
            if (Objects.equals(this.lastDetail, next)) {
                return;
            }
            this.lastDetail = next;
            this.detail.setText(next);
        }
    }

    static class ImageButton
    extends GuiElement {
        private final ResourceLocation texture;
        private final Runnable action;
        private final int texW;
        private final int texH;
        private int borderColor = 0;
        private int hoverBorderColor = -2130706433;
        private int selectedBorderColor = 0;

        ImageButton(int x, int y, int maxWidth, int maxHeight, ResourceLocation texture, Runnable action) {
            super(x, y, maxWidth, maxHeight);
            this.texture = texture;
            this.action = action;
            AbstractTexture tex = Minecraft.m_91087_().m_91097_().m_118506_(texture);
            this.texW = maxWidth;
            this.texH = maxHeight;
        }

        ImageButton(int x, int y, int maxWidth, int maxHeight, int texW, int texH, ResourceLocation texture, Runnable action) {
            super(x, y, maxWidth, maxHeight);
            this.texture = texture;
            this.action = action;
            this.texW = texW;
            this.texH = texH;
        }

        ImageButton setBorderColor(int color) {
            this.borderColor = color;
            return this;
        }

        ImageButton setHoverBorderColor(int color) {
            this.hoverBorderColor = color;
            return this;
        }

        ImageButton setSelectedBorderColor(int color) {
            this.selectedBorderColor = color;
            return this;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button != 0 || !this.isVisible() || !this.hasFocus()) {
                return false;
            }
            if (this.action != null) {
                this.action.run();
            }
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            int outlineColor;
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            int maxW = this.getWidth();
            int maxH = this.getHeight();
            float scale = Math.min((float)maxW / (float)this.texW, (float)maxH / (float)this.texH);
            int drawW = (int)((float)this.texW * scale);
            int drawH = (int)((float)this.texH * scale);
            int drawX = bx + (maxW - drawW) / 2;
            int drawY = by + (maxH - drawH) / 2;
            if (EspetroAuiWidgets.hasAlpha(this.selectedBorderColor)) {
                outlineColor = this.hasFocus() && EspetroAuiWidgets.hasAlpha(this.hoverBorderColor) ? this.hoverBorderColor : this.selectedBorderColor;
            } else {
                int n = outlineColor = this.hasFocus() ? this.hoverBorderColor : this.borderColor;
            }
            if (EspetroAuiWidgets.hasAlpha(outlineColor)) {
                int pad = EspetroAuiWidgets.hasAlpha(this.selectedBorderColor) ? 2 : 1;
                graphics.m_280637_(drawX - pad, drawY - pad, drawW + pad * 2, drawH + pad * 2, outlineColor);
            }
            graphics.m_280411_(this.texture, drawX, drawY, drawW, drawH, 0.0f, 0.0f, this.texW, this.texH, this.texW, this.texH);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class TextureImage
    extends GuiElement {
        private final ResourceLocation texture;
        private float alpha = 1.0f;

        TextureImage(int x, int y, int width, int height, ResourceLocation texture) {
            super(x, y, width, height);
            this.texture = texture;
        }

        TextureImage setAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            graphics.m_280246_(1.0f, 1.0f, 1.0f, this.alpha);
            graphics.m_280163_(this.texture, x + this.getX(), y + this.getY(), 0.0f, 0.0f, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
            graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }
}

