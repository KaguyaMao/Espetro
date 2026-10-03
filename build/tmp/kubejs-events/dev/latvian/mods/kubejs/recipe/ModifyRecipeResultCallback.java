/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.ModifyRecipeCraftingGrid;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ModifyRecipeResultCallback {
    public ItemStack modify(ModifyRecipeCraftingGrid var1, ItemStack var2);
}

