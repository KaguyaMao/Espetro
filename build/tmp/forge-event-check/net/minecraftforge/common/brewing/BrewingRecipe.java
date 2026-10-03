/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.NotNull;

public class BrewingRecipe
implements IBrewingRecipe {
    @NotNull
    private final Ingredient input;
    @NotNull
    private final Ingredient ingredient;
    @NotNull
    private final ItemStack output;

    public BrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(@NotNull ItemStack stack) {
        return this.input.test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return this.isInput(input) && this.isIngredient(ingredient) ? this.getOutput().m_41777_() : ItemStack.f_41583_;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return this.ingredient.test(ingredient);
    }
}

