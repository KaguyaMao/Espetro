/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.entity.player.StackedContents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class IntersectionIngredient
extends AbstractIngredient {
    private final List<Ingredient> children;
    private final boolean isSimple;
    private ItemStack[] intersectedMatchingStacks = null;
    private IntList packedMatchingStacks = null;

    protected IntersectionIngredient(List<Ingredient> children) {
        if (children.size() < 2) {
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with one or no children");
        }
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream().allMatch(Ingredient::isSimple);
    }

    public static Ingredient of(Ingredient ... ingredients) {
        if (ingredients.length == 0) {
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with no children, use Ingredient.of() to create an empty ingredient");
        }
        if (ingredients.length == 1) {
            return ingredients[0];
        }
        return new IntersectionIngredient(Arrays.asList(ingredients));
    }

    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return false;
        }
        for (Ingredient ingredient : this.children) {
            if (ingredient.test(stack)) continue;
            return false;
        }
        return true;
    }

    public ItemStack[] m_43908_() {
        if (this.intersectedMatchingStacks == null) {
            this.intersectedMatchingStacks = (ItemStack[])Arrays.stream(this.children.get(0).m_43908_()).filter(stack -> {
                for (int i = 1; i < this.children.size(); ++i) {
                    if (this.children.get(i).test(stack)) continue;
                    return false;
                }
                return true;
            }).toArray(ItemStack[]::new);
        }
        return this.intersectedMatchingStacks;
    }

    public boolean m_43947_() {
        return this.children.stream().anyMatch(Ingredient::m_43947_);
    }

    @Override
    public boolean isSimple() {
        return this.isSimple;
    }

    protected void invalidate() {
        super.invalidate();
        this.intersectedMatchingStacks = null;
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
        JsonArray array = new JsonArray();
        for (Ingredient ingredient : this.children) {
            array.add(ingredient.m_43942_());
        }
        json.add("children", (JsonElement)array);
        return json;
    }

    public IIngredientSerializer<IntersectionIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer
    implements IIngredientSerializer<IntersectionIngredient> {
        public static final IIngredientSerializer<IntersectionIngredient> INSTANCE = new Serializer();

        @Override
        public IntersectionIngredient parse(JsonObject json) {
            JsonArray children = GsonHelper.m_13933_((JsonObject)json, (String)"children");
            if (children.size() < 2) {
                throw new JsonSyntaxException("Must have at least two children for an intersection ingredient");
            }
            return new IntersectionIngredient(IntStream.range(0, children.size()).mapToObj(i -> Ingredient.m_288218_((JsonElement)children.get(i), (boolean)false)).toList());
        }

        @Override
        public IntersectionIngredient parse(FriendlyByteBuf buffer) {
            return new IntersectionIngredient(Stream.generate(() -> Ingredient.m_43940_((FriendlyByteBuf)buffer)).limit(buffer.m_130242_()).toList());
        }

        @Override
        public void write(FriendlyByteBuf buffer, IntersectionIngredient intersection) {
            buffer.m_130130_(intersection.children.size());
            for (Ingredient ingredient : intersection.children) {
                ingredient.m_43923_(buffer);
            }
        }
    }
}

