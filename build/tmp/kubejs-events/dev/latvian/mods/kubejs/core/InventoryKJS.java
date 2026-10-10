/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.Container
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface InventoryKJS {
    default public boolean kjs$isMutable() {
        return false;
    }

    default public int kjs$getSlots() {
        throw new NoMixinException();
    }

    default public ItemStack kjs$getStackInSlot(int slot) {
        throw new NoMixinException();
    }

    default public void kjs$setStackInSlot(int slot, ItemStack stack) {
        throw new IllegalStateException("This item handler can't be modified directly! Use insertItem or extractItem instead!");
    }

    default public ItemStack kjs$insertItem(int slot, ItemStack stack, boolean simulate) {
        throw new NoMixinException();
    }

    default public ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
        throw new NoMixinException();
    }

    default public ItemStack kjs$insertItem(ItemStack stack, boolean simulate) {
        if (stack.m_41619_()) {
            return stack;
        }
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            if (!(stack = this.kjs$insertItem(i, stack, simulate)).m_41619_()) continue;
            return ItemStack.f_41583_;
        }
        return stack;
    }

    default public int kjs$getSlotLimit(int slot) {
        throw new NoMixinException();
    }

    default public boolean kjs$isItemValid(int slot, ItemStack stack) {
        throw new NoMixinException();
    }

    default public int kjs$getWidth() {
        return Math.min(this.kjs$getSlots(), 9);
    }

    default public int kjs$getHeight() {
        return (this.kjs$getSlots() + 8) / 9;
    }

    default public void kjs$clear() {
        for (int i = this.kjs$getSlots(); i >= 0; --i) {
            if (this.kjs$isMutable()) {
                this.kjs$setStackInSlot(i, ItemStack.f_41583_);
                continue;
            }
            this.kjs$extractItem(i, this.kjs$getStackInSlot(i).m_41613_(), false);
        }
    }

    default public void kjs$clear(Ingredient ingredient) {
        if (ingredient.kjs$isWildcard()) {
            this.kjs$clear();
        }
        for (int i = this.kjs$getSlots(); i >= 0; --i) {
            if (!ingredient.test(this.kjs$getStackInSlot(i))) continue;
            if (this.kjs$isMutable()) {
                this.kjs$setStackInSlot(i, ItemStack.f_41583_);
                continue;
            }
            this.kjs$extractItem(i, this.kjs$getStackInSlot(i).m_41613_(), false);
        }
    }

    default public int kjs$find() {
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            ItemStack stack1 = this.kjs$getStackInSlot(i);
            if (stack1.m_41619_()) continue;
            return i;
        }
        return -1;
    }

    default public int kjs$find(Ingredient ingredient) {
        if (ingredient.kjs$isWildcard()) {
            return this.kjs$find();
        }
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            ItemStack stack1 = this.kjs$getStackInSlot(i);
            if (!ingredient.test(stack1)) continue;
            return i;
        }
        return -1;
    }

    default public int kjs$count() {
        int count = 0;
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            count += this.kjs$getStackInSlot(i).m_41613_();
        }
        return count;
    }

    default public int kjs$count(Ingredient ingredient) {
        if (ingredient.kjs$isWildcard()) {
            return this.kjs$count();
        }
        int count = 0;
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            ItemStack stack1 = this.kjs$getStackInSlot(i);
            if (!ingredient.test(stack1)) continue;
            count += stack1.m_41613_();
        }
        return count;
    }

    default public int kjs$countNonEmpty() {
        int count = 0;
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            if (this.kjs$getStackInSlot(i).m_41619_()) continue;
            ++count;
        }
        return count;
    }

    default public int kjs$countNonEmpty(Ingredient ingredient) {
        if (ingredient.kjs$isWildcard()) {
            return this.kjs$countNonEmpty();
        }
        int count = 0;
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            ItemStack stack1 = this.kjs$getStackInSlot(i);
            if (!ingredient.test(stack1)) continue;
            ++count;
        }
        return count;
    }

    default public boolean kjs$isEmpty() {
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            if (this.kjs$getStackInSlot(i).m_41619_()) continue;
            return false;
        }
        return true;
    }

    default public void kjs$setChanged() {
    }

    @Nullable
    default public BlockContainerJS kjs$getBlock(Level level) {
        return null;
    }

    default public List<ItemStack> kjs$getAllItems() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < this.kjs$getSlots(); ++i) {
            ItemStack is = this.kjs$getStackInSlot(i);
            if (is.m_41619_()) continue;
            list.add(is);
        }
        return list;
    }

    default public Container kjs$asContainer() {
        return null;
    }
}

