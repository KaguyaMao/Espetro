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
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class ModIngredient
extends KubeJSIngredient {
    public static final KubeJSIngredientSerializer<ModIngredient> SERIALIZER = new KubeJSIngredientSerializer<ModIngredient>(ModIngredient::ofModFromJson, ModIngredient::ofModFromNetwork);
    public final String mod;

    public static ModIngredient ofModFromNetwork(FriendlyByteBuf buf) {
        return new ModIngredient(buf.m_130277_());
    }

    public static ModIngredient ofModFromJson(JsonObject json) {
        return new ModIngredient(json.get("mod").getAsString());
    }

    public ModIngredient(String mod) {
        this.mod = mod;
    }

    public boolean test(@Nullable ItemStack stack) {
        return stack != null && stack.kjs$getMod().equals(this.mod);
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty("mod", this.mod);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.mod);
    }
}

