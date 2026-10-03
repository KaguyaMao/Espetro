/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.common.crafting.IIngredientSerializer
 */
package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredient;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public record KubeJSIngredientSerializer<T extends KubeJSIngredient>(Function<JsonObject, T> fromJson, Function<FriendlyByteBuf, T> fromNet) implements IIngredientSerializer<T>
{
    public T parse(JsonObject json) {
        return (T)((KubeJSIngredient)this.fromJson.apply(json));
    }

    public T parse(FriendlyByteBuf buf) {
        return (T)((KubeJSIngredient)this.fromNet.apply(buf));
    }

    public void write(FriendlyByteBuf buf, T ingredient) {
        ((KubeJSIngredient)ingredient).write(buf);
    }
}

