/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={CraftingContainer.class})
public interface CraftingContainerMixin
extends CraftingContainerKJS {
    @Override
    @Nullable
    default public AbstractContainerMenu kjs$getMenu() {
        AbstractContainerMenu abstractContainerMenu;
        CraftingContainerMixin craftingContainerMixin = this;
        if (craftingContainerMixin instanceof TransientCraftingContainer) {
            TransientCraftingContainer container = (TransientCraftingContainer)craftingContainerMixin;
            abstractContainerMenu = container.f_286998_;
        } else {
            abstractContainerMenu = null;
        }
        return abstractContainerMenu;
    }
}

