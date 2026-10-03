/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.item;

import com.google.common.base.Preconditions;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RangedWrapper
implements InventoryKJS {
    private final InventoryKJS compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(InventoryKJS compose, int minSlot, int maxSlotExclusive) {
        Preconditions.checkArgument((maxSlotExclusive > minSlot ? 1 : 0) != 0, (Object)"Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    @Override
    public boolean kjs$isMutable() {
        return this.compose.kjs$isMutable();
    }

    @Override
    public int kjs$getSlots() {
        return this.maxSlot - this.minSlot;
    }

    @Override
    @NotNull
    public ItemStack kjs$getStackInSlot(int slot) {
        if (this.checkSlot(slot)) {
            return this.compose.kjs$getStackInSlot(slot + this.minSlot);
        }
        return ItemStack.f_41583_;
    }

    @Override
    @NotNull
    public ItemStack kjs$insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (this.checkSlot(slot)) {
            return this.compose.kjs$insertItem(slot + this.minSlot, stack, simulate);
        }
        return stack;
    }

    @Override
    @NotNull
    public ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
        if (this.checkSlot(slot)) {
            return this.compose.kjs$extractItem(slot + this.minSlot, amount, simulate);
        }
        return ItemStack.f_41583_;
    }

    @Override
    public void kjs$setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (this.checkSlot(slot)) {
            this.compose.kjs$setStackInSlot(slot + this.minSlot, stack);
        }
    }

    @Override
    public int kjs$getSlotLimit(int slot) {
        if (this.checkSlot(slot)) {
            return this.compose.kjs$getSlotLimit(slot + this.minSlot);
        }
        return 0;
    }

    @Override
    public boolean kjs$isItemValid(int slot, @NotNull ItemStack stack) {
        if (this.checkSlot(slot)) {
            return this.compose.kjs$isItemValid(slot + this.minSlot, stack);
        }
        return false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + this.minSlot < this.maxSlot;
    }
}

