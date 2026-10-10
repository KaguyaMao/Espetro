/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IForgeRegistry<V>
extends Iterable<V> {
    public ResourceKey<Registry<V>> getRegistryKey();

    public ResourceLocation getRegistryName();

    public void register(String var1, V var2);

    public void register(ResourceLocation var1, V var2);

    public boolean containsKey(ResourceLocation var1);

    public boolean containsValue(V var1);

    public boolean isEmpty();

    @Nullable
    public V getValue(ResourceLocation var1);

    @Nullable
    public ResourceLocation getKey(V var1);

    @Nullable
    public ResourceLocation getDefaultKey();

    @NotNull
    public Optional<ResourceKey<V>> getResourceKey(V var1);

    @NotNull
    public Set<ResourceLocation> getKeys();

    @NotNull
    public Collection<V> getValues();

    @NotNull
    public Set<Map.Entry<ResourceKey<V>, V>> getEntries();

    @NotNull
    public Codec<V> getCodec();

    @NotNull
    public Optional<Holder<V>> getHolder(ResourceKey<V> var1);

    @NotNull
    public Optional<Holder<V>> getHolder(ResourceLocation var1);

    @NotNull
    public Optional<Holder<V>> getHolder(V var1);

    @Nullable
    public ITagManager<V> tags();

    @NotNull
    public Optional<Holder.Reference<V>> getDelegate(ResourceKey<V> var1);

    @NotNull
    public Holder.Reference<V> getDelegateOrThrow(ResourceKey<V> var1);

    @NotNull
    public Optional<Holder.Reference<V>> getDelegate(ResourceLocation var1);

    @NotNull
    public Holder.Reference<V> getDelegateOrThrow(ResourceLocation var1);

    @NotNull
    public Optional<Holder.Reference<V>> getDelegate(V var1);

    @NotNull
    public Holder.Reference<V> getDelegateOrThrow(V var1);

    public <T> T getSlaveMap(ResourceLocation var1, Class<T> var2);

    @FunctionalInterface
    public static interface MissingFactory<V> {
        public V createMissing(ResourceLocation var1, boolean var2);
    }

    @FunctionalInterface
    public static interface BakeCallback<V> {
        public void onBake(IForgeRegistryInternal<V> var1, RegistryManager var2);
    }

    @FunctionalInterface
    public static interface ValidateCallback<V> {
        public void onValidate(IForgeRegistryInternal<V> var1, RegistryManager var2, int var3, ResourceLocation var4, V var5);
    }

    @FunctionalInterface
    public static interface CreateCallback<V> {
        public void onCreate(IForgeRegistryInternal<V> var1, RegistryManager var2);
    }

    @FunctionalInterface
    public static interface ClearCallback<V> {
        public void onClear(IForgeRegistryInternal<V> var1, RegistryManager var2);
    }

    @FunctionalInterface
    public static interface AddCallback<V> {
        public void onAdd(IForgeRegistryInternal<V> var1, RegistryManager var2, int var3, ResourceKey<V> var4, V var5, @Nullable V var6);
    }
}

