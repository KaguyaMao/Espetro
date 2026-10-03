/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.gui.screens.inventory.MenuAccess
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package dev.latvian.mods.kubejs.gui;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class KubeJSScreen
extends AbstractContainerScreen<KubeJSMenu>
implements MenuAccess<KubeJSMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    public final int containerRows;

    public KubeJSScreen(KubeJSMenu menu, Inventory inventory, Component component) {
        super((AbstractContainerMenu)menu, inventory, component);
        this.f_97726_ = menu.guiData.width;
        this.f_97727_ = menu.guiData.height;
        this.containerRows = (menu.guiData.inventory.kjs$getSlots() + 8) / 9;
        this.f_97727_ = 114 + this.containerRows * 18;
        this.f_97731_ = this.f_97727_ - 94;
    }

    public void m_88315_(GuiGraphics guiGraphics, int i, int j, float f) {
        this.m_280273_(guiGraphics);
        super.m_88315_(guiGraphics, i, j, f);
        this.m_280072_(guiGraphics, i, j);
    }

    protected void m_7286_(GuiGraphics guiGraphics, float f, int i, int j) {
        int k = (this.f_96543_ - this.f_97726_) / 2;
        int l = (this.f_96544_ - this.f_97727_) / 2;
        guiGraphics.m_280218_(CONTAINER_BACKGROUND, k, l, 0, 0, this.f_97726_, this.containerRows * 18 + 17);
        guiGraphics.m_280218_(CONTAINER_BACKGROUND, k, l + this.containerRows * 18 + 17, 0, 126, this.f_97726_, 96);
    }
}

