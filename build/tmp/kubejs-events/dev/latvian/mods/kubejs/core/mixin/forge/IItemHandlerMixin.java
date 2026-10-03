/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.IItemHandlerModifiable
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={IItemHandler.class}, remap=false)
public interface IItemHandlerMixin
extends InventoryKJS {
    default public IItemHandler kjs$self() {
        return (IItemHandler)this;
    }

    @Override
    default public boolean kjs$isMutable() {
        return this.kjs$self() instanceof IItemHandlerModifiable;
    }

    @Override
    default public int kjs$getSlots() {
        return this.kjs$self().getSlots();
    }

    @Override
    default public ItemStack kjs$getStackInSlot(int i) {
        return this.kjs$self().getStackInSlot(i);
    }

    @Override
    default public void kjs$setStackInSlot(int slot, ItemStack stack) {
        IItemHandler iItemHandler = this.kjs$self();
        if (iItemHandler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable mod = (IItemHandlerModifiable)iItemHandler;
            mod.setStackInSlot(slot, stack);
        } else {
            InventoryKJS.super.kjs$setStackInSlot(slot, stack);
        }
    }

    @Override
    default public ItemStack kjs$insertItem(int i, ItemStack itemStack, boolean b) {
        return this.kjs$self().insertItem(i, itemStack, b);
    }

    @Override
    default public ItemStack kjs$extractItem(int i, int i1, boolean b) {
        return this.kjs$self().extractItem(i, i1, b);
    }

    @Override
    default public int kjs$getSlotLimit(int i) {
        return this.kjs$self().getSlotLimit(i);
    }

    @Override
    default public boolean kjs$isItemValid(int i, ItemStack itemStack) {
        return this.kjs$self().isItemValid(i, itemStack);
    }

    @Override
    @Nullable
    default public BlockContainerJS kjs$getBlock(Level level) {
        IItemHandler iItemHandler = this.kjs$self();
        if (iItemHandler instanceof BlockEntity) {
            BlockEntity entity = (BlockEntity)iItemHandler;
            return level.kjs$getBlock(entity);
        }
        return null;
    }
}

