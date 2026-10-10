/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.Multimap
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.Unpooled
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderOwner
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.Registry
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraftforge.fml.ModLoadingContext
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.common.util.TablePrinter;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryTagManager;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.NamespacedDefaultedWrapper;
import net.minecraftforge.registries.NamespacedWrapper;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ForgeRegistry<V>
implements IForgeRegistryInternal<V>,
IForgeRegistryModifiable<V> {
    public static final Marker REGISTRIES = MarkerManager.getMarker((String)"REGISTRIES");
    private static final Marker REGISTRYDUMP = MarkerManager.getMarker((String)"REGISTRYDUMP");
    private static final Logger LOGGER = LogManager.getLogger();
    private final RegistryManager stage;
    private final BiMap<Integer, V> ids = HashBiMap.create();
    private final BiMap<ResourceLocation, V> names = HashBiMap.create();
    private final BiMap<ResourceKey<V>, V> keys = HashBiMap.create();
    private final Map<ResourceLocation, ResourceLocation> aliases = new HashMap<ResourceLocation, ResourceLocation>();
    final Map<ResourceLocation, ?> slaves = new HashMap();
    private final ResourceLocation defaultKey;
    private final IForgeRegistry.CreateCallback<V> create;
    private final IForgeRegistry.AddCallback<V> add;
    private final IForgeRegistry.ClearCallback<V> clear;
    private final IForgeRegistry.ValidateCallback<V> validate;
    private final IForgeRegistry.BakeCallback<V> bake;
    private final IForgeRegistry.MissingFactory<V> missing;
    private final BitSet availabilityMap;
    private final IntSet blocked = new IntOpenHashSet();
    private final Multimap<ResourceLocation, V> overrides = ArrayListMultimap.create();
    private final Map<ResourceLocation, Holder.Reference<V>> delegatesByName = new HashMap<ResourceLocation, Holder.Reference<V>>();
    private final Map<V, Holder.Reference<V>> delegatesByValue = new HashMap<V, Holder.Reference<V>>();
    private final BiMap<OverrideOwner<V>, V> owners = HashBiMap.create();
    private final ForgeRegistryTagManager<V> tagManager;
    private final int min;
    private final int max;
    private final boolean allowOverrides;
    private final boolean isModifiable;
    private final boolean hasWrapper;
    private V defaultValue = null;
    boolean isFrozen = false;
    private final ResourceLocation name;
    private final ResourceKey<Registry<V>> key;
    private final RegistryBuilder<V> builder;
    private final Codec<V> codec = new RegistryCodec();

    ForgeRegistry(RegistryManager stage, ResourceLocation name, RegistryBuilder<V> builder) {
        this.name = name;
        this.key = ResourceKey.m_135788_((ResourceLocation)name);
        this.builder = builder;
        this.stage = stage;
        this.defaultKey = builder.getDefault();
        this.min = builder.getMinId();
        this.max = builder.getMaxId();
        this.availabilityMap = new BitSet(Math.min(this.max + 1, 4095));
        this.create = builder.getCreate();
        this.add = builder.getAdd();
        this.clear = builder.getClear();
        this.validate = builder.getValidate();
        this.bake = builder.getBake();
        this.missing = builder.getMissingFactory();
        this.allowOverrides = builder.getAllowOverrides();
        this.isModifiable = builder.getAllowModifications();
        this.hasWrapper = builder.getHasWrapper();
        ForgeRegistryTagManager forgeRegistryTagManager = this.tagManager = this.hasWrapper ? new ForgeRegistryTagManager(this) : null;
        if (this.create != null) {
            this.create.onCreate(this, stage);
        }
    }

    @Override
    public void register(String key, V value) {
        this.register(GameData.checkPrefix(key, true), value);
    }

    @Override
    public void register(ResourceLocation key, V value) {
        this.add(-1, key, value);
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>(){
            int cur = -1;
            V next = null;
            {
                this.next();
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public V next() {
                Object ret = this.next;
                do {
                    this.cur = ForgeRegistry.this.availabilityMap.nextSetBit(this.cur + 1);
                    this.next = ForgeRegistry.this.ids.get((Object)this.cur);
                } while (this.next == null && this.cur != -1);
                return ret;
            }
        };
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public ResourceKey<Registry<V>> getRegistryKey() {
        return this.key;
    }

    @Override
    @NotNull
    public Codec<V> getCodec() {
        return this.codec;
    }

    @Override
    public boolean containsKey(ResourceLocation key) {
        while (key != null) {
            if (this.names.containsKey((Object)key)) {
                return true;
            }
            key = this.aliases.get(key);
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        return this.names.containsValue(value);
    }

    @Override
    public boolean isEmpty() {
        return this.names.isEmpty();
    }

    int size() {
        return this.names.size();
    }

    @Override
    public V getValue(ResourceLocation key) {
        Object ret = this.names.get((Object)key);
        key = this.aliases.get(key);
        while (ret == null && key != null) {
            ret = this.names.get((Object)key);
            key = this.aliases.get(key);
        }
        return (V)(ret == null ? this.defaultValue : ret);
    }

    @Override
    public ResourceLocation getKey(V value) {
        return this.getResourceKey(value).map(ResourceKey::m_135782_).orElse(this.defaultKey);
    }

    @Override
    @NotNull
    public Optional<ResourceKey<V>> getResourceKey(V value) {
        return Optional.ofNullable((OverrideOwner)this.owners.inverse().get(value)).map(OverrideOwner::key);
    }

    @Nullable
    NamespacedWrapper<V> getWrapper() {
        if (!this.hasWrapper) {
            return null;
        }
        return this.defaultKey != null ? (NamespacedWrapper)this.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class) : this.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
    }

    @NotNull
    NamespacedWrapper<V> getWrapperOrThrow() {
        NamespacedWrapper<V> wrapper = this.getWrapper();
        if (wrapper == null) {
            throw new IllegalStateException("Cannot query wrapper for non-wrapped forge registry!");
        }
        return wrapper;
    }

    void onBindTags(Map<TagKey<V>, HolderSet.Named<V>> tags, Set<TagKey<V>> defaultedTags) {
        if (this.tagManager != null) {
            this.tagManager.bind(tags, defaultedTags);
        }
    }

    @Override
    @NotNull
    public Optional<Holder<V>> getHolder(ResourceKey<V> key) {
        return Optional.ofNullable(this.getWrapper()).flatMap(wrapper -> wrapper.m_203636_(key));
    }

    @Override
    @NotNull
    public Optional<Holder<V>> getHolder(ResourceLocation location) {
        return Optional.ofNullable(this.getWrapper()).flatMap(wrapper -> wrapper.getHolder(location));
    }

    @Override
    @NotNull
    public Optional<Holder<V>> getHolder(V value) {
        return Optional.ofNullable(this.getWrapper()).flatMap(wrapper -> wrapper.getHolder(value));
    }

    @Override
    @Nullable
    public ITagManager<V> tags() {
        return this.tagManager;
    }

    @Override
    @NotNull
    public Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet(this.names.keySet());
    }

    @NotNull
    Set<ResourceKey<V>> getResourceKeys() {
        return Collections.unmodifiableSet(this.keys.keySet());
    }

    @Override
    @NotNull
    public Collection<V> getValues() {
        return Collections.unmodifiableSet(this.names.values());
    }

    @Override
    @NotNull
    public Set<Map.Entry<ResourceKey<V>, V>> getEntries() {
        return Collections.unmodifiableSet(this.keys.entrySet());
    }

    @Override
    public <T> T getSlaveMap(ResourceLocation name, Class<T> type) {
        return (T)this.slaves.get(name);
    }

    @Override
    public void setSlaveMap(ResourceLocation name, Object obj) {
        this.slaves.put(name, obj);
    }

    public int getID(V value) {
        Integer ret = (Integer)this.ids.inverse().get(value);
        if (ret == null && this.defaultValue != null) {
            ret = (Integer)this.ids.inverse().get(this.defaultValue);
        }
        return ret == null ? -1 : ret;
    }

    public int getID(ResourceLocation name) {
        return this.getID(this.names.get((Object)name));
    }

    private int getIDRaw(V value) {
        Integer ret = (Integer)this.ids.inverse().get(value);
        return ret == null ? -1 : ret;
    }

    private int getIDRaw(ResourceLocation name) {
        return this.getIDRaw(this.names.get((Object)name));
    }

    @Override
    public V getValue(int id) {
        Object ret = this.ids.get((Object)id);
        return (V)(ret == null ? this.defaultValue : ret);
    }

    @Nullable
    public ResourceKey<V> getKey(int id) {
        V value = this.getValue(id);
        return (ResourceKey)this.keys.inverse().get(value);
    }

    void validateKey() {
        if (this.defaultKey != null) {
            Validate.notNull(this.defaultValue, (String)("Missing default of ForgeRegistry: " + String.valueOf(this.defaultKey) + " Name: " + String.valueOf(this.name)), (Object[])new Object[0]);
        }
    }

    @Override
    @Nullable
    public ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }

    ForgeRegistry<V> copy(RegistryManager stage) {
        return new ForgeRegistry<V>(stage, this.name, this.builder);
    }

    @Override
    public void register(int id, ResourceLocation key, V value) {
        this.add(id, key, value, key.m_135827_());
    }

    int add(int id, ResourceLocation key, V value) {
        String owner = ModLoadingContext.get().getActiveNamespace();
        return this.add(id, key, value, owner);
    }

    int add(int id, ResourceLocation key, V value, String owner) {
        Integer foundId;
        Preconditions.checkNotNull((Object)key, (String)"Can't use a null-name for the registry, object %s.", value);
        Preconditions.checkNotNull(value, (String)"Can't add null-object to the registry, name %s.", (Object)key);
        int idToUse = id;
        if (idToUse < 0 || this.availabilityMap.get(idToUse)) {
            idToUse = this.availabilityMap.nextClearBit(this.min);
        }
        if (idToUse > this.max) {
            throw new RuntimeException(String.format(Locale.ENGLISH, "Invalid id %d - maximum id range exceeded.", idToUse));
        }
        V oldEntry = this.getRaw(key);
        if (oldEntry == value) {
            LOGGER.warn(REGISTRIES, "Registry {}: The object {} has been registered twice for the same name {}.", (Object)this.name, value, (Object)key);
            return this.getID(value);
        }
        if (oldEntry != null) {
            if (!this.allowOverrides) {
                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "The name %s has been registered twice, for %s and %s.", key, this.getRaw(key), value));
            }
            if (owner == null) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Could not determine owner for the override on %s. Value: %s", key, value));
            }
            LOGGER.debug(REGISTRIES, "Registry {} Override: {} {} -> {}", (Object)this.name, (Object)key, oldEntry, value);
            idToUse = this.getID(oldEntry);
        }
        if ((foundId = (Integer)this.ids.inverse().get(value)) != null) {
            Object otherThing = this.ids.get((Object)foundId);
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", value, System.identityHashCode(value), this.getKey(value), key, otherThing, System.identityHashCode(otherThing)));
        }
        if (this.isLocked()) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "The object %s (name %s) is being added too late.", value, key));
        }
        if (this.defaultKey != null && this.defaultKey.equals((Object)key)) {
            if (this.defaultValue != null) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Attemped to override already set default value. This is not allowed: The object %s (name %s)", value, key));
            }
            this.defaultValue = value;
        }
        ResourceKey rkey = ResourceKey.m_135785_(this.key, (ResourceLocation)key);
        this.names.put((Object)key, value);
        this.keys.put((Object)rkey, value);
        this.ids.put((Object)idToUse, value);
        this.availabilityMap.set(idToUse);
        this.owners.put(new OverrideOwner(owner == null ? key.m_135827_() : owner, rkey), value);
        if (this.hasWrapper) {
            this.bindDelegate(rkey, value);
            if (oldEntry != null) {
                if (!this.overrides.get((Object)key).contains(oldEntry)) {
                    this.overrides.put((Object)key, oldEntry);
                }
                this.overrides.get((Object)key).remove(value);
            }
        }
        if (this.add != null) {
            this.add.onAdd(this, this.stage, idToUse, rkey, value, oldEntry);
        }
        LOGGER.trace(REGISTRIES, "Registry {} add: {} {} {} (req. id {})", (Object)this.name, (Object)key, (Object)idToUse, value, (Object)id);
        return idToUse;
    }

    public V getRaw(ResourceLocation key) {
        Object ret = this.names.get((Object)key);
        key = this.aliases.get(key);
        while (ret == null && key != null) {
            ret = this.names.get((Object)key);
            key = this.aliases.get(key);
        }
        return (V)ret;
    }

    public void addAlias(ResourceLocation src, ResourceLocation dst) {
        if (this.isLocked()) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Attempted to register the alias %s -> %s too late", src, dst));
        }
        if (src.equals((Object)dst)) {
            LOGGER.warn(REGISTRIES, "Registry {} Ignoring invalid alias: {} -> {}", (Object)this.name, (Object)src, (Object)dst);
            return;
        }
        this.aliases.put(src, dst);
        LOGGER.trace(REGISTRIES, "Registry {} alias: {} -> {}", (Object)this.name, (Object)src, (Object)dst);
    }

    @Override
    @NotNull
    public Optional<Holder.Reference<V>> getDelegate(ResourceKey<V> rkey) {
        return Optional.ofNullable(this.delegatesByName.get(rkey.m_135782_()));
    }

    @Override
    @NotNull
    public Holder.Reference<V> getDelegateOrThrow(ResourceKey<V> rkey) {
        return this.getDelegate((V)rkey).orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key %s", rkey)));
    }

    @Override
    @NotNull
    public Optional<Holder.Reference<V>> getDelegate(ResourceLocation key) {
        return Optional.ofNullable(this.delegatesByName.get(key));
    }

    @Override
    @NotNull
    public Holder.Reference<V> getDelegateOrThrow(ResourceLocation key) {
        return this.getDelegate((V)key).orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key %s", key)));
    }

    @Override
    @NotNull
    public Optional<Holder.Reference<V>> getDelegate(V value) {
        return Optional.ofNullable(this.delegatesByValue.get(value));
    }

    @Override
    @NotNull
    public Holder.Reference<V> getDelegateOrThrow(V value) {
        return this.getDelegate(value).orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for value %s", value)));
    }

    private Holder.Reference<V> bindDelegate(ResourceKey<V> rkey, V value) {
        Holder.Reference delegate = this.delegatesByName.computeIfAbsent(rkey.m_135782_(), k -> Holder.Reference.m_254896_((HolderOwner)this.getWrapperOrThrow().m_255331_(), (ResourceKey)rkey));
        delegate.m_246870_(rkey);
        delegate.m_247654_(value);
        this.delegatesByValue.put((Holder.Reference)value, (Holder.Reference<Holder.Reference>)delegate);
        return delegate;
    }

    void resetDelegates() {
        if (!this.hasWrapper) {
            return;
        }
        for (Map.Entry entry : this.keys.entrySet()) {
            this.bindDelegate((ResourceKey)entry.getKey(), entry.getValue());
        }
        for (Map.Entry entry : this.overrides.entries()) {
            this.bindDelegate(ResourceKey.m_135785_(this.key, (ResourceLocation)((ResourceLocation)entry.getKey())), entry.getValue());
        }
    }

    V getDefault() {
        return this.defaultValue;
    }

    void validateContent(ResourceLocation registryName) {
        for (V obj : this) {
            int id = this.getID(obj);
            ResourceLocation name = this.getKey(obj);
            if (name == null) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for %s %s, id %d, doesn't yield a name.", registryName, obj, id));
            }
            if (id > this.max) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for %s %s, name %s uses the too large id %d.", registryName, obj, name, id));
            }
            if (this.getValue(id) != obj) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, registryName, obj));
            }
            if (this.getValue(name) != obj) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, registryName, obj));
            }
            if (this.getID(name) != id) {
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for name %s doesn't yield the expected id %d.", name, id));
            }
            if (this.validate == null) continue;
            this.validate.onValidate(this, this.stage, id, name, obj);
        }
    }

    public void bake() {
        if (this.bake != null) {
            this.bake.onBake(this, this.stage);
        }
    }

    void sync(ResourceLocation name, ForgeRegistry<V> from) {
        LOGGER.debug(REGISTRIES, "Registry {} Sync: {} -> {}", (Object)this.name, (Object)this.stage.getName(), (Object)from.stage.getName());
        if (this == from) {
            throw new IllegalArgumentException("WTF We are the same!?!?!");
        }
        this.isFrozen = false;
        if (this.clear != null) {
            this.clear.onClear(this, this.stage);
        }
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : from.aliases.entrySet()) {
            if (this.aliases.containsKey(entry.getKey())) continue;
            this.aliases.put(entry.getKey(), entry.getValue());
        }
        this.ids.clear();
        this.names.clear();
        this.keys.clear();
        this.availabilityMap.clear(0, this.availabilityMap.length());
        this.defaultValue = null;
        this.overrides.clear();
        this.owners.clear();
        boolean errored = false;
        for (Map.Entry entry : from.names.entrySet()) {
            ArrayList overrides = new ArrayList(from.overrides.get((Object)((ResourceLocation)entry.getKey())));
            int id = from.getID((ResourceLocation)entry.getKey());
            if (overrides.isEmpty()) {
                int realId = this.add(id, (ResourceLocation)entry.getKey(), entry.getValue());
                if (id == realId || id == -1) continue;
                LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", (Object)this.name, entry.getKey(), (Object)id, (Object)realId);
                errored = true;
                continue;
            }
            overrides.add(entry.getValue());
            for (Object value : overrides) {
                OverrideOwner owner = (OverrideOwner)from.owners.inverse().get(value);
                if (owner == null) {
                    LOGGER.warn(REGISTRIES, "Registry {}: Override did not have an associated owner object. Name: {} Value: {}", (Object)this.name, entry.getKey(), value);
                    errored = true;
                    continue;
                }
                int realId = this.add(id, (ResourceLocation)entry.getKey(), value, owner.owner);
                if (id == realId || id == -1) continue;
                LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", (Object)this.name, entry.getKey(), (Object)id, (Object)realId);
                errored = true;
            }
        }
        if (errored) {
            throw new RuntimeException("One of more entry values did not copy to the correct id. Check log for details!");
        }
    }

    @Override
    public void clear() {
        if (!this.isModifiable) {
            throw new UnsupportedOperationException("Attempted to clear a non-modifiable Forge Registry");
        }
        if (this.isLocked()) {
            throw new IllegalStateException("Attempted to clear the registry to late.");
        }
        if (this.clear != null) {
            this.clear.onClear(this, this.stage);
        }
        this.aliases.clear();
        this.ids.clear();
        this.names.clear();
        this.keys.clear();
        this.availabilityMap.clear(0, this.availabilityMap.length());
    }

    @Override
    public V remove(ResourceLocation key) {
        if (!this.isModifiable) {
            throw new UnsupportedOperationException("Attempted to remove from a non-modifiable Forge Registry");
        }
        if (this.isLocked()) {
            throw new IllegalStateException("Attempted to remove from the registry to late.");
        }
        Object value = this.names.remove((Object)key);
        if (value != null) {
            ResourceKey rkey = (ResourceKey)this.keys.inverse().remove(value);
            if (rkey == null) {
                throw new IllegalStateException("Removed a entry that did not have an associated RegistryKey: " + String.valueOf(key) + " " + value.toString() + " This should never happen unless hackery!");
            }
            Integer id = (Integer)this.ids.inverse().remove(value);
            if (id == null) {
                throw new IllegalStateException("Removed a entry that did not have an associated id: " + String.valueOf(key) + " " + value.toString() + " This should never happen unless hackery!");
            }
            LOGGER.trace(REGISTRIES, "Registry {} remove: {} {}", (Object)this.name, (Object)key, (Object)id);
        }
        return (V)value;
    }

    void block(int id) {
        this.blocked.add(id);
        this.availabilityMap.set(id);
    }

    @Override
    public boolean isLocked() {
        return this.isFrozen;
    }

    public void freeze() {
        this.isFrozen = true;
        NamespacedWrapper<V> wrapper = this.getWrapper();
        if (wrapper != null) {
            wrapper.locked = true;
        }
    }

    public void unfreeze() {
        this.isFrozen = false;
        NamespacedWrapper<V> wrapper = this.getWrapper();
        if (wrapper != null) {
            wrapper.locked = false;
        }
    }

    void dump(ResourceLocation name) {
        if (LOGGER.isDebugEnabled(REGISTRYDUMP)) {
            TablePrinter<DumpRow> tab = new TablePrinter<DumpRow>().header("ID", r -> r.id).header("Key", r -> r.key).header("Value", r -> r.value);
            LOGGER.debug(REGISTRYDUMP, () -> LogMessageAdapter.adapt(sb -> {
                sb.append("Registry Name: ").append(name).append('\n');
                tab.clearRows();
                this.getKeys().stream().map(this::getID).sorted().map(id -> {
                    V val = this.getValue((int)id);
                    ResourceLocation key = this.getKey(val);
                    return new DumpRow(Integer.toString(id), key.toString(), val.toString());
                }).forEach(tab::add);
                tab.build((StringBuilder)sb);
            }));
        }
    }

    public void loadIds(Object2IntMap<ResourceLocation> ids, Map<ResourceLocation, String> overrides, Object2IntMap<ResourceLocation> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remapped, ForgeRegistry<V> old, ResourceLocation name) {
        ResourceLocation itemName;
        HashMap<ResourceLocation, String> ovs = new HashMap<ResourceLocation, String>(overrides);
        for (Object2IntMap.Entry entry : ids.object2IntEntrySet()) {
            Object obj;
            itemName = (ResourceLocation)entry.getKey();
            int newId = entry.getIntValue();
            int currId = old.getIDRaw(itemName);
            if (currId == -1) {
                LOGGER.info(REGISTRIES, "Registry {}: Found a missing id from the world {}", (Object)this.name, (Object)itemName);
                missing.put((Object)itemName, newId);
                continue;
            }
            if (currId != newId) {
                LOGGER.debug(REGISTRIES, "Registry {}: Fixed {} id mismatch {}: {} (init) -> {} (map).", (Object)this.name, (Object)name, (Object)itemName, (Object)currId, (Object)newId);
                remapped.put(itemName, new IdMappingEvent.IdRemapping(currId, newId));
            }
            Preconditions.checkState(((obj = old.getRaw(itemName)) != null ? 1 : 0) != 0, (Object)"objectKey has an ID but no object. Reflection/ASM hackery? Registry bug?");
            ArrayList<V> lst = new ArrayList<V>(old.overrides.get((Object)itemName));
            String primaryName = null;
            if (old.overrides.containsKey((Object)itemName)) {
                if (!overrides.containsKey(itemName)) {
                    lst.add(obj);
                    obj = old.overrides.get((Object)itemName).iterator().next();
                    primaryName = ((OverrideOwner)old.owners.inverse().get(obj)).owner;
                } else {
                    primaryName = overrides.get(itemName);
                }
            }
            for (Object value : lst) {
                int realId;
                OverrideOwner owner = (OverrideOwner)old.owners.inverse().get(value);
                if (owner == null) {
                    LOGGER.warn(REGISTRIES, "Registry {}: Override did not have an associated owner object. Name: {} Value: {}", (Object)this.name, entry.getKey(), value);
                    continue;
                }
                if (primaryName.equals(owner.owner) || newId == (realId = this.add(newId, itemName, value, owner.owner))) continue;
                LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", (Object)this.name, entry.getKey(), (Object)newId, (Object)realId);
            }
            int realId = this.add(newId, itemName, obj, primaryName == null ? itemName.m_135827_() : primaryName);
            if (realId != newId) {
                LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", (Object)this.name, entry.getKey(), (Object)newId, (Object)realId);
            }
            ovs.remove(itemName);
        }
        for (Map.Entry entry : ovs.entrySet()) {
            int realId;
            String current;
            itemName = (ResourceLocation)entry.getKey();
            String owner = (String)entry.getValue();
            if (owner.equals(current = ((OverrideOwner)this.owners.inverse().get(this.getRaw((ResourceLocation)itemName))).owner)) continue;
            Object _new = this.owners.get(new OverrideOwner(owner, ResourceKey.m_135785_(this.key, (ResourceLocation)itemName)));
            if (_new == null) {
                LOGGER.warn(REGISTRIES, "Registry {}: Skipping override for {}, Unknown owner {}", (Object)this.name, (Object)itemName, (Object)owner);
                continue;
            }
            LOGGER.info(REGISTRIES, "Registry {}: Activating override {} for {}", (Object)this.name, (Object)owner, (Object)itemName);
            int newId = this.getID(itemName);
            if (newId == (realId = this.add(newId, itemName, _new, owner))) continue;
            LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", (Object)this.name, entry.getKey(), (Object)newId, (Object)realId);
        }
    }

    public Snapshot makeSnapshot() {
        Snapshot ret = new Snapshot();
        this.ids.forEach((id, value) -> ret.ids.put((Object)this.getKey(value), id));
        ret.aliases.putAll(this.aliases);
        ret.blocked.addAll((IntCollection)this.blocked);
        ret.overrides.putAll(this.getOverrideOwners());
        return ret;
    }

    Map<ResourceLocation, String> getOverrideOwners() {
        HashMap<ResourceLocation, String> ret = new HashMap<ResourceLocation, String>();
        for (ResourceLocation key : this.overrides.keySet()) {
            Object obj = this.names.get((Object)key);
            OverrideOwner owner = (OverrideOwner)this.owners.inverse().get(obj);
            if (owner == null) {
                LOGGER.debug(REGISTRIES, "Registry {} {}: Invalid override {} {}", (Object)this.name, (Object)this.stage.getName(), (Object)key, obj);
            }
            ret.put(key, owner.owner);
        }
        return ret;
    }

    public MissingMappingsEvent getMissingEvent(ResourceLocation name, Object2IntMap<ResourceLocation> map) {
        ArrayList lst = new ArrayList();
        ForgeRegistry pool = RegistryManager.ACTIVE.getRegistry(name);
        map.object2IntEntrySet().forEach(entry -> lst.add(new MissingMappingsEvent.Mapping(this, pool, (ResourceLocation)entry.getKey(), entry.getIntValue())));
        return new MissingMappingsEvent(ResourceKey.m_135788_((ResourceLocation)name), this, lst);
    }

    void processMissingEvent(ResourceLocation name, ForgeRegistry<V> pool, List<MissingMappingsEvent.Mapping<V>> mappings, Object2IntMap<ResourceLocation> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies) {
        LOGGER.debug(REGISTRIES, "Processing missing event for {}:", (Object)name);
        int ignored = 0;
        for (MissingMappingsEvent.Mapping<V> remap : mappings) {
            MissingMappingsEvent.Action action = remap.action;
            if (action == MissingMappingsEvent.Action.REMAP) {
                int currId = this.getID(remap.target);
                ResourceLocation newName = pool.getKey(remap.target);
                LOGGER.debug(REGISTRIES, "  Remapping {} -> {}.", (Object)remap.key, (Object)newName);
                missing.removeInt((Object)remap.key);
                int realId = this.add(remap.id, newName, remap.target);
                if (realId != remap.id) {
                    LOGGER.warn(REGISTRIES, "Registered object did not get ID it asked for. Name: {} Expected: {} Got: {}", (Object)newName, (Object)remap.id, (Object)realId);
                }
                this.addAlias(remap.key, newName);
                if (currId == realId) continue;
                LOGGER.info(REGISTRIES, "Fixed id mismatch {}: {} (init) -> {} (map).", (Object)newName, (Object)currId, (Object)realId);
                remaps.put(newName, new IdMappingEvent.IdRemapping(currId, realId));
                continue;
            }
            if (action == MissingMappingsEvent.Action.DEFAULT) {
                V m;
                V v = m = this.missing == null ? null : (V)this.missing.createMissing(remap.key, injectNetworkDummies);
                if (m == null) {
                    defaulted.add(remap.key);
                } else {
                    this.add(remap.id, remap.key, m, remap.key.m_135827_());
                }
            } else if (action == MissingMappingsEvent.Action.IGNORE) {
                LOGGER.debug(REGISTRIES, "Ignoring {}", (Object)remap.key);
                ++ignored;
            } else if (action == MissingMappingsEvent.Action.FAIL) {
                LOGGER.debug(REGISTRIES, "Failing {}!", (Object)remap.key);
                failed.add(remap.key);
            } else if (action == MissingMappingsEvent.Action.WARN) {
                LOGGER.warn(REGISTRIES, "{} may cause world breakage!", (Object)remap.key);
            }
            this.block(remap.id);
        }
        if (failed.isEmpty() && ignored > 0) {
            LOGGER.debug(REGISTRIES, "There were {} missing mappings that have been ignored", (Object)ignored);
        }
    }

    RegistryBuilder<V> getBuilder() {
        return this.builder;
    }

    private class RegistryCodec
    implements Codec<V> {
        private RegistryCodec() {
        }

        public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
            if (ops.compressMaps()) {
                return ops.getNumberValue(input).flatMap(n -> {
                    int id = n.intValue();
                    if (ForgeRegistry.this.ids.get((Object)id) == null) {
                        return DataResult.error(() -> "Unknown registry id in " + String.valueOf(ForgeRegistry.this.key) + ": " + String.valueOf(n));
                    }
                    Object val = ForgeRegistry.this.getValue(id);
                    return DataResult.success(val);
                }).map(v -> Pair.of((Object)v, (Object)ops.empty()));
            }
            return ResourceLocation.f_135803_.decode(ops, input).flatMap(keyValuePair -> !ForgeRegistry.this.containsKey((ResourceLocation)keyValuePair.getFirst()) ? DataResult.error(() -> "Unknown registry key in " + String.valueOf(ForgeRegistry.this.key) + ": " + String.valueOf(keyValuePair.getFirst())) : DataResult.success((Object)keyValuePair.mapFirst(ForgeRegistry.this::getValue)));
        }

        public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
            ResourceLocation key = ForgeRegistry.this.getKey(input);
            if (key == null) {
                return DataResult.error(() -> "Unknown registry element in " + String.valueOf(ForgeRegistry.this.key) + ": " + String.valueOf(input));
            }
            Object toMerge = ops.compressMaps() ? ops.createInt(ForgeRegistry.this.getID(input)) : ops.createString(key.toString());
            return ops.mergeToPrimitive(prefix, toMerge);
        }
    }

    private record OverrideOwner<V>(String owner, ResourceKey<V> key) {
    }

    public static class Snapshot {
        private static final Comparator<ResourceLocation> sorter = ResourceLocation::compareNamespaced;
        public final Object2IntMap<ResourceLocation> ids = new Object2IntRBTreeMap(sorter);
        public final Map<ResourceLocation, ResourceLocation> aliases = new TreeMap<ResourceLocation, ResourceLocation>(sorter);
        public final IntSet blocked = new IntRBTreeSet();
        public final Map<ResourceLocation, String> overrides = new TreeMap<ResourceLocation, String>(sorter);
        private FriendlyByteBuf binary = null;

        public CompoundTag write() {
            CompoundTag data = new CompoundTag();
            ListTag ids = new ListTag();
            this.ids.object2IntEntrySet().forEach(e -> {
                CompoundTag tag = new CompoundTag();
                tag.m_128359_("K", ((ResourceLocation)e.getKey()).toString());
                tag.m_128405_("V", e.getIntValue());
                ids.add((Object)tag);
            });
            data.m_128365_("ids", (Tag)ids);
            ListTag aliases = new ListTag();
            this.aliases.entrySet().forEach(e -> {
                CompoundTag tag = new CompoundTag();
                tag.m_128359_("K", ((ResourceLocation)e.getKey()).toString());
                tag.m_128359_("V", ((ResourceLocation)e.getValue()).toString());
                aliases.add((Object)tag);
            });
            data.m_128365_("aliases", (Tag)aliases);
            ListTag overrides = new ListTag();
            this.overrides.entrySet().forEach(e -> {
                CompoundTag tag = new CompoundTag();
                tag.m_128359_("K", ((ResourceLocation)e.getKey()).toString());
                tag.m_128359_("V", (String)e.getValue());
                overrides.add((Object)tag);
            });
            data.m_128365_("overrides", (Tag)overrides);
            int[] blocked = this.blocked.intStream().sorted().toArray();
            data.m_128385_("blocked", blocked);
            return data;
        }

        public static Snapshot read(CompoundTag nbt) {
            int[] blocked;
            Snapshot ret = new Snapshot();
            if (nbt == null) {
                return ret;
            }
            ListTag list = nbt.m_128437_("ids", 10);
            list.forEach(e -> {
                CompoundTag comp = (CompoundTag)e;
                ret.ids.put((Object)new ResourceLocation(comp.m_128461_("K")), comp.m_128451_("V"));
            });
            list = nbt.m_128437_("aliases", 10);
            list.forEach(e -> {
                CompoundTag comp = (CompoundTag)e;
                ret.aliases.put(new ResourceLocation(comp.m_128461_("K")), new ResourceLocation(comp.m_128461_("V")));
            });
            list = nbt.m_128437_("overrides", 10);
            list.forEach(e -> {
                CompoundTag comp = (CompoundTag)e;
                ret.overrides.put(new ResourceLocation(comp.m_128461_("K")), comp.m_128461_("V"));
            });
            for (int i : blocked = nbt.m_128465_("blocked")) {
                ret.blocked.add(i);
            }
            return ret;
        }

        public synchronized FriendlyByteBuf getPacketData() {
            if (this.binary == null) {
                FriendlyByteBuf pkt = new FriendlyByteBuf(Unpooled.buffer());
                pkt.m_236831_(this.ids, FriendlyByteBuf::m_130085_, FriendlyByteBuf::m_130130_);
                pkt.m_236831_(this.aliases, FriendlyByteBuf::m_130085_, FriendlyByteBuf::m_130085_);
                pkt.m_236831_(this.overrides, FriendlyByteBuf::m_130085_, (b, v) -> b.m_130072_(v, 256));
                pkt.m_236828_((Collection)this.blocked, FriendlyByteBuf::m_130130_);
                this.binary = pkt;
            }
            return new FriendlyByteBuf(this.binary.slice());
        }

        public static Snapshot read(FriendlyByteBuf buf) {
            if (buf == null) {
                return new Snapshot();
            }
            Snapshot ret = new Snapshot();
            ret.ids.putAll(buf.m_236847_(FriendlyByteBuf::m_130281_, FriendlyByteBuf::m_130242_));
            ret.aliases.putAll(buf.m_236847_(FriendlyByteBuf::m_130281_, FriendlyByteBuf::m_130281_));
            ret.overrides.putAll(buf.m_236847_(FriendlyByteBuf::m_130281_, b -> b.m_130136_(256)));
            ret.blocked.addAll((Collection)buf.m_236845_(FriendlyByteBuf::m_130242_));
            return ret;
        }
    }

    private record DumpRow(String id, String key, String value) {
    }
}

