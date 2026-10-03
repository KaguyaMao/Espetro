/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;

public class OutputFilter
implements RecipeFilter {
    private final ReplacementMatch match;

    public OutputFilter(ReplacementMatch match) {
        this.match = match;
    }

    @Override
    public boolean test(RecipeKJS r) {
        return r.hasOutput(this.match);
    }

    public String toString() {
        return "OutputFilter{" + String.valueOf(this.match) + "}";
    }
}

