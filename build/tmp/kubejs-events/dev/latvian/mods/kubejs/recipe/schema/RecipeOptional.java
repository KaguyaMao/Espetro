/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;

@FunctionalInterface
public interface RecipeOptional<T> {
    public static final RecipeOptional<?> DEFAULT = type -> null;

    public T getDefaultValue(RecipeSchemaType var1);

    default public boolean isDefault() {
        return this == DEFAULT;
    }

    public record Constant<T>(T value) implements RecipeOptional<T>
    {
        @Override
        public T getDefaultValue(RecipeSchemaType type) {
            return this.value;
        }
    }
}

