/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package net.minecraftforge.common.brewing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

public class BrewingRecipeRegistry {
    private static List<IBrewingRecipe> recipes = new ArrayList<IBrewingRecipe>();

    public static boolean addRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        return BrewingRecipeRegistry.addRecipe(new BrewingRecipe(input, ingredient, output));
    }

    public static boolean addRecipe(IBrewingRecipe recipe) {
        return recipes.add(recipe);
    }

    public static ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (input.m_41619_() || input.m_41613_() != 1) {
            return ItemStack.f_41583_;
        }
        if (ingredient.m_41619_()) {
            return ItemStack.f_41583_;
        }
        for (IBrewingRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput(input, ingredient);
            if (output.m_41619_()) continue;
            return output;
        }
        return ItemStack.f_41583_;
    }

    public static boolean hasOutput(ItemStack input, ItemStack ingredient) {
        return !BrewingRecipeRegistry.getOutput(input, ingredient).m_41619_();
    }

    public static boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes) {
        if (ingredient.m_41619_()) {
            return false;
        }
        for (int i : inputIndexes) {
            if (!BrewingRecipeRegistry.hasOutput((ItemStack)inputs.get(i), ingredient)) continue;
            return true;
        }
        return false;
    }

    public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes) {
        for (int i : inputIndexes) {
            ItemStack output = BrewingRecipeRegistry.getOutput((ItemStack)inputs.get(i), ingredient);
            if (output.m_41619_()) continue;
            inputs.set(i, (Object)output);
        }
    }

    public static boolean isValidIngredient(ItemStack stack) {
        if (stack.m_41619_()) {
            return false;
        }
        for (IBrewingRecipe recipe : recipes) {
            if (!recipe.isIngredient(stack)) continue;
            return true;
        }
        return false;
    }

    public static boolean isValidInput(ItemStack stack) {
        for (IBrewingRecipe recipe : recipes) {
            if (!recipe.isInput(stack)) continue;
            return true;
        }
        return false;
    }

    public static List<IBrewingRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    static {
        BrewingRecipeRegistry.addRecipe(new VanillaBrewingRecipe());
    }
}

