/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChestMenuContainerSlot
extends Slot {
    public final CustomChestMenu menu;
    public final int _index;

    public ChestMenuContainerSlot(CustomChestMenu menu, int index, int xPosition, int yPosition) {
        super((Container)KubeJSGUI.EMPTY_CONTAINER, index, xPosition, yPosition);
        this.menu = menu;
        this._index = index;
    }

    public boolean m_5857_(@NotNull ItemStack stack) {
        return false;
    }

    @NotNull
    public ItemStack m_7993_() {
        return this.menu.data.slots[this._index].getItem();
    }

    public void m_5852_(@NotNull ItemStack stack) {
        this.menu.data.slots[this._index].setItem(stack);
    }

    public void m_40234_(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
    }

    public int m_6641_() {
        return Integer.MAX_VALUE;
    }

    public int m_5866_(@NotNull ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    public boolean m_8010_(Player playerIn) {
        return false;
    }

    @NotNull
    public ItemStack m_6201_(int amount) {
        return ItemStack.f_41583_;
    }
}

