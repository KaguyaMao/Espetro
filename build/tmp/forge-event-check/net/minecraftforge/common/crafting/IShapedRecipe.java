/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.item.crafting.Recipe
 */
package net.minecraftforge.common.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public interface IShapedRecipe<T extends Container>
extends Recipe<T> {
    public int getRecipeWidth();

    public int getRecipeHeight();
}

