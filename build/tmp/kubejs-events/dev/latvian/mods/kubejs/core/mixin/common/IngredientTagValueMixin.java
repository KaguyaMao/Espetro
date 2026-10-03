/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import java.util.Collection;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"net/minecraft/world/item/crafting/Ingredient$TagValue"})
public abstract class IngredientTagValueMixin {
    @Shadow
    @Final
    private TagKey<Item> f_43959_;

    @Inject(method={"getItems"}, at={@At(value="HEAD")}, cancellable=true)
    private void kjs$getItems(CallbackInfoReturnable<Collection<ItemStack>> info) {
        if (RecipesEventJS.instance != null) {
            info.setReturnValue(((TagContext)TagContext.INSTANCE.getValue()).patchIngredientTags(this.f_43959_));
        }
    }
}

