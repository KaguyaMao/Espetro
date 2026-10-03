/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Wrapper
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilder;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class RecipeComponentBuilderMap
extends AbstractMap<RecipeKey<?>, Object> {
    public static final RecipeComponentBuilderMap EMPTY = new RecipeComponentBuilderMap(RecipeComponentValue.EMPTY_ARRAY);
    public final RecipeComponentValue<?>[] holders;
    private Set<Map.Entry<RecipeKey<?>, Object>> holderSet;
    public boolean hasChanged;

    public RecipeComponentBuilderMap(RecipeComponentBuilder builder) {
        this.holders = new RecipeComponentValue[builder.keys.size()];
        for (int i = 0; i < this.holders.length; ++i) {
            this.holders[i] = new RecipeComponentValue(builder.keys.get(i), i);
        }
        this.hasChanged = false;
    }

    public RecipeComponentBuilderMap(RecipeComponentValue<?>[] holders) {
        this.holders = new RecipeComponentValue[holders.length];
        for (int i = 0; i < holders.length; ++i) {
            this.holders[i] = holders[i].copy();
        }
    }

    public RecipeComponentBuilderMap(RecipeKey<?>[] keys) {
        this.holders = new RecipeComponentValue[keys.length];
        for (int i = 0; i < this.holders.length; ++i) {
            this.holders[i] = new RecipeComponentValue(keys[i], i);
        }
    }

    @Override
    @NotNull
    public Set<Map.Entry<RecipeKey<?>, Object>> entrySet() {
        if (this.holderSet == null) {
            this.holderSet = (Set)UtilsJS.cast(Set.of(this.holders));
        }
        return this.holderSet;
    }

    @Override
    public Object put(RecipeKey<?> key, Object value) {
        for (RecipeComponentValue<?> holder : this.holders) {
            if (holder.key != key) continue;
            return holder.setValue(UtilsJS.cast(Wrapper.unwrapped((Object)value)));
        }
        throw new IllegalArgumentException("Key " + String.valueOf(key) + " is not in this map!");
    }

    public RecipeComponentValue<?> getHolder(Object key) {
        for (RecipeComponentValue<?> holder : this.holders) {
            if (holder.key != key) continue;
            return holder;
        }
        return null;
    }

    @Override
    public Object get(Object key) {
        RecipeComponentValue<?> h = this.getHolder(key);
        return h == null ? null : h.value;
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        Object v = this.get(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public int hashCode() {
        int i = 1;
        for (RecipeComponentValue<?> holder : this.holders) {
            i = 31 * i + holder.key.hashCode();
            i = 31 * i + Objects.hashCode(holder.value);
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RecipeComponentBuilderMap) {
            RecipeComponentBuilderMap map = (RecipeComponentBuilderMap)o;
            if (this.holders.length != map.holders.length) {
                return false;
            }
            for (int i = 0; i < this.holders.length; ++i) {
                if (this.holders[i].key == map.holders[i].key && Objects.equals(this.holders[i].value, map.holders[i].value)) continue;
                return false;
            }
            return true;
        }
        return false;
    }
}

