/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCapability
public interface IItemHandler {
    public int getSlots();

    @NotNull
    public ItemStack getStackInSlot(int var1);

    @NotNull
    public ItemStack insertItem(int var1, @NotNull ItemStack var2, boolean var3);

    @NotNull
    public ItemStack extractItem(int var1, int var2, boolean var3);

    public int getSlotLimit(int var1);

    public boolean isItemValid(int var1, @NotNull ItemStack var2);
}

