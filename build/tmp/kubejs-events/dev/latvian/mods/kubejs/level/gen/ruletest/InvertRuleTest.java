/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType
 */
package dev.latvian.mods.kubejs.level.gen.ruletest;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.level.gen.ruletest.KubeJSRuleTests;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class InvertRuleTest
extends RuleTest {
    public static final Codec<InvertRuleTest> CODEC = RuleTest.f_74307_.fieldOf("original").xmap(InvertRuleTest::new, t -> t.original).codec();
    public final RuleTest original;

    public InvertRuleTest(RuleTest t) {
        this.original = t;
    }

    public boolean m_213865_(BlockState blockState, RandomSource random) {
        return !this.original.m_213865_(blockState, random);
    }

    protected RuleTestType<?> m_7319_() {
        return KubeJSRuleTests.INVERT;
    }
}

