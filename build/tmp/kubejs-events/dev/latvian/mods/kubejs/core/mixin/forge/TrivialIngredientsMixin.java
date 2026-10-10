/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.crafting.AbstractIngredient
 *  net.minecraftforge.common.crafting.PartialNBTIngredient
 *  net.minecraftforge.common.crafting.StrictNBTIngredient
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.forge;

import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={PartialNBTIngredient.class, StrictNBTIngredient.class})
public abstract class TrivialIngredientsMixin
extends AbstractIngredient {
    public boolean kjs$canBeUsedForMatching() {
        return true;
    }
}

