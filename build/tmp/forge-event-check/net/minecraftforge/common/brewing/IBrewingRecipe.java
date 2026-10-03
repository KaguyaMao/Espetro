/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraftforge.common.brewing;

import net.minecraft.world.item.ItemStack;

public interface IBrewingRecipe {
    public boolean isInput(ItemStack var1);

    public boolean isIngredient(ItemStack var1);

    public ItemStack getOutput(ItemStack var1, ItemStack var2);
}

