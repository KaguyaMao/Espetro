/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import java.util.ArrayList;
import java.util.List;

public class AndFilter
implements RecipeFilter {
    public final List<RecipeFilter> list = new ArrayList<RecipeFilter>(2);

    @Override
    public boolean test(RecipeKJS r) {
        for (RecipeFilter p : this.list) {
            if (p.test(r)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return "AndFilter[" + String.valueOf(this.list) + "]";
    }
}

