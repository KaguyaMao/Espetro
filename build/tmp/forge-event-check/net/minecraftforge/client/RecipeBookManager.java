/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.client.RecipeBookCategories
 *  net.minecraft.world.inventory.RecipeBookType
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class RecipeBookManager {
    private static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES = new HashMap<RecipeBookCategories, List<RecipeBookCategories>>();
    private static final Map<RecipeBookType, List<RecipeBookCategories>> TYPE_CATEGORIES = new HashMap<RecipeBookType, List<RecipeBookCategories>>();
    private static final Map<RecipeType<?>, Function<Recipe<?>, RecipeBookCategories>> RECIPE_CATEGORY_LOOKUPS = new HashMap();
    private static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES_VIEW = Collections.unmodifiableMap(AGGREGATE_CATEGORIES);

    @Nullable
    public static <T extends Recipe<?>> RecipeBookCategories findCategories(RecipeType<T> type, T recipe) {
        Function<Recipe<?>, RecipeBookCategories> lookup = RECIPE_CATEGORY_LOOKUPS.get(type);
        return lookup != null ? lookup.apply(recipe) : null;
    }

    @ApiStatus.Internal
    public static Map<RecipeBookCategories, List<RecipeBookCategories>> getAggregateCategories() {
        return AGGREGATE_CATEGORIES_VIEW;
    }

    @ApiStatus.Internal
    public static List<RecipeBookCategories> getCustomCategoriesOrEmpty(RecipeBookType recipeBookType) {
        return TYPE_CATEGORIES.getOrDefault(recipeBookType, List.of());
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap<RecipeBookCategories, ImmutableList<RecipeBookCategories>> aggregateCategories = new HashMap<RecipeBookCategories, ImmutableList<RecipeBookCategories>>((Map<RecipeBookCategories, ImmutableList<RecipeBookCategories>>)ImmutableMap.of((Object)RecipeBookCategories.CRAFTING_SEARCH, (Object)ImmutableList.of((Object)RecipeBookCategories.CRAFTING_EQUIPMENT, (Object)RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, (Object)RecipeBookCategories.CRAFTING_MISC, (Object)RecipeBookCategories.CRAFTING_REDSTONE), (Object)RecipeBookCategories.FURNACE_SEARCH, (Object)ImmutableList.of((Object)RecipeBookCategories.FURNACE_FOOD, (Object)RecipeBookCategories.FURNACE_BLOCKS, (Object)RecipeBookCategories.FURNACE_MISC), (Object)RecipeBookCategories.BLAST_FURNACE_SEARCH, (Object)ImmutableList.of((Object)RecipeBookCategories.BLAST_FURNACE_BLOCKS, (Object)RecipeBookCategories.BLAST_FURNACE_MISC), (Object)RecipeBookCategories.SMOKER_SEARCH, (Object)ImmutableList.of((Object)RecipeBookCategories.SMOKER_FOOD)));
        HashMap<RecipeBookType, ImmutableList<RecipeBookCategories>> typeCategories = new HashMap<RecipeBookType, ImmutableList<RecipeBookCategories>>();
        HashMap recipeCategoryLookups = new HashMap();
        RegisterRecipeBookCategoriesEvent event = new RegisterRecipeBookCategoriesEvent(aggregateCategories, typeCategories, recipeCategoryLookups);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        AGGREGATE_CATEGORIES.putAll(aggregateCategories);
        TYPE_CATEGORIES.putAll(typeCategories);
        RECIPE_CATEGORY_LOOKUPS.putAll(recipeCategoryLookups);
    }
}

