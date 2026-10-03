/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface OutputFluid
extends FluidLike,
OutputReplacement {
    @Override
    default public Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
        if (original instanceof FluidLike) {
            FluidLike o = (FluidLike)((Object)original);
            return this.kjs$copy(o.kjs$getAmount());
        }
        return this.kjs$copy(this.kjs$getAmount());
    }
}

