/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
 *  net.minecraft.world.level.block.entity.BrewingStandBlockEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.items.wrapper;

import java.util.function.IntUnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SidedInvWrapper
implements IItemHandlerModifiable {
    protected final WorldlyContainer inv;
    @Nullable
    protected final Direction side;
    private final IntUnaryOperator slotLimit;
    private final InsertLimit newStackInsertLimit;

    public static LazyOptional<IItemHandlerModifiable>[] create(WorldlyContainer inv, Direction ... sides) {
        LazyOptional[] ret = new LazyOptional[sides.length];
        for (int x = 0; x < sides.length; ++x) {
            Direction side = sides[x];
            ret[x] = LazyOptional.of(() -> new SidedInvWrapper(inv, side));
        }
        return ret;
    }

    public SidedInvWrapper(WorldlyContainer inv, @Nullable Direction side) {
        this.inv = inv;
        this.side = side;
        this.slotLimit = inv instanceof BrewingStandBlockEntity ? wrapperSlot -> SidedInvWrapper.getSlot(inv, wrapperSlot, side) < 3 ? 1 : inv.m_6893_() : wrapperSlot -> inv.m_6893_();
        this.newStackInsertLimit = inv instanceof AbstractFurnaceBlockEntity ? (wrapperSlot, invSlot, stack) -> invSlot == 1 && stack.m_150930_(Items.f_42446_) ? 1 : Math.min(stack.m_41741_(), this.getSlotLimit(wrapperSlot)) : (wrapperSlot, invSlot, stack) -> Math.min(stack.m_41741_(), this.getSlotLimit(wrapperSlot));
    }

    public static int getSlot(WorldlyContainer inv, int slot, @Nullable Direction side) {
        int[] slots = inv.m_7071_(side);
        if (slot < slots.length) {
            return slots[slot];
        }
        return -1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SidedInvWrapper that = (SidedInvWrapper)o;
        return this.inv.equals(that.inv) && this.side == that.side;
    }

    public int hashCode() {
        int result = this.inv.hashCode();
        result = 31 * result + (this.side == null ? 0 : this.side.hashCode());
        return result;
    }

    @Override
    public int getSlots() {
        return this.inv.m_7071_(this.side).length;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        int i = SidedInvWrapper.getSlot(this.inv, slot, this.side);
        return i == -1 ? ItemStack.f_41583_ : this.inv.m_8020_(i);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.m_41619_()) {
            return ItemStack.f_41583_;
        }
        int slot1 = SidedInvWrapper.getSlot(this.inv, slot, this.side);
        if (slot1 == -1) {
            return stack;
        }
        ItemStack stackInSlot = this.inv.m_8020_(slot1);
        if (!stackInSlot.m_41619_()) {
            if (stackInSlot.m_41613_() >= Math.min(stackInSlot.m_41741_(), this.getSlotLimit(slot))) {
                return stack;
            }
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }
            if (!this.inv.m_7155_(slot1, stack, this.side) || !this.inv.m_7013_(slot1, stack)) {
                return stack;
            }
            int m = Math.min(stack.m_41741_(), this.getSlotLimit(slot)) - stackInSlot.m_41613_();
            if (stack.m_41613_() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.m_41777_();
                    copy.m_41769_(stackInSlot.m_41613_());
                    this.setInventorySlotContents(slot1, copy);
                }
                return ItemStack.f_41583_;
            }
            stack = stack.m_41777_();
            if (!simulate) {
                ItemStack copy = stack.m_41620_(m);
                copy.m_41769_(stackInSlot.m_41613_());
                this.setInventorySlotContents(slot1, copy);
                return stack;
            }
            stack.m_41774_(m);
            return stack;
        }
        if (!this.inv.m_7155_(slot1, stack, this.side) || !this.inv.m_7013_(slot1, stack)) {
            return stack;
        }
        int m = this.newStackInsertLimit.limitInsert(slot, slot1, stack);
        if (m < stack.m_41613_()) {
            stack = stack.m_41777_();
            if (!simulate) {
                this.setInventorySlotContents(slot1, stack.m_41620_(m));
                return stack;
            }
            stack.m_41774_(m);
            return stack;
        }
        if (!simulate) {
            this.setInventorySlotContents(slot1, stack);
        }
        return ItemStack.f_41583_;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int slot1 = SidedInvWrapper.getSlot(this.inv, slot, this.side);
        if (slot1 != -1) {
            this.setInventorySlotContents(slot1, stack);
        }
    }

    private void setInventorySlotContents(int slot, ItemStack stack) {
        this.inv.m_6596_();
        this.inv.m_6836_(slot, stack);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.f_41583_;
        }
        int slot1 = SidedInvWrapper.getSlot(this.inv, slot, this.side);
        if (slot1 == -1) {
            return ItemStack.f_41583_;
        }
        ItemStack stackInSlot = this.inv.m_8020_(slot1);
        if (stackInSlot.m_41619_()) {
            return ItemStack.f_41583_;
        }
        if (!this.inv.m_7157_(slot1, stackInSlot, this.side)) {
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
        ItemStack ret = this.inv.m_7407_(slot1, m);
        this.inv.m_6596_();
        return ret;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotLimit.applyAsInt(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int slot1 = SidedInvWrapper.getSlot(this.inv, slot, this.side);
        return slot1 == -1 ? false : this.inv.m_7013_(slot1, stack);
    }

    private static interface InsertLimit {
        public int limitInsert(int var1, int var2, ItemStack var3);
    }
}

