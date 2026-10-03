/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.shedaniel.rei.api.client.registry.category.CategoryRegistry
 *  me.shedaniel.rei.api.client.registry.category.CategoryRegistry$CategoryConfiguration
 *  me.shedaniel.rei.api.common.category.CategoryIdentifier
 *  me.shedaniel.rei.api.common.util.CollectionUtils
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

public class RemoveREICategoryEventJS
extends EventJS {
    private final Set<CategoryIdentifier<?>> categoriesRemoved;
    private final CategoryRegistry registry;

    public RemoveREICategoryEventJS(Set<CategoryIdentifier<?>> categoriesRemoved) {
        this.categoriesRemoved = categoriesRemoved;
        this.registry = CategoryRegistry.getInstance();
    }

    public CategoryRegistry getRegistry() {
        return this.registry;
    }

    public CategoryRegistry getCategories() {
        return this.registry;
    }

    public Collection<ResourceLocation> getCategoryIds() {
        return CollectionUtils.map((Iterable)this.registry, CategoryRegistry.CategoryConfiguration::getIdentifier);
    }

    public void remove(ResourceLocation ... categories) {
        for (ResourceLocation id : categories) {
            this.categoriesRemoved.add(CategoryIdentifier.of((ResourceLocation)id));
        }
    }

    public void removeIf(Predicate<CategoryRegistry.CategoryConfiguration<?>> filter) {
        this.registry.stream().filter(filter).map(CategoryRegistry.CategoryConfiguration::getIdentifier).forEach(xva$0 -> this.remove((ResourceLocation)xva$0));
    }
}

