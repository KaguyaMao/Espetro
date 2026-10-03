/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.SetMultimap
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeferredRegister<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String modid;
    private final boolean optionalRegistry;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<RegistryObject<T>, Supplier<? extends T>>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(this.entries.keySet());
    @Nullable
    private Supplier<RegistryBuilder<?>> registryFactory;
    @Nullable
    private SetMultimap<TagKey<T>, Supplier<T>> optionalTags;
    private boolean seenRegisterEvent = false;

    public static <B> DeferredRegister<B> create(IForgeRegistry<B> reg, String modid) {
        return new DeferredRegister<B>(reg, modid);
    }

    public static <B> DeferredRegister<B> create(ResourceKey<? extends Registry<B>> key, String modid) {
        return new DeferredRegister(key, modid, false);
    }

    public static <B> DeferredRegister<B> createOptional(ResourceKey<? extends Registry<B>> key, String modid) {
        return new DeferredRegister(key, modid, true);
    }

    public static <B> DeferredRegister<B> create(ResourceLocation registryName, String modid) {
        return new DeferredRegister(ResourceKey.m_135788_((ResourceLocation)registryName), modid, false);
    }

    public static <B> DeferredRegister<B> createOptional(ResourceLocation registryName, String modid) {
        return new DeferredRegister(ResourceKey.m_135788_((ResourceLocation)registryName), modid, true);
    }

    private DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String modid, boolean optionalRegistry) {
        this.registryKey = registryKey;
        this.modid = modid;
        this.optionalRegistry = optionalRegistry;
    }

    private DeferredRegister(IForgeRegistry<T> reg, String modid) {
        this(reg.getRegistryKey(), modid, false);
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup) {
        if (this.seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after RegisterEvent has been fired.");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        ResourceLocation key = new ResourceLocation(this.modid, name);
        if (this.registryKey == null) {
            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");
        }
        RegistryObject ret = this.optionalRegistry ? RegistryObject.createOptional(key, this.registryKey, this.modid) : RegistryObject.create(key, this.registryKey, this.modid);
        if (this.entries.putIfAbsent(ret, sup) != null) {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }
        return ret;
    }

    public Supplier<IForgeRegistry<T>> makeRegistry(Supplier<RegistryBuilder<T>> sup) {
        return this.makeRegistry(this.registryKey.m_135782_(), sup);
    }

    @NotNull
    public TagKey<T> createTagKey(@NotNull String path) {
        Objects.requireNonNull(path);
        return this.createTagKey(new ResourceLocation(this.modid, path));
    }

    @NotNull
    public TagKey<T> createTagKey(@NotNull ResourceLocation location) {
        if (this.registryKey == null) {
            throw new IllegalStateException("The registry name was not set, cannot create a tag key");
        }
        Objects.requireNonNull(location);
        return TagKey.m_203882_(this.registryKey, (ResourceLocation)location);
    }

    @NotNull
    public TagKey<T> createOptionalTagKey(@NotNull String path, @NotNull Set<? extends Supplier<T>> defaults) {
        Objects.requireNonNull(path);
        return this.createOptionalTagKey(new ResourceLocation(this.modid, path), defaults);
    }

    @NotNull
    public TagKey<T> createOptionalTagKey(@NotNull ResourceLocation location, @NotNull Set<? extends Supplier<T>> defaults) {
        TagKey<T> tagKey = this.createTagKey(location);
        this.addOptionalTagDefaults(tagKey, defaults);
        return tagKey;
    }

    public void addOptionalTagDefaults(@NotNull TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults) {
        Objects.requireNonNull(defaults);
        if (this.optionalTags == null) {
            this.optionalTags = Multimaps.newSetMultimap(new IdentityHashMap(), HashSet::new);
        }
        this.optionalTags.putAll(name, defaults);
    }

    public void register(IEventBus bus) {
        bus.register((Object)new EventDispatcher(this));
        if (this.registryFactory != null) {
            bus.addListener(this::createRegistry);
        }
    }

    public Collection<RegistryObject<T>> getEntries() {
        return this.entriesView;
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return this.registryKey;
    }

    @NotNull
    public ResourceLocation getRegistryName() {
        return Objects.requireNonNull(this.registryKey).m_135782_();
    }

    private Supplier<IForgeRegistry<T>> makeRegistry(ResourceLocation registryName, Supplier<RegistryBuilder<T>> sup) {
        if (registryName == null) {
            throw new IllegalStateException("Cannot create a registry without specifying a registry name");
        }
        if (RegistryManager.ACTIVE.getRegistry(registryName) != null || this.registryFactory != null) {
            throw new IllegalStateException("Cannot create a registry for a type that already exists");
        }
        this.registryFactory = () -> ((RegistryBuilder)sup.get()).setName(registryName);
        return new RegistryHolder(this.registryKey);
    }

    private void onFill(IForgeRegistry<?> registry) {
        if (this.optionalTags == null) {
            return;
        }
        ITagManager<?> tagManager = registry.tags();
        if (tagManager == null) {
            throw new IllegalStateException("The forge registry " + String.valueOf(registry.getRegistryName()) + " does not support tags, but optional tags were registered!");
        }
        Multimaps.asMap(this.optionalTags).forEach(tagManager::addOptionalTagDefaults);
    }

    private void addEntries(RegisterEvent event) {
        if (event.getRegistryKey().equals(this.registryKey)) {
            this.seenRegisterEvent = true;
            for (Map.Entry e : this.entries.entrySet()) {
                event.register(this.registryKey, e.getKey().getId(), () -> ((Supplier)e.getValue()).get());
                e.getKey().updateReference(event);
            }
        }
    }

    private void createRegistry(NewRegistryEvent event) {
        event.create(this.registryFactory.get(), this::onFill);
    }

    public static class EventDispatcher {
        private final DeferredRegister<?> register;

        public EventDispatcher(DeferredRegister<?> register) {
            this.register = register;
        }

        @SubscribeEvent
        public void handleEvent(RegisterEvent event) {
            this.register.addEntries(event);
        }
    }

    private static class RegistryHolder<V>
    implements Supplier<IForgeRegistry<V>> {
        private final ResourceKey<? extends Registry<V>> registryKey;
        private IForgeRegistry<V> registry = null;

        private RegistryHolder(ResourceKey<? extends Registry<V>> registryKey) {
            this.registryKey = registryKey;
        }

        @Override
        public IForgeRegistry<V> get() {
            if (this.registry == null) {
                this.registry = RegistryManager.ACTIVE.getRegistry(this.registryKey);
            }
            return this.registry;
        }
    }
}

