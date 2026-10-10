/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.recipe.ingredientaction;

import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ConsumeAction
extends IngredientAction {
    @Override
    public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
        return ItemStack.f_41583_;
    }

    @Override
    public String getType() {
        return "consume";
    }
}

