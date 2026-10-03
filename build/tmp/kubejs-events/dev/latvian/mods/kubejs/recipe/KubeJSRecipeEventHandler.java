/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.registries.DeferredRegister
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package dev.latvian.mods.kubejs.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.mods.kubejs.recipe.special.ShapelessKubeJSRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class KubeJSRecipeEventHandler {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create((String)"kubejs", (ResourceKey)Registries.f_256764_);
    public static Supplier<RecipeSerializer<?>> SHAPED;
    public static Supplier<RecipeSerializer<?>> SHAPELESS;

    public static void init() {
        if (!CommonProperties.get().serverOnly) {
            KubeJSRecipeEventHandler.registry();
        }
    }

    private static void registry() {
        SHAPED = REGISTER.register("shaped", ShapedKubeJSRecipe.SerializerKJS::new);
        SHAPELESS = REGISTER.register("shapeless", ShapelessKubeJSRecipe.SerializerKJS::new);
        REGISTER.register();
    }
}

