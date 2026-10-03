/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegistryObject<T>
implements Supplier<T> {
    @Nullable
    private final ResourceLocation name;
    @Nullable
    private ResourceKey<T> key;
    private final boolean optionalRegistry;
    @Nullable
    private T value;
    @Nullable
    private Holder<T> holder;
    private static final RegistryObject<?> EMPTY = new RegistryObject();

    public static <T, U extends T> RegistryObject<U> create(ResourceLocation name, IForgeRegistry<T> registry) {
        return new RegistryObject<T>(name, registry);
    }

    public static <T, U extends T> RegistryObject<U> create(ResourceLocation name, ResourceKey<? extends Registry<T>> registryKey, String modid) {
        return new RegistryObject<T>(name, registryKey.m_135782_(), modid, false);
    }

    public static <T, U extends T> RegistryObject<U> createOptional(ResourceLocation name, ResourceKey<? extends Registry<T>> registryKey, String modid) {
        return new RegistryObject<T>(name, registryKey.m_135782_(), modid, true);
    }

    public static <T, U extends T> RegistryObject<U> create(ResourceLocation name, ResourceLocation registryName, String modid) {
        return new RegistryObject<T>(name, registryName, modid, false);
    }

    public static <T, U extends T> RegistryObject<U> createOptional(ResourceLocation name, ResourceLocation registryName, String modid) {
        return new RegistryObject<T>(name, registryName, modid, true);
    }

    private static <T> RegistryObject<T> empty() {
        RegistryObject<?> t = EMPTY;
        return t;
    }

    private RegistryObject() {
        this.name = null;
        this.key = null;
        this.optionalRegistry = false;
    }

    private RegistryObject(ResourceLocation name, IForgeRegistry<?> registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Invalid registry argument, must not be null");
        }
        this.name = name;
        this.key = ResourceKey.m_135785_(registry.getRegistryKey(), (ResourceLocation)name);
        this.optionalRegistry = false;
        ObjectHolderRegistry.addHandler(pred -> {
            if (pred.test(registry.getRegistryName())) {
                this.updateReference(registry);
            }
        });
        this.updateReference(registry);
    }

    private RegistryObject(ResourceLocation name, final ResourceLocation registryName, final String modid, boolean optionalRegistry) {
        this.name = name;
        this.key = ResourceKey.m_135785_((ResourceKey)ResourceKey.m_135788_((ResourceLocation)registryName), (ResourceLocation)name);
        this.optionalRegistry = optionalRegistry;
        final Throwable callerStack = new Throwable("Calling Site from mod: " + modid);
        ObjectHolderRegistry.addHandler(new Consumer<Predicate<ResourceLocation>>(){
            private boolean registryExists = false;
            private boolean invalidRegistry = false;

            @Override
            public void accept(Predicate<ResourceLocation> pred) {
                if (this.invalidRegistry) {
                    return;
                }
                if (!RegistryObject.this.optionalRegistry && !this.registryExists) {
                    if (!RegistryObject.registryExists(registryName)) {
                        this.invalidRegistry = true;
                        throw new IllegalStateException("Unable to find registry with key " + String.valueOf(registryName) + " for mod \"" + modid + "\". Check the 'caused by' to see further stack.", callerStack);
                    }
                    this.registryExists = true;
                }
                if (pred.test(registryName)) {
                    RegistryObject.this.updateReference(registryName);
                }
            }
        });
        this.updateReference(registryName);
    }

    @Override
    @NotNull
    public T get() {
        T ret = this.value;
        Objects.requireNonNull(ret, () -> "Registry Object not present: " + String.valueOf(this.name));
        return ret;
    }

    void updateReference(IForgeRegistry<? extends T> registry) {
        if (this.name == null || this.key == null) {
            return;
        }
        if (registry.containsKey(this.name)) {
            this.value = registry.getValue(this.name);
            this.holder = registry.getHolder((T)this.name).orElse(null);
        } else {
            this.value = null;
            this.holder = null;
        }
    }

    void updateReference(Registry<? extends T> registry) {
        if (this.name == null || this.key == null) {
            return;
        }
        if (registry.m_7804_(this.name)) {
            this.value = registry.m_7745_(this.name);
            this.holder = registry.m_203636_(this.key).orElse(null);
        } else {
            this.value = null;
            this.holder = null;
        }
    }

    void updateReference(ResourceLocation registryName) {
        if (this.name == null) {
            return;
        }
        ForgeRegistry forgeRegistry = RegistryManager.ACTIVE.getRegistry(registryName);
        if (forgeRegistry != null) {
            this.updateReference(forgeRegistry);
            return;
        }
        Registry vanillaRegistry = (Registry)BuiltInRegistries.f_257047_.m_7745_(registryName);
        if (vanillaRegistry != null) {
            this.updateReference(vanillaRegistry);
            return;
        }
        this.value = null;
        this.holder = null;
    }

    void updateReference(RegisterEvent event) {
        IForgeRegistry forgeRegistry = event.getForgeRegistry();
        if (forgeRegistry != null) {
            this.updateReference(forgeRegistry);
            return;
        }
        Registry vanillaRegistry = event.getVanillaRegistry();
        if (vanillaRegistry != null) {
            this.updateReference(vanillaRegistry);
        } else {
            this.value = null;
        }
    }

    private static boolean registryExists(ResourceLocation registryName) {
        return RegistryManager.ACTIVE.getRegistry(registryName) != null || BuiltInRegistries.f_257047_.m_7804_(registryName);
    }

    public ResourceLocation getId() {
        return this.name;
    }

    @Nullable
    public ResourceKey<T> getKey() {
        return this.key;
    }

    public Stream<T> stream() {
        return this.isPresent() ? Stream.of(this.get()) : Stream.of(new Object[0]);
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (this.isPresent()) {
            consumer.accept(this.get());
        }
    }

    public RegistryObject<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!this.isPresent()) {
            return this;
        }
        return predicate.test(this.get()) ? this : RegistryObject.empty();
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.apply(this.get()));
    }

    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return Optional.empty();
        }
        return Objects.requireNonNull(mapper.apply(this.get()));
    }

    public <U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return () -> this.isPresent() ? mapper.apply((T)this.get()) : null;
    }

    public T orElse(T other) {
        return this.isPresent() ? this.get() : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        return this.isPresent() ? this.get() : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.isPresent()) {
            return this.get();
        }
        throw (Throwable)exceptionSupplier.get();
    }

    @NotNull
    public Optional<Holder<T>> getHolder() {
        return Optional.ofNullable(this.holder);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RegistryObject) {
            RegistryObject o = (RegistryObject)obj;
            return o.key == this.key && Objects.equals(o.name, this.name);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this.name);
    }
}

