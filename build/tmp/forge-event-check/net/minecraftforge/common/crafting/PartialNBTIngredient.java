/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.advancements.critereon.NbtPredicate
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PartialNBTIngredient
extends AbstractIngredient {
    private final Set<Item> items;
    private final CompoundTag nbt;
    private final NbtPredicate predicate;

    protected PartialNBTIngredient(Set<Item> items, CompoundTag nbt) {
        super(items.stream().map(item -> {
            ItemStack stack = new ItemStack((ItemLike)item);
            stack.m_41751_(nbt.m_6426_());
            return new Ingredient.ItemValue(stack);
        }));
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a PartialNBTIngredient with no items");
        }
        this.items = Collections.unmodifiableSet(items);
        this.nbt = nbt;
        this.predicate = new NbtPredicate(nbt);
    }

    public static PartialNBTIngredient of(CompoundTag nbt, ItemLike ... items) {
        return new PartialNBTIngredient(Arrays.stream(items).map(ItemLike::m_5456_).collect(Collectors.toSet()), nbt);
    }

    public static PartialNBTIngredient of(ItemLike item, CompoundTag nbt) {
        return new PartialNBTIngredient(Set.of(item.m_5456_()), nbt);
    }

    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }
        return this.items.contains(input.m_41720_()) && this.predicate.m_57483_((Tag)input.getShareTag());
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
        if (this.items.size() == 1) {
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(this.items.iterator().next()).toString());
        } else {
            JsonArray items = new JsonArray();
            this.items.stream().map(ForgeRegistries.ITEMS::getKey).sorted().forEach(name -> items.add(name.toString()));
            json.add("items", (JsonElement)items);
        }
        json.addProperty("nbt", this.nbt.toString());
        return json;
    }

    public static class Serializer
    implements IIngredientSerializer<PartialNBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public PartialNBTIngredient parse(JsonObject json) {
            ImmutableSet items;
            if (json.has("item")) {
                items = Set.of(CraftingHelper.getItem(GsonHelper.m_13906_((JsonObject)json, (String)"item"), true));
            } else if (json.has("items")) {
                ImmutableSet.Builder builder = ImmutableSet.builder();
                JsonArray itemArray = GsonHelper.m_13933_((JsonObject)json, (String)"items");
                for (int i = 0; i < itemArray.size(); ++i) {
                    builder.add((Object)CraftingHelper.getItem(GsonHelper.m_13805_((JsonElement)itemArray.get(i), (String)("items[" + i + "]")), true));
                }
                items = builder.build();
            } else {
                throw new JsonSyntaxException("Must set either 'item' or 'items'");
            }
            if (!json.has("nbt")) {
                throw new JsonSyntaxException("Missing nbt, expected to find a String or JsonObject");
            }
            CompoundTag nbt = CraftingHelper.getNBT(json.get("nbt"));
            return new PartialNBTIngredient((Set<Item>)items, nbt);
        }

        @Override
        public PartialNBTIngredient parse(FriendlyByteBuf buffer) {
            Set<Item> items = Stream.generate(() -> (Item)buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS)).limit(buffer.m_130242_()).collect(Collectors.toSet());
            CompoundTag nbt = buffer.m_130260_();
            return new PartialNBTIngredient(items, Objects.requireNonNull(nbt));
        }

        @Override
        public void write(FriendlyByteBuf buffer, PartialNBTIngredient ingredient) {
            buffer.m_130130_(ingredient.items.size());
            for (Item item : ingredient.items) {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, (Object)item);
            }
            buffer.m_130079_(ingredient.nbt);
        }
    }
}

