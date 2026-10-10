/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ShapelessRecipeSchema {
    public static final RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
    public static final RecipeKey<InputItem[]> INGREDIENTS = ItemComponents.UNWRAPPED_INPUT_ARRAY.key("ingredients");
    public static final RecipeSchema SCHEMA = new RecipeSchema(ShapelessRecipeJS.class, ShapelessRecipeJS::new, RESULT, INGREDIENTS).uniqueOutputId(RESULT);

    public static class ShapelessRecipeJS
    extends RecipeJS {
        @Override
        public RecipeTypeFunction getSerializationTypeFunction() {
            if (!(this.type != this.type.event.shapeless || this.type.event.shapeless == this.type.event.vanillaShapeless || this.json.has("kubejs:actions") || this.json.has("kubejs:modify_result") || this.json.has("kubejs:stage"))) {
                return this.type.event.vanillaShapeless;
            }
            return super.getSerializationTypeFunction();
        }
    }
}

