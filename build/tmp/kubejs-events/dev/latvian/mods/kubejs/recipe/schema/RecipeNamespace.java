/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaRegistryEventJS;
import dev.latvian.mods.kubejs.recipe.schema.JsonRecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapedRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.ShapelessRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.minecraft.SpecialRecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeNamespace
extends LinkedHashMap<String, RecipeSchemaType> {
    private static Map<String, ResourceLocation> mappedRecipes;
    private static Map<String, RecipeNamespace> all;
    public final String name;

    public static Map<String, RecipeNamespace> getAll() {
        if (all == null) {
            all = new HashMap<String, RecipeNamespace>();
            mappedRecipes = new HashMap<String, ResourceLocation>();
            for (Map.Entry<ResourceKey<RecipeSerializer>, RecipeSerializer> entry : RegistryInfo.RECIPE_SERIALIZER.entrySet()) {
                RecipeNamespace ns = all.computeIfAbsent(entry.getKey().m_135782_().m_135827_(), RecipeNamespace::new);
                ns.put(entry.getKey().m_135782_().m_135815_(), new JsonRecipeSchemaType(ns, entry.getKey().m_135782_(), entry.getValue()));
            }
            KubeJSPlugins.forEachPlugin(new RegisterRecipeSchemasEvent(all, mappedRecipes), KubeJSPlugin::registerRecipeSchemas);
            StartupEvents.RECIPE_SCHEMA_REGISTRY.post(ScriptType.STARTUP, new RecipeSchemaRegistryEventJS(all, mappedRecipes));
        }
        return all;
    }

    public static Map<String, ResourceLocation> getMappedRecipes() {
        RecipeNamespace.getAll();
        return mappedRecipes;
    }

    public RecipeNamespace(String name) {
        this.name = name;
    }

    public RecipeNamespace register(String id, RecipeSchema type) {
        this.put(id, new RecipeSchemaType(this, new ResourceLocation(this.name, id), type));
        return this;
    }

    public RecipeNamespace registerBasic(String id, RecipeKey<?> ... keys) {
        return this.register(id, new RecipeSchema(keys));
    }

    public RecipeNamespace shaped(String id) {
        return this.register(id, ShapedRecipeSchema.SCHEMA);
    }

    public RecipeNamespace shapeless(String id) {
        return this.register(id, ShapelessRecipeSchema.SCHEMA);
    }

    public RecipeNamespace special(String id) {
        return this.register(id, SpecialRecipeSchema.SCHEMA);
    }

    @Override
    public String toString() {
        return this.name;
    }
}

