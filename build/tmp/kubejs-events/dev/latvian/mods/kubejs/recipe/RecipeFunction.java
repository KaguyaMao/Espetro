/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaObject
 *  dev.latvian.mods.rhino.Scriptable
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.ErroredRecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValueFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import java.util.HashMap;
import java.util.Map;

public class RecipeFunction
extends NativeJavaObject {
    public final RecipeJS recipe;
    public final Map<String, RecipeComponentValueFunction> builderFunctions;

    public RecipeFunction(Context cx, Scriptable scope, Class<?> staticType, RecipeJS recipe) {
        super(scope, (Object)recipe, staticType, cx);
        this.recipe = recipe;
        Map<String, RecipeComponentValue<?>> map = recipe.getAllValueMap();
        this.builderFunctions = new HashMap<String, RecipeComponentValueFunction>(map.size());
        for (Map.Entry<String, RecipeComponentValue<?>> entry : map.entrySet()) {
            String key = entry.getKey();
            RecipeComponentValue<?> value = entry.getValue();
            if (value.key.noBuilders) continue;
            this.builderFunctions.put(key, new RecipeComponentValueFunction(recipe, value));
        }
    }

    public Object get(Context cx, String name, Scriptable start) {
        RecipeComponentValueFunction r;
        RecipeJS recipeJS = this.recipe;
        if (recipeJS instanceof ErroredRecipeJS) {
            ErroredRecipeJS errored = (ErroredRecipeJS)recipeJS;
            return errored.dummyFunction;
        }
        Object s = super.get(cx, name, start);
        if (s == Scriptable.NOT_FOUND && (r = this.builderFunctions.get(name)) != null) {
            return r;
        }
        return s;
    }
}

