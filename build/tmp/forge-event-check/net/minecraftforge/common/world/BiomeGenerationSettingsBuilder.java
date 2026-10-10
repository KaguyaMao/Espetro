/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.biome.BiomeGenerationSettings
 *  net.minecraft.world.level.biome.BiomeGenerationSettings$PlainBuilder
 *  net.minecraft.world.level.levelgen.GenerationStep$Carving
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 */
package net.minecraftforge.common.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeGenerationSettingsBuilder
extends BiomeGenerationSettings.PlainBuilder {
    public BiomeGenerationSettingsBuilder(BiomeGenerationSettings orig) {
        orig.getCarvingStages().forEach(k -> {
            this.f_254678_.put(k, new ArrayList());
            orig.m_204187_(k).forEach(v -> ((List)this.f_254678_.get(k)).add(v));
        });
        orig.m_47818_().forEach(l -> {
            ArrayList featureList = new ArrayList();
            l.forEach(featureList::add);
            this.f_254648_.add(featureList);
        });
    }

    public List<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage) {
        this.m_255276_(stage.ordinal());
        return (List)this.f_254648_.get(stage.ordinal());
    }

    public List<Holder<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving stage) {
        return this.f_254678_.computeIfAbsent(stage, key -> new ArrayList());
    }
}

