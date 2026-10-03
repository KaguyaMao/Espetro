/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 */
package net.minecraftforge.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class UnicodeGlyphButton
extends ExtendedButton {
    public String glyph;
    public float glyphScale;

    public UnicodeGlyphButton(int xPos, int yPos, int width, int height, Component displayString, String glyph, float glyphScale, Button.OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.f_93624_) {
            Minecraft mc = Minecraft.m_91087_();
            boolean bl = this.f_93622_ = mouseX >= this.m_252754_() && mouseY >= this.m_252907_() && mouseX < this.m_252754_() + this.f_93618_ && mouseY < this.m_252907_() + this.f_93619_;
            int k = !this.f_93623_ ? 0 : (this.m_198029_() ? 2 : 1);
            guiGraphics.blitWithBorder(f_93617_, this.m_252754_(), this.m_252907_(), 0, 46 + k * 20, this.f_93618_, this.f_93619_, 200, 20, 2, 3, 2, 2);
            MutableComponent buttonText = this.m_5646_();
            int glyphWidth = (int)((float)mc.f_91062_.m_92895_(this.glyph) * this.glyphScale);
            int strWidth = mc.f_91062_.m_92852_((FormattedText)buttonText);
            int ellipsisWidth = mc.f_91062_.m_92895_("...");
            int totalWidth = strWidth + glyphWidth;
            if (totalWidth > this.f_93618_ - 6 && totalWidth > ellipsisWidth) {
                buttonText = Component.m_237113_((String)(mc.f_91062_.m_92854_((FormattedText)buttonText, this.f_93618_ - 6 - ellipsisWidth).getString().trim() + "..."));
            }
            strWidth = mc.f_91062_.m_92852_((FormattedText)buttonText);
            totalWidth = glyphWidth + strWidth;
            guiGraphics.m_280168_().m_85836_();
            guiGraphics.m_280168_().m_85841_(this.glyphScale, this.glyphScale, 1.0f);
            guiGraphics.m_280653_(mc.f_91062_, (Component)Component.m_237113_((String)this.glyph), (int)((float)(this.m_252754_() + this.f_93618_ / 2 - strWidth / 2) / this.glyphScale - (float)glyphWidth / (2.0f * this.glyphScale) + 2.0f), (int)(((float)this.m_252907_() + (float)(this.f_93619_ - 8) / this.glyphScale / 2.0f - 1.0f) / this.glyphScale), this.getFGColor());
            guiGraphics.m_280168_().m_85849_();
            guiGraphics.m_280653_(mc.f_91062_, (Component)buttonText, (int)((float)(this.m_252754_() + this.f_93618_ / 2) + (float)glyphWidth / this.glyphScale), this.m_252907_() + (this.f_93619_ - 8) / 2, this.getFGColor());
        }
    }
}

