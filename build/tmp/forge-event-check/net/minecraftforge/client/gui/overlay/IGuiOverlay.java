/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package net.minecraftforge.client.gui.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@FunctionalInterface
public interface IGuiOverlay {
    public void render(ForgeGui var1, GuiGraphics var2, float var3, int var4, int var5);
}

