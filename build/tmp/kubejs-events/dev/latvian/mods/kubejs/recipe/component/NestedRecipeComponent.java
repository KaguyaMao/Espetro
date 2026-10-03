/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class NestedRecipeComponent
implements RecipeComponent<RecipeJS> {
    public static final RecipeComponent<RecipeJS> RECIPE = new NestedRecipeComponent();
    public static final RecipeComponent<RecipeJS[]> RECIPE_ARRAY = RECIPE.asArray();

    @Override
    public Class<?> componentClass() {
        return RecipeJS.class;
    }

    @Override
    public JsonElement write(RecipeJS recipe, RecipeJS value) {
        value.serialize();
        value.json.addProperty("type", value.type.idString);
        return value.json;
    }

    @Override
    public RecipeJS read(RecipeJS recipe, Object from) {
        JsonObject json;
        if (from instanceof RecipeJS) {
            RecipeJS r = (RecipeJS)from;
            r.newRecipe = false;
            return r;
        }
        if (from instanceof JsonObject && (json = (JsonObject)from).has("type")) {
            RecipeJS r = recipe.type.event.custom(json);
            r.newRecipe = false;
            return r;
        }
        throw new IllegalArgumentException("Can't parse recipe from " + String.valueOf(from));
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        JsonObject json;
        return from instanceof RecipeJS || from instanceof JsonObject && (json = (JsonObject)from).has("type");
    }
}

