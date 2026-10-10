/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class VanillaIngredientSerializer
implements IIngredientSerializer<Ingredient> {
    public static final VanillaIngredientSerializer INSTANCE = new VanillaIngredientSerializer();

    @Override
    public Ingredient parse(FriendlyByteBuf buffer) {
        return Ingredient.m_43938_(Stream.generate(() -> new Ingredient.ItemValue(buffer.m_130267_())).limit(buffer.m_130242_()));
    }

    @Override
    public Ingredient parse(JsonObject json) {
        return Ingredient.m_43938_(Stream.of(Ingredient.m_43919_((JsonObject)json)));
    }

    @Override
    public void write(FriendlyByteBuf buffer, Ingredient ingredient) {
        ItemStack[] items = ingredient.m_43908_();
        buffer.m_130130_(items.length);
        for (ItemStack stack : items) {
            buffer.m_130055_(stack);
        }
    }
}

