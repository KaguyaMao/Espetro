/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;

public class TypeFilter
implements RecipeFilter {
    private final ResourceLocation type;

    public TypeFilter(ResourceLocation t) {
        this.type = t;
        if (RecipeJS.itemErrors && !RegistryInfo.RECIPE_SERIALIZER.hasValue(this.type)) {
            throw new RecipeExceptionJS("Type '" + String.valueOf(this.type) + "' doesn't exist!").error();
        }
    }

    @Override
    public boolean test(RecipeKJS r) {
        return r.kjs$getType().equals((Object)this.type);
    }

    public String toString() {
        return "TypeFilter{" + String.valueOf(this.type) + "}";
    }
}

