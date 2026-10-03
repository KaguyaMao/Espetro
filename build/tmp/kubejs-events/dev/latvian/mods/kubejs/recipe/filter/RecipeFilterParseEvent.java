/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import java.util.Map;

public interface RecipeFilterParseEvent {
    public void parse(Context var1, List<RecipeFilter> var2, Map<?, ?> var3);
}

