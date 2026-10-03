/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  dev.latvian.mods.rhino.mod.util.JsonSerializable
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@RemapPrefixForJS(value="kjs$")
public interface IngredientKJS
extends IngredientSupplierKJS,
JsonSerializable {
    default public Ingredient kjs$self() {
        throw new NoMixinException();
    }

    default public boolean kjs$testItem(Item item) {
        return this.kjs$self().test(item.m_7968_());
    }

    default public ItemStackSet kjs$getStacks() {
        return new ItemStackSet(this.kjs$self().m_43908_());
    }

    default public ItemStackSet kjs$getDisplayStacks() {
        ItemStackSet set = new ItemStackSet();
        for (ItemStack stack : ItemStackJS.getList()) {
            if (!this.kjs$self().test(stack)) continue;
            set.add(stack);
        }
        return set;
    }

    default public Set<Item> kjs$getItemTypes() {
        ItemStack[] items = this.kjs$self().m_43908_();
        if (items.length == 1 && !items[0].m_41619_()) {
            return Set.of(items[0].m_41720_());
        }
        LinkedHashSet<Item> set = new LinkedHashSet<Item>(items.length);
        for (ItemStack stack : items) {
            if (stack.m_41619_()) continue;
            set.add(stack.m_41720_());
        }
        return set;
    }

    default public Set<String> kjs$getItemIds() {
        ItemStack[] items = this.kjs$self().m_43908_();
        if (items.length == 1 && !items[0].m_41619_()) {
            return Set.of(items[0].kjs$getId());
        }
        LinkedHashSet<String> ids = new LinkedHashSet<String>(items.length);
        for (ItemStack item : items) {
            if (item.m_41619_()) continue;
            ids.add(item.kjs$getId());
        }
        return ids;
    }

    default public ItemStack kjs$getFirst() {
        for (ItemStack stack : this.kjs$self().m_43908_()) {
            if (stack.m_41619_()) continue;
            return stack;
        }
        return ItemStack.f_41583_;
    }

    default public Ingredient kjs$and(Ingredient ingredient) {
        return ingredient == Ingredient.f_43901_ ? this.kjs$self() : (this == Ingredient.f_43901_ ? ingredient : IngredientPlatformHelper.get().and(new Ingredient[]{this.kjs$self(), ingredient}));
    }

    default public Ingredient kjs$or(Ingredient ingredient) {
        return ingredient == Ingredient.f_43901_ ? this.kjs$self() : (this == Ingredient.f_43901_ ? ingredient : IngredientPlatformHelper.get().or(new Ingredient[]{this.kjs$self(), ingredient}));
    }

    default public Ingredient kjs$subtract(Ingredient subtracted) {
        return IngredientPlatformHelper.get().subtract(this.kjs$self(), subtracted);
    }

    default public InputItem kjs$asStack() {
        return InputItem.of(this.kjs$self(), 1);
    }

    default public InputItem kjs$withCount(int count) {
        return InputItem.of(this.kjs$self(), count);
    }

    default public boolean kjs$isWildcard() {
        return IngredientPlatformHelper.get().isWildcard(this.kjs$self());
    }

    default public boolean kjs$canBeUsedForMatching() {
        return this.getClass() == Ingredient.class;
    }

    @Override
    default public Ingredient kjs$asIngredient() {
        return this.kjs$self();
    }

    default public JsonElement toJsonJS() {
        return this.kjs$self().m_43942_();
    }
}

