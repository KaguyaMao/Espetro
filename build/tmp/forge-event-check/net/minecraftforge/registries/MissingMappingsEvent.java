/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.commons.lang3.Validate
 */
package net.minecraftforge.registries;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.Validate;

public class MissingMappingsEvent
extends Event {
    private final ResourceKey<? extends Registry<?>> key;
    private final IForgeRegistry<?> registry;
    private final List<Mapping<?>> mappings;

    public MissingMappingsEvent(ResourceKey<? extends Registry<?>> key, IForgeRegistry<?> registry, Collection<Mapping<?>> missed) {
        this.key = key;
        this.registry = registry;
        this.mappings = List.copyOf(missed);
    }

    public ResourceKey<? extends Registry<?>> getKey() {
        return this.key;
    }

    public IForgeRegistry<?> getRegistry() {
        return this.registry;
    }

    public <T> List<Mapping<T>> getMappings(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        return registryKey == this.key ? this.mappings.stream().filter(e -> e.key.m_135827_().equals(namespace)).toList() : List.of();
    }

    public <T> List<Mapping<T>> getAllMappings(ResourceKey<? extends Registry<T>> registryKey) {
        return registryKey == this.key ? this.mappings : List.of();
    }

    public static class Mapping<T>
    implements Comparable<Mapping<T>> {
        private final IForgeRegistry<T> registry;
        private final IForgeRegistry<T> pool;
        final ResourceLocation key;
        final int id;
        Action action = Action.DEFAULT;
        T target;

        public Mapping(IForgeRegistry<T> registry, IForgeRegistry<T> pool, ResourceLocation key, int id) {
            this.registry = registry;
            this.pool = pool;
            this.key = key;
            this.id = id;
        }

        public void ignore() {
            this.action = Action.IGNORE;
        }

        public void warn() {
            this.action = Action.WARN;
        }

        public void fail() {
            this.action = Action.FAIL;
        }

        public void remap(T target) {
            Validate.notNull(target, (String)"Remap target can not be null", (Object[])new Object[0]);
            Validate.isTrue((this.pool.getKey(target) != null ? 1 : 0) != 0, (String)String.format(Locale.ENGLISH, "The specified entry %s hasn't been registered in registry yet.", target), (Object[])new Object[0]);
            this.action = Action.REMAP;
            this.target = target;
        }

        public IForgeRegistry<T> getRegistry() {
            return this.registry;
        }

        public ResourceLocation getKey() {
            return this.key;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public int compareTo(Mapping<T> o) {
            int ret = this.registry.getRegistryName().compareNamespaced(o.registry.getRegistryName());
            if (ret == 0) {
                ret = this.key.compareNamespaced(o.key);
            }
            return ret;
        }
    }

    public static enum Action {
        DEFAULT,
        IGNORE,
        WARN,
        FAIL,
        REMAP;

    }
}

