/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.crafting.Ingredient
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public interface IIngredientSerializer<T extends Ingredient> {
    public T parse(FriendlyByteBuf var1);

    public T parse(JsonObject var1);

    public void write(FriendlyByteBuf var1, T var2);
}

