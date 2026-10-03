/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.RegistryDataLoader$RegistryData
 *  net.minecraft.resources.ResourceKey
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class DataPackRegistryEvent
extends Event
implements IModBusEvent {
    @ApiStatus.Internal
    public DataPackRegistryEvent() {
    }

    record DataPackRegistryData<T>(RegistryDataLoader.RegistryData<T> loaderData, @Nullable Codec<T> networkCodec) {
    }

    public static final class NewRegistry
    extends DataPackRegistryEvent {
        private final List<DataPackRegistryData<?>> registryDataList = new ArrayList();

        @ApiStatus.Internal
        public NewRegistry() {
        }

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
            this.dataPackRegistry(registryKey, codec, null);
        }

        public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
            this.registryDataList.add(new DataPackRegistryData<T>(new RegistryDataLoader.RegistryData(registryKey, codec), networkCodec));
        }

        void process() {
            for (DataPackRegistryData<?> registryData : this.registryDataList) {
                DataPackRegistriesHooks.addRegistryCodec(registryData);
            }
        }
    }
}

