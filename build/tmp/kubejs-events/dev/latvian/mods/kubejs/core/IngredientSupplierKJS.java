/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientSupplierKJS {
    default public Ingredient kjs$asIngredient() {
        throw new NoMixinException();
    }
}

