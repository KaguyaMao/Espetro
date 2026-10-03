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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class AnyMatchRuleTest
extends RuleTest {
    public static final Codec<AnyMatchRuleTest> CODEC = RuleTest.f_74307_.listOf().fieldOf("rules").xmap(AnyMatchRuleTest::new, t -> t.rules).codec();
    public final List<RuleTest> rules;

    public AnyMatchRuleTest() {
        this(new ArrayList<RuleTest>());
    }

    public AnyMatchRuleTest(List<RuleTest> rules) {
        this.rules = rules;
    }

    public boolean m_213865_(BlockState blockState, RandomSource random) {
        for (RuleTest test : this.rules) {
            if (!test.m_213865_(blockState, random)) continue;
            return true;
        }
        return this.rules.isEmpty();
    }

    protected RuleTestType<?> m_7319_() {
        return KubeJSRuleTests.ANY_MATCH;
    }
}

