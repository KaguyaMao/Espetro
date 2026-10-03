/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.Wrapper
 */
package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;

public class RecipeComponentValueFunction
extends BaseFunction {
    public final RecipeJS recipe;
    public final RecipeComponentValue<?> componentValue;

    public RecipeComponentValueFunction(RecipeJS recipe, RecipeComponentValue<?> componentValue) {
        this.recipe = recipe;
        this.componentValue = componentValue;
    }

    public RecipeJS call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return this.recipe.setValue(this.componentValue.key, UtilsJS.cast(this.componentValue.key.component.read(this.recipe, Wrapper.unwrapped((Object)args[0]))));
    }
}

