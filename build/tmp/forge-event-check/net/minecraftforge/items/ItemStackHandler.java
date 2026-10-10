/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ItemStackHandler
implements IItemHandler,
IItemHandlerModifiable,
INBTSerializable<CompoundTag> {
    protected NonNullList<ItemStack> stacks;

    public ItemStackHandler() {
        this(1);
    }

    public ItemStackHandler(int size) {
        this.stacks = NonNullList.m_122780_((int)size, (Object)ItemStack.f_41583_);
    }

    public ItemStackHandler(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void setSize(int size) {
        this.stacks = NonNullList.m_122780_((int)size, (Object)ItemStack.f_41583_);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.validateSlotIndex(slot);
        this.stacks.set(slot, (Object)stack);
        this.onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return this.stacks.size();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        return (ItemStack)this.stacks.get(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        boolean reachedLimit;
        if (stack.m_41619_()) {
            return ItemStack.f_41583_;
        }
        if (!this.isItemValid(slot, stack)) {
            return stack;
        }
        this.validateSlotIndex(slot);
        ItemStack existing = (ItemStack)this.stacks.get(slot);
        int limit = this.getStackLimit(slot, stack);
        if (!existing.m_41619_()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }
            limit -= existing.m_41613_();
        }
        if (limit <= 0) {
            return stack;
        }
        boolean bl = reachedLimit = stack.m_41613_() > limit;
        if (!simulate) {
            if (existing.m_41619_()) {
                this.stacks.set(slot, (Object)(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack));
            } else {
                existing.m_41769_(reachedLimit ? limit : stack.m_41613_());
            }
            this.onContentsChanged(slot);
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.m_41613_() - limit) : ItemStack.f_41583_;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.f_41583_;
        }
        this.validateSlotIndex(slot);
        ItemStack existing = (ItemStack)this.stacks.get(slot);
        if (existing.m_41619_()) {
            return ItemStack.f_41583_;
        }
        int toExtract = Math.min(amount, existing.m_41741_());
        if (existing.m_41613_() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, (Object)ItemStack.f_41583_);
                this.onContentsChanged(slot);
                return existing;
            }
            return existing.m_41777_();
        }
        if (!simulate) {
            this.stacks.set(slot, (Object)ItemHandlerHelper.copyStackWithSize(existing, existing.m_41613_() - toExtract));
            this.onContentsChanged(slot);
        }
        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.m_41741_());
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.stacks.size(); ++i) {
            if (((ItemStack)this.stacks.get(i)).m_41619_()) continue;
            CompoundTag itemTag = new CompoundTag();
            itemTag.m_128405_("Slot", i);
            ((ItemStack)this.stacks.get(i)).m_41739_(itemTag);
            nbtTagList.add((Object)itemTag);
        }
        CompoundTag nbt = new CompoundTag();
        nbt.m_128365_("Items", (Tag)nbtTagList);
        nbt.m_128405_("Size", this.stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setSize(nbt.m_128425_("Size", 3) ? nbt.m_128451_("Size") : this.stacks.size());
        ListTag tagList = nbt.m_128437_("Items", 10);
        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.m_128728_(i);
            int slot = itemTags.m_128451_("Slot");
            if (slot < 0 || slot >= this.stacks.size()) continue;
            this.stacks.set(slot, (Object)ItemStack.m_41712_((CompoundTag)itemTags));
        }
        this.onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    protected void onLoad() {
    }

    protected void onContentsChanged(int slot) {
    }
}

