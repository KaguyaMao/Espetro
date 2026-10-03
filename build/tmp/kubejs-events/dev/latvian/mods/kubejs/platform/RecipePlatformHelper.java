/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.ReloadableServerResources
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.Lazy;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public interface RecipePlatformHelper {
    public static final Lazy<RecipePlatformHelper> INSTANCE = Lazy.serviceLoader(RecipePlatformHelper.class);

    public static RecipePlatformHelper get() {
        return INSTANCE.get();
    }

    @Nullable
    public Recipe<?> fromJson(RecipeSerializer<?> var1, ResourceLocation var2, JsonObject var3);

    @Nullable
    public JsonObject checkConditions(JsonObject var1);

    public Ingredient getCustomIngredient(JsonObject var1);

    public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> var1);

    public boolean processConditions(RecipeManager var1, JsonObject var2);

    public Object createRecipeContext(ReloadableServerResources var1);
}

