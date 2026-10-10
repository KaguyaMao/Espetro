/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraftforge.common.crafting.IIngredientSerializer
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class WildcardIngredient
extends KubeJSIngredient {
    public static WildcardIngredient INSTANCE = new WildcardIngredient();
    public static final KubeJSIngredientSerializer<WildcardIngredient> SERIALIZER = new KubeJSIngredientSerializer<WildcardIngredient>(json -> INSTANCE, buf -> INSTANCE);

    private WildcardIngredient() {
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    public boolean test(@Nullable ItemStack stack) {
        return stack != null;
    }

    @Override
    protected void dissolve() {
        if (this.f_43903_ == null) {
            this.f_43903_ = ItemStackJS.getList().toArray(new ItemStack[0]);
        }
    }

    @Override
    public void toJson(JsonObject json) {
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }
}

