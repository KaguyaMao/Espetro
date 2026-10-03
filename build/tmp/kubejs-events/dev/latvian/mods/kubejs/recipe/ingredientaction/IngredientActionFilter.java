/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.recipe.ingredientaction;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.util.MapJS;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientActionFilter {
    public int filterIndex = -1;
    public Ingredient filterIngredient = null;

    public static IngredientActionFilter filterOf(Object o) {
        IngredientActionFilter filter = new IngredientActionFilter();
        if (o instanceof Number) {
            Number num = (Number)o;
            filter.filterIndex = num.intValue();
        } else if (o instanceof String || o instanceof Ingredient) {
            filter.filterIngredient = IngredientJS.of(o);
        } else {
            Map<?, ?> map = MapJS.of(o);
            if (map != null && !map.isEmpty()) {
                if (map.containsKey("item")) {
                    filter.filterIngredient = IngredientJS.of(map.get("item"));
                }
                if (map.containsKey("index")) {
                    filter.filterIndex = ((Number)map.get("index")).intValue();
                }
            }
        }
        return filter;
    }

    public void copyFrom(IngredientActionFilter filter) {
        this.filterIndex = filter.filterIndex;
        this.filterIngredient = filter.filterIngredient;
    }

    public boolean checkFilter(int index, ItemStack stack) {
        return !(this.filterIndex != -1 && this.filterIndex != index || this.filterIngredient != null && !this.filterIngredient.test(stack));
    }
}

