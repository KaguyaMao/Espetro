/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public class RecipesUpdatedEvent
extends Event {
    private final RecipeManager recipeManager;

    @ApiStatus.Internal
    public RecipesUpdatedEvent(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }
}

