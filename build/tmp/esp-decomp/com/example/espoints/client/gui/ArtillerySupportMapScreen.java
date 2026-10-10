/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import com.example.espoints.client.gui.EspetroMenuScreen;
import com.example.espoints.client.gui.HcrAuiWidgets;
import com.example.espoints.hud.TacticalMapHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

public class ArtillerySupportMapScreen
extends EspetroMenuScreen {
    private int mapX;
    private int mapY;
    private int mapW;
    private int mapH;

    public ArtillerySupportMapScreen() {
        super((Component)Component.m_237113_((String)"\u706b\u70ae\u652f\u63f4"));
    }

    public static void open() {
        Minecraft.m_91087_().m_91152_((Screen)new ArtillerySupportMapScreen());
    }

    protected void m_7856_() {
        super.m_7856_();
        TacticalMapHUD.getInstance().beginArtillerySelection();
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.computeMapBounds();
        root.addChild((GuiElement)HcrAuiWidgets.panel(this.mapX, this.mapY, this.mapW, this.mapH, 0, -1525668));
        root.addChild((GuiElement)HcrAuiWidgets.text(8, 7, "\u00a76\u00a7l\u706b\u70ae\u652f\u63f4\u9009\u70b9", -14490));
        root.addChild((GuiElement)HcrAuiWidgets.text(8, 20, "\u00a77\u53f3\u952e\u9009\u62e9\u76ee\u6807\uff0c\u6eda\u8f6e\u7f29\u653e\u5730\u56fe", -5722440));
        root.addChild((GuiElement)HcrAuiWidgets.button(this.f_96543_ - 50, 6, 42, 18, "\u8fd4\u56de", () -> ((ArtillerySupportMapScreen)this).m_7379_()));
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        HcrAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
        this.computeMapBounds();
        TacticalMapHUD.getInstance().renderArtillerySelectionMap(graphics, this.mapX, this.mapY, this.mapW, this.mapH, partialTick);
    }

    private void computeMapBounds() {
        this.mapX = 6;
        this.mapY = 36;
        this.mapW = Math.max(1, this.f_96543_ - 12);
        this.mapH = Math.max(1, this.f_96544_ - this.mapY - 8);
    }

    public void m_7861_() {
        TacticalMapHUD.getInstance().endArtillerySelection();
        super.m_7861_();
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button == 1 && TacticalMapHUD.getInstance().submitArtillerySelectionTarget(mouseX, mouseY)) {
            return true;
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (TacticalMapHUD.getInstance().zoomArtillerySelectionMap(mouseX, mouseY, delta)) {
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    public boolean m_7043_() {
        return false;
    }
}

