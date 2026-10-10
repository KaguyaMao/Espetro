/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientActionCallback;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

@Info(value="Various Ingredient related helper methods")
public interface IngredientWrapper {
    @Info(value="A completely empty ingredient that will only match air")
    public static final Ingredient none = Ingredient.f_43901_;
    @Info(value="An ingredient that matches everything")
    public static final Ingredient all = IngredientPlatformHelper.get().wildcard();

    @Info(value="Returns an ingredient of the input")
    public static Ingredient of(Ingredient ingredient) {
        return ingredient;
    }

    @Info(value="Returns an ingredient of the input, with the specified count")
    public static InputItem of(Ingredient ingredient, int count) {
        return ingredient.kjs$withCount(count);
    }

    @Info(value="Make a custom ingredient where items must match both the parent ingredient and the custom predicate function")
    public static Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate) {
        if (RecipesEventJS.customIngredientMap != null) {
            IngredientWithCustomPredicate ingredient = new IngredientWithCustomPredicate(parent, UUID.randomUUID(), predicate);
            RecipesEventJS.customIngredientMap.put(ingredient.uuid, ingredient);
            return IngredientPlatformHelper.get().custom(parent, ingredient.uuid);
        }
        return IngredientPlatformHelper.get().custom(parent, predicate);
    }

    @Info(value="Make a custom ingredient where a match must match the provided predicate function")
    public static Ingredient custom(Predicate<ItemStack> predicate) {
        return IngredientWrapper.custom(all, predicate);
    }

    @Info(value="Make a custom ingredient where an item must match both the parent ingredient and the item's nbt must match the custom predicate function")
    public static Ingredient customNBT(Ingredient in, Predicate<CompoundTag> predicate) {
        return IngredientWrapper.custom(in, is -> is.m_41782_() && predicate.test(is.m_41783_()));
    }

    @Info(value="Register a custom ingredient action for use in recipes with Recipe#customIngredientAction")
    public static void registerCustomIngredientAction(String id, CustomIngredientActionCallback callback) {
        CustomIngredientAction.MAP.put(id, callback);
    }

    @Info(value="Checks if the passed in object is an Ingredient.\nNote that this does not mean it will not function as an Ingredient if passed to something that requests one.\n")
    public static boolean isIngredient(@Nullable Object o) {
        return o instanceof Ingredient;
    }
}

