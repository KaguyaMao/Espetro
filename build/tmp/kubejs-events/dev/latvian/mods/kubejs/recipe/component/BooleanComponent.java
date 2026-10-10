/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

public class BooleanComponent
implements RecipeComponent<Boolean> {
    public static final RecipeComponent<Boolean> BOOLEAN = new BooleanComponent();

    @Override
    public String componentType() {
        return "boolean";
    }

    @Override
    public Class<?> componentClass() {
        return Boolean.class;
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return TypeDescJS.BOOLEAN;
    }

    @Override
    public JsonElement write(RecipeJS recipe, Boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public Boolean read(RecipeJS recipe, Object from) {
        if (from instanceof Boolean) {
            Boolean n = (Boolean)from;
            return n;
        }
        if (from instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)from;
            return json.getAsBoolean();
        }
        if (from instanceof CharSequence) {
            return Boolean.parseBoolean(from.toString());
        }
        throw new IllegalStateException("Expected a boolean!");
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        JsonPrimitive json;
        return from instanceof Boolean || from instanceof JsonPrimitive && (json = (JsonPrimitive)from).isBoolean();
    }

    public String toString() {
        return this.componentType();
    }
}

