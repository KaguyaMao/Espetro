/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class JsonRecipeJS
extends RecipeJS {
    @Override
    public void deserialize(boolean merge) {
    }

    @Override
    public void serialize() {
    }

    @Override
    public boolean hasInput(ReplacementMatch match) {
        if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch) {
            ItemMatch m = (ItemMatch)match;
            if (this.getOriginalRecipe() != null) {
                for (Ingredient ingredient : this.getOriginalRecipe().m_7527_()) {
                    if (ingredient == Ingredient.f_43901_ || !ingredient.kjs$canBeUsedForMatching() || !m.contains(ingredient)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
        return false;
    }

    @Override
    public boolean hasOutput(ReplacementMatch match) {
        if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch) {
            ItemMatch m = (ItemMatch)match;
            if (this.getOriginalRecipe() != null) {
                ItemStack result = this.getOriginalRecipe().m_8043_(UtilsJS.staticRegistryAccess);
                return result != null && result != ItemStack.f_41583_ && !result.m_41619_() && m.contains(result);
            }
        }
        return false;
    }

    @Override
    public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
        return false;
    }
}

