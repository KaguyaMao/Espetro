/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Interner
 *  com.google.common.collect.Interners
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.recipe.filter;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public class RegexIDFilter
implements RecipeFilter {
    private final Pattern pattern;
    private final ConcurrentHashMap<ResourceLocation, Boolean> matchCache = new ConcurrentHashMap();
    private static final Interner<RegexIDFilter> INTERNER = Interners.newStrongInterner();

    private RegexIDFilter(Pattern i) {
        this.pattern = i;
    }

    public static RegexIDFilter of(Pattern i) {
        return (RegexIDFilter)INTERNER.intern((Object)new RegexIDFilter(i));
    }

    @Override
    public boolean test(RecipeKJS recipe) {
        return this.matchCache.computeIfAbsent(recipe.kjs$getOrCreateId(), location -> this.pattern.matcher(location.toString()).find());
    }

    public String toString() {
        return "RegexIDFilter{pattern=" + String.valueOf(this.pattern) + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RegexIDFilter that = (RegexIDFilter)o;
        return this.pattern.pattern().equals(that.pattern.pattern()) && this.pattern.flags() == that.pattern.flags();
    }

    public int hashCode() {
        return Objects.hash(this.pattern.pattern(), this.pattern.flags());
    }
}

