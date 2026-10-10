/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items.wrapper;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class InvWrapper
implements IItemHandlerModifiable {
    private final Container inv;

    public InvWrapper(Container inv) {
        this.inv = inv;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InvWrapper that = (InvWrapper)o;
        return this.getInv().equals(that.getInv());
    }

    public int hashCode() {
        return this.getInv().hashCode();
    }

    @Override
    public int getSlots() {
        return this.getInv().m_6643_();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return this.getInv().m_8020_(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.m_41619_()) {
            return ItemStack.f_41583_;
        }
        ItemStack stackInSlot = this.getInv().m_8020_(slot);
        if (!stackInSlot.m_41619_()) {
            if (stackInSlot.m_41613_() >= Math.min(stackInSlot.m_41741_(), this.getSlotLimit(slot))) {
                return stack;
            }
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }
            if (!this.getInv().m_7013_(slot, stack)) {
                return stack;
            }
            int m = Math.min(stack.m_41741_(), this.getSlotLimit(slot)) - stackInSlot.m_41613_();
            if (stack.m_41613_() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.m_41777_();
                    copy.m_41769_(stackInSlot.m_41613_());
                    this.getInv().m_6836_(slot, copy);
                    this.getInv().m_6596_();
                }
                return ItemStack.f_41583_;
            }
            stack = stack.m_41777_();
            if (!simulate) {
                ItemStack copy = stack.m_41620_(m);
                copy.m_41769_(stackInSlot.m_41613_());
                this.getInv().m_6836_(slot, copy);
                this.getInv().m_6596_();
                return stack;
            }
            stack.m_41774_(m);
            return stack;
        }
        if (!this.getInv().m_7013_(slot, stack)) {
            return stack;
        }
        int m = Math.min(stack.m_41741_(), this.getSlotLimit(slot));
        if (m < stack.m_41613_()) {
            stack = stack.m_41777_();
            if (!simulate) {
                this.getInv().m_6836_(slot, stack.m_41620_(m));
                this.getInv().m_6596_();
                return stack;
            }
            stack.m_41774_(m);
            return stack;
        }
        if (!simulate) {
            this.getInv().m_6836_(slot, stack);
            this.getInv().m_6596_();
        }
        return ItemStack.f_41583_;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.f_41583_;
        }
        ItemStack stackInSlot = this.getInv().m_8020_(slot);
        if (stackInSlot.m_41619_()) {
            return ItemStack.f_41583_;
        }
        if (simulate) {
            if (stackInSlot.m_41613_() < amount) {
                return stackInSlot.m_41777_();
            }
            ItemStack copy = stackInSlot.m_41777_();
            copy.m_41764_(amount);
            return copy;
        }
        int m = Math.min(stackInSlot.m_41613_(), amount);
        ItemStack decrStackSize = this.getInv().m_7407_(slot, m);
        this.getInv().m_6596_();
        return decrStackSize;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.getInv().m_6836_(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getInv().m_6893_();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.getInv().m_7013_(slot, stack);
    }

    public Container getInv() {
        return this.inv;
    }
}

