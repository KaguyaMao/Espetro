/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items.wrapper;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;

public class CombinedInvWrapper
implements IItemHandlerModifiable {
    protected final IItemHandlerModifiable[] itemHandler;
    protected final int[] baseIndex;
    protected final int slotCount;

    public CombinedInvWrapper(IItemHandlerModifiable ... itemHandler) {
        this.itemHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; ++i) {
            this.baseIndex[i] = index += itemHandler[i].getSlots();
        }
        this.slotCount = index;
    }

    protected int getIndexForSlot(int slot) {
        if (slot < 0) {
            return -1;
        }
        for (int i = 0; i < this.baseIndex.length; ++i) {
            if (slot - this.baseIndex[i] >= 0) continue;
            return i;
        }
        return -1;
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index) {
        if (index < 0 || index >= this.itemHandler.length) {
            return (IItemHandlerModifiable)EmptyHandler.INSTANCE;
        }
        return this.itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index) {
        if (index <= 0 || index >= this.baseIndex.length) {
            return slot;
        }
        return slot - this.baseIndex[index - 1];
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.slotCount;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        slot = this.getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int index = this.getIndexForSlot(slot);
        IItemHandlerModifiable handler = this.getHandlerFromIndex(index);
        int localSlot = this.getSlotFromIndex(slot, index);
        return handler.isItemValid(localSlot, stack);
    }
}

