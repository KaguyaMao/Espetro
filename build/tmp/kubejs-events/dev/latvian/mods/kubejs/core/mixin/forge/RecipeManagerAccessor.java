/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraftforge.common.crafting.conditions.ICondition$IContext
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.latvian.mods.kubejs.core.mixin.forge;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RecipeManager.class})
public interface RecipeManagerAccessor {
    @Accessor(value="context", remap=false)
    public ICondition.IContext getContext();
}

