/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.registries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class IdMappingEvent
extends Event {
    private final Map<ResourceLocation, ImmutableList<ModRemapping>> remaps;
    private final ImmutableSet<ResourceLocation> keys;
    private final boolean isFrozen;

    public IdMappingEvent(Map<ResourceLocation, Map<ResourceLocation, IdRemapping>> remaps, boolean isFrozen) {
        this.isFrozen = isFrozen;
        this.remaps = Maps.newHashMap();
        remaps.forEach((name, rm) -> {
            ArrayList tmp = Lists.newArrayList();
            rm.forEach((key, value) -> tmp.add(new ModRemapping((ResourceLocation)name, (ResourceLocation)key, value.currId, value.newId)));
            tmp.sort(Comparator.comparingInt(o -> o.newId));
            this.remaps.put((ResourceLocation)name, (ImmutableList<ModRemapping>)ImmutableList.copyOf((Collection)tmp));
        });
        this.keys = ImmutableSet.copyOf(this.remaps.keySet());
    }

    public ImmutableSet<ResourceLocation> getRegistries() {
        return this.keys;
    }

    public ImmutableList<ModRemapping> getRemaps(ResourceLocation registry) {
        return this.remaps.get(registry);
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    public static class ModRemapping {
        public final ResourceLocation registry;
        public final ResourceLocation key;
        public final int oldId;
        public final int newId;

        private ModRemapping(ResourceLocation registry, ResourceLocation key, int oldId, int newId) {
            this.registry = registry;
            this.key = key;
            this.oldId = oldId;
            this.newId = newId;
        }
    }

    public record IdRemapping(int currId, int newId) {
    }
}

