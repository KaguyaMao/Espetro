/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompoundIngredient
extends AbstractIngredient {
    private List<Ingredient> children;
    private ItemStack[] stacks;
    private IntList itemIds;
    private final boolean isSimple;

    protected CompoundIngredient(List<Ingredient> children) {
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream().allMatch(Ingredient::isSimple);
    }

    public static Ingredient of(Ingredient ... children) {
        if (children.length == 0) {
            throw new IllegalArgumentException("Cannot create a compound ingredient with no children, use Ingredient.of() to create an empty ingredient");
        }
        if (children.length == 1) {
            return children[0];
        }
        ArrayList<Ingredient> vanillaIngredients = new ArrayList<Ingredient>();
        ArrayList<Ingredient> allIngredients = new ArrayList<Ingredient>();
        for (Ingredient child : children) {
            if (child.getSerializer() == VanillaIngredientSerializer.INSTANCE) {
                vanillaIngredients.add(child);
                continue;
            }
            allIngredients.add(child);
        }
        if (!vanillaIngredients.isEmpty()) {
            allIngredients.add(CompoundIngredient.merge(vanillaIngredients));
        }
        if (allIngredients.size() == 1) {
            return (Ingredient)allIngredients.get(0);
        }
        return new CompoundIngredient(allIngredients);
    }

    @NotNull
    public ItemStack[] m_43908_() {
        if (this.stacks == null) {
            ArrayList tmp = Lists.newArrayList();
            for (Ingredient child : this.children) {
                Collections.addAll(tmp, child.m_43908_());
            }
            this.stacks = tmp.toArray(new ItemStack[tmp.size()]);
        }
        return this.stacks;
    }

    @NotNull
    public IntList m_43931_() {
        boolean childrenNeedInvalidation = false;
        for (Ingredient child : this.children) {
            childrenNeedInvalidation |= child.checkInvalidation();
        }
        if (childrenNeedInvalidation || this.itemIds == null || this.checkInvalidation()) {
            this.markValid();
            this.itemIds = new IntArrayList();
            for (Ingredient child : this.children) {
                this.itemIds.addAll(child.m_43931_());
            }
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.itemIds;
    }

    public boolean test(@Nullable ItemStack target) {
        if (target == null) {
            return false;
        }
        return this.children.stream().anyMatch(c -> c.test(target));
    }

    protected void invalidate() {
        this.itemIds = null;
        this.stacks = null;
    }

    @Override
    public boolean isSimple() {
        return this.isSimple;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @NotNull
    public Collection<Ingredient> getChildren() {
        return this.children;
    }

    @Override
    public JsonElement m_43942_() {
        if (this.children.size() == 1) {
            return this.children.get(0).m_43942_();
        }
        JsonArray json = new JsonArray();
        this.children.stream().forEach(e -> json.add(e.m_43942_()));
        return json;
    }

    public boolean m_43947_() {
        return this.children.stream().allMatch(Ingredient::m_43947_);
    }

    public static class Serializer
    implements IIngredientSerializer<CompoundIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public CompoundIngredient parse(FriendlyByteBuf buffer) {
            return new CompoundIngredient(Stream.generate(() -> Ingredient.m_43940_((FriendlyByteBuf)buffer)).limit(buffer.m_130242_()).collect(Collectors.toList()));
        }

        @Override
        public CompoundIngredient parse(JsonObject json) {
            throw new JsonSyntaxException("CompoundIngredient should not be directly referenced in json, just use an array of ingredients.");
        }

        @Override
        public void write(FriendlyByteBuf buffer, CompoundIngredient ingredient) {
            buffer.m_130130_(ingredient.children.size());
            ingredient.children.forEach(c -> c.m_43923_(buffer));
        }
    }
}

