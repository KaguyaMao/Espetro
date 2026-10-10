/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.shedaniel.rei.api.client.registry.category.CategoryRegistry
 *  me.shedaniel.rei.api.client.registry.category.CategoryRegistry$CategoryConfiguration
 *  me.shedaniel.rei.api.client.registry.display.DisplayRegistry
 *  me.shedaniel.rei.api.common.category.CategoryIdentifier
 *  me.shedaniel.rei.api.common.util.CollectionUtils
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

public class RemoveREIRecipeEventJS
extends EventJS {
    private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved;
    private final CategoryRegistry categories;
    private final DisplayRegistry displays;

    public RemoveREIRecipeEventJS(Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved) {
        this.recipesRemoved = recipesRemoved;
        this.categories = CategoryRegistry.getInstance();
        this.displays = DisplayRegistry.getInstance();
    }

    public CategoryRegistry getCategories() {
        return this.categories;
    }

    public DisplayRegistry getDisplays() {
        return this.displays;
    }

    public List<?> getDisplaysFor(ResourceLocation category) {
        return this.displays.get(CategoryIdentifier.of((ResourceLocation)category));
    }

    public Collection<ResourceLocation> getCategoryIds() {
        return CollectionUtils.map((Iterable)this.categories, CategoryRegistry.CategoryConfiguration::getIdentifier);
    }

    public void remove(ResourceLocation category, ResourceLocation ... recipesToRemove) {
        CategoryIdentifier catId = CategoryIdentifier.of((ResourceLocation)category);
        if (this.categories.tryGet(catId).isEmpty()) {
            KubeJS.LOGGER.warn("Failed to remove recipes for type {}: Category doesn't exist!", (Object)category);
            KubeJS.LOGGER.info("Use event.categoryIds to get a list of all categories.");
            return;
        }
        this.recipesRemoved.computeIfAbsent(catId, _0 -> new HashSet()).addAll(List.of(recipesToRemove));
    }

    public void removeFromAll(ResourceLocation ... recipesToRemove) {
        List<ResourceLocation> asList = List.of(recipesToRemove);
        for (CategoryRegistry.CategoryConfiguration catId : this.categories) {
            this.recipesRemoved.computeIfAbsent(catId.getCategoryIdentifier(), _0 -> new HashSet()).addAll(asList);
        }
    }
}

