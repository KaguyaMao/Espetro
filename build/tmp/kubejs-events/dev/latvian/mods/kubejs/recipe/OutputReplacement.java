/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.OutputReplacementTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface OutputReplacement {
    public static OutputReplacement of(Object o) {
        OutputReplacement outputReplacement;
        if (o instanceof OutputReplacement) {
            OutputReplacement r = (OutputReplacement)o;
            outputReplacement = r;
        } else {
            outputReplacement = OutputItem.of(o);
        }
        return outputReplacement;
    }

    default public OutputReplacementTransformer.Replacement transform(OutputReplacementTransformer transformer) {
        return new OutputReplacementTransformer.Replacement(this, transformer);
    }

    default public Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
        return this;
    }
}

