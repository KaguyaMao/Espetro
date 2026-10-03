/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.common.crafting.CompoundIngredient
 *  net.minecraftforge.common.crafting.CraftingHelper
 *  net.minecraftforge.common.crafting.DifferenceIngredient
 *  net.minecraftforge.common.crafting.IntersectionIngredient
 *  net.minecraftforge.common.crafting.PartialNBTIngredient
 *  net.minecraftforge.common.crafting.StrictNBTIngredient
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.forge.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.CustomIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.CustomPredicateIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.ModIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.WildcardIngredient;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.jetbrains.annotations.Nullable;

public class IngredientForgeHelper
implements IngredientPlatformHelper {
    public static void register() {
        CraftingHelper.register((ResourceLocation)KubeJS.id("wildcard"), WildcardIngredient.SERIALIZER);
        CraftingHelper.register((ResourceLocation)KubeJS.id("custom"), CustomIngredient.SERIALIZER);
        CraftingHelper.register((ResourceLocation)KubeJS.id("custom_predicate"), CustomPredicateIngredient.SERIALIZER);
        CraftingHelper.register((ResourceLocation)KubeJS.id("mod"), ModIngredient.SERIALIZER);
        CraftingHelper.register((ResourceLocation)KubeJS.id("regex"), RegExIngredient.SERIALIZER);
        CraftingHelper.register((ResourceLocation)KubeJS.id("creative_tab"), CreativeTabIngredient.SERIALIZER);
    }

    @Override
    public Ingredient wildcard() {
        return WildcardIngredient.INSTANCE;
    }

    @Override
    public Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate) {
        return new CustomIngredient(predicate);
    }

    @Override
    public Ingredient custom(Ingredient parent, @Nullable UUID uuid) {
        return new CustomPredicateIngredient(parent, uuid);
    }

    @Override
    public Ingredient mod(String mod) {
        return new ModIngredient(mod);
    }

    @Override
    public Ingredient regex(Pattern pattern) {
        return new RegExIngredient(pattern);
    }

    @Override
    public Ingredient creativeTab(CreativeModeTab tab) {
        return new CreativeTabIngredient(tab);
    }

    @Override
    public Ingredient subtract(Ingredient base, Ingredient subtracted) {
        return DifferenceIngredient.of((Ingredient)base, (Ingredient)subtracted);
    }

    @Override
    public Ingredient or(Ingredient[] ingredients) {
        return ingredients.length == 0 ? Ingredient.f_43901_ : CompoundIngredient.of((Ingredient[])ingredients);
    }

    @Override
    public Ingredient and(Ingredient[] ingredients) {
        return ingredients.length == 0 ? Ingredient.f_43901_ : (ingredients.length == 1 ? ingredients[0] : IntersectionIngredient.of((Ingredient[])ingredients));
    }

    @Override
    public Ingredient strongNBT(ItemStack item) {
        return StrictNBTIngredient.of((ItemStack)item.m_41777_());
    }

    @Override
    public Ingredient weakNBT(ItemStack item) {
        return item.m_41783_() == null ? item.kjs$asIngredient() : PartialNBTIngredient.of((ItemLike)item.m_41720_(), (CompoundTag)item.m_41783_().m_6426_());
    }

    @Override
    public boolean isWildcard(Ingredient ingredient) {
        return ingredient == WildcardIngredient.INSTANCE;
    }
}

