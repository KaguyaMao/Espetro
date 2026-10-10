/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 */
package net.minecraftforge.registries.holdersets;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.holdersets.ICustomHolderSet;

@FunctionalInterface
public interface HolderSetType {
    public <T> Codec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> var1, Codec<Holder<T>> var2, boolean var3);
}

