/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeSchemaType {
    public final RecipeNamespace namespace;
    public final ResourceLocation id;
    public final RecipeSchema schema;
    public RecipeSchemaType parent;
    protected Optional<RecipeSerializer<?>> serializer;

    public RecipeSchemaType(RecipeNamespace namespace, ResourceLocation id, RecipeSchema schema) {
        this.namespace = namespace;
        this.id = id;
        this.schema = schema;
    }

    public RecipeSerializer<?> getSerializer() {
        RecipeSerializer s;
        if (this.serializer == null) {
            this.serializer = Optional.ofNullable(RegistryInfo.RECIPE_SERIALIZER.getValue(this.id));
        }
        if ((s = (RecipeSerializer)this.serializer.orElse(null)) == null) {
            throw new RecipeExceptionJS("Serializer for type " + String.valueOf(this.id) + " is not found!");
        }
        return s;
    }

    public String toString() {
        return this.id.toString();
    }
}

