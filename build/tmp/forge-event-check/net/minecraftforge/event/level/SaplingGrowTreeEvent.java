/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

@Event.HasResult
public class SaplingGrowTreeEvent
extends LevelEvent {
    private final RandomSource randomSource;
    private final BlockPos pos;
    @Nullable
    private Holder<ConfiguredFeature<?, ?>> feature;

    public SaplingGrowTreeEvent(LevelAccessor level, RandomSource randomSource, BlockPos pos, @Nullable Holder<ConfiguredFeature<?, ?>> feature) {
        super(level);
        this.randomSource = randomSource;
        this.pos = pos;
        this.feature = feature;
    }

    public RandomSource getRandomSource() {
        return this.randomSource;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Nullable
    public Holder<ConfiguredFeature<?, ?>> getFeature() {
        return this.feature;
    }

    public void setFeature(@Nullable Holder<ConfiguredFeature<?, ?>> feature) {
        this.feature = feature;
    }

    public void setFeature(ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        this.feature = this.getLevel().m_9598_().m_175515_(Registries.f_256911_).m_203636_(featureKey).orElse(null);
    }
}

