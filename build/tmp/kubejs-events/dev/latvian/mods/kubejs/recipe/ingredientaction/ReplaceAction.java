/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ReplaceAction
extends IngredientAction {
    public final ItemStack item;

    public ReplaceAction(ItemStack a) {
        this.item = a;
    }

    @Override
    public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
        return this.item.m_41777_();
    }

    @Override
    public String getType() {
        return "replace";
    }

    @Override
    public void toJson(JsonObject json) {
        json.add("item", (JsonElement)this.item.toJsonJS());
    }
}

