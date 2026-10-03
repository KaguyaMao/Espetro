/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public record RegisterRecipeSchemasEvent(Map<String, RecipeNamespace> namespaces, Map<String, ResourceLocation> mappedRecipes) {
    public RecipeNamespace namespace(String namespace) {
        return this.namespaces.computeIfAbsent(namespace, RecipeNamespace::new);
    }

    public void register(ResourceLocation id, RecipeSchema schema) {
        this.namespace(id.m_135827_()).register(id.m_135815_(), schema);
    }

    public void mapRecipe(String name, ResourceLocation type) {
        this.mappedRecipes.put(name, type);
    }

    public void mapRecipe(String name, String type) {
        this.mapRecipe(name, new ResourceLocation(type));
    }
}

