/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import net.minecraft.resources.ResourceLocation;

public class IDFilter
implements RecipeFilter {
    public final ResourceLocation id;

    public IDFilter(ResourceLocation i) {
        this.id = i;
    }

    @Override
    public boolean test(RecipeKJS r) {
        return r.kjs$getOrCreateId().equals((Object)this.id);
    }

    public String toString() {
        return "IDFilter{id=" + String.valueOf(this.id) + "}";
    }
}

