/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.HolderSetCodec
 *  net.minecraft.resources.ResourceKey
 */
package net.minecraftforge.registries.holdersets;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.holdersets.CompositeHolderSet;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.ICustomHolderSet;

public class AndHolderSet<T>
extends CompositeHolderSet<T> {
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
        return HolderSetCodec.m_206685_(registryKey, holderCodec, (boolean)forceList).listOf().xmap(AndHolderSet::new, CompositeHolderSet::homogenize).fieldOf("values").codec();
    }

    public AndHolderSet(List<HolderSet<T>> values) {
        super(values);
    }

    @Override
    public HolderSetType type() {
        return ForgeMod.AND_HOLDER_SET.get();
    }

    @Override
    protected Set<Holder<T>> createSet() {
        List components = this.getComponents();
        if (components.size() < 1) {
            return Set.of();
        }
        if (components.size() == 1) {
            return components.get(0).m_203614_().collect(Collectors.toSet());
        }
        List remainingComponents = components.subList(1, components.size());
        return components.get(0).m_203614_().filter(holder -> remainingComponents.stream().allMatch(holderset -> holderset.m_203333_(holder))).collect(Collectors.toSet());
    }

    public String toString() {
        return "AndSet[" + String.valueOf(this.getComponents()) + "]";
    }
}

