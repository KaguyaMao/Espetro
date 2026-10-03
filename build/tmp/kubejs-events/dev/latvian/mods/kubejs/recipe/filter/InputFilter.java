/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;

public class InputFilter
implements RecipeFilter {
    private final ReplacementMatch match;

    public InputFilter(ReplacementMatch match) {
        this.match = match;
    }

    @Override
    public boolean test(RecipeKJS r) {
        return r.hasInput(this.match);
    }

    public String toString() {
        return "InputFilter{" + String.valueOf(this.match) + "}";
    }
}

