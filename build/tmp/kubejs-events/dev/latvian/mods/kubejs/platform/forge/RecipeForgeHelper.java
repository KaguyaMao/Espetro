/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.ReloadableServerResources
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraftforge.common.crafting.CraftingHelper
 *  net.minecraftforge.common.crafting.conditions.ICondition$IContext
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.mixin.forge.RecipeManagerAccessor;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class RecipeForgeHelper
implements RecipePlatformHelper {
    public static final String FORGE_CONDITIONAL = "forge:conditional";

    @Override
    @Nullable
    public Recipe<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
        return serializer.fromJson(id, json, (ICondition.IContext)KubeJSReloadListener.recipeContext);
    }

    @Override
    @Nullable
    public JsonObject checkConditions(JsonObject json) {
        JsonArray arr;
        ICondition.IContext context = (ICondition.IContext)KubeJSReloadListener.recipeContext;
        if (!json.has("type")) {
            return null;
        }
        if (json.get("type").getAsString().equals(FORGE_CONDITIONAL)) {
            for (JsonElement ele : GsonHelper.m_13933_((JsonObject)json, (String)"recipes")) {
                if (!ele.isJsonObject()) {
                    return null;
                }
                if (!CraftingHelper.processConditions((JsonArray)GsonHelper.m_13933_((JsonObject)ele.getAsJsonObject(), (String)"conditions"), (ICondition.IContext)context)) continue;
                return GsonHelper.m_13930_((JsonObject)ele.getAsJsonObject(), (String)"recipe");
            }
            return null;
        }
        JsonElement jsonElement = json.get("conditions");
        if (jsonElement instanceof JsonArray && !CraftingHelper.processConditions((JsonArray)(arr = (JsonArray)jsonElement), (ICondition.IContext)context)) {
            return null;
        }
        return json;
    }

    @Override
    public Ingredient getCustomIngredient(JsonObject object) {
        return CraftingHelper.getIngredient((JsonElement)object, (boolean)false);
    }

    @Override
    public boolean processConditions(RecipeManager recipeManager, JsonObject json) {
        return !json.has("conditions") || CraftingHelper.processConditions((JsonObject)json, (String)"conditions", (ICondition.IContext)((ICondition.IContext)KubeJSReloadListener.recipeContext));
    }

    @Override
    public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
    }

    @Override
    public Object createRecipeContext(ReloadableServerResources resources) {
        return ((RecipeManagerAccessor)resources.m_206887_()).getContext();
    }
}

