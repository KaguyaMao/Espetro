/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.Util
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Holder$Reference$Type
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderOwner
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.MappedRegistry
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 *  org.apache.commons.lang3.Validate
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraftforge.registries;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.ILockableRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

class NamespacedWrapper<T>
extends MappedRegistry<T>
implements ILockableRegistry {
    static final Logger LOGGER = LogUtils.getLogger();
    private final ForgeRegistry<T> delegate;
    @Nullable
    private final Function<T, Holder.Reference<T>> intrusiveHolderCallback;
    private final Multimap<TagKey<T>, Supplier<T>> optionalTags = Multimaps.newSetMultimap(new IdentityHashMap(), HashSet::new);
    boolean locked = false;
    Lifecycle registryLifecycle = Lifecycle.stable();
    private boolean frozen = false;
    private List<Holder.Reference<T>> holdersSorted;
    private ObjectList<Holder.Reference<T>> holdersById = new ObjectArrayList(256);
    private Map<ResourceLocation, Holder.Reference<T>> holdersByName = new HashMap<ResourceLocation, Holder.Reference<T>>();
    private Map<T, Holder.Reference<T>> holders = new IdentityHashMap<T, Holder.Reference<T>>();
    private RegistryManager stage;
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<TagKey<T>, HolderSet.Named<T>>();

    NamespacedWrapper(ForgeRegistry<T> fowner, Function<T, Holder.Reference<T>> intrusiveHolderCallback, RegistryManager stage) {
        super(fowner.getRegistryKey(), Lifecycle.stable(), intrusiveHolderCallback != null);
        this.delegate = fowner;
        this.intrusiveHolderCallback = intrusiveHolderCallback;
        this.stage = stage;
    }

    public Holder.Reference<T> m_203704_(int id, ResourceKey<T> key, T value, Lifecycle lifecycle) {
        if (this.locked) {
            throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");
        }
        Validate.notNull(value);
        this.markKnown();
        this.registryLifecycle = this.registryLifecycle.add(lifecycle);
        int realId = this.delegate.add(id, key.m_135782_(), value);
        if (realId != id && id != -1) {
            LOGGER.warn("Registered object did not get ID it asked for. Name: {} Expected: {} Got: {}", new Object[]{key, id, realId});
        }
        return this.getHolder(key, value);
    }

    public Holder.Reference<T> m_255290_(ResourceKey<T> key, T value, Lifecycle lifecycle) {
        return this.m_203704_(-1, key, value, lifecycle);
    }

    @Nullable
    public T m_7745_(@Nullable ResourceLocation name) {
        return this.delegate.getRaw(name);
    }

    public Optional<T> m_6612_(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.delegate.getRaw(name));
    }

    @Nullable
    public T m_6246_(@Nullable ResourceKey<T> name) {
        return name == null ? null : (T)this.delegate.getRaw(name.m_135782_());
    }

    @Nullable
    public ResourceLocation m_7981_(T value) {
        return this.delegate.getKey(value);
    }

    public Optional<ResourceKey<T>> m_7854_(T p_122755_) {
        return this.delegate.getResourceKey(p_122755_);
    }

    public boolean m_7804_(ResourceLocation key) {
        return this.delegate.containsKey(key);
    }

    public boolean m_142003_(ResourceKey<T> key) {
        return this.delegate.getRegistryName().equals((Object)key.m_211136_()) && this.m_7804_(key.m_135782_());
    }

    public int m_7447_(@Nullable T value) {
        return this.delegate.getID(value);
    }

    @Nullable
    public T m_7942_(int id) {
        return this.delegate.getValue(id);
    }

    public Lifecycle m_6228_(T value) {
        return Lifecycle.stable();
    }

    public Lifecycle m_203658_() {
        return this.registryLifecycle;
    }

    public Iterator<T> iterator() {
        return this.delegate.iterator();
    }

    public Set<ResourceLocation> m_6566_() {
        return this.delegate.getKeys();
    }

    public Set<ResourceKey<T>> m_214010_() {
        return this.delegate.getResourceKeys();
    }

    public Set<Map.Entry<ResourceKey<T>, T>> m_6579_() {
        return this.delegate.getEntries();
    }

    public boolean m_142427_() {
        return this.delegate.isEmpty();
    }

    public int m_13562_() {
        return this.delegate.size();
    }

    @Override
    @Deprecated
    public void lock() {
        this.locked = true;
    }

    public Optional<Holder.Reference<T>> m_203300_(int id) {
        return id >= 0 && id < this.holdersById.size() ? Optional.ofNullable((Holder.Reference)this.holdersById.get(id)) : Optional.empty();
    }

    public Optional<Holder.Reference<T>> m_203636_(ResourceKey<T> key) {
        return Optional.ofNullable(this.holdersByName.get(key.m_135782_()));
    }

    @NotNull
    public Holder<T> m_263177_(@NotNull T value) {
        Holder holder = (Holder)this.holders.get(value);
        return holder == null ? Holder.m_205709_(value) : holder;
    }

    Optional<Holder<T>> getHolder(ResourceLocation location) {
        return Optional.ofNullable((Holder)this.holdersByName.get(location));
    }

    Optional<Holder<T>> getHolder(T value) {
        return Optional.ofNullable((Holder)this.holders.get(value));
    }

    public HolderGetter<T> m_203505_() {
        this.m_245419_();
        return new HolderGetter<T>(){

            public Optional<Holder.Reference<T>> m_254902_(ResourceKey<T> p_259097_) {
                return Optional.of(this.m_255043_(p_259097_));
            }

            public Holder.Reference<T> m_255043_(ResourceKey<T> p_259750_) {
                return NamespacedWrapper.this.m_245420_(p_259750_);
            }

            public Optional<HolderSet.Named<T>> m_254901_(TagKey<T> p_259486_) {
                return Optional.of(this.m_254956_(p_259486_));
            }

            public HolderSet.Named<T> m_254956_(TagKey<T> p_260298_) {
                return NamespacedWrapper.this.m_203561_(p_260298_);
            }
        };
    }

    void m_245419_() {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen");
        }
    }

    void m_205921_(ResourceKey<T> key) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + String.valueOf(key) + ")");
        }
    }

    Holder.Reference<T> m_245420_(ResourceKey<T> key) {
        return this.holdersByName.computeIfAbsent(key.m_135782_(), k -> {
            if (this.intrusiveHolderCallback != null) {
                throw new IllegalStateException("This registry can't create new holders without value");
            }
            this.m_205921_(key);
            return Holder.Reference.m_254896_((HolderOwner)this.m_255331_(), (ResourceKey)key);
        });
    }

    public Optional<Holder.Reference<T>> m_213642_(RandomSource rand) {
        return Util.m_214676_(this.getSortedHolders(), (RandomSource)rand);
    }

    public Stream<Holder.Reference<T>> m_203611_() {
        return this.getSortedHolders().stream();
    }

    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> m_203612_() {
        return this.tags.entrySet().stream().map(e -> Pair.of((Object)((TagKey)e.getKey()), (Object)((HolderSet.Named)e.getValue())));
    }

    public HolderSet.Named<T> m_203561_(TagKey<T> name) {
        HolderSet.Named<T> named = this.tags.get(name);
        if (named == null) {
            named = this.m_211067_(name);
            IdentityHashMap<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap<TagKey<T>, HolderSet.Named<T>>(this.tags);
            map.put(name, named);
            this.tags = map;
        }
        return named;
    }

    void addOptionalTag(TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults) {
        this.optionalTags.putAll(name, defaults);
    }

    public Stream<TagKey<T>> m_203613_() {
        return this.tags.keySet().stream();
    }

    public Registry<T> m_203521_() {
        this.frozen = true;
        List<ResourceLocation> unregistered = this.holdersByName.entrySet().stream().filter(e -> !((Holder.Reference)e.getValue()).m_203633_()).map(Map.Entry::getKey).sorted().toList();
        if (!unregistered.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + String.valueOf(this.m_123023_()) + ": " + unregistered.stream().map(ResourceLocation::toString).collect(Collectors.joining(", \n\t")));
        }
        if (this.f_244282_ != null && this.f_244282_.values().stream().anyMatch(r -> !r.m_203633_() && r.getType() == Holder.Reference.Type.INTRUSIVE)) {
            throw new IllegalStateException("Some intrusive holders were not registered: " + String.valueOf(this.f_244282_.values()) + " Hint: Did you register all your registry objects? Registry stage: " + this.stage.getName());
        }
        return this;
    }

    public Holder.Reference<T> m_203693_(T value) {
        if (this.intrusiveHolderCallback == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        }
        this.m_245419_();
        return super.m_203693_(value);
    }

    public Optional<HolderSet.Named<T>> m_203431_(TagKey<T> name) {
        return Optional.ofNullable(this.tags.get(name));
    }

    public void m_203652_(Map<TagKey<T>, List<Holder<T>>> newTags) {
        IdentityHashMap<Holder.Reference, List> holderToTag = new IdentityHashMap<Holder.Reference, List>();
        this.holdersByName.values().forEach(v -> holderToTag.put((Holder.Reference)v, new ArrayList()));
        newTags.forEach((name, values) -> values.forEach(holder -> this.addTagToHolder(holderToTag, (TagKey<T>)name, (Holder<T>)holder)));
        HashSet set = new HashSet(Sets.difference(this.tags.keySet(), newTags.keySet()));
        set.removeAll(this.optionalTags.keySet());
        if (!set.isEmpty()) {
            LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", (Object)this.m_123023_(), (Object)set.stream().map(k -> k.f_203868_().toString()).sorted().collect(Collectors.joining(", \n\t")));
        }
        IdentityHashMap<TagKey<T>, HolderSet.Named<T>> tmpTags = new IdentityHashMap<TagKey<T>, HolderSet.Named<T>>(this.tags);
        newTags.forEach((k, v) -> tmpTags.computeIfAbsent((TagKey<T>)k, this::m_211067_).m_205835_(v));
        Sets.SetView defaultedTags = Sets.difference((Set)this.optionalTags.keySet(), newTags.keySet());
        defaultedTags.forEach(name -> {
            List<Holder> defaults = this.optionalTags.get(name).stream().map(valueSupplier -> this.getHolder(valueSupplier.get()).orElse(null)).filter(Objects::nonNull).distinct().toList();
            defaults.forEach(holder -> this.addTagToHolder(holderToTag, (TagKey<T>)name, (Holder<T>)holder));
            tmpTags.computeIfAbsent((TagKey<T>)name, this::m_211067_).m_205835_(defaults);
        });
        holderToTag.forEach(Holder.Reference::m_205769_);
        this.tags = tmpTags;
        this.delegate.onBindTags(this.tags, (Set<TagKey<T>>)defaultedTags);
    }

    private void addTagToHolder(Map<Holder.Reference<T>, List<TagKey<T>>> holderToTag, TagKey<T> name, Holder<T> holder) {
        if (!holder.m_203401_(this.m_255331_())) {
            throw new IllegalStateException("Can't create named set " + String.valueOf(name) + " containing value " + String.valueOf(holder) + " from outside registry " + String.valueOf(this));
        }
        if (!(holder instanceof Holder.Reference)) {
            throw new IllegalStateException("Found direct holder " + String.valueOf(holder) + " value in tag " + String.valueOf(name));
        }
        holderToTag.get((Holder.Reference)holder).add(name);
    }

    public void m_203635_() {
        this.tags.values().forEach(t -> t.m_205835_(List.of()));
        this.holders.values().forEach(v -> v.m_205769_(Set.of()));
    }

    public void unfreeze() {
        this.frozen = false;
    }

    boolean isFrozen() {
        return this.frozen;
    }

    boolean isIntrusive() {
        return this.intrusiveHolderCallback != null;
    }

    @Nullable
    Holder.Reference<T> onAdded(RegistryManager stage, int id, ResourceKey<T> key, T newValue, T oldValue) {
        if (!(stage == RegistryManager.ACTIVE || this.intrusiveHolderCallback != null && stage.isStaging())) {
            return null;
        }
        Holder.Reference<T> newHolder = this.getHolder(key, newValue);
        this.holdersById.size(Math.max(this.holdersById.size(), id + 1));
        this.holdersById.set(id, newHolder);
        this.holdersByName.put(key.m_135782_(), newHolder);
        this.holders.put(newValue, newHolder);
        if (this.f_244282_ != null) {
            this.f_244282_.remove(newValue);
            newHolder.m_246870_(key);
        }
        newHolder.m_247654_(newValue);
        this.holdersSorted = null;
        return newHolder;
    }

    private HolderSet.Named<T> m_211067_(TagKey<T> name) {
        return HolderSet.m_255229_((HolderOwner)this.m_255331_(), name);
    }

    private Holder.Reference<T> getHolder(ResourceKey<T> key, T value) {
        if (this.intrusiveHolderCallback != null) {
            return this.intrusiveHolderCallback.apply(value);
        }
        return this.holdersByName.computeIfAbsent(key.m_135782_(), k -> Holder.Reference.m_254896_((HolderOwner)this.m_255331_(), (ResourceKey)key));
    }

    private List<Holder.Reference<T>> getSortedHolders() {
        if (this.holdersSorted == null) {
            this.holdersSorted = this.holdersById.stream().filter(Objects::nonNull).toList();
        }
        return this.holdersSorted;
    }

    public static class Factory<V>
    implements IForgeRegistry.CreateCallback<V>,
    IForgeRegistry.AddCallback<V> {
        public static final ResourceLocation ID = new ResourceLocation("forge", "registry_defaulted_wrapper");

        @Override
        public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage) {
            ForgeRegistry fowner = (ForgeRegistry)owner;
            owner.setSlaveMap(ID, new NamespacedWrapper(fowner, fowner.getBuilder().getIntrusiveHolderCallback(), stage));
        }

        @Override
        public void onAdd(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, ResourceKey<V> key, V value, V oldValue) {
            owner.getSlaveMap(ID, NamespacedWrapper.class).onAdded(stage, id, key, value, oldValue);
        }
    }
}

