/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.registries;

import com.google.common.collect.Iterators;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.ForgeRegistryTag;
import net.minecraftforge.registries.NamespacedWrapper;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

class ForgeRegistryTagManager<V>
implements ITagManager<V> {
    private final ForgeRegistry<V> owner;
    private volatile Map<TagKey<V>, ITag<V>> tags = new IdentityHashMap<TagKey<V>, ITag<V>>();

    ForgeRegistryTagManager(ForgeRegistry<V> owner) {
        this.owner = owner;
    }

    void bind(Map<TagKey<V>, HolderSet.Named<V>> holderTags, Set<TagKey<V>> defaultedTags) {
        IdentityHashMap newTags = new IdentityHashMap(this.tags);
        newTags.values().forEach(tag -> ((ForgeRegistryTag)tag).bind(null));
        holderTags.forEach((? super K key, ? super V holderSet) -> ((ForgeRegistryTag)newTags.computeIfAbsent((TagKey)key, ForgeRegistryTag::new)).bind(holderSet));
        this.tags = newTags;
    }

    @Override
    @NotNull
    public ITag<V> getTag(@NotNull TagKey<V> name) {
        Objects.requireNonNull(name);
        ITag<V> tag = this.tags.get(name);
        if (tag == null) {
            tag = new ForgeRegistryTag<V>(name);
            IdentityHashMap<TagKey<ITag<ITag<V>>>, ITag<ITag<ITag<V>>>> map = new IdentityHashMap<TagKey<ITag<ITag<V>>>, ITag<ITag<ITag<V>>>>(this.tags);
            map.put(name, tag);
            this.tags = map;
        }
        return tag;
    }

    @Override
    @NotNull
    public Optional<IReverseTag<V>> getReverseTag(@NotNull V value) {
        Objects.requireNonNull(value);
        return this.owner.getHolder(value);
    }

    @Override
    public boolean isKnownTagName(@NotNull TagKey<V> name) {
        Objects.requireNonNull(name);
        ITag<V> tag = this.tags.get(name);
        return tag != null && tag.isBound();
    }

    @Override
    @NotNull
    public Iterator<ITag<V>> iterator() {
        return Iterators.unmodifiableIterator(this.tags.values().iterator());
    }

    @Override
    @NotNull
    public Stream<ITag<V>> stream() {
        return this.tags.values().stream();
    }

    @Override
    @NotNull
    public Stream<TagKey<V>> getTagNames() {
        return this.tags.keySet().stream();
    }

    @Override
    @NotNull
    public TagKey<V> createTagKey(@NotNull ResourceLocation location) {
        Objects.requireNonNull(location);
        return TagKey.m_203882_(this.owner.getRegistryKey(), (ResourceLocation)location);
    }

    @Override
    @NotNull
    public TagKey<V> createOptionalTagKey(@NotNull ResourceLocation location, @NotNull Set<? extends Supplier<V>> defaults) {
        TagKey<V> tagKey = this.createTagKey(location);
        this.addOptionalTagDefaults(tagKey, defaults);
        return tagKey;
    }

    @Override
    public void addOptionalTagDefaults(@NotNull TagKey<V> name, @NotNull Set<? extends Supplier<V>> defaults) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(defaults);
        NamespacedWrapper<V> wrapper = this.owner.getWrapper();
        if (wrapper != null) {
            wrapper.addOptionalTag(name, defaults);
        }
    }
}

