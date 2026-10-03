/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items.wrapper;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class RangedWrapper
implements IItemHandlerModifiable {
    private final IItemHandlerModifiable compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        Preconditions.checkArgument((maxSlotExclusive > minSlot ? 1 : 0) != 0, (Object)"Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    @Override
    public int getSlots() {
        return this.maxSlot - this.minSlot;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        if (this.checkSlot(slot)) {
            return this.compose.getStackInSlot(slot + this.minSlot);
        }
        return ItemStack.f_41583_;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (this.checkSlot(slot)) {
            return this.compose.insertItem(slot + this.minSlot, stack, simulate);
        }
        return stack;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.checkSlot(slot)) {
            return this.compose.extractItem(slot + this.minSlot, amount, simulate);
        }
        return ItemStack.f_41583_;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (this.checkSlot(slot)) {
            this.compose.setStackInSlot(slot + this.minSlot, stack);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (this.checkSlot(slot)) {
            return this.compose.getSlotLimit(slot + this.minSlot);
        }
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (this.checkSlot(slot)) {
            return this.compose.isItemValid(slot + this.minSlot, stack);
        }
        return false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + this.minSlot < this.maxSlot;
    }
}

