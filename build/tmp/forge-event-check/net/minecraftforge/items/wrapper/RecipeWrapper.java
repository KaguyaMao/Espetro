/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraftforge.items.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RecipeWrapper
implements Container {
    protected final IItemHandlerModifiable inv;

    public RecipeWrapper(IItemHandlerModifiable inv) {
        this.inv = inv;
    }

    public int m_6643_() {
        return this.inv.getSlots();
    }

    public ItemStack m_8020_(int slot) {
        return this.inv.getStackInSlot(slot);
    }

    public ItemStack m_7407_(int slot, int count) {
        ItemStack stack = this.inv.getStackInSlot(slot);
        return stack.m_41619_() ? ItemStack.f_41583_ : stack.m_41620_(count);
    }

    public void m_6836_(int slot, ItemStack stack) {
        this.inv.setStackInSlot(slot, stack);
    }

    public ItemStack m_8016_(int index) {
        ItemStack s = this.m_8020_(index);
        if (s.m_41619_()) {
            return ItemStack.f_41583_;
        }
        this.m_6836_(index, ItemStack.f_41583_);
        return s;
    }

    public boolean m_7983_() {
        for (int i = 0; i < this.inv.getSlots(); ++i) {
            if (this.inv.getStackInSlot(i).m_41619_()) continue;
            return false;
        }
        return true;
    }

    public boolean m_7013_(int slot, ItemStack stack) {
        return this.inv.isItemValid(slot, stack);
    }

    public void m_6211_() {
        for (int i = 0; i < this.inv.getSlots(); ++i) {
            this.inv.setStackInSlot(i, ItemStack.f_41583_);
        }
    }

    public int m_6893_() {
        return 0;
    }

    public void m_6596_() {
    }

    public boolean m_6542_(Player player) {
        return false;
    }

    public void m_5856_(Player player) {
    }

    public void m_5785_(Player player) {
    }
}

