/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class StrictNBTIngredient
extends AbstractIngredient {
    private final ItemStack stack;

    protected StrictNBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.ItemValue(stack)));
        this.stack = stack;
    }

    public static StrictNBTIngredient of(ItemStack stack) {
        return new StrictNBTIngredient(stack);
    }

    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }
        return this.stack.m_41720_() == input.m_41720_() && this.stack.m_41773_() == input.m_41773_() && this.stack.areShareTagsEqual(input);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement m_43942_() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(this.stack.m_41720_()).toString());
        json.addProperty("count", (Number)this.stack.m_41613_());
        if (this.stack.m_41782_()) {
            json.addProperty("nbt", this.stack.m_41783_().toString());
        }
        return json;
    }

    public static class Serializer
    implements IIngredientSerializer<StrictNBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public StrictNBTIngredient parse(FriendlyByteBuf buffer) {
            return new StrictNBTIngredient(buffer.m_130267_());
        }

        @Override
        public StrictNBTIngredient parse(JsonObject json) {
            return new StrictNBTIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(FriendlyByteBuf buffer, StrictNBTIngredient ingredient) {
            buffer.m_130055_(ingredient.stack);
        }
    }
}

