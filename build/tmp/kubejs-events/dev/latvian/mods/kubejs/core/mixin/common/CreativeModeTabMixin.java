/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.CreativeModeTabKJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={CreativeModeTab.class})
public abstract class CreativeModeTabMixin
implements CreativeModeTabKJS {
    @Shadow
    @Final
    @Mutable
    private Component f_40764_;
    @Shadow
    private ItemStack f_40770_;

    @Override
    public void kjs$setDisplayName(Component component) {
        this.f_40764_ = component;
    }

    @Override
    public void kjs$setIcon(ItemStack icon) {
        this.f_40770_ = icon;
    }
}

