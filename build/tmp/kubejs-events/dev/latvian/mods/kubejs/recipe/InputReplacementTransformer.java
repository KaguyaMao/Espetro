/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

@FunctionalInterface
public interface InputReplacementTransformer {
    public Object transform(RecipeKJS var1, ReplacementMatch var2, InputReplacement var3, InputReplacement var4);

    public record Replacement(InputReplacement with, InputReplacementTransformer transformer) implements InputReplacement
    {
        @Override
        public String toString() {
            return String.valueOf(this.with) + " [transformed]";
        }
    }
}

