/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 */
package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeBiomeModifiers {
    private ForgeBiomeModifiers() {
    }

    public record RemoveSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements BiomeModifier
    {
        @Override
        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == BiomeModifier.Phase.REMOVE && this.biomes.m_203333_(biome)) {
                MobSpawnSettingsBuilder spawnBuilder = builder.getMobSpawnSettings();
                for (MobCategory category : MobCategory.values()) {
                    List<MobSpawnSettings.SpawnerData> spawns = spawnBuilder.getSpawner(category);
                    spawns.removeIf(spawnerData -> this.entityTypes.m_203333_(ForgeRegistries.ENTITY_TYPES.getHolder(spawnerData.f_48404_).get()));
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return ForgeMod.REMOVE_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }
    }

    public record AddSpawnsBiomeModifier(HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier
    {
        public static AddSpawnsBiomeModifier singleSpawn(HolderSet<Biome> biomes, MobSpawnSettings.SpawnerData spawner) {
            return new AddSpawnsBiomeModifier(biomes, List.of(spawner));
        }

        @Override
        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == BiomeModifier.Phase.ADD && this.biomes.m_203333_(biome)) {
                MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
                for (MobSpawnSettings.SpawnerData spawner : this.spawners) {
                    EntityType type = spawner.f_48404_;
                    spawns.m_48376_(type.m_20674_(), spawner);
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return ForgeMod.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }
    }

    public record RemoveFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<GenerationStep.Decoration> steps) implements BiomeModifier
    {
        public static RemoveFeaturesBiomeModifier allSteps(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) {
            return new RemoveFeaturesBiomeModifier(biomes, features, EnumSet.allOf(GenerationStep.Decoration.class));
        }

        @Override
        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == BiomeModifier.Phase.REMOVE && this.biomes.m_203333_(biome)) {
                BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
                for (GenerationStep.Decoration step : this.steps) {
                    generationSettings.getFeatures(step).removeIf(arg_0 -> this.features.m_203333_(arg_0));
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return ForgeMod.REMOVE_FEATURES_BIOME_MODIFIER_TYPE.get();
        }
    }

    public record AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier
    {
        @Override
        public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == BiomeModifier.Phase.ADD && this.biomes.m_203333_(biome)) {
                BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
                this.features.forEach(holder -> generationSettings.m_255419_(this.step, (Holder)holder));
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return ForgeMod.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
        }
    }
}

