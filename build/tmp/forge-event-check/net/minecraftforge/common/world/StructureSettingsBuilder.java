/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderSet
 *  net.minecraft.util.random.WeightedRandomList
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.structure.Structure$StructureSettings
 *  net.minecraft.world.level.levelgen.structure.StructureSpawnOverride
 *  net.minecraft.world.level.levelgen.structure.StructureSpawnOverride$BoundingBoxType
 *  net.minecraft.world.level.levelgen.structure.TerrainAdjustment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.jetbrains.annotations.Nullable;

public class StructureSettingsBuilder {
    private HolderSet<Biome> biomes;
    private final Map<MobCategory, StructureSpawnOverrideBuilder> spawnOverrides;
    private GenerationStep.Decoration step;
    private TerrainAdjustment terrainAdaptation;

    public static StructureSettingsBuilder copyOf(Structure.StructureSettings settings) {
        return new StructureSettingsBuilder((HolderSet<Biome>)settings.f_226689_(), settings.f_226690_(), settings.f_226691_(), settings.f_226692_());
    }

    private StructureSettingsBuilder(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdaptation) {
        this.biomes = biomes;
        this.spawnOverrides = spawnOverrides.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> StructureSpawnOverrideBuilder.copyOf((StructureSpawnOverride)entry.getValue())));
        this.step = step;
        this.terrainAdaptation = terrainAdaptation;
    }

    public Structure.StructureSettings build() {
        Map<MobCategory, StructureSpawnOverride> overrides = Collections.unmodifiableMap(this.spawnOverrides.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((StructureSpawnOverrideBuilder)entry.getValue()).build())));
        return new Structure.StructureSettings(this.biomes, overrides, this.step, this.terrainAdaptation);
    }

    public HolderSet<Biome> getBiomes() {
        return this.biomes;
    }

    public void setBiomes(HolderSet<Biome> biomes) {
        this.biomes = biomes;
    }

    @Nullable
    public StructureSpawnOverrideBuilder getSpawnOverrides(MobCategory category) {
        return this.spawnOverrides.get(category);
    }

    public StructureSpawnOverrideBuilder getOrAddSpawnOverrides(MobCategory category) {
        return this.spawnOverrides.computeIfAbsent(category, c -> new StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType.PIECE, Collections.emptyList()));
    }

    public void removeSpawnOverrides(MobCategory category) {
        this.spawnOverrides.remove(category);
    }

    public GenerationStep.Decoration getDecorationStep() {
        return this.step;
    }

    public void setDecorationStep(GenerationStep.Decoration step) {
        this.step = step;
    }

    public TerrainAdjustment getTerrainAdaptation() {
        return this.terrainAdaptation;
    }

    public void setTerrainAdaptation(TerrainAdjustment terrainAdaptation) {
        this.terrainAdaptation = terrainAdaptation;
    }

    public static class StructureSpawnOverrideBuilder {
        private StructureSpawnOverride.BoundingBoxType boundingBox;
        private final List<MobSpawnSettings.SpawnerData> spawns;
        private final List<MobSpawnSettings.SpawnerData> spawnsView;

        public static StructureSpawnOverrideBuilder copyOf(StructureSpawnOverride override) {
            return new StructureSpawnOverrideBuilder(override.f_210043_(), override.f_210044_().m_146338_());
        }

        private StructureSpawnOverrideBuilder(StructureSpawnOverride.BoundingBoxType boundingBox, List<MobSpawnSettings.SpawnerData> spawns) {
            this.boundingBox = boundingBox;
            this.spawns = new ArrayList<MobSpawnSettings.SpawnerData>(spawns);
            this.spawnsView = Collections.unmodifiableList(this.spawns);
        }

        public StructureSpawnOverride.BoundingBoxType getBoundingBox() {
            return this.boundingBox;
        }

        public void setBoundingBox(StructureSpawnOverride.BoundingBoxType boundingBox) {
            this.boundingBox = boundingBox;
        }

        public List<MobSpawnSettings.SpawnerData> getSpawns() {
            return this.spawnsView;
        }

        public void addSpawn(MobSpawnSettings.SpawnerData spawn) {
            this.spawns.add(spawn);
        }

        public void removeSpawn(MobSpawnSettings.SpawnerData spawn) {
            this.spawns.remove(spawn);
        }

        public StructureSpawnOverride build() {
            return new StructureSpawnOverride(this.boundingBox, WeightedRandomList.m_146328_(this.spawns));
        }
    }
}

