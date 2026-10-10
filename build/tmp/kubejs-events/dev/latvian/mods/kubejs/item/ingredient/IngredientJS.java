/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.Wrapper
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  dev.latvian.mods.rhino.regexp.NativeRegExp
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public interface IngredientJS {
    public static Ingredient of(@Nullable Object o) {
        while (o instanceof Wrapper) {
            Wrapper w = (Wrapper)o;
            o = w.unwrap();
        }
        if (o == null || o == ItemStack.f_41583_ || o == Items.f_41852_ || o == Ingredient.f_43901_) {
            return Ingredient.f_43901_;
        }
        if (o instanceof IngredientSupplierKJS) {
            IngredientSupplierKJS ingr = (IngredientSupplierKJS)o;
            return ingr.kjs$asIngredient();
        }
        if (o instanceof TagKey) {
            TagKey tag = (TagKey)o;
            return Ingredient.m_204132_((TagKey)TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)tag.f_203868_()));
        }
        if (o instanceof Pattern || o instanceof NativeRegExp) {
            Pattern reg = UtilsJS.parseRegex(o);
            if (reg != null) {
                return IngredientPlatformHelper.get().regex(reg);
            }
            return Ingredient.f_43901_;
        }
        if (o instanceof JsonElement) {
            JsonElement json = (JsonElement)o;
            return IngredientJS.ofJson(json);
        }
        if (o instanceof CharSequence) {
            return IngredientJS.parse(o.toString());
        }
        List<?> list = ListJS.of(o);
        if (list != null) {
            ArrayList<Ingredient> inList = new ArrayList<Ingredient>(list.size());
            for (Object o1 : list) {
                Ingredient ingredient = IngredientJS.of(o1);
                if (ingredient == Ingredient.f_43901_) continue;
                inList.add(ingredient);
            }
            if (inList.isEmpty()) {
                return Ingredient.f_43901_;
            }
            if (inList.size() == 1) {
                return (Ingredient)inList.get(0);
            }
            return IngredientPlatformHelper.get().or(inList.toArray(new Ingredient[0]));
        }
        Map<?, ?> map = MapJS.of(o);
        if (map != null) {
            Ingredient in = Ingredient.f_43901_;
            boolean val = map.containsKey("value");
            if (map.containsKey("type")) {
                if ("forge:nbt".equals(map.get("type"))) {
                    in = ItemStackJS.of(map.get("item")).kjs$withNBT(NBTUtils.toTagCompound(map.get("nbt"))).kjs$strongNBT();
                } else {
                    JsonObject json = MapJS.json(o);
                    if (json == null) {
                        throw new RecipeExceptionJS("Failed to parse custom ingredient (" + String.valueOf(o) + " is not a json object");
                    }
                    try {
                        in = RecipePlatformHelper.get().getCustomIngredient(json);
                    }
                    catch (Exception ex) {
                        throw new RecipeExceptionJS("Failed to parse custom ingredient (" + String.valueOf(json.get("type")) + ") from " + String.valueOf(json) + ": " + String.valueOf(ex));
                    }
                }
            } else if (val || map.containsKey("ingredient")) {
                in = IngredientJS.of(val ? map.get("value") : map.get("ingredient"));
            } else if (map.containsKey("tag")) {
                in = IngredientPlatformHelper.get().tag(map.get("tag").toString());
            } else if (map.containsKey("item")) {
                in = ItemStackJS.of(map).m_41720_().kjs$asIngredient();
            }
            return in;
        }
        return ItemStackJS.of(o).kjs$asIngredient();
    }

    public static Ingredient parse(String s) {
        if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
            return Ingredient.f_43901_;
        }
        if (s.equals("*")) {
            return IngredientPlatformHelper.get().wildcard();
        }
        if (s.startsWith("#")) {
            return IngredientPlatformHelper.get().tag(s.substring(1));
        }
        if (s.startsWith("@")) {
            return IngredientPlatformHelper.get().mod(s.substring(1));
        }
        if (s.startsWith("%")) {
            CreativeModeTab group = UtilsJS.findCreativeTab(new ResourceLocation(s.substring(1)));
            if (group == null) {
                if (RecipeJS.itemErrors) {
                    throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
                }
                return Ingredient.f_43901_;
            }
            return IngredientPlatformHelper.get().creativeTab(group);
        }
        Pattern reg = UtilsJS.parseRegex(s);
        if (reg != null) {
            return IngredientPlatformHelper.get().regex(reg);
        }
        Item item = RegistryInfo.ITEM.getValue(new ResourceLocation(s));
        if (item == null || item == Items.f_41852_) {
            return Ingredient.f_43901_;
        }
        return item.kjs$asIngredient();
    }

    public static Ingredient ofJson(JsonElement json) {
        if (json == null || json.isJsonNull() || json.isJsonArray() && json.getAsJsonArray().isEmpty()) {
            return Ingredient.f_43901_;
        }
        if (json.isJsonPrimitive()) {
            return IngredientJS.of(json.getAsString());
        }
        return Ingredient.m_43917_((JsonElement)json);
    }

    public static Ingredient ofNetwork(FriendlyByteBuf buf) {
        return Ingredient.m_43940_((FriendlyByteBuf)buf);
    }
}

