/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.typings.Info
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 */
package cc.sighs.auratip.compat.kubejs.tip;

import cc.sighs.auratip.util.ColorUtil;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class TipText {
    @Info(value="Create a text builder from any object. If base is a Component it's used directly; otherwise it becomes a literal.")
    public static Builder of(Object base) {
        if (base instanceof Component) {
            Component component = (Component)base;
            return new Builder(component);
        }
        return new Builder((Component)Component.m_237113_((String)String.valueOf(base)));
    }

    @Info(value="Create a translatable text builder (translation key + args).")
    public static Builder translatable(String key, Object ... args) {
        return new Builder((Component)Component.m_237110_((String)key, (Object[])args));
    }

    @Info(value="Join multiple parts into one Component. Parts can be Builder, Component, or any object (converted to literal).")
    public static Component join(Object ... parts) {
        MutableComponent out = Component.m_237119_();
        if (parts == null) {
            return out;
        }
        for (Object p : parts) {
            if (p == null) continue;
            if (p instanceof Builder) {
                Builder b = (Builder)p;
                out.m_7220_(b.build());
                continue;
            }
            if (p instanceof Component) {
                Component c = (Component)p;
                out.m_7220_(c);
                continue;
            }
            out.m_7220_((Component)Component.m_237113_((String)String.valueOf(p)));
        }
        return out;
    }

    private static Style styleFromHex(String hex) {
        if (hex == null || hex.isBlank()) {
            return Style.f_131099_;
        }
        int argb = ColorUtil.parseArgb(hex);
        int rgb = argb & 0xFFFFFF;
        TextColor color = TextColor.m_131266_((int)rgb);
        return Style.f_131099_.m_131148_(color);
    }

    public static final class Builder {
        private final MutableComponent component;

        private Builder(Component base) {
            this.component = base.m_6881_();
        }

        @Info(value="Set text color from a hex string. Accepts #RRGGBB / RRGGBB / #AARRGGBB, but only RGB is used.")
        public Builder colorHex(String hex) {
            Style style = TipText.styleFromHex(hex).m_131146_(this.component.m_7383_());
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Set text color from 0xRRGGBB.")
        public Builder colorRgb(int rgb) {
            TextColor color = TextColor.m_131266_((int)(rgb & 0xFFFFFF));
            Style style = this.component.m_7383_().m_131148_(color);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Apply a ChatFormatting by name (case-insensitive, e.g. 'gold'). Invalid values are ignored.")
        public Builder formatting(String formattingName) {
            ChatFormatting formatting;
            if (formattingName == null || formattingName.isBlank()) {
                return this;
            }
            try {
                formatting = ChatFormatting.valueOf((String)formattingName.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException e) {
                return this;
            }
            Style style = this.component.m_7383_().m_131157_(formatting);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Apply bold formatting.")
        public Builder bold() {
            Style style = this.component.m_7383_().m_131157_(ChatFormatting.BOLD);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Apply italic formatting.")
        public Builder italic() {
            Style style = this.component.m_7383_().m_131157_(ChatFormatting.ITALIC);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Apply underlined formatting.")
        public Builder underlined() {
            Style style = this.component.m_7383_().m_131157_(ChatFormatting.UNDERLINE);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Apply strikethrough formatting.")
        public Builder strikethrough() {
            Style style = this.component.m_7383_().m_131157_(ChatFormatting.STRIKETHROUGH);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Apply obfuscated formatting.")
        public Builder obfuscated() {
            Style style = this.component.m_7383_().m_131157_(ChatFormatting.OBFUSCATED);
            this.component.m_6270_(style);
            return this;
        }

        @Info(value="Append plain text.")
        public Builder appendText(String text) {
            if (text != null && !text.isEmpty()) {
                this.component.m_130946_(text);
            }
            return this;
        }

        @Info(value="Append a Component.")
        public Builder appendComponent(Component other) {
            if (other != null) {
                this.component.m_7220_(other);
            }
            return this;
        }

        @Info(value="Build the final Component.")
        public Component build() {
            return this.component;
        }
    }
}

