/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$Value
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractIngredient
extends Ingredient {
    protected AbstractIngredient() {
        super(Stream.of(new Ingredient.Value[0]));
    }

    protected AbstractIngredient(Stream<? extends Ingredient.Value> values) {
        super(values);
    }

    public abstract boolean isSimple();

    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();

    public abstract JsonElement m_43942_();

    @Deprecated
    public static Ingredient m_43938_(Stream<? extends Ingredient.Value> values) {
        throw new UnsupportedOperationException("Use Ingredient.fromValues()");
    }

    @Deprecated
    public static Ingredient m_151265_() {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    @Deprecated
    public static Ingredient m_43929_(ItemLike ... items) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    @Deprecated
    public static Ingredient m_43927_(ItemStack ... stacks) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    @Deprecated
    public static Ingredient m_43921_(Stream<ItemStack> stacks) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    @Deprecated
    public static Ingredient m_204132_(TagKey<Item> tag) {
        throw new UnsupportedOperationException("Use Ingredient.of()");
    }

    @Deprecated
    public static Ingredient m_43940_(FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException("Use Ingredient.fromNetwork()");
    }

    @Deprecated
    public static Ingredient m_43917_(@Nullable JsonElement json) {
        throw new UnsupportedOperationException("Use Ingredient.fromJson()");
    }
}

