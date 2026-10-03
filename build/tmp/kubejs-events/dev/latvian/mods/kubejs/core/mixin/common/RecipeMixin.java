/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Recipe.class})
public interface RecipeMixin
extends RecipeKJS {
    @Shadow
    @HideFromJS
    public String m_6076_();

    @Shadow
    @HideFromJS
    public RecipeType<?> m_6671_();
}

