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
package dev.latvian.mods.kubejs.gui;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryKJSSlot
extends Slot {
    public final InventoryKJS inventory;
    public final int invIndex;

    public InventoryKJSSlot(InventoryKJS inventory, int invIndex, int xPosition, int yPosition) {
        super((Container)KubeJSGUI.EMPTY_CONTAINER, invIndex, xPosition, yPosition);
        this.inventory = inventory;
        this.invIndex = invIndex;
    }

    public boolean m_5857_(@NotNull ItemStack stack) {
        if (stack.m_41619_()) {
            return false;
        }
        return this.inventory.kjs$isItemValid(this.invIndex, stack);
    }

    @NotNull
    public ItemStack m_7993_() {
        return this.inventory.kjs$getStackInSlot(this.invIndex);
    }

    public void m_5852_(@NotNull ItemStack stack) {
        this.inventory.kjs$setStackInSlot(this.invIndex, stack);
        this.m_6654_();
    }

    public void m_40234_(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
    }

    public void m_6654_() {
        this.inventory.kjs$setChanged();
    }

    public int m_6641_() {
        return this.inventory.kjs$getSlotLimit(this.invIndex);
    }

    public int m_5866_(@NotNull ItemStack stack) {
        ItemStack maxAdd = stack.m_41777_();
        int maxInput = stack.m_41741_();
        maxAdd.m_41764_(maxInput);
        ItemStack currentStack = this.inventory.kjs$getStackInSlot(this.invIndex);
        if (this.inventory.kjs$isMutable()) {
            this.inventory.kjs$setStackInSlot(this.invIndex, ItemStack.f_41583_);
            ItemStack remainder = this.inventory.kjs$insertItem(this.invIndex, maxAdd, true);
            this.inventory.kjs$setStackInSlot(this.invIndex, currentStack);
            return maxInput - remainder.m_41613_();
        }
        ItemStack remainder = this.inventory.kjs$insertItem(this.invIndex, maxAdd, true);
        int current = currentStack.m_41613_();
        int added = maxInput - remainder.m_41613_();
        return current + added;
    }

    public boolean m_8010_(Player playerIn) {
        return !this.inventory.kjs$extractItem(this.invIndex, 1, true).m_41619_();
    }

    @NotNull
    public ItemStack m_6201_(int amount) {
        return this.inventory.kjs$extractItem(this.invIndex, amount, false);
    }
}

