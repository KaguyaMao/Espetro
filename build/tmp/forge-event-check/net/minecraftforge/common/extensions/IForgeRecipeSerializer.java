/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package net.minecraftforge.common.extensions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;

public interface IForgeRecipeSerializer<T extends Recipe<?>> {
    private RecipeSerializer<T> self() {
        return (RecipeSerializer)this;
    }

    default public T fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context) {
        return (T)this.self().m_6729_(recipeLoc, recipeJson);
    }
}

