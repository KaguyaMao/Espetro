/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.gui.chest;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChestMenuInventoryClickEvent {
    private final Slot slot;
    public final ClickType type;
    public final int button;

    public ChestMenuInventoryClickEvent(Slot slot, ClickType type, int button) {
        this.slot = slot;
        this.type = type;
        this.button = button;
    }

    public int getIndex() {
        return this.slot.m_150661_();
    }

    public ItemStack getItem() {
        return this.slot.m_7993_();
    }

    public void setItem(ItemStack item) {
        this.slot.m_5852_(item);
    }

    public static interface Callback {
        public void onClick(ChestMenuInventoryClickEvent var1);
    }
}

