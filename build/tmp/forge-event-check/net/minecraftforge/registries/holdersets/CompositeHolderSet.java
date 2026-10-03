/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderOwner
 *  net.minecraft.core.HolderSet
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries.holdersets;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.extensions.IForgeHolderSet;
import net.minecraftforge.registries.holdersets.ICustomHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import org.jetbrains.annotations.Nullable;

public abstract class CompositeHolderSet<T>
implements ICustomHolderSet<T> {
    private final List<Runnable> owners = new ArrayList<Runnable>();
    private final List<HolderSet<T>> components;
    @Nullable
    private Set<Holder<T>> set = null;
    @Nullable
    private List<Holder<T>> list = null;

    public CompositeHolderSet(List<HolderSet<T>> components) {
        this.components = components;
        for (HolderSet<T> holderset : components) {
            holderset.addInvalidationListener(this::invalidate);
        }
    }

    protected abstract Set<Holder<T>> createSet();

    public List<HolderSet<T>> getComponents() {
        return this.components;
    }

    public Set<Holder<T>> getSet() {
        Set<Holder<T>> thisSet = this.set;
        if (thisSet == null) {
            Set<Holder<T>> set = this.createSet();
            this.set = set;
            return set;
        }
        return thisSet;
    }

    public List<Holder<T>> getList() {
        List<Holder<T>> thisList = this.list;
        if (thisList == null) {
            List<Holder<T>> list = List.copyOf(this.getSet());
            this.list = list;
            return list;
        }
        return thisList;
    }

    public void addInvalidationListener(Runnable runnable) {
        this.owners.add(runnable);
    }

    private void invalidate() {
        this.set = null;
        this.list = null;
        for (Runnable runnable : this.owners) {
            runnable.run();
        }
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

    public Optional<Holder<T>> m_213653_(RandomSource rand) {
        List<Holder<T>> list = this.getList();
        int size = list.size();
        return size > 0 ? Optional.of(list.get(rand.m_188503_(size))) : Optional.empty();
    }

    public Holder<T> m_203662_(int i) {
        return this.getList().get(i);
    }

    public boolean m_203333_(Holder<T> holder) {
        return this.getSet().contains(holder);
    }

    public boolean m_207277_(HolderOwner<T> holderOwner) {
        for (HolderSet<T> component : this.components) {
            if (component.m_207277_(holderOwner)) continue;
            return false;
        }
        return true;
    }

    public Optional<TagKey<T>> m_245234_() {
        return Optional.empty();
    }

    public Iterator<Holder<T>> iterator() {
        return this.getList().iterator();
    }

    public List<HolderSet<T>> homogenize() {
        List<HolderSet<T>> components = this.getComponents();
        if (this.isHomogenous()) {
            return components;
        }
        ArrayList<HolderSet<T>> outputs = new ArrayList<HolderSet<T>>();
        for (HolderSet<T> holderset : components) {
            if (holderset instanceof ICustomHolderSet) {
                outputs.add(holderset);
                continue;
            }
            outputs.add(new OrHolderSet<T>(List.of(holderset)));
        }
        return outputs;
    }

    public boolean isHomogenous() {
        List<HolderSet<T>> holderSets = this.getComponents();
        if (holderSets.size() < 2) {
            return true;
        }
        IForgeHolderSet.SerializationType firstType = holderSets.get(0).serializationType();
        if (firstType == IForgeHolderSet.SerializationType.UNKNOWN) {
            return false;
        }
        int size = holderSets.size();
        for (int i = 1; i < size; ++i) {
            IForgeHolderSet.SerializationType type = holderSets.get(i).serializationType();
            if (type == firstType) continue;
            return false;
        }
        return true;
    }
}

