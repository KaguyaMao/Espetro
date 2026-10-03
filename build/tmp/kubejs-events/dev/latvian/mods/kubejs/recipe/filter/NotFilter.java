/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;

public record NotFilter(RecipeFilter original) implements RecipeFilter
{
    @Override
    public boolean test(RecipeKJS r) {
        return !this.original.test(r);
    }

    @Override
    public String toString() {
        return "NotFilter{" + String.valueOf(this.original) + "}";
    }
}

