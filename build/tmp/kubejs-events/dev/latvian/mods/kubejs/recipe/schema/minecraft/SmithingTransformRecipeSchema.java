/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 */
package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeConstructor;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface SmithingTransformRecipeSchema {
    public static final RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
    public static final RecipeKey<InputItem> TEMPLATE = ItemComponents.INPUT.key("template");
    public static final RecipeKey<InputItem> BASE = ItemComponents.INPUT.key("base");
    public static final RecipeKey<InputItem> ADDITION = ItemComponents.INPUT.key("addition");
    public static final RecipeSchema SCHEMA = new RecipeSchema(RESULT, TEMPLATE, BASE, ADDITION).uniqueOutputId(RESULT).constructor(RESULT, TEMPLATE, BASE, ADDITION).constructor(RecipeConstructor.Factory.defaultWith((recipe, key) -> {
        if (key == TEMPLATE) {
            return InputItem.of(Ingredient.m_43929_((ItemLike[])new ItemLike[]{Items.f_265918_}), 1);
        }
        return null;
    }), RESULT, BASE, ADDITION);
}

