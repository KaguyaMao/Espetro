/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biomes
 */
package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeManager {
    private static TrackedList<BiomeEntry>[] biomes = BiomeManager.setupBiomes();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomes = new ArrayList<ResourceKey<Biome>>();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomesView = Collections.unmodifiableList(additionalOverworldBiomes);

    private static TrackedList<BiomeEntry>[] setupBiomes() {
        TrackedList[] currentBiomes = new TrackedList[BiomeType.values().length];
        currentBiomes[BiomeType.DESERT_LEGACY.ordinal()] = new TrackedList(new BiomeEntry((ResourceKey<Biome>)Biomes.f_48203_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48205_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48207_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48202_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48206_, 10));
        currentBiomes[BiomeType.DESERT.ordinal()] = new TrackedList(new BiomeEntry((ResourceKey<Biome>)Biomes.f_48203_, 30), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48157_, 20), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48202_, 10));
        currentBiomes[BiomeType.WARM.ordinal()] = new TrackedList(new BiomeEntry((ResourceKey<Biome>)Biomes.f_48205_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48151_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48202_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48149_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48207_, 10));
        currentBiomes[BiomeType.COOL.ordinal()] = new TrackedList(new BiomeEntry((ResourceKey<Biome>)Biomes.f_48205_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48206_, 10), new BiomeEntry((ResourceKey<Biome>)Biomes.f_48202_, 10));
        currentBiomes[BiomeType.ICY.ordinal()] = new TrackedList(new BiomeEntry((ResourceKey<Biome>)Biomes.f_48152_, 10));
        return currentBiomes;
    }

    public static void addAdditionalOverworldBiomes(ResourceKey<Biome> biome) {
        if (!"minecraft".equals(biome.m_135782_().m_135827_()) && additionalOverworldBiomes.stream().noneMatch(entry -> entry.m_135782_().equals((Object)biome.m_135782_()))) {
            additionalOverworldBiomes.add(biome);
        }
    }

    public static boolean addBiome(BiomeType type, BiomeEntry entry) {
        TrackedList<BiomeEntry> list;
        int idx = type.ordinal();
        TrackedList<BiomeEntry> trackedList = list = idx > biomes.length ? null : biomes[idx];
        if (list != null) {
            additionalOverworldBiomes.add(entry.key);
            return list.add(entry);
        }
        return false;
    }

    public static boolean removeBiome(BiomeType type, BiomeEntry entry) {
        int idx = type.ordinal();
        TrackedList<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        return list == null ? false : list.remove(entry);
    }

    public static List<ResourceKey<Biome>> getAdditionalOverworldBiomes() {
        return additionalOverworldBiomesView;
    }

    public static ImmutableList<BiomeEntry> getBiomes(BiomeType type) {
        int idx = type.ordinal();
        TrackedList<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];
        return list != null ? ImmutableList.copyOf(list) : ImmutableList.of();
    }

    public static boolean isTypeListModded(BiomeType type) {
        int idx = type.ordinal();
        TrackedList<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        return list == null ? false : list.isModded();
    }

    public static enum BiomeType {
        DESERT,
        DESERT_LEGACY,
        WARM,
        COOL,
        ICY;

    }

    private static class TrackedList<E>
    extends ArrayList<E> {
        private static final long serialVersionUID = 1L;
        private boolean isModded = false;

        @SafeVarargs
        private <T extends E> TrackedList(T ... c) {
            super(Arrays.asList(c));
        }

        @Override
        public E set(int index, E element) {
            this.isModded = true;
            return super.set(index, element);
        }

        @Override
        public boolean add(E e) {
            this.isModded = true;
            return super.add(e);
        }

        @Override
        public void add(int index, E element) {
            this.isModded = true;
            super.add(index, element);
        }

        @Override
        public E remove(int index) {
            this.isModded = true;
            return super.remove(index);
        }

        @Override
        public boolean remove(Object o) {
            this.isModded = true;
            return super.remove(o);
        }

        @Override
        public void clear() {
            this.isModded = true;
            super.clear();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            this.isModded = true;
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            this.isModded = true;
            return super.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            this.isModded = true;
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            this.isModded = true;
            return super.retainAll(c);
        }

        public boolean isModded() {
            return this.isModded;
        }
    }

    public static class BiomeEntry {
        private final ResourceKey<Biome> key;

        public BiomeEntry(ResourceKey<Biome> key, int weight) {
            this.key = key;
        }

        public ResourceKey<Biome> getKey() {
            return this.key;
        }
    }
}

