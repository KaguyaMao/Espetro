/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  dev.architectury.hooks.level.biome.BiomeProperties$Mutable
 *  dev.architectury.registry.level.biome.BiomeModifications
 *  dev.architectury.registry.level.biome.BiomeModifications$BiomeContext
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.level.gen;

import com.google.common.collect.ImmutableSet;
import dev.architectury.hooks.level.biome.BiomeProperties;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.properties.RemoveOresProperties;
import dev.latvian.mods.kubejs.level.gen.properties.RemoveSpawnsProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class RemoveWorldgenEventJS
extends StartupEventJS {
    protected static boolean checkTree(ConfiguredFeature<?, ?> configuredFeature, Predicate<FeatureConfiguration> predicate) {
        return predicate.test(configuredFeature.f_65378_()) || configuredFeature.f_65378_().m_7817_().anyMatch(cf -> RemoveWorldgenEventJS.checkTree(cf, predicate));
    }

    private void removeFeature(BiomeFilter filter, GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
        BiomeModifications.replaceProperties((Predicate)filter, (ctx, properties) -> {
            ArrayList removedFeatures = new ArrayList();
            for (Holder feature : properties.getGenerationProperties().getFeatures(decoration)) {
                if (!RemoveWorldgenEventJS.checkTree((ConfiguredFeature)((PlacedFeature)feature.m_203334_()).f_191775_().m_203334_(), predicate)) continue;
                feature.m_203543_().ifPresentOrElse(key -> {
                    ConsoleJS.STARTUP.debug("Removing feature %s from generation step %s in biome %s".formatted(key, decoration.name().toLowerCase(), ctx.getKey()));
                    removedFeatures.add(feature);
                }, () -> ConsoleJS.STARTUP.warn("Feature %s was not removed since it was not found in the registry!".formatted(feature.m_203334_())));
            }
            for (Holder feature : removedFeatures) {
                properties.getGenerationProperties().removeFeature(decoration, (ResourceKey)feature.m_203543_().get());
            }
        });
    }

    private void removeSpawn(BiomeFilter filter, BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
        BiomeModifications.replaceProperties((Predicate)filter, (ctx, properties) -> properties.getSpawnProperties().removeSpawns(predicate));
    }

    public void printFeatures() {
        this.printFeatures(null);
    }

    public void printFiltered() {
        this.printFiltered(null);
    }

    public void printFeatures(@Nullable GenerationStep.Decoration type) {
        this.printFeatures(type, BiomeFilter.ALWAYS_TRUE);
    }

    public void printFiltered(@Nullable GenerationStep.Decoration type) {
        this.printFiltered(type, BiomeFilter.ALWAYS_TRUE);
    }

    public void printFeatures(@Nullable GenerationStep.Decoration type, BiomeFilter filter) {
        this.printFeaturesForType(type, filter, false);
    }

    public void printFiltered(@Nullable GenerationStep.Decoration type, BiomeFilter filter) {
        this.printFeaturesForType(type, filter, true);
    }

    public void printFeaturesForType(final @Nullable GenerationStep.Decoration type, BiomeFilter filter, boolean afterRemoval) {
        if (type == null) {
            for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
                this.printFeaturesForType(step, filter, afterRemoval);
            }
        } else {
            BiConsumer<BiomeModifications.BiomeContext, BiomeProperties.Mutable> printer = new BiConsumer<BiomeModifications.BiomeContext, BiomeProperties.Mutable>(){
                boolean called = false;

                @Override
                public void accept(BiomeModifications.BiomeContext ctx, BiomeProperties.Mutable properties) {
                    if (this.called) {
                        return;
                    }
                    this.called = true;
                    Optional biome = ctx.getKey();
                    Iterable features = properties.getGenerationProperties().getFeatures(type);
                    ConsoleJS.STARTUP.info("Features with type '%s' in biome '%s':".formatted(type.name().toLowerCase(), biome));
                    MutableInt unknown = new MutableInt(0);
                    for (Holder feature : features) {
                        feature.m_203543_().ifPresentOrElse(key -> ConsoleJS.STARTUP.info("- " + String.valueOf(key)), () -> ((MutableInt)unknown).increment());
                    }
                    if (unknown.intValue() > 0) {
                        ConsoleJS.STARTUP.info("- " + String.valueOf(unknown) + " features with unknown id");
                    }
                }
            };
            if (afterRemoval) {
                BiomeModifications.postProcessProperties((Predicate)filter, (BiConsumer)printer);
            } else {
                BiomeModifications.removeProperties((Predicate)filter, (BiConsumer)printer);
            }
        }
    }

    public void removeFeatureById(BiomeFilter filter, GenerationStep.Decoration decoration, ResourceLocation[] ids) {
        BiomeModifications.replaceProperties((Predicate)filter, (ctx, properties) -> Stream.of(ids).map(id -> ResourceKey.m_135785_((ResourceKey)Registries.f_256988_, (ResourceLocation)id)).forEach(id -> properties.getGenerationProperties().removeFeature(decoration, id)));
    }

    public void removeFeatureById(GenerationStep.Decoration type, ResourceLocation[] ids) {
        this.removeFeatureById(BiomeFilter.ALWAYS_TRUE, type, ids);
    }

    public void removeAllFeatures(BiomeFilter filter, GenerationStep.Decoration type) {
        this.removeFeature(filter, type, configuredFeature -> true);
    }

    public void removeAllFeatures(BiomeFilter filter) {
        for (GenerationStep.Decoration decoration : GenerationStep.Decoration.values()) {
            this.removeAllFeatures(filter, decoration);
        }
    }

    public void removeAllFeatures() {
        this.removeAllFeatures(BiomeFilter.ALWAYS_TRUE);
    }

    public void removeOres(Consumer<RemoveOresProperties> p) {
        RemoveOresProperties properties = new RemoveOresProperties();
        p.accept(properties);
        this.removeFeature(properties.biomes, properties.worldgenLayer, fc -> {
            if (fc instanceof OreConfiguration) {
                OreConfiguration ore = (OreConfiguration)fc;
                return properties.blocks.check(ore.f_161005_);
            }
            if (fc instanceof ReplaceBlockConfiguration) {
                ReplaceBlockConfiguration rb = (ReplaceBlockConfiguration)fc;
                return properties.blocks.check(rb.f_161083_);
            }
            return false;
        });
    }

    public void printSpawns(@Nullable MobCategory category) {
        BiomeModifications.addProperties((ctx, properties) -> {
            Optional biome = ctx.getKey();
            Map spawns = properties.getSpawnProperties().getSpawners();
            ImmutableSet cats = category == null ? spawns.keySet() : ImmutableSet.of((Object)category);
            for (MobCategory cat : cats) {
                ConsoleJS.STARTUP.info("Mob spawns with type '%s' in biome '%s':".formatted(cat.m_21607_(), biome));
                for (MobSpawnSettings.SpawnerData data : (List)spawns.get(cat)) {
                    ConsoleJS.STARTUP.info("- " + data.toString());
                }
            }
        });
    }

    public void printSpawns() {
        this.printSpawns(null);
    }

    public void removeSpawns(Consumer<RemoveSpawnsProperties> p) {
        RemoveSpawnsProperties properties = new RemoveSpawnsProperties();
        p.accept(properties);
        this.removeSpawn(properties.biomes, properties.mobs);
    }

    public void removeAllSpawns() {
        this.removeSpawn(BiomeFilter.ALWAYS_TRUE, (mobCategory, spawnerData) -> true);
    }
}

