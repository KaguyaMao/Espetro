/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.ItemHandlerUtils;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={Container.class})
public interface ContainerMixin
extends InventoryKJS {
    default public Container kjs$self() {
        return (Container)this;
    }

    @Override
    default public boolean kjs$isMutable() {
        return true;
    }

    @Override
    default public int kjs$getSlots() {
        return this.kjs$self().m_6643_();
    }

    @Override
    @NotNull
    default public ItemStack kjs$getStackInSlot(int slot) {
        return this.kjs$self().m_8020_(slot);
    }

    @Override
    @NotNull
    default public ItemStack kjs$insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.m_41619_()) {
            return ItemStack.f_41583_;
        }
        ItemStack stackInSlot = this.kjs$self().m_8020_(slot);
        if (!stackInSlot.m_41619_()) {
            if (stackInSlot.m_41613_() >= Math.min(stackInSlot.m_41741_(), this.kjs$getSlotLimit(slot))) {
                return stack;
            }
            if (!ItemHandlerUtils.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }
            if (!this.kjs$self().m_7013_(slot, stack)) {
                return stack;
            }
            int m = Math.min(stack.m_41741_(), this.kjs$getSlotLimit(slot)) - stackInSlot.m_41613_();
            if (stack.m_41613_() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.m_41777_();
                    copy.m_41769_(stackInSlot.m_41613_());
                    this.kjs$self().m_6836_(slot, copy);
                    this.kjs$self().m_6596_();
                }
                return ItemStack.f_41583_;
            }
            stack = stack.m_41777_();
            if (!simulate) {
                ItemStack copy = stack.m_41620_(m);
                copy.m_41769_(stackInSlot.m_41613_());
                this.kjs$self().m_6836_(slot, copy);
                this.kjs$self().m_6596_();
            } else {
                stack.m_41774_(m);
            }
            return stack;
        }
        if (!this.kjs$self().m_7013_(slot, stack)) {
            return stack;
        }
        int m = Math.min(stack.m_41741_(), this.kjs$getSlotLimit(slot));
        if (m < stack.m_41613_()) {
            stack = stack.m_41777_();
            if (!simulate) {
                this.kjs$self().m_6836_(slot, stack.m_41620_(m));
                this.kjs$self().m_6596_();
                return stack;
            }
            stack.m_41774_(m);
            return stack;
        }
        if (!simulate) {
            this.kjs$self().m_6836_(slot, stack);
            this.kjs$self().m_6596_();
        }
        return ItemStack.f_41583_;
    }

    @Override
    @NotNull
    default public ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.f_41583_;
        }
        ItemStack stackInSlot = this.kjs$self().m_8020_(slot);
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
        ItemStack decrStackSize = this.kjs$self().m_7407_(slot, m);
        this.kjs$self().m_6596_();
        return decrStackSize;
    }

    @Override
    default public void kjs$setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.kjs$self().m_6836_(slot, stack);
    }

    @Override
    default public int kjs$getSlotLimit(int slot) {
        return this.kjs$self().m_6893_();
    }

    @Override
    default public boolean kjs$isItemValid(int slot, @NotNull ItemStack stack) {
        return this.kjs$self().m_7013_(slot, stack);
    }

    @Override
    default public int kjs$getWidth() {
        int n;
        Container container = this.kjs$self();
        if (container instanceof CraftingContainer) {
            CraftingContainer crafter = (CraftingContainer)container;
            n = crafter.m_39347_();
        } else {
            n = InventoryKJS.super.kjs$getWidth();
        }
        return n;
    }

    @Override
    default public int kjs$getHeight() {
        int n;
        Container container = this.kjs$self();
        if (container instanceof CraftingContainer) {
            CraftingContainer crafter = (CraftingContainer)container;
            n = crafter.m_39346_();
        } else {
            n = InventoryKJS.super.kjs$getHeight();
        }
        return n;
    }

    @Override
    default public void kjs$clear() {
        this.kjs$self().m_6211_();
    }

    @Override
    default public void kjs$setChanged() {
        this.kjs$self().m_6596_();
        Container container = this.kjs$self();
        if (container instanceof Inventory) {
            Inventory inv = (Inventory)container;
            inv.f_35978_.kjs$sendInventoryUpdate();
        }
    }

    @Override
    @Nullable
    default public BlockContainerJS kjs$getBlock(Level level) {
        Container container = this.kjs$self();
        if (container instanceof BlockEntity) {
            BlockEntity be = (BlockEntity)container;
            return level.kjs$getBlock(be);
        }
        return null;
    }

    @Override
    default public Container kjs$asContainer() {
        return this.kjs$self();
    }
}

