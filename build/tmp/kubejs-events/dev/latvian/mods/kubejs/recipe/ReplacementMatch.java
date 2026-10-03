/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.SingleItemMatch;
import net.minecraft.world.item.crafting.Ingredient;

public interface ReplacementMatch {
    public static final ReplacementMatch NONE = new ReplacementMatch(){

        public String toString() {
            return "NONE";
        }
    };

    public static ReplacementMatch of(Object o) {
        if (o == null) {
            return NONE;
        }
        if (o instanceof ReplacementMatch) {
            ReplacementMatch m = (ReplacementMatch)o;
            return m;
        }
        Ingredient in = IngredientJS.of(o);
        if (in.m_43947_()) {
            return NONE;
        }
        if (in.m_43908_().length == 1) {
            return new SingleItemMatch(in.m_43908_()[0]);
        }
        return new IngredientMatch(in, false);
    }
}

