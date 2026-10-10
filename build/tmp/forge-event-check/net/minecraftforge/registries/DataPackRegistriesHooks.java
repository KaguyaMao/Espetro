/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistrySynchronization
 *  net.minecraft.core.RegistrySynchronization$NetworkedRegistryData
 *  net.minecraft.resources.RegistryDataLoader
 *  net.minecraft.resources.RegistryDataLoader$RegistryData
 *  net.minecraft.resources.ResourceKey
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.registries;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class DataPackRegistriesHooks {
    private static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = new LinkedHashMap();
    private static final List<RegistryDataLoader.RegistryData<?>> DATA_PACK_REGISTRIES = new ArrayList(RegistryDataLoader.f_243803_);
    private static final List<RegistryDataLoader.RegistryData<?>> DATA_PACK_REGISTRIES_VIEW = Collections.unmodifiableList(DATA_PACK_REGISTRIES);
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES = new HashSet();
    private static final Set<ResourceKey<? extends Registry<?>>> SYNCED_CUSTOM_REGISTRIES_VIEW = Collections.unmodifiableSet(SYNCED_CUSTOM_REGISTRIES);

    private DataPackRegistriesHooks() {
    }

    public static Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> grabNetworkableRegistries(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
        if (!StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().equals(RegistrySynchronization.class)) {
            throw new IllegalCallerException("Attempted to call DataPackRegistriesHooks#grabNetworkableRegistries!");
        }
        NETWORKABLE_REGISTRIES.forEach((arg_0, arg_1) -> builder.put(arg_0, arg_1));
        NETWORKABLE_REGISTRIES.clear();
        NETWORKABLE_REGISTRIES.putAll((Map<ResourceKey<Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>)builder.build());
        return Collections.unmodifiableMap(NETWORKABLE_REGISTRIES);
    }

    static <T> void addRegistryCodec(DataPackRegistryEvent.DataPackRegistryData<T> data) {
        RegistryDataLoader.RegistryData<T> loaderData = data.loaderData();
        DATA_PACK_REGISTRIES.add(loaderData);
        if (data.networkCodec() != null) {
            SYNCED_CUSTOM_REGISTRIES.add(loaderData.f_243794_());
            NETWORKABLE_REGISTRIES.put(loaderData.f_243794_(), new RegistrySynchronization.NetworkedRegistryData(loaderData.f_243794_(), data.networkCodec()));
        }
    }

    public static List<RegistryDataLoader.RegistryData<?>> getDataPackRegistries() {
        return DATA_PACK_REGISTRIES_VIEW;
    }

    public static Stream<RegistryDataLoader.RegistryData<?>> getDataPackRegistriesWithDimensions() {
        return Stream.concat(DATA_PACK_REGISTRIES_VIEW.stream(), RegistryDataLoader.f_244547_.stream());
    }

    public static Set<ResourceKey<? extends Registry<?>>> getSyncedCustomRegistries() {
        return SYNCED_CUSTOM_REGISTRIES_VIEW;
    }
}

