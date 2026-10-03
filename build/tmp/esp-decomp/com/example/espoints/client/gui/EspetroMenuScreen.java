/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  org.espetro.client.aui.AuiScreen
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import com.example.espoints.client.gui.HcrAuiWidgets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.AuiScreen;
import org.espetro.client.aui.GuiElement;

abstract class EspetroMenuScreen
extends AuiScreen {
    protected EspetroMenuScreen(Component title) {
        super(title);
    }

    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        HcrAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    protected abstract void buildMenuRoot(GuiElement var1);
}

