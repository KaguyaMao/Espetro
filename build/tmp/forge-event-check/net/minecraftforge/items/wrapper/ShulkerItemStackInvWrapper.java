/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.items.wrapper;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ShulkerItemStackInvWrapper
implements IItemHandlerModifiable,
ICapabilityProvider {
    private final ItemStack stack;
    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> this);
    private CompoundTag cachedTag;
    private NonNullList<ItemStack> itemStacksCache;

    @ApiStatus.Internal
    @Nullable
    public static ICapabilityProvider createDefaultProvider(ItemStack itemStack) {
        Item item = itemStack.m_41720_();
        if (item == Items.f_42265_ || item == Items.f_42229_ || item == Items.f_42225_ || item == Items.f_42226_ || item == Items.f_42275_ || item == Items.f_42273_ || item == Items.f_42227_ || item == Items.f_42269_ || item == Items.f_42274_ || item == Items.f_42271_ || item == Items.f_42268_ || item == Items.f_42267_ || item == Items.f_42272_ || item == Items.f_42224_ || item == Items.f_42228_ || item == Items.f_42266_ || item == Items.f_42270_) {
            return new ShulkerItemStackInvWrapper(itemStack);
        }
        return null;
    }

    private ShulkerItemStackInvWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        return (ItemStack)this.getItemList().get(slot);
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
        NonNullList<ItemStack> itemStacks = this.getItemList();
        ItemStack existing = (ItemStack)itemStacks.get(slot);
        int limit = Math.min(this.getSlotLimit(slot), stack.m_41741_());
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
                itemStacks.set(slot, (Object)(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack));
            } else {
                existing.m_41769_(reachedLimit ? limit : stack.m_41613_());
            }
            this.setItemList(itemStacks);
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.m_41613_() - limit) : ItemStack.f_41583_;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        NonNullList<ItemStack> itemStacks = this.getItemList();
        if (amount == 0) {
            return ItemStack.f_41583_;
        }
        this.validateSlotIndex(slot);
        ItemStack existing = (ItemStack)itemStacks.get(slot);
        if (existing.m_41619_()) {
            return ItemStack.f_41583_;
        }
        int toExtract = Math.min(amount, existing.m_41741_());
        if (existing.m_41613_() <= toExtract) {
            if (!simulate) {
                itemStacks.set(slot, (Object)ItemStack.f_41583_);
                this.setItemList(itemStacks);
                return existing;
            }
            return existing.m_41777_();
        }
        if (!simulate) {
            itemStacks.set(slot, (Object)ItemHandlerHelper.copyStackWithSize(existing, existing.m_41613_() - toExtract));
            this.setItemList(itemStacks);
        }
        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.getSlots()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.m_41720_().m_142095_();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.validateSlotIndex(slot);
        if (!this.isItemValid(slot, stack)) {
            throw new RuntimeException("Invalid stack " + String.valueOf(stack) + " for slot " + slot + ")");
        }
        NonNullList<ItemStack> itemStacks = this.getItemList();
        itemStacks.set(slot, (Object)stack);
        this.setItemList(itemStacks);
    }

    private NonNullList<ItemStack> getItemList() {
        CompoundTag rootTag = BlockItem.m_186336_((ItemStack)this.stack);
        if (this.cachedTag == null || !this.cachedTag.equals((Object)rootTag)) {
            this.itemStacksCache = this.refreshItemList(rootTag);
        }
        return this.itemStacksCache;
    }

    private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag) {
        NonNullList itemStacks = NonNullList.m_122780_((int)this.getSlots(), (Object)ItemStack.f_41583_);
        if (rootTag != null && rootTag.m_128425_("Items", 9)) {
            ContainerHelper.m_18980_((CompoundTag)rootTag, (NonNullList)itemStacks);
        }
        this.cachedTag = rootTag;
        return itemStacks;
    }

    private void setItemList(NonNullList<ItemStack> itemStacks) {
        CompoundTag existing = BlockItem.m_186336_((ItemStack)this.stack);
        CompoundTag rootTag = ContainerHelper.m_18973_((CompoundTag)(existing == null ? new CompoundTag() : existing), itemStacks);
        BlockItem.m_186338_((ItemStack)this.stack, (BlockEntityType)BlockEntityType.f_58939_, (CompoundTag)rootTag);
        this.cachedTag = rootTag;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, this.holder);
    }
}

