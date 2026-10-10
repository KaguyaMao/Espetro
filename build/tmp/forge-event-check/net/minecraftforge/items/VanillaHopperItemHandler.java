/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class VanillaHopperItemHandler
extends InvWrapper {
    private final HopperBlockEntity hopper;

    public VanillaHopperItemHandler(HopperBlockEntity hopper) {
        super((Container)hopper);
        this.hopper = hopper;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (simulate) {
            return super.insertItem(slot, stack, simulate);
        }
        boolean wasEmpty = this.getInv().m_7983_();
        int originalStackSize = stack.m_41613_();
        stack = super.insertItem(slot, stack, simulate);
        if (wasEmpty && originalStackSize > stack.m_41613_() && !this.hopper.m_59409_()) {
            this.hopper.m_59395_(8);
        }
        return stack;
    }
}

