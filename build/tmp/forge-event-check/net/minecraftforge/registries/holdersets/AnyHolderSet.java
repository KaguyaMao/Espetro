/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  net.minecraft.Util
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$RegistryLookup
 *  net.minecraft.core.HolderOwner
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 */
package net.minecraftforge.registries.holdersets;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.ICustomHolderSet;

public record AnyHolderSet<T>(HolderLookup.RegistryLookup<T> registryLookup) implements ICustomHolderSet<T>
{
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
        return RegistryOps.retrieveRegistryLookup(registryKey).xmap(AnyHolderSet::new, AnyHolderSet::registryLookup).codec();
    }

    @Override
    public HolderSetType type() {
        return ForgeMod.ANY_HOLDER_SET.get();
    }

    public Iterator<Holder<T>> iterator() {
        return this.m_203614_().iterator();
    }

    public Stream<Holder<T>> m_203614_() {
        return this.registryLookup.m_214062_().map(Function.identity());
    }

    public int m_203632_() {
        return (int)this.m_203614_().count();
    }

    public Either<TagKey<T>, List<Holder<T>>> m_203440_() {
        return Either.right(this.m_203614_().toList());
    }

    public Optional<Holder<T>> m_213653_(RandomSource random) {
        return Util.m_214676_(this.m_203614_().toList(), (RandomSource)random);
    }

    public Holder<T> m_203662_(int i) {
        Holder<T> holder;
        List<Holder<T>> holders = this.m_203614_().toList();
        Holder<T> holder2 = holder = i >= holders.size() ? null : holders.get(i);
        if (holder == null) {
            throw new NoSuchElementException("No element " + i + " in registry " + String.valueOf(this.registryLookup.m_254879_()));
        }
        return holder;
    }

    public boolean m_203333_(Holder<T> holder) {
        return holder.m_203543_().map(key -> this.registryLookup.m_255209_().anyMatch(arg_0 -> ((ResourceKey)key).equals(arg_0))).orElse(false);
    }

    public boolean m_207277_(HolderOwner<T> holderOwner) {
        return this.registryLookup.m_254921_(holderOwner);
    }

    public Optional<TagKey<T>> m_245234_() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "AnySet(" + String.valueOf(this.registryLookup.m_254879_()) + ")";
    }
}

