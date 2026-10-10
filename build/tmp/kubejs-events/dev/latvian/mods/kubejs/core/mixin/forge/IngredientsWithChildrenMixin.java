/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraftforge.common.crafting.AbstractIngredient
 *  net.minecraftforge.common.crafting.CompoundIngredient
 *  net.minecraftforge.common.crafting.IntersectionIngredient
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.forge;

import java.util.List;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={CompoundIngredient.class, IntersectionIngredient.class})
public abstract class IngredientsWithChildrenMixin
extends AbstractIngredient {
    @Shadow(remap=false)
    private List<Ingredient> children;

    public boolean kjs$canBeUsedForMatching() {
        for (Ingredient child : this.children) {
            if (child.kjs$canBeUsedForMatching()) continue;
            return false;
        }
        return true;
    }
}

