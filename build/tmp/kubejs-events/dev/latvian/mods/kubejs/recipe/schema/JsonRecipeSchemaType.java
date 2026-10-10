/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.schema.JsonRecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class JsonRecipeSchemaType
extends RecipeSchemaType {
    public JsonRecipeSchemaType(RecipeNamespace namespace, ResourceLocation id, RecipeSerializer<?> serializer) {
        super(namespace, id, JsonRecipeSchema.SCHEMA);
        this.serializer = Optional.ofNullable(serializer);
    }
}

