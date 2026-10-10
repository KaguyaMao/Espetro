/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.valueproviders.ConstantInt
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.VerticalAnchor
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState
 *  net.minecraft.world.level.levelgen.placement.HeightRangePlacement
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 */
package dev.latvian.mods.kubejs.level.gen.properties;

import com.google.common.collect.Iterables;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class AddOreProperties {
    public ResourceLocation id = null;
    public GenerationStep.Decoration worldgenLayer = GenerationStep.Decoration.UNDERGROUND_ORES;
    public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
    public List<OreConfiguration.TargetBlockState> targets = new ArrayList<OreConfiguration.TargetBlockState>();
    public int size = 9;
    public float noSurface = 0.0f;
    public IntProvider count = ConstantInt.m_146483_((int)1);
    public int chance = 0;
    public boolean squared = false;
    public HeightRangePlacement height = HeightRangePlacement.m_191680_((VerticalAnchor)VerticalAnchor.m_158922_((int)0), (VerticalAnchor)VerticalAnchor.m_158922_((int)64));
    public int retrogen = 0;

    public void addTarget(RuleTest ruleTest, BlockStatePredicate targetState) {
        BlockState blockState = (BlockState)Iterables.getFirst(targetState.getBlockStates(), (Object)Blocks.f_50016_.m_49966_());
        if (blockState.m_60795_()) {
            ConsoleJS.STARTUP.error("Target block state is empty!");
        } else {
            this.targets.add(OreConfiguration.m_161021_((RuleTest)ruleTest, (BlockState)blockState));
        }
    }

    public AddOreProperties count(int c) {
        this.count = ConstantInt.m_146483_((int)c);
        return this;
    }

    public AddOreProperties count(int min, int max) {
        this.count = UniformInt.m_146622_((int)min, (int)max);
        return this;
    }

    public AddOreProperties count(IntProvider c) {
        this.count = c;
        return this;
    }

    public AddOreProperties chance(int c) {
        this.chance = c;
        return this;
    }

    public AddOreProperties size(int s) {
        this.size = s;
        return this;
    }

    public AddOreProperties squared() {
        return this.squared(true);
    }

    private AddOreProperties squared(boolean b) {
        this.squared = b;
        return this;
    }

    public AddOreProperties uniformHeight(int min, int max) {
        return this.uniformHeight(VerticalAnchor.m_158922_((int)min), VerticalAnchor.m_158922_((int)max));
    }

    public AddOreProperties triangleHeight(int min, int max) {
        return this.triangleHeight(VerticalAnchor.m_158922_((int)min), VerticalAnchor.m_158922_((int)max));
    }

    public AddOreProperties uniformHeight(VerticalAnchor absolute, VerticalAnchor absolute1) {
        this.height = HeightRangePlacement.m_191680_((VerticalAnchor)absolute, (VerticalAnchor)absolute1);
        return this;
    }

    public AddOreProperties triangleHeight(VerticalAnchor absolute, VerticalAnchor absolute1) {
        this.height = HeightRangePlacement.m_191692_((VerticalAnchor)absolute, (VerticalAnchor)absolute1);
        return this;
    }

    @Deprecated
    public VerticalAnchor aboveBottom(int y) {
        return VerticalAnchor.m_158930_((int)y);
    }

    @Deprecated
    public VerticalAnchor belowTop(int y) {
        return VerticalAnchor.m_158935_((int)y);
    }

    @Deprecated
    public VerticalAnchor bottom() {
        return VerticalAnchor.m_158921_();
    }

    @Deprecated
    public VerticalAnchor top() {
        return VerticalAnchor.m_158929_();
    }
}

