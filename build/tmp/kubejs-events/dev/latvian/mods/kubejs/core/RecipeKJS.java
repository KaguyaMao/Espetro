/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@RemapPrefixForJS(value="kjs$")
public interface RecipeKJS {
    default public String kjs$getGroup() {
        return ((Recipe)this).m_6076_();
    }

    default public void kjs$setGroup(String group) {
    }

    default public ResourceLocation kjs$getOrCreateId() {
        return ((Recipe)this).m_6423_();
    }

    default public RecipeSchema kjs$getSchema() {
        ResourceLocation s = RegistryInfo.RECIPE_SERIALIZER.getId(((Recipe)this).m_7707_());
        return ((RecipeSchemaType)RecipeNamespace.getAll().get((Object)s.m_135827_()).get((Object)s.m_135815_())).schema;
    }

    default public String kjs$getMod() {
        return this.kjs$getOrCreateId().m_135827_();
    }

    default public ResourceLocation kjs$getType() {
        return RegistryInfo.RECIPE_SERIALIZER.getId(((Recipe)this).m_7707_());
    }

    default public boolean hasInput(ReplacementMatch match) {
        if (match instanceof ItemMatch) {
            ItemMatch m = (ItemMatch)match;
            for (Ingredient in : ((Recipe)this).m_7527_()) {
                if (!m.contains(in)) continue;
                return true;
            }
        }
        return false;
    }

    default public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
        return false;
    }

    default public boolean hasOutput(ReplacementMatch match) {
        if (match instanceof ItemMatch) {
            ItemMatch m = (ItemMatch)match;
            ItemStack result = ((Recipe)this).m_8043_(UtilsJS.staticRegistryAccess);
            return result != null && result != ItemStack.f_41583_ && !result.m_41619_() && m.contains(result);
        }
        return false;
    }

    default public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
        return false;
    }
}

