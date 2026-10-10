/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.ObjectDescJS;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeComponentBuilder
implements RecipeComponent<RecipeComponentBuilderMap> {
    public final List<RecipeKey<?>> keys;
    public Predicate<Set<String>> hasPriority;
    public ComponentRole role = ComponentRole.OTHER;

    public RecipeComponentBuilder(int init) {
        this.keys = new ArrayList(init);
    }

    public RecipeComponentBuilder add(RecipeKey<?> key) {
        this.keys.add(key);
        return this;
    }

    public RecipeComponentBuilder hasPriority(Predicate<Set<String>> hasPriority) {
        this.hasPriority = hasPriority;
        return this;
    }

    public RecipeComponentBuilder inputRole() {
        this.role = ComponentRole.INPUT;
        return this;
    }

    public RecipeComponentBuilder outputRole() {
        this.role = ComponentRole.OUTPUT;
        return this;
    }

    public RecipeComponentBuilder createCopy() {
        RecipeComponentBuilder copy = new RecipeComponentBuilder(this.keys.size());
        copy.keys.addAll(this.keys);
        copy.hasPriority = this.hasPriority;
        copy.role = this.role;
        return copy;
    }

    @Override
    public ComponentRole role() {
        return this.role;
    }

    @Override
    public String componentType() {
        return "builder";
    }

    @Override
    public Class<?> componentClass() {
        return RecipeComponentBuilderMap.class;
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        ObjectDescJS obj = TypeDescJS.object(this.keys.size());
        for (RecipeKey<?> key : this.keys) {
            obj.add(key.name, key.component.constructorDescription(ctx), key.optional());
        }
        return obj;
    }

    @Override
    public JsonElement write(RecipeJS recipe, RecipeComponentBuilderMap value) {
        JsonObject json = new JsonObject();
        for (RecipeComponentValue<?> val : value.holders) {
            if (val.value == null) continue;
            RecipeComponentValue vc = new RecipeComponentValue(val.key, val.getIndex());
            vc.value = UtilsJS.cast(val.value);
            val.key.component.writeToJson(recipe, (RecipeComponentValue)UtilsJS.cast(vc), json);
        }
        return json;
    }

    @Override
    public RecipeComponentBuilderMap read(RecipeJS recipe, Object from) {
        RecipeComponentBuilderMap value = new RecipeComponentBuilderMap(this);
        if (from instanceof JsonObject) {
            JsonObject json = (JsonObject)from;
            for (RecipeComponentValue<?> holder : value.holders) {
                holder.key.component.readFromJson(recipe, (RecipeComponentValue)UtilsJS.cast(holder), json);
                if (holder.key.optional() || holder.value != null) continue;
                throw new IllegalArgumentException("Missing required key '" + String.valueOf(holder.key) + "'!");
            }
        } else if (from instanceof Map) {
            Map map = (Map)from;
            for (RecipeComponentValue<?> holder : value.holders) {
                holder.key.component.readFromMap(recipe, (RecipeComponentValue)UtilsJS.cast(holder), map);
                if (holder.key.optional() || holder.value != null) continue;
                throw new IllegalArgumentException("Missing required key '" + String.valueOf(holder.key) + "'!");
            }
        } else {
            throw new IllegalArgumentException("Expected JSON object!");
        }
        return value;
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        if (from instanceof Map) {
            Map m = (Map)from;
            if (this.hasPriority != null) {
                return this.hasPriority.test(m.keySet());
            }
            for (RecipeKey<?> key : this.keys) {
                if (key.optional() || m.containsKey(key.name)) continue;
                return false;
            }
            return true;
        }
        if (from instanceof JsonObject) {
            JsonObject json = (JsonObject)from;
            if (this.hasPriority != null) {
                return this.hasPriority.test(json.keySet());
            }
            for (RecipeKey<?> key : this.keys) {
                if (key.optional() || json.has(key.name)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isInput(RecipeJS recipe, RecipeComponentBuilderMap value, ReplacementMatch match) {
        for (RecipeComponentValue<?> e : value.holders) {
            if (!e.isInput(recipe, match)) continue;
            return true;
        }
        return false;
    }

    @Override
    public RecipeComponentBuilderMap replaceInput(RecipeJS recipe, RecipeComponentBuilderMap original, ReplacementMatch match, InputReplacement with) {
        for (RecipeComponentValue<?> e : original.holders) {
            if (!e.replaceInput(recipe, match, with)) continue;
            original.hasChanged = true;
        }
        return original;
    }

    @Override
    public boolean isOutput(RecipeJS recipe, RecipeComponentBuilderMap value, ReplacementMatch match) {
        for (RecipeComponentValue<?> e : value.holders) {
            if (!e.isOutput(recipe, match)) continue;
            return true;
        }
        return false;
    }

    @Override
    public RecipeComponentBuilderMap replaceOutput(RecipeJS recipe, RecipeComponentBuilderMap original, ReplacementMatch match, OutputReplacement with) {
        for (RecipeComponentValue<?> e : original.holders) {
            if (!e.replaceOutput(recipe, match, with)) continue;
            original.hasChanged = true;
        }
        return original;
    }

    public String toString() {
        return this.keys.stream().map(RecipeKey::toString).collect(Collectors.joining(",", "builder{", "}"));
    }

    @Override
    public boolean checkValueHasChanged(RecipeComponentBuilderMap oldValue, RecipeComponentBuilderMap newValue) {
        return newValue.hasChanged;
    }
}

