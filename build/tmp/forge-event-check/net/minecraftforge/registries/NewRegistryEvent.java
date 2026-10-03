/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.core.MappedRegistry
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraftforge.registries;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NewRegistryEvent
extends Event
implements IModBusEvent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<RegistryData<?>> registries = new ArrayList();

    public <V> Supplier<IForgeRegistry<V>> create(RegistryBuilder<V> builder) {
        return this.create(builder, null);
    }

    public <V> Supplier<IForgeRegistry<V>> create(RegistryBuilder<V> builder, @Nullable Consumer<IForgeRegistry<V>> onFill) {
        RegistryHolder registryHolder = new RegistryHolder();
        this.registries.add(new RegistryData<V>(builder, registryHolder, onFill));
        return registryHolder;
    }

    void fill() {
        Object rootRegistry;
        RuntimeException aggregate = new RuntimeException();
        IdentityHashMap builtRegistries = new IdentityHashMap();
        Registry registry = BuiltInRegistries.f_257047_;
        if (registry instanceof MappedRegistry) {
            rootRegistry = (MappedRegistry)registry;
            rootRegistry.unfreeze();
        }
        for (RegistryData registryData : this.registries) {
            try {
                this.buildRegistry(builtRegistries, registryData);
            }
            catch (Throwable t) {
                aggregate.addSuppressed(t);
                return;
            }
        }
        Registry registry2 = BuiltInRegistries.f_257047_;
        if (registry2 instanceof MappedRegistry) {
            rootRegistry = (MappedRegistry)registry2;
            rootRegistry.m_203521_();
        }
        if (aggregate.getSuppressed().length > 0) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to create some forge registries, see suppressed exceptions for details", (Throwable)aggregate);
        }
    }

    private <T> void buildRegistry(Map<RegistryBuilder<?>, IForgeRegistry<?>> builtRegistries, RegistryData<T> data) {
        RegistryBuilder builder = data.builder;
        IForgeRegistry registry = builder.create();
        builtRegistries.put(builder, registry);
        if (builder.getHasWrapper() && !BuiltInRegistries.f_257047_.m_7804_(registry.getRegistryName())) {
            RegistryManager.registerToRootRegistry((ForgeRegistry)registry);
        }
        data.registryHolder.registry = registry;
        if (data.onFill != null) {
            data.onFill.accept(registry);
        }
    }

    public String toString() {
        return "RegistryEvent.NewRegistry";
    }

    private static class RegistryHolder<V>
    implements Supplier<IForgeRegistry<V>> {
        IForgeRegistry<V> registry = null;

        private RegistryHolder() {
        }

        @Override
        public IForgeRegistry<V> get() {
            return this.registry;
        }
    }

    private record RegistryData<V>(RegistryBuilder<V> builder, RegistryHolder<V> registryHolder, Consumer<IForgeRegistry<V>> onFill) {
    }
}

