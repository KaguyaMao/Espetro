/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.ResultContainer
 */
package net.minecraftforge.common.crafting;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;

public interface IRecipeContainer {
    public ResultContainer getCraftResult();

    public CraftingContainer getCraftMatrix();
}

