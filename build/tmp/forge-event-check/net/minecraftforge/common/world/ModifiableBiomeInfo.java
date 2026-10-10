/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$ClimateSettings
 *  net.minecraft.world.level.biome.BiomeGenerationSettings
 *  net.minecraft.world.level.biome.BiomeSpecialEffects
 *  net.minecraft.world.level.biome.MobSpawnSettings
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.world;

import java.util.List;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeSpecialEffectsBuilder;
import net.minecraftforge.common.world.ClimateSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifiableBiomeInfo {
    @NotNull
    private final BiomeInfo originalBiomeInfo;
    @Nullable
    private BiomeInfo modifiedBiomeInfo = null;

    public ModifiableBiomeInfo(@NotNull BiomeInfo originalBiomeInfo) {
        this.originalBiomeInfo = originalBiomeInfo;
    }

    @NotNull
    public BiomeInfo get() {
        return this.modifiedBiomeInfo == null ? this.originalBiomeInfo : this.modifiedBiomeInfo;
    }

    @NotNull
    public BiomeInfo getOriginalBiomeInfo() {
        return this.originalBiomeInfo;
    }

    @Nullable
    public BiomeInfo getModifiedBiomeInfo() {
        return this.modifiedBiomeInfo;
    }

    @ApiStatus.Internal
    public void applyBiomeModifiers(Holder<Biome> biome, List<BiomeModifier> biomeModifiers) {
        if (this.modifiedBiomeInfo != null) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Biome %s already modified", biome));
        }
        BiomeInfo original = this.getOriginalBiomeInfo();
        BiomeInfo.Builder builder = BiomeInfo.Builder.copyOf(original);
        for (BiomeModifier.Phase phase : BiomeModifier.Phase.values()) {
            for (BiomeModifier modifier : biomeModifiers) {
                modifier.modify(biome, phase, builder);
            }
        }
        this.modifiedBiomeInfo = builder.build();
    }

    public record BiomeInfo(Biome.ClimateSettings climateSettings, BiomeSpecialEffects effects, BiomeGenerationSettings generationSettings, MobSpawnSettings mobSpawnSettings) {

        public static class Builder {
            private ClimateSettingsBuilder climateSettings;
            private BiomeSpecialEffectsBuilder effects;
            private BiomeGenerationSettingsBuilder generationSettings;
            private MobSpawnSettingsBuilder mobSpawnSettings;

            public static Builder copyOf(BiomeInfo original) {
                ClimateSettingsBuilder climateBuilder = ClimateSettingsBuilder.copyOf(original.climateSettings());
                BiomeSpecialEffectsBuilder effectsBuilder = BiomeSpecialEffectsBuilder.copyOf(original.effects());
                BiomeGenerationSettingsBuilder generationBuilder = new BiomeGenerationSettingsBuilder(original.generationSettings());
                MobSpawnSettingsBuilder mobSpawnBuilder = new MobSpawnSettingsBuilder(original.mobSpawnSettings());
                return new Builder(climateBuilder, effectsBuilder, generationBuilder, mobSpawnBuilder);
            }

            private Builder(ClimateSettingsBuilder climateSettings, BiomeSpecialEffectsBuilder effects, BiomeGenerationSettingsBuilder generationSettings, MobSpawnSettingsBuilder mobSpawnSettings) {
                this.climateSettings = climateSettings;
                this.effects = effects;
                this.generationSettings = generationSettings;
                this.mobSpawnSettings = mobSpawnSettings;
            }

            public BiomeInfo build() {
                return new BiomeInfo(this.climateSettings.build(), this.effects.m_48018_(), this.generationSettings.m_255380_(), this.mobSpawnSettings.m_48381_());
            }

            public ClimateSettingsBuilder getClimateSettings() {
                return this.climateSettings;
            }

            public BiomeSpecialEffectsBuilder getSpecialEffects() {
                return this.effects;
            }

            public BiomeGenerationSettingsBuilder getGenerationSettings() {
                return this.generationSettings;
            }

            public MobSpawnSettingsBuilder getMobSpawnSettings() {
                return this.mobSpawnSettings;
            }
        }
    }
}

