/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Lifecycle
 *  net.minecraft.core.Registry
 *  net.minecraft.core.WritableRegistry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NamespacedWrapper;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryManager {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");
    public static final RegistryManager VANILLA = new RegistryManager("VANILLA");
    public static final RegistryManager FROZEN = new RegistryManager("FROZEN");
    private static Set<ResourceLocation> vanillaRegistryKeys = Set.of();
    BiMap<ResourceLocation, ForgeRegistry<?>> registries = HashBiMap.create();
    private Set<ResourceLocation> persisted = Sets.newHashSet();
    private Set<ResourceLocation> synced = Sets.newHashSet();
    private Map<ResourceLocation, ResourceLocation> legacyNames = new HashMap<ResourceLocation, ResourceLocation>();
    private final String name;

    RegistryManager() {
        this("STAGING");
    }

    public RegistryManager(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    boolean isStaging() {
        return "STAGING".equals(this.name);
    }

    public <V> ForgeRegistry<V> getRegistry(ResourceLocation key) {
        return (ForgeRegistry)this.registries.get((Object)key);
    }

    public <V> ForgeRegistry<V> getRegistry(ResourceKey<? extends Registry<V>> key) {
        return this.getRegistry(key.m_135782_());
    }

    public <V> ResourceLocation getName(IForgeRegistry<V> reg) {
        return (ResourceLocation)this.registries.inverse().get(reg);
    }

    public <V> ResourceLocation updateLegacyName(ResourceLocation legacyName) {
        ResourceLocation originalName = legacyName;
        while (this.getRegistry(legacyName) == null) {
            if ((legacyName = this.legacyNames.get(legacyName)) != null) continue;
            return originalName;
        }
        return legacyName;
    }

    public <V> ForgeRegistry<V> getRegistry(ResourceLocation key, RegistryManager other) {
        if (!this.registries.containsKey((Object)key)) {
            ForgeRegistry<V> ot = other.getRegistry(key);
            if (ot == null) {
                return null;
            }
            this.registries.put((Object)key, ot.copy(this));
            if (other.persisted.contains(key)) {
                this.persisted.add(key);
            }
            if (other.synced.contains(key)) {
                this.synced.add(key);
            }
            other.legacyNames.entrySet().stream().filter(e -> ((ResourceLocation)e.getValue()).equals((Object)key)).forEach(e -> this.addLegacyName((ResourceLocation)e.getKey(), (ResourceLocation)e.getValue()));
        }
        return this.getRegistry(key);
    }

    <V> ForgeRegistry<V> createRegistry(ResourceLocation name, RegistryBuilder<V> builder) {
        if (this.registries.containsKey((Object)name)) {
            throw new IllegalArgumentException("Attempted to register a registry for " + String.valueOf(name) + " but it already exists");
        }
        ForgeRegistry<V> reg = new ForgeRegistry<V>(this, name, builder);
        this.registries.put((Object)name, reg);
        if (builder.getSaveToDisc()) {
            this.persisted.add(name);
        }
        if (builder.getSync()) {
            this.synced.add(name);
        }
        for (ResourceLocation legacyName : builder.getLegacyNames()) {
            this.addLegacyName(legacyName, name);
        }
        return this.getRegistry(name);
    }

    static <V> void registerToRootRegistry(ForgeRegistry<V> forgeReg) {
        RegistryManager.injectForgeRegistry(forgeReg, BuiltInRegistries.f_257047_);
    }

    private static <V> void injectForgeRegistry(ForgeRegistry<V> forgeReg, Registry<? extends Registry<?>> rootRegistry) {
        WritableRegistry registry = (WritableRegistry)rootRegistry;
        NamespacedWrapper<V> wrapper = forgeReg.getWrapper();
        if (wrapper != null) {
            registry.m_255290_(forgeReg.getRegistryKey(), wrapper, Lifecycle.experimental());
        }
    }

    public static void postNewRegistryEvent() {
        NewRegistryEvent event = new NewRegistryEvent();
        DataPackRegistryEvent.NewRegistry dataPackEvent = new DataPackRegistryEvent.NewRegistry();
        vanillaRegistryKeys = Set.copyOf(BuiltInRegistries.f_257047_.m_6566_());
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        ModLoader.get().postEventWrapContainerInModOrder((Event)dataPackEvent);
        event.fill();
        dataPackEvent.process();
    }

    private void addLegacyName(ResourceLocation legacyName, ResourceLocation name) {
        if (this.legacyNames.containsKey(legacyName)) {
            throw new IllegalArgumentException("Legacy name conflict for registry " + String.valueOf(name) + ", upgrade path must be linear: " + String.valueOf(legacyName));
        }
        this.legacyNames.put(legacyName, name);
    }

    private void findSuperTypes(Class<?> type, Set<Class<?>> types) {
        if (type == null || type == Object.class) {
            return;
        }
        types.add(type);
        for (Class<?> interfac : type.getInterfaces()) {
            this.findSuperTypes(interfac, types);
        }
        this.findSuperTypes(type.getSuperclass(), types);
    }

    public Map<ResourceLocation, ForgeRegistry.Snapshot> takeSnapshot(boolean savingToDisc) {
        HashMap ret = Maps.newHashMap();
        Set<ResourceLocation> keys = savingToDisc ? this.persisted : this.synced;
        keys.forEach(name -> ret.put(name, this.getRegistry((ResourceLocation)name).makeSnapshot()));
        return ret;
    }

    public void clean() {
        this.persisted.clear();
        this.synced.clear();
        this.registries.clear();
    }

    public static List<Pair<String, HandshakeMessages.S2CRegistry>> generateRegistryPackets(boolean isLocal) {
        return !isLocal ? ACTIVE.takeSnapshot(false).entrySet().stream().map(e -> Pair.of((Object)("Registry " + String.valueOf(e.getKey())), (Object)new HandshakeMessages.S2CRegistry((ResourceLocation)e.getKey(), (ForgeRegistry.Snapshot)e.getValue()))).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static List<ResourceLocation> getRegistryNamesForSyncToClient() {
        return RegistryManager.ACTIVE.registries.keySet().stream().filter(resloc -> RegistryManager.ACTIVE.synced.contains(resloc)).collect(Collectors.toList());
    }

    public static Set<ResourceLocation> getVanillaRegistryKeys() {
        return vanillaRegistryKeys;
    }
}

