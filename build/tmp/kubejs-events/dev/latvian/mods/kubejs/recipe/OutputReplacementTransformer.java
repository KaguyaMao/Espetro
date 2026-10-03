/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

@FunctionalInterface
public interface OutputReplacementTransformer {
    public Object transform(RecipeKJS var1, ReplacementMatch var2, OutputReplacement var3, OutputReplacement var4);

    public record Replacement(OutputReplacement with, OutputReplacementTransformer transformer) implements OutputReplacement
    {
        @Override
        public String toString() {
            return String.valueOf(this.with) + " [transformed]";
        }

        @Override
        public Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
            return this.transformer.transform(recipe, match, original, this.with);
        }
    }
}

