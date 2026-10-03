/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.player.StackedContents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class DifferenceIngredient
extends AbstractIngredient {
    private final Ingredient base;
    private final Ingredient subtracted;
    private ItemStack[] filteredMatchingStacks;
    private IntList packedMatchingStacks;

    protected DifferenceIngredient(Ingredient base, Ingredient subtracted) {
        this.base = base;
        this.subtracted = subtracted;
    }

    public static DifferenceIngredient of(Ingredient base, Ingredient subtracted) {
        return new DifferenceIngredient(base, subtracted);
    }

    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return false;
        }
        return this.base.test(stack) && !this.subtracted.test(stack);
    }

    public ItemStack[] m_43908_() {
        if (this.filteredMatchingStacks == null) {
            this.filteredMatchingStacks = (ItemStack[])Arrays.stream(this.base.m_43908_()).filter(stack -> !this.subtracted.test(stack)).toArray(ItemStack[]::new);
        }
        return this.filteredMatchingStacks;
    }

    public boolean m_43947_() {
        return this.base.m_43947_();
    }

    @Override
    public boolean isSimple() {
        return this.base.isSimple() && this.subtracted.isSimple();
    }

    protected void invalidate() {
        super.invalidate();
        this.filteredMatchingStacks = null;
        this.packedMatchingStacks = null;
    }

    public IntList m_43931_() {
        if (this.packedMatchingStacks == null || this.checkInvalidation()) {
            this.markValid();
            ItemStack[] matchingStacks = this.m_43908_();
            this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
            for (ItemStack stack : matchingStacks) {
                this.packedMatchingStacks.add(StackedContents.m_36496_((ItemStack)stack));
            }
            this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.packedMatchingStacks;
    }

    @Override
    public JsonElement m_43942_() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.add("base", this.base.m_43942_());
        json.add("subtracted", this.subtracted.m_43942_());
        return json;
    }

    public IIngredientSerializer<DifferenceIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer
    implements IIngredientSerializer<DifferenceIngredient> {
        public static final IIngredientSerializer<DifferenceIngredient> INSTANCE = new Serializer();

        @Override
        public DifferenceIngredient parse(JsonObject json) {
            Ingredient base = Ingredient.m_288218_((JsonElement)json.get("base"), (boolean)false);
            Ingredient without = Ingredient.m_288218_((JsonElement)json.get("subtracted"), (boolean)false);
            return new DifferenceIngredient(base, without);
        }

        @Override
        public DifferenceIngredient parse(FriendlyByteBuf buffer) {
            Ingredient base = Ingredient.m_43940_((FriendlyByteBuf)buffer);
            Ingredient without = Ingredient.m_43940_((FriendlyByteBuf)buffer);
            return new DifferenceIngredient(base, without);
        }

        @Override
        public void write(FriendlyByteBuf buffer, DifferenceIngredient ingredient) {
            ingredient.base.m_43923_(buffer);
            ingredient.subtracted.m_43923_(buffer);
        }
    }
}

