/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface StonecuttingRecipeSchema {
    public static final RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT_ID_WITH_COUNT.key("result");
    public static final RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key("ingredient");
    public static final RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT).uniqueOutputId(RESULT);
}

