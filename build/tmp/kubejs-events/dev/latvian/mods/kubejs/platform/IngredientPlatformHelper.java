/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public interface IngredientPlatformHelper {
    public static final Lazy<IngredientPlatformHelper> INSTANCE = Lazy.serviceLoader(IngredientPlatformHelper.class);

    public static IngredientPlatformHelper get() {
        return INSTANCE.get();
    }

    default public InputItem stack(Ingredient ingredient, int count) {
        return InputItem.of(ingredient, count);
    }

    public Ingredient wildcard();

    public Ingredient custom(Ingredient var1, Predicate<ItemStack> var2);

    public Ingredient custom(Ingredient var1, @Nullable UUID var2);

    default public Ingredient tag(String tag) {
        return Ingredient.m_204132_(Tags.item(UtilsJS.getMCID(null, tag)));
    }

    public Ingredient mod(String var1);

    public Ingredient regex(Pattern var1);

    public Ingredient creativeTab(CreativeModeTab var1);

    public Ingredient subtract(Ingredient var1, Ingredient var2);

    public Ingredient or(Ingredient[] var1);

    public Ingredient and(Ingredient[] var1);

    public Ingredient strongNBT(ItemStack var1);

    public Ingredient weakNBT(ItemStack var1);

    public boolean isWildcard(Ingredient var1);
}

