/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 */
package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;

@RemapPrefixForJS(value="kjs$")
public interface FluidLike
extends ReplacementMatch {
    public long kjs$getAmount();

    default public boolean kjs$isEmpty() {
        return this.kjs$getAmount() <= 0L;
    }

    default public FluidLike kjs$copy(long amount) {
        return this;
    }

    default public boolean matches(FluidLike other) {
        return this.equals(other);
    }
}

