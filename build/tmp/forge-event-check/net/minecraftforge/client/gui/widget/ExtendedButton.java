/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$Builder
 *  net.minecraft.client.gui.components.Button$CreateNarration
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 */
package net.minecraftforge.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class ExtendedButton
extends Button {
    public ExtendedButton(int xPos, int yPos, int width, int height, Component displayString, Button.OnPress handler) {
        this(xPos, yPos, width, height, displayString, handler, f_252438_);
    }

    public ExtendedButton(int xPos, int yPos, int width, int height, Component displayString, Button.OnPress handler, Button.CreateNarration createNarration) {
        super(xPos, yPos, width, height, displayString, handler, createNarration);
    }

    public ExtendedButton(Button.Builder builder) {
        super(builder);
    }

    public void m_87963_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.m_91087_();
        int k = !this.f_93623_ ? 0 : (this.m_198029_() ? 2 : 1);
        guiGraphics.blitWithBorder(f_93617_, this.m_252754_(), this.m_252907_(), 0, 46 + k * 20, this.f_93618_, this.f_93619_, 200, 20, 2, 3, 2, 2);
        FormattedText buttonText = mc.f_91062_.ellipsize((FormattedText)this.m_6035_(), this.f_93618_ - 6);
        guiGraphics.m_280364_(mc.f_91062_, Language.m_128107_().m_5536_(buttonText), this.m_252754_() + this.f_93618_ / 2, this.m_252907_() + (this.f_93619_ - 8) / 2, this.getFGColor());
    }
}

