/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.item.ItemPropertiesRegistry
 *  net.minecraft.client.renderer.item.ClampedItemPropertyFunction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 */
package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ItemModelPropertiesEventJS
extends StartupEventJS {
    @Info(value="Register a model property for an item. Model properties are used to change the appearance of an item in the world.\n\nMore about model properties: https://minecraft.fandom.com/wiki/Model#Item_predicates\n")
    public void register(Ingredient ingredient, String overwriteId, ClampedItemPropertyFunction callback) {
        if (ingredient.kjs$isWildcard()) {
            this.registerAll(overwriteId, callback);
        } else {
            for (ItemStack stack : ingredient.kjs$getStacks()) {
                ItemPropertiesRegistry.register((ItemLike)stack.m_41720_(), (ResourceLocation)new ResourceLocation(KubeJS.appendModId(overwriteId)), (ClampedItemPropertyFunction)callback);
            }
        }
    }

    @Info(value="Register a model property for all items.")
    public void registerAll(String overwriteId, ClampedItemPropertyFunction callback) {
        ItemPropertiesRegistry.registerGeneric((ResourceLocation)new ResourceLocation(KubeJS.appendModId(overwriteId)), (ClampedItemPropertyFunction)callback);
    }
}

