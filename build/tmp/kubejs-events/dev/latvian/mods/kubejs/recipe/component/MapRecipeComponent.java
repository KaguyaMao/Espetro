/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.TinyMap;
import java.util.Map;

public record MapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> component, boolean patternKey) implements RecipeComponent<TinyMap<K, V>>
{
    public static final RecipeComponent<TinyMap<Character, InputItem>> ITEM_PATTERN_KEY = new MapRecipeComponent<Character, InputItem>(StringComponent.CHARACTER, ItemComponents.INPUT, true);

    @Override
    public String componentType() {
        if (this.patternKey) {
            return this.component.componentType() + "_pattern_key";
        }
        return "map";
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return this.component.constructorDescription(ctx).asMap(this.key.constructorDescription(ctx));
    }

    @Override
    public ComponentRole role() {
        return this.component.role();
    }

    @Override
    public Class<?> componentClass() {
        return TinyMap.class;
    }

    public JsonObject write(RecipeJS recipe, TinyMap<K, V> value) {
        JsonObject json = new JsonObject();
        for (TinyMap.Entry<K, V> entry : value.entries()) {
            json.add(this.key.write(recipe, entry.key()).getAsString(), this.component.write(recipe, entry.value()));
        }
        return json;
    }

    @Override
    public TinyMap<K, V> read(RecipeJS recipe, Object from) {
        if (from instanceof TinyMap) {
            TinyMap map = (TinyMap)from;
            return map;
        }
        if (from instanceof JsonObject) {
            JsonObject o = (JsonObject)from;
            TinyMap map = new TinyMap(new TinyMap.Entry[o.size()]);
            int i = 0;
            for (Map.Entry entry : o.entrySet()) {
                K k = this.key.read(recipe, entry.getKey());
                V v = this.component.read(recipe, entry.getValue());
                map.entries()[i++] = new TinyMap.Entry<K, V>(k, v);
            }
            return map;
        }
        if (from instanceof Map) {
            Map m = (Map)from;
            TinyMap map = new TinyMap(new TinyMap.Entry[m.size()]);
            int i = 0;
            for (Map.Entry entry : m.entrySet()) {
                K k = this.key.read(recipe, entry.getKey());
                V v = this.component.read(recipe, entry.getValue());
                map.entries()[i++] = new TinyMap.Entry<K, V>(k, v);
            }
            return map;
        }
        throw new IllegalArgumentException("Expected JSON object!");
    }

    @Override
    public String checkEmpty(RecipeKey<TinyMap<K, V>> key, TinyMap<K, V> value) {
        if (value.isEmpty()) {
            return "Map '" + key.name + "' can't be empty!";
        }
        return "";
    }

    @Override
    public boolean isInput(RecipeJS recipe, TinyMap<K, V> value, ReplacementMatch match) {
        for (TinyMap.Entry<K, V> entry : value.entries()) {
            if (!this.component.isInput(recipe, entry.value(), match)) continue;
            return true;
        }
        return false;
    }

    @Override
    public TinyMap<K, V> replaceInput(RecipeJS recipe, TinyMap<K, V> original, ReplacementMatch match, InputReplacement with) {
        TinyMap<K, V> map = original;
        for (int i = 0; i < original.entries().length; ++i) {
            V r = this.component.replaceInput(recipe, original.entries()[i].value(), match, with);
            if (r == original.entries()[i].value()) continue;
            if (map == original) {
                map = new TinyMap<K, V>(original);
            }
            map.entries()[i] = new TinyMap.Entry<K, V>(original.entries()[i].key(), r);
        }
        return map;
    }

    @Override
    public boolean isOutput(RecipeJS recipe, TinyMap<K, V> value, ReplacementMatch match) {
        for (TinyMap.Entry<K, V> entry : value.entries()) {
            if (!this.component.isOutput(recipe, entry.value(), match)) continue;
            return true;
        }
        return false;
    }

    @Override
    public TinyMap<K, V> replaceOutput(RecipeJS recipe, TinyMap<K, V> original, ReplacementMatch match, OutputReplacement with) {
        TinyMap<K, V> map = original;
        for (int i = 0; i < original.entries().length; ++i) {
            V r = this.component.replaceOutput(recipe, original.entries()[i].value(), match, with);
            if (r == original.entries()[i].value()) continue;
            if (map == original) {
                map = new TinyMap<K, V>(original);
            }
            map.entries()[i] = new TinyMap.Entry<K, V>(original.entries()[i].key(), r);
        }
        return map;
    }

    @Override
    public String toString() {
        if (this.patternKey) {
            return this.componentType();
        }
        return "map{" + String.valueOf(this.key) + ":" + String.valueOf(this.component) + "}";
    }
}

