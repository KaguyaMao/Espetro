/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.material.Fluids
 */
package dev.latvian.mods.kubejs.level.gen.properties;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluids;

public class AddLakeProperties {
    public ResourceLocation id = null;
    public GenerationStep.Decoration worldgenLayer = GenerationStep.Decoration.LAKES;
    public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
    public BlockStatePredicate fluid = BlockStatePredicate.of(Fluids.f_76193_.m_5613_().m_76145_().m_76188_());
    public BlockStatePredicate barrier = BlockStatePredicate.of(Blocks.f_50069_.m_49966_());
    public int chance = 20;
    public int retrogen = 0;

    public void setInner(BlockStatePredicate p) {
        this.fluid = p;
    }

    public void setOuter(BlockStatePredicate p) {
        this.barrier = p;
    }
}

