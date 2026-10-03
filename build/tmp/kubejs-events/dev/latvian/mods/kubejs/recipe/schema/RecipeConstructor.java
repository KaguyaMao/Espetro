/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public record RecipeConstructor(RecipeSchema schema, RecipeKey<?>[] keys, Factory factory) {
    @Override
    public String toString() {
        return Arrays.stream(this.keys).map(RecipeKey::toString).collect(Collectors.joining(", ", "(", ")"));
    }

    @FunctionalInterface
    public static interface Factory {
        public static final Factory DEFAULT = (recipe, schemaType, keys, from) -> {
            for (RecipeKey key : keys) {
                recipe.setValue(key, UtilsJS.cast(from.getValue(recipe, key)));
            }
        };

        public static Factory defaultWith(BiFunction<RecipeJS, RecipeKey<?>, Object> valueSupplier) {
            return (recipe, schemaType, keys, from) -> {
                DEFAULT.setValues(recipe, schemaType, keys, from);
                for (RecipeKey<?> key : schemaType.schema.keys) {
                    Object v = valueSupplier.apply(recipe, key);
                    if (v == null) continue;
                    recipe.setValue(key, UtilsJS.cast(v));
                }
            };
        }

        default public RecipeJS create(RecipeTypeFunction type, RecipeSchemaType schemaType, RecipeKey<?>[] keys, ComponentValueMap from) {
            RecipeJS r = schemaType.schema.factory.get();
            r.type = type;
            r.json = new JsonObject();
            r.json.addProperty("type", "unknown");
            r.newRecipe = true;
            r.initValues(true);
            this.setValues(r, schemaType, keys, from);
            return r;
        }

        public void setValues(RecipeJS var1, RecipeSchemaType var2, RecipeKey<?>[] var3, ComponentValueMap var4);
    }
}

