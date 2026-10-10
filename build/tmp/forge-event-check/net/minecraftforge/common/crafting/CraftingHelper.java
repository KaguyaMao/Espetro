/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.nbt.TagParser
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

public class CraftingHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CRAFTHELPER = MarkerManager.getMarker((String)"CRAFTHELPER");
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<ResourceLocation, IConditionSerializer<?>> conditions = new HashMap();
    private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();

    public static IConditionSerializer<?> register(IConditionSerializer<?> serializer) {
        ResourceLocation key = serializer.getID();
        if (conditions.containsKey(key)) {
            throw new IllegalStateException("Duplicate recipe condition serializer: " + String.valueOf(key));
        }
        conditions.put(key, serializer);
        return serializer;
    }

    public static <T extends Ingredient> IIngredientSerializer<T> register(ResourceLocation key, IIngredientSerializer<T> serializer) {
        if (ingredients.containsKey((Object)key)) {
            throw new IllegalStateException("Duplicate recipe ingredient serializer: " + String.valueOf(key));
        }
        if (ingredients.containsValue(serializer)) {
            throw new IllegalStateException("Duplicate recipe ingredient serializer: " + String.valueOf(key) + " " + String.valueOf(serializer));
        }
        ingredients.put((Object)key, serializer);
        return serializer;
    }

    @Nullable
    public static ResourceLocation getID(IIngredientSerializer<?> serializer) {
        return (ResourceLocation)ingredients.inverse().get(serializer);
    }

    public static <T extends Ingredient> void write(FriendlyByteBuf buffer, T ingredient) {
        IIngredientSerializer serializer = ingredient.getSerializer();
        ResourceLocation key = (ResourceLocation)ingredients.inverse().get((Object)serializer);
        if (key == null) {
            throw new IllegalArgumentException("Tried to serialize unregistered Ingredient: " + String.valueOf(ingredient) + " " + String.valueOf(serializer));
        }
        if (serializer != VanillaIngredientSerializer.INSTANCE) {
            buffer.m_130130_(-1);
            buffer.m_130085_(key);
        }
        serializer.write(buffer, ingredient);
    }

    public static Ingredient getIngredient(ResourceLocation type, FriendlyByteBuf buffer) {
        IIngredientSerializer serializer = (IIngredientSerializer)ingredients.get((Object)type);
        if (serializer == null) {
            throw new IllegalArgumentException("Can not deserialize unknown Ingredient type: " + String.valueOf(type));
        }
        return serializer.parse(buffer);
    }

    public static Ingredient getIngredient(JsonElement json, boolean allowEmpty) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Json cannot be null");
        }
        if (json.isJsonArray()) {
            ArrayList ingredients = Lists.newArrayList();
            ArrayList vanilla = Lists.newArrayList();
            json.getAsJsonArray().forEach(ele -> {
                Ingredient ing = CraftingHelper.getIngredient(ele, allowEmpty);
                if (ing.getClass() == Ingredient.class) {
                    vanilla.add(ing);
                } else {
                    ingredients.add(ing);
                }
            });
            if (!vanilla.isEmpty()) {
                ingredients.add(Ingredient.merge((Collection)vanilla));
            }
            if (ingredients.size() == 0) {
                if (allowEmpty) {
                    return Ingredient.f_43901_;
                }
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            if (ingredients.size() == 1) {
                return (Ingredient)ingredients.get(0);
            }
            return new CompoundIngredient(ingredients);
        }
        if (!json.isJsonObject()) {
            throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");
        }
        JsonObject obj = (JsonObject)json;
        String type = GsonHelper.m_13851_((JsonObject)obj, (String)"type", (String)"minecraft:item");
        if (type.isEmpty()) {
            throw new JsonSyntaxException("Ingredient type can not be an empty string");
        }
        IIngredientSerializer serializer = (IIngredientSerializer)ingredients.get((Object)new ResourceLocation(type));
        if (serializer == null) {
            throw new JsonSyntaxException("Unknown ingredient type: " + type);
        }
        return serializer.parse(obj);
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT) {
        return CraftingHelper.getItemStack(json, readNBT, false);
    }

    public static Item getItem(String itemName, boolean disallowsAirInRecipe) {
        ResourceLocation itemKey = new ResourceLocation(itemName);
        if (!ForgeRegistries.ITEMS.containsKey(itemKey)) {
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");
        }
        Item item = ForgeRegistries.ITEMS.getValue(itemKey);
        if (disallowsAirInRecipe && item == Items.f_41852_) {
            throw new JsonSyntaxException("Invalid item: " + itemName);
        }
        return Objects.requireNonNull(item);
    }

    public static CompoundTag getNBT(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                return TagParser.m_129359_((String)GSON.toJson(element));
            }
            return TagParser.m_129359_((String)GsonHelper.m_13805_((JsonElement)element, (String)"nbt"));
        }
        catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid NBT Entry: " + String.valueOf((Object)e));
        }
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
        String itemName = GsonHelper.m_13906_((JsonObject)json, (String)"item");
        Item item = CraftingHelper.getItem(itemName, disallowsAirInRecipe);
        if (readNBT && json.has("nbt")) {
            CompoundTag nbt = CraftingHelper.getNBT(json.get("nbt"));
            CompoundTag tmp = new CompoundTag();
            if (nbt.m_128441_("ForgeCaps")) {
                tmp.m_128365_("ForgeCaps", nbt.m_128423_("ForgeCaps"));
                nbt.m_128473_("ForgeCaps");
            }
            tmp.m_128365_("tag", (Tag)nbt);
            tmp.m_128359_("id", itemName);
            tmp.m_128405_("Count", GsonHelper.m_13824_((JsonObject)json, (String)"count", (int)1));
            return ItemStack.m_41712_((CompoundTag)tmp);
        }
        return new ItemStack((ItemLike)item, GsonHelper.m_13824_((JsonObject)json, (String)"count", (int)1));
    }

    public static boolean processConditions(JsonObject json, String memberName, ICondition.IContext context) {
        return !json.has(memberName) || CraftingHelper.processConditions(GsonHelper.m_13933_((JsonObject)json, (String)memberName), context);
    }

    public static boolean processConditions(JsonArray conditions, ICondition.IContext context) {
        for (int x = 0; x < conditions.size(); ++x) {
            if (!conditions.get(x).isJsonObject()) {
                throw new JsonSyntaxException("Conditions must be an array of JsonObjects");
            }
            JsonObject json = conditions.get(x).getAsJsonObject();
            if (CraftingHelper.getCondition(json).test(context)) continue;
            return false;
        }
        return true;
    }

    public static ICondition getCondition(JsonObject json) {
        ResourceLocation type = new ResourceLocation(GsonHelper.m_13906_((JsonObject)json, (String)"type"));
        IConditionSerializer<?> serializer = conditions.get(type);
        if (serializer == null) {
            throw new JsonSyntaxException("Unknown condition type: " + type.toString());
        }
        return serializer.read(json);
    }

    public static <T extends ICondition> JsonObject serialize(T condition) {
        IConditionSerializer<?> serializer = conditions.get(condition.getID());
        if (serializer == null) {
            throw new JsonSyntaxException("Unknown condition type: " + condition.getID().toString());
        }
        return serializer.getJson(condition);
    }

    public static JsonArray serialize(ICondition ... conditions) {
        JsonArray arr = new JsonArray();
        for (ICondition iCond : conditions) {
            arr.add((JsonElement)CraftingHelper.serialize(iCond));
        }
        return arr;
    }
}

