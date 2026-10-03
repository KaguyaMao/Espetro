/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.common.ForgeHooks
 */
package com.sighs.apricityui.slot;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sighs.apricityui.slot.IngredientDisplaySpec;
import com.sighs.apricityui.slot.ItemStackExpressionCompiler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ForgeHooks;

public final class IngredientExpressionCompiler {
    public static final int MAX_CANDIDATES = 128;
    public static final ResourceLocation FURNACE_FUEL_TAG = new ResourceLocation("apricityui", "furnace_fuels");
    private static final Map<ResourceLocation, List<ItemStack>> TAG_CACHE = new ConcurrentHashMap<ResourceLocation, List<ItemStack>>();

    private IngredientExpressionCompiler() {
    }

    public static IngredientDisplaySpec compile(String rawExpression, boolean cycleEnabled, long cycleIntervalMs) {
        String normalized = ItemStackExpressionCompiler.normalize(rawExpression);
        if (normalized.isBlank()) {
            return IngredientDisplaySpec.EMPTY;
        }
        List<ItemStack> candidates = IngredientExpressionCompiler.compileCandidates(normalized, 128);
        return new IngredientDisplaySpec(candidates, cycleEnabled && candidates.size() > 1, cycleIntervalMs);
    }

    public static void clearTagCache() {
        TAG_CACHE.clear();
    }

    public static String furnaceFuelTagLiteral() {
        return "#" + FURNACE_FUEL_TAG;
    }

    private static List<ItemStack> compileCandidates(String expression, int maxCandidates) {
        if (expression.contains("|")) {
            return IngredientExpressionCompiler.compilePipe(expression, maxCandidates);
        }
        if (expression.startsWith("#")) {
            return IngredientExpressionCompiler.tagCandidates(IngredientExpressionCompiler.parseTag(expression), maxCandidates);
        }
        if (expression.startsWith("{") || expression.startsWith("[")) {
            return IngredientExpressionCompiler.jsonCandidates(expression, maxCandidates);
        }
        ItemStack stack = ItemStackExpressionCompiler.parse(expression);
        return stack.m_41619_() ? List.of() : List.of(stack);
    }

    private static List<ItemStack> compilePipe(String expression, int maxCandidates) {
        LinkedHashMap<String, ItemStack> candidates = new LinkedHashMap<String, ItemStack>();
        for (String part : expression.split("\\|")) {
            if (candidates.size() >= maxCandidates) break;
            String normalized = ItemStackExpressionCompiler.normalize(part);
            if (normalized.isBlank()) continue;
            if (normalized.startsWith("#")) {
                IngredientExpressionCompiler.append(candidates, IngredientExpressionCompiler.tagCandidates(IngredientExpressionCompiler.parseTag(normalized), maxCandidates - candidates.size()), maxCandidates);
                continue;
            }
            ItemStack stack = ItemStackExpressionCompiler.parse(normalized);
            if (stack.m_41619_()) continue;
            IngredientExpressionCompiler.append(candidates, List.of(stack), maxCandidates);
        }
        return List.copyOf(candidates.values());
    }

    private static List<ItemStack> jsonCandidates(String expression, int maxCandidates) {
        try {
            JsonElement json = JsonParser.parseString((String)expression);
            Ingredient ingredient = Ingredient.m_43917_((JsonElement)json);
            ItemStack[] items = ingredient.m_43908_();
            if (items == null || items.length == 0) {
                return List.of();
            }
            LinkedHashMap<String, ItemStack> candidates = new LinkedHashMap<String, ItemStack>();
            for (ItemStack stack : items) {
                IngredientExpressionCompiler.append(candidates, List.of(stack), maxCandidates);
            }
            return List.copyOf(candidates.values());
        }
        catch (Exception ignored) {
            return List.of();
        }
    }

    private static void append(Map<String, ItemStack> output, List<ItemStack> stacks, int maxCandidates) {
        if (stacks == null) {
            return;
        }
        for (ItemStack stack : stacks) {
            if (output.size() >= maxCandidates) {
                return;
            }
            if (stack == null || stack.m_41619_()) continue;
            ItemStack copy = stack.m_41777_();
            if (copy.m_41613_() <= 0) {
                copy.m_41764_(1);
            }
            output.putIfAbsent(ItemStackExpressionCompiler.serialize(copy), copy);
        }
    }

    private static ResourceLocation parseTag(String expression) {
        return expression == null || expression.length() < 2 ? null : ResourceLocation.m_135820_((String)expression.substring(1));
    }

    private static List<ItemStack> tagCandidates(ResourceLocation id, int maxCandidates) {
        if (id == null || maxCandidates <= 0) {
            return List.of();
        }
        List cached = TAG_CACHE.computeIfAbsent(id, IngredientExpressionCompiler::buildTagCandidates);
        return cached.size() <= maxCandidates ? cached : cached.subList(0, maxCandidates);
    }

    private static List<ItemStack> buildTagCandidates(ResourceLocation id) {
        ArrayList<ItemStack> candidates = new ArrayList<ItemStack>();
        if (FURNACE_FUEL_TAG.equals((Object)id)) {
            for (Item item : BuiltInRegistries.f_257033_) {
                ItemStack stack2 = new ItemStack((ItemLike)item);
                if (ForgeHooks.getBurnTime((ItemStack)stack2, (RecipeType)RecipeType.f_44108_) <= 0) continue;
                candidates.add(stack2);
                if (candidates.size() < 128) continue;
                break;
            }
        } else {
            TagKey key = TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)id);
            for (Item item : BuiltInRegistries.f_257033_) {
                ItemStack stack3 = new ItemStack((ItemLike)item);
                if (!stack3.m_204117_(key)) continue;
                candidates.add(stack3);
                if (candidates.size() < 128) continue;
                break;
            }
        }
        candidates.sort(Comparator.comparing(stack -> String.valueOf(BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_()))));
        return List.copyOf(candidates);
    }
}

