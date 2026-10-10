/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegisterEvent
extends Event
implements IModBusEvent {
    @NotNull
    private final ResourceKey<? extends Registry<?>> registryKey;
    @Nullable
    final ForgeRegistry<?> forgeRegistry;
    @Nullable
    private final Registry<?> vanillaRegistry;

    RegisterEvent(@NotNull ResourceKey<? extends Registry<?>> registryKey, @Nullable ForgeRegistry<?> forgeRegistry, @Nullable Registry<?> vanillaRegistry) {
        this.registryKey = registryKey;
        this.forgeRegistry = forgeRegistry;
        this.vanillaRegistry = vanillaRegistry;
    }

    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation name, Supplier<T> valueSupplier) {
        if (this.registryKey.equals(registryKey)) {
            if (this.forgeRegistry != null) {
                this.forgeRegistry.register(name, valueSupplier.get());
            } else if (this.vanillaRegistry != null) {
                Registry.m_122965_(this.vanillaRegistry, (ResourceLocation)name, valueSupplier.get());
            }
        }
    }

    public <T> void register(ResourceKey<? extends Registry<T>> registryKey, Consumer<RegisterHelper<T>> consumer) {
        if (this.registryKey.equals(registryKey)) {
            consumer.accept((name, value) -> this.register(registryKey, name, () -> value));
        }
    }

    @NotNull
    public ResourceKey<? extends Registry<?>> getRegistryKey() {
        return this.registryKey;
    }

    @Nullable
    public <T> IForgeRegistry<T> getForgeRegistry() {
        return this.forgeRegistry;
    }

    @Nullable
    public <T> Registry<T> getVanillaRegistry() {
        return this.vanillaRegistry;
    }

    public String toString() {
        return "RegisterEvent";
    }

    @FunctionalInterface
    public static interface RegisterHelper<T> {
        default public void register(String name, T value) {
            this.register(new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name), value);
        }

        default public void register(ResourceKey<T> key, T value) {
            this.register(key.m_135782_(), value);
        }

        public void register(ResourceLocation var1, T var2);
    }
}

