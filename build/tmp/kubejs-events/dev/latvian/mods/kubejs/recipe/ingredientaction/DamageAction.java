/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientAction;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class DamageAction
extends IngredientAction {
    public final int amount;

    public DamageAction(int a) {
        this.amount = a;
    }

    @Override
    public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
        old.m_41721_(old.m_41773_() + this.amount);
        return old.m_41773_() >= old.m_41776_() ? ItemStack.f_41583_ : old;
    }

    @Override
    public String getType() {
        return "damage";
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty("damage", (Number)this.amount);
    }
}

