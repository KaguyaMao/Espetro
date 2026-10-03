/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.JsonRecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class JsonRecipeSchema
extends RecipeSchema {
    public static final JsonRecipeSchema SCHEMA = new JsonRecipeSchema();

    public JsonRecipeSchema() {
        super(JsonRecipeJS.class, JsonRecipeJS::new, new RecipeKey[0]);
    }
}

