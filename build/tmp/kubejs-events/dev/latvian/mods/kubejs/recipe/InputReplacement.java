/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacementTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface InputReplacement {
    public static InputReplacement of(Object o) {
        InputReplacement inputReplacement;
        if (o instanceof InputReplacement) {
            InputReplacement r = (InputReplacement)o;
            inputReplacement = r;
        } else {
            inputReplacement = InputItem.of(o);
        }
        return inputReplacement;
    }

    default public InputReplacementTransformer.Replacement transform(InputReplacementTransformer transformer) {
        return new InputReplacementTransformer.Replacement(this, transformer);
    }

    default public Object replaceInput(RecipeJS recipe, ReplacementMatch match, InputReplacement original) {
        return this;
    }
}

