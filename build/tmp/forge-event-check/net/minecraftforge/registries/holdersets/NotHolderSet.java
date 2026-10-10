/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$RegistryLookup
 *  net.minecraft.core.HolderOwner
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.HolderSetCodec
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries.holdersets;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.ICustomHolderSet;
import org.jetbrains.annotations.Nullable;

public class NotHolderSet<T>
implements ICustomHolderSet<T> {
    private final List<Runnable> owners = new ArrayList<Runnable>();
    private final HolderLookup.RegistryLookup<T> registryLookup;
    private final HolderSet<T> value;
    @Nullable
    private List<Holder<T>> list = null;

    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
        return RecordCodecBuilder.create(builder -> builder.group((App)RegistryOps.retrieveRegistryLookup((ResourceKey)registryKey).forGetter(NotHolderSet::registryLookup), (App)HolderSetCodec.m_206685_((ResourceKey)registryKey, (Codec)holderCodec, (boolean)forceList).fieldOf("value").forGetter(NotHolderSet::value)).apply((Applicative)builder, NotHolderSet::new));
    }

    public HolderLookup.RegistryLookup<T> registryLookup() {
        return this.registryLookup;
    }

    public HolderSet<T> value() {
        return this.value;
    }

    public NotHolderSet(HolderLookup.RegistryLookup<T> registryLookup, HolderSet<T> value) {
        this.registryLookup = registryLookup;
        this.value = value;
        this.value.addInvalidationListener(this::invalidate);
    }

    @Override
    public HolderSetType type() {
        return ForgeMod.NOT_HOLDER_SET.get();
    }

    public void addInvalidationListener(Runnable runnable) {
        this.owners.add(runnable);
    }

    public Iterator<Holder<T>> iterator() {
        return this.getList().iterator();
    }

    public Stream<Holder<T>> m_203614_() {
        return this.getList().stream();
    }

    public int m_203632_() {
        return this.getList().size();
    }

    public Either<TagKey<T>, List<Holder<T>>> m_203440_() {
        return Either.right(this.getList());
    }

    public Optional<Holder<T>> m_213653_(RandomSource random) {
        List<Holder<T>> list = this.getList();
        int size = list.size();
        return size > 0 ? Optional.of(list.get(random.m_188503_(size))) : Optional.empty();
    }

    public Holder<T> m_203662_(int i) {
        return this.getList().get(i);
    }

    public boolean m_203333_(Holder<T> holder) {
        return !this.value.m_203333_(holder);
    }

    public boolean m_207277_(HolderOwner<T> holderOwner) {
        return this.registryLookup.m_254921_(holderOwner);
    }

    public Optional<TagKey<T>> m_245234_() {
        return Optional.empty();
    }

    public String toString() {
        return "NotSet(" + String.valueOf(this.value) + ")";
    }

    private List<Holder<T>> getList() {
        List<Holder<T>> thisList = this.list;
        if (thisList == null) {
            List<Holder<T>> list = this.registryLookup.m_214062_().filter(holder -> !this.value.m_203333_((Holder)holder)).map(holder -> holder).toList();
            this.list = list;
            return list;
        }
        return thisList;
    }

    private void invalidate() {
        this.list = null;
        for (Runnable runnable : this.owners) {
            runnable.run();
        }
    }
}

