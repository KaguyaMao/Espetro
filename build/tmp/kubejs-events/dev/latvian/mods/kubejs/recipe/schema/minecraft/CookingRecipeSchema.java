/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  dev.architectury.platform.Platform
 */
package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CookingRecipeSchema {
    public static final RecipeComponent<OutputItem> PLATFORM_OUTPUT_ITEM = new RecipeComponentWithParent<OutputItem>(){

        @Override
        public RecipeComponent<OutputItem> parentComponent() {
            return ItemComponents.OUTPUT;
        }

        @Override
        public JsonElement write(RecipeJS recipe, OutputItem value) {
            if (Platform.isForge()) {
                return ItemComponents.OUTPUT.write(recipe, value);
            }
            return new JsonPrimitive(value.item.kjs$getId());
        }

        public String toString() {
            return this.parentComponent().toString();
        }
    };
    public static final RecipeKey<OutputItem> RESULT = PLATFORM_OUTPUT_ITEM.key("result");
    public static final RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key("ingredient");
    public static final RecipeKey<Float> XP = NumberComponent.FLOAT.key("experience").optional(Float.valueOf(0.0f)).preferred("xp");
    public static final RecipeKey<Long> COOKING_TIME = TimeComponent.TICKS.key("cookingtime").optional(200L).preferred("cookingTime");
    public static final RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT, XP, COOKING_TIME).uniqueOutputId(RESULT);
}

