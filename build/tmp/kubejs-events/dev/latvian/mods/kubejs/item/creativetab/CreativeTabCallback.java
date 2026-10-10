/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.CreativeModeTab$TabVisibility
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.item.creativetab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface CreativeTabCallback {
    public void addAfter(ItemStack var1, ItemStack[] var2, CreativeModeTab.TabVisibility var3);

    public void addBefore(ItemStack var1, ItemStack[] var2, CreativeModeTab.TabVisibility var3);

    public void remove(Ingredient var1, boolean var2, boolean var3);
}

