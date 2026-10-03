/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface SmithingTrimRecipeSchema {
    public static final RecipeKey<InputItem> TEMPLATE = ItemComponents.INPUT.key("template");
    public static final RecipeKey<InputItem> BASE = ItemComponents.INPUT.key("base");
    public static final RecipeKey<InputItem> ADDITION = ItemComponents.INPUT.key("addition");
    public static final RecipeSchema SCHEMA = new RecipeSchema(TEMPLATE, BASE, ADDITION).uniqueInputId(BASE);
}

