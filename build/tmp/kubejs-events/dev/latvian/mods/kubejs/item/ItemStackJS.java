/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Wrapper
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  dev.latvian.mods.rhino.regexp.NativeRegExp
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemStackLinkedSet
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public interface ItemStackJS {
    public static final Map<String, ItemStack> PARSE_CACHE = new HashMap<String, ItemStack>();
    public static final ItemStack[] EMPTY_ARRAY = new ItemStack[0];
    public static final Lazy<List<String>> CACHED_ITEM_TYPE_LIST = Lazy.of(() -> {
        ArrayList<String> cachedItemTypeList = new ArrayList<String>();
        for (Map.Entry<ResourceKey<Item>, Item> entry : RegistryInfo.ITEM.entrySet()) {
            cachedItemTypeList.add(entry.getKey().m_135782_().toString());
        }
        return cachedItemTypeList;
    });
    public static final Lazy<Map<ResourceLocation, Collection<ItemStack>>> CACHED_ITEM_MAP = Lazy.of(() -> {
        HashMap<ResourceLocation, Collection> map = new HashMap<ResourceLocation, Collection>();
        Set stackList = ItemStackLinkedSet.m_261170_();
        stackList.addAll(CreativeModeTabs.m_258007_().m_260957_());
        for (ItemStack stack : stackList) {
            if (stack.m_41619_()) continue;
            map.computeIfAbsent(stack.m_41720_().kjs$getIdLocation(), _rl -> ItemStackLinkedSet.m_261170_()).add(stack.kjs$withCount(1));
        }
        for (String itemId : CACHED_ITEM_TYPE_LIST.get()) {
            ResourceLocation itemRl = new ResourceLocation(itemId);
            map.computeIfAbsent(itemRl, id -> Set.of(RegistryInfo.ITEM.getValue((ResourceLocation)id).m_7968_()));
        }
        return map;
    });
    public static final Lazy<List<ItemStack>> CACHED_ITEM_LIST = Lazy.of(() -> CACHED_ITEM_MAP.get().values().stream().flatMap(Collection::stream).toList());

    public static ItemStack of(@Nullable Object o) {
        if (o instanceof Wrapper) {
            Wrapper w = (Wrapper)o;
            o = w.unwrap();
        }
        if (o == null || o == ItemStack.f_41583_ || o == Items.f_41852_) {
            return ItemStack.f_41583_;
        }
        if (o instanceof ItemStack) {
            ItemStack stack = (ItemStack)o;
            return stack.m_41619_() ? ItemStack.f_41583_ : stack;
        }
        if (o instanceof OutputItem) {
            OutputItem out = (OutputItem)o;
            return out.item;
        }
        if (o instanceof Ingredient) {
            Ingredient ingr = (Ingredient)o;
            return ingr.kjs$getFirst();
        }
        if (o instanceof ResourceLocation) {
            ResourceLocation id = (ResourceLocation)o;
            Item item = RegistryInfo.ITEM.getValue(id);
            if (item == null || item == Items.f_41852_) {
                if (RecipeJS.itemErrors) {
                    throw new RecipeExceptionJS("Item '" + String.valueOf(id) + "' not found!").error();
                }
                return ItemStack.f_41583_;
            }
            return item.m_7968_();
        }
        if (o instanceof ItemLike) {
            ItemLike itemLike = (ItemLike)o;
            return new ItemStack((ItemLike)itemLike.m_5456_());
        }
        if (o instanceof JsonElement) {
            JsonElement json = (JsonElement)o;
            return ItemStackJS.resultFromRecipeJson(json);
        }
        if (o instanceof StringTag) {
            StringTag tag = (StringTag)o;
            return ItemStackJS.of(tag.m_7916_());
        }
        if (o instanceof Pattern || o instanceof NativeRegExp) {
            Pattern reg = UtilsJS.parseRegex(o);
            if (reg != null) {
                return IngredientPlatformHelper.get().regex(reg).kjs$getFirst();
            }
            return ItemStack.f_41583_;
        }
        if (o instanceof CharSequence) {
            String os;
            String s = os = o.toString().trim();
            ItemStack cached = PARSE_CACHE.get(os);
            if (cached != null) {
                return cached.m_41619_() ? ItemStack.f_41583_ : cached.m_41777_();
            }
            int count = 1;
            int spaceIndex = s.indexOf(32);
            if (spaceIndex >= 2 && s.indexOf(120) == spaceIndex - 1) {
                count = Integer.parseInt(s.substring(0, spaceIndex - 1));
                s = s.substring(spaceIndex + 1);
            }
            cached = ItemStackJS.parse(s);
            cached.m_41764_(count);
            PARSE_CACHE.put(os, cached);
            return cached.m_41777_();
        }
        Map<?, ?> map = MapJS.of(o);
        if (map != null) {
            if (map.containsKey("item")) {
                ResourceLocation id = UtilsJS.getMCID(null, map.get("item").toString());
                Item item = RegistryInfo.ITEM.getValue(id);
                if (item == Items.f_41852_) {
                    if (RecipeJS.itemErrors) {
                        throw new RecipeExceptionJS("Item '" + String.valueOf(id) + "' not found!").error();
                    }
                    return ItemStack.f_41583_;
                }
                ItemStack stack = new ItemStack((ItemLike)item);
                Object obj = map.get("count");
                if (obj instanceof Number) {
                    Number number = (Number)obj;
                    stack.m_41764_(number.intValue());
                }
                if (map.containsKey("nbt")) {
                    stack.m_41751_(NBTUtils.toTagCompound(map.get("nbt")));
                }
                return stack;
            }
            Object id = map.get("tag");
            if (id instanceof CharSequence) {
                CharSequence s = (CharSequence)id;
                ItemStack stack = IngredientPlatformHelper.get().tag(s.toString()).kjs$getFirst();
                if (map.containsKey("count")) {
                    stack.m_41764_(UtilsJS.parseInt(map.get("count"), 1));
                }
                return stack;
            }
        }
        return ItemStack.f_41583_;
    }

    public static ItemStack parse(String s) {
        String tagStr;
        if (s.isEmpty() || s.equals("-") || s.equals("air") || s.equals("minecraft:air")) {
            return ItemStack.f_41583_;
        }
        if (s.startsWith("#")) {
            return IngredientPlatformHelper.get().tag(s.substring(1)).kjs$getFirst();
        }
        if (s.startsWith("@")) {
            return IngredientPlatformHelper.get().mod(s.substring(1)).kjs$getFirst();
        }
        if (s.startsWith("%")) {
            CreativeModeTab group = UtilsJS.findCreativeTab(new ResourceLocation(s.substring(1)));
            if (group == null) {
                if (RecipeJS.itemErrors) {
                    throw new RecipeExceptionJS("Item group '" + s.substring(1) + "' not found!").error();
                }
                return ItemStack.f_41583_;
            }
            return IngredientPlatformHelper.get().creativeTab(group).kjs$getFirst();
        }
        Pattern reg = UtilsJS.parseRegex(s);
        if (reg != null) {
            return IngredientPlatformHelper.get().regex(reg).kjs$getFirst();
        }
        int spaceIndex = s.indexOf(32);
        String id = spaceIndex == -1 ? s : s.substring(0, spaceIndex);
        Item item = RegistryInfo.ITEM.getValue(new ResourceLocation(id));
        if (item == Items.f_41852_) {
            if (RecipeJS.itemErrors) {
                throw new RecipeExceptionJS("Item '" + id + "' not found!").error();
            }
            return ItemStack.f_41583_;
        }
        ItemStack stack = new ItemStack((ItemLike)item);
        if (spaceIndex != -1 && (tagStr = s.substring(spaceIndex + 1)).length() >= 2 && tagStr.charAt(0) == '{') {
            stack.m_41751_(NBTUtils.toTagCompound((Object)tagStr));
        }
        return stack;
    }

    public static Item getRawItem(Context cx, @Nullable Object o) {
        if (o == null) {
            return Items.f_41852_;
        }
        if (o instanceof ItemLike) {
            ItemLike item = (ItemLike)o;
            return item.m_5456_();
        }
        if (o instanceof CharSequence) {
            String s = o.toString();
            if (s.isEmpty()) {
                return Items.f_41852_;
            }
            if (s.charAt(0) != '#') {
                return RegistryInfo.ITEM.getValue(UtilsJS.getMCID(cx, s));
            }
        }
        return ItemStackJS.of(o).m_41720_();
    }

    public static ItemStack resultFromRecipeJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ItemStack.f_41583_;
        }
        if (json.isJsonPrimitive()) {
            return ItemStackJS.of(json.getAsString());
        }
        if (json instanceof JsonObject) {
            JsonObject jsonObj = (JsonObject)json;
            ItemStack stack = null;
            if (jsonObj.has("item")) {
                stack = ItemStackJS.of(jsonObj.get("item").getAsString());
            } else if (jsonObj.has("tag")) {
                stack = IngredientPlatformHelper.get().tag(jsonObj.get("tag").getAsString()).kjs$getFirst();
            }
            if (stack != null) {
                if (jsonObj.has("count")) {
                    stack.m_41764_(jsonObj.get("count").getAsInt());
                } else if (jsonObj.has("amount")) {
                    stack.m_41764_(jsonObj.get("amount").getAsInt());
                }
                if (jsonObj.has("nbt")) {
                    JsonElement element = jsonObj.get("nbt");
                    if (element.isJsonObject()) {
                        stack.m_41751_(NBTUtils.toTagCompound((Object)element));
                    } else {
                        stack.m_41751_(NBTUtils.toTagCompound((Object)element.getAsString()));
                    }
                }
                return stack;
            }
        }
        return ItemStack.f_41583_;
    }

    public static String toItemString(Object object) {
        return ItemStackJS.of(object).kjs$toItemString();
    }

    public static List<ItemStack> getList() {
        return CACHED_ITEM_LIST.get();
    }

    public static List<String> getTypeList() {
        return CACHED_ITEM_TYPE_LIST.get();
    }

    public static Map<ResourceLocation, Collection<ItemStack>> getTypeToStacks() {
        return CACHED_ITEM_MAP.get();
    }

    public static void clearAllCaches() {
        CACHED_ITEM_LIST.forget();
        CACHED_ITEM_TYPE_LIST.forget();
        PARSE_CACHE.clear();
        InputItem.PARSE_CACHE.clear();
    }
}

