/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={ItemStack.class})
public abstract class ItemStackMixin
implements ItemStackKJS {
    @Shadow
    @RemapForJS(value="enchantStack")
    public abstract void m_41663_(Enchantment var1, int var2);

    @Shadow
    @RemapForJS(value="getNbt")
    public abstract CompoundTag m_41783_();

    @Shadow
    @RemapForJS(value="setNbt")
    public abstract void m_41751_(CompoundTag var1);

    @Shadow
    @RemapForJS(value="hasNBT")
    public abstract boolean m_41782_();
}

