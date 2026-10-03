/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  org.espetro.api.EspetroAPI
 *  org.espetro.client.aui.GuiElement
 *  org.espetro.client.aui.GuiRect
 */
package com.example.espoints.client.gui;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.espetro.api.EspetroAPI;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.aui.GuiRect;

final class HcrAuiWidgets {
    static final int BACKDROP = -1340992233;
    static final int PANEL = -535225314;
    static final int PANEL_SOFT = -1340005081;
    static final int BORDER = -1604623776;
    static final int BORDER_ACTIVE = -1525668;
    static final int TEXT = -790040;
    static final int MUTED = -5722440;
    static final int DIM = -9209465;
    static final int GOLD = -14490;
    static final int POSITIVE = -9054838;
    static final int WARNING = -19380;
    static final int NEGATIVE = -39322;
    static final int BLUE = -10514945;
    static final int CYAN = -14297390;
    static final int PURPLE = -5145345;
    static final int ATTACK = -41386;
    static final int DEFEND = -10514945;
    private static final Pattern FORMAT_CODE = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private HcrAuiWidgets() {
    }

    static String stripFormatting(String text) {
        return text == null ? "" : FORMAT_CODE.matcher(text).replaceAll("");
    }

    static GuiRect rect(int x, int y, int width, int height, int color) {
        return new GuiRect(x, y, width, height, color);
    }

    static void drawScreenShade(GuiGraphics graphics, int width, int height) {
        graphics.m_280509_(0, 0, width, height, -1340992233);
    }

    static Panel panel(int x, int y, int width, int height) {
        return new Panel(x, y, width, height, -535225314, -1604623776);
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
        return HcrAuiWidgets.button(x, y, HcrAuiWidgets.textButtonWidth(label), 13, label, action);
    }

    static int textButtonWidth(String label) {
        return Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(label)) + 12;
    }

    static int teamColor(String team) {
        return "ATTACK".equals(team) ? -41386 : -10514945;
    }

    static String teamName(String team) {
        try {
            return EspetroAPI.teamDisplayName((String)team);
        }
        catch (Throwable ignored) {
            return "ATTACK".equals(team) ? "\u8fdb\u653b\u65b9" : "\u9632\u5b88\u65b9";
        }
    }

    static String teamPrefix(String team) {
        return "ATTACK".equals(team) ? "\u00a7c" : "\u00a79";
    }

    static String trimToWidth(String value, int maxWidth) {
        if (value == null) {
            return "";
        }
        if (Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(value)) <= maxWidth) {
            return value;
        }
        String plain = HcrAuiWidgets.stripFormatting(value);
        String suffix = "...";
        int suffixWidth = Minecraft.m_91087_().f_91062_.m_92895_(suffix);
        return Minecraft.m_91087_().f_91062_.m_92834_(plain, Math.max(0, maxWidth - suffixWidth)) + suffix;
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

        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            if (HcrAuiWidgets.hasAlpha(this.color)) {
                graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), this.color);
            }
            if (HcrAuiWidgets.hasAlpha(this.borderColor)) {
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

        Text(int x, int y, int width, String value, int color, boolean centered) {
            int n = width > 0 ? width : Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(value));
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            super(x, y, n, 9);
            this.value = value == null ? "" : value;
            this.color = color;
            this.centered = centered;
        }

        void setText(String value) {
            String string = this.value = value == null ? "" : value;
            if (!this.centered) {
                this.setWidth(Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(this.value)));
            }
        }

        void setColor(int color) {
            this.color = color;
        }

        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            int tx = x + this.getX();
            if (this.centered) {
                tx += Math.max(0, (this.getWidth() - Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(this.value))) / 2);
            }
            graphics.m_280614_(Minecraft.m_91087_().f_91062_, (Component)Component.m_237113_((String)this.value), tx, y + this.getY(), this.color, false);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class TextBlock
    extends GuiElement {
        private String value;
        private int color;

        TextBlock(int x, int y, int width, String value, int color) {
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            super(x, y, width, 9);
            this.value = value == null ? "" : value;
            this.color = color;
            this.updateHeight();
        }

        void setText(String value) {
            this.value = value == null ? "" : value;
            this.updateHeight();
        }

        void setColor(int color) {
            this.color = color;
        }

        private void updateHeight() {
            this.setHeight(Minecraft.m_91087_().f_91062_.m_92920_(HcrAuiWidgets.stripFormatting(this.value), this.getWidth()));
        }

        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            graphics.m_280554_(Minecraft.m_91087_().f_91062_, (FormattedText)Component.m_237113_((String)this.value), x + this.getX(), y + this.getY(), this.getWidth(), this.color);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class ActionButton
    extends GuiElement {
        private final Runnable action;
        private String label;
        private boolean enabled = true;
        private boolean selected = false;
        private int normalColor = -1340400096;
        private int hoverColor = -800894651;
        private int selectedColor = -800439511;
        private int disabledColor = 1880627997;
        private int borderColor = -2141494688;
        private int textColor = -790040;

        ActionButton(int x, int y, int width, int height, String label, Runnable action) {
            super(x, y, width, height);
            this.label = label == null ? "" : label;
            this.action = action;
        }

        ActionButton setLabel(String label) {
            this.label = label == null ? "" : label;
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

        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (!(button == 0 && this.enabled && this.isVisible() && this.hasFocus())) {
                return false;
            }
            if (this.action != null) {
                this.action.run();
            }
            return true;
        }

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
            if (HcrAuiWidgets.hasAlpha(fillColor)) {
                graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), fillColor);
            }
            if (HcrAuiWidgets.hasAlpha(outline)) {
                graphics.m_280637_(bx, by, this.getWidth(), this.getHeight(), outline);
            }
            String drawnLabel = HcrAuiWidgets.trimToWidth(this.label, Math.max(8, this.getWidth() - 10));
            int labelWidth = Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(drawnLabel));
            int color = this.enabled ? this.textColor : -9209465;
            Font font = Minecraft.m_91087_().f_91062_;
            MutableComponent mutableComponent = Component.m_237113_((String)drawnLabel);
            int n3 = bx + Math.max(4, (this.getWidth() - labelWidth) / 2);
            int n4 = this.getHeight();
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            graphics.m_280614_(font, (Component)mutableComponent, n3, by + Math.max(1, (n4 - 9) / 2), color, false);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    static class TextInput
    extends GuiElement {
        private static TextInput activeInput;
        private String value;
        private final int maxLength;
        private final Pattern filter;
        private final Consumer<String> responder;
        private boolean focused;
        private long lastBlinkMs = System.currentTimeMillis();
        private boolean cursorVisible = true;

        TextInput(int x, int y, int width, int height, String value, int maxLength, Pattern filter, Consumer<String> responder) {
            super(x, y, width, height);
            this.value = value == null ? "" : value;
            this.maxLength = Math.max(1, maxLength);
            this.filter = filter;
            this.responder = responder;
        }

        String getValue() {
            return this.value;
        }

        void setValue(String value) {
            String next;
            String string = next = value == null ? "" : value;
            if (next.length() > this.maxLength) {
                next = next.substring(0, this.maxLength);
            }
            if (this.filter == null || this.filter.matcher(next).matches()) {
                this.value = next;
            }
        }

        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button != 0 || !this.isVisible()) {
                return false;
            }
            if (this.hasFocus()) {
                if (activeInput != null && activeInput != this) {
                    TextInput.activeInput.focused = false;
                }
                activeInput = this;
                this.focused = true;
            } else if (activeInput == this) {
                activeInput = null;
                this.focused = false;
            }
            this.cursorVisible = true;
            this.lastBlinkMs = System.currentTimeMillis();
            return this.focused;
        }

        public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
            if (activeInput != this || !this.focused || !this.isVisible()) {
                return false;
            }
            if (keyCode == 259) {
                if (!this.value.isEmpty()) {
                    this.value = this.value.substring(0, this.value.length() - 1);
                    this.notifyResponder();
                }
                return true;
            }
            if (keyCode == 261) {
                if (!this.value.isEmpty()) {
                    this.value = "";
                    this.notifyResponder();
                }
                return true;
            }
            if (keyCode == 257 || keyCode == 335 || keyCode == 256) {
                activeInput = null;
                this.focused = false;
                return keyCode != 256;
            }
            return false;
        }

        public boolean onCharType(char codePoint, int modifiers) {
            if (activeInput != this || !this.focused || !this.isVisible() || Character.isISOControl(codePoint)) {
                return false;
            }
            if (this.value.length() >= this.maxLength) {
                return true;
            }
            String next = this.value + codePoint;
            if (this.filter == null || this.filter.matcher(next).matches()) {
                this.value = next;
                this.notifyResponder();
            }
            return true;
        }

        protected void onBlur() {
        }

        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), this.focused ? -802738901 : -1340400096);
            graphics.m_280637_(bx, by, this.getWidth(), this.getHeight(), this.focused ? -1525668 : -1604623776);
            String drawn = HcrAuiWidgets.trimToWidth(this.value, Math.max(8, this.getWidth() - 12));
            Font font = Minecraft.m_91087_().f_91062_;
            MutableComponent mutableComponent = Component.m_237113_((String)drawn);
            int n = this.getHeight();
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            graphics.m_280614_(font, (Component)mutableComponent, bx + 5, by + Math.max(1, (n - 9) / 2), -790040, false);
            long now = System.currentTimeMillis();
            if (now - this.lastBlinkMs > 450L) {
                this.cursorVisible = !this.cursorVisible;
                this.lastBlinkMs = now;
            }
            if (this.focused && this.cursorVisible) {
                int cursorX = Math.min(bx + this.getWidth() - 6, bx + 5 + Minecraft.m_91087_().f_91062_.m_92895_(HcrAuiWidgets.stripFormatting(drawn)));
                graphics.m_280509_(cursorX, by + 4, cursorX + 1, by + this.getHeight() - 4, -790040);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }

        private void notifyResponder() {
            if (this.responder != null) {
                this.responder.accept(this.value);
            }
        }
    }
}

