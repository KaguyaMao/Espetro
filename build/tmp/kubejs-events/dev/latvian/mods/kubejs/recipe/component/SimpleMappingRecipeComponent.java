/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.component.MappingRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.MapJS;
import java.util.Map;
import java.util.Objects;

public class SimpleMappingRecipeComponent<T>
extends MappingRecipeComponent<T> {
    public SimpleMappingRecipeComponent(RecipeComponent<T> parent, Object mappings) {
        this((BiMap<String, String>)HashBiMap.create(Objects.requireNonNull(MapJS.of(mappings), "mappings null or invalid map. try using {left: 'right', more: 'mappings'} format")), parent);
    }

    private SimpleMappingRecipeComponent(BiMap<String, String> mappings, RecipeComponent<T> parent) {
        super(parent, o -> SimpleMappingRecipeComponent.to(o, (Map<String, String>)mappings), j -> SimpleMappingRecipeComponent.from(j, (Map<String, String>)mappings.inverse()));
    }

    @Override
    public String componentType() {
        return "simple_mapping";
    }

    public static Object to(Object o, Map<String, String> mappings) {
        Map<?, ?> m = MapJS.of(o);
        if (m == null) {
            return o;
        }
        mappings.forEach((from, to) -> {
            if (m.containsKey(from)) {
                Object value = m.get(from);
                m.remove(from);
                m.put(to, value);
            }
        });
        return m;
    }

    public static JsonElement from(JsonElement parentOutput, Map<String, String> mappings) {
        if (parentOutput instanceof JsonObject) {
            JsonObject json = (JsonObject)parentOutput;
            Map map = json.asMap();
            mappings.forEach((from, to) -> {
                if (map.containsKey(from)) {
                    JsonElement value = (JsonElement)map.get(from);
                    map.remove(from);
                    map.put(to, value);
                }
            });
        }
        return parentOutput;
    }
}

