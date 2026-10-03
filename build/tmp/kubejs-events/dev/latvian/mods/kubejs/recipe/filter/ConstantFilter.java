/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;

public record ConstantFilter(boolean filter) implements RecipeFilter
{
    public static final ConstantFilter TRUE = new ConstantFilter(true);
    public static final ConstantFilter FALSE = new ConstantFilter(false);

    @Override
    public boolean test(RecipeKJS r) {
        return this.filter;
    }

    @Override
    public String toString() {
        return this.filter ? "*" : "-";
    }
}

