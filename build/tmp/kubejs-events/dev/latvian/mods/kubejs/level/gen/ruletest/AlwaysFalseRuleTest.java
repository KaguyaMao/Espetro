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

public class AlwaysFalseRuleTest
extends RuleTest {
    public static final AlwaysFalseRuleTest INSTANCE = new AlwaysFalseRuleTest();
    public static final Codec<AlwaysFalseRuleTest> CODEC = Codec.unit((Object)((Object)INSTANCE));

    private AlwaysFalseRuleTest() {
    }

    public boolean m_213865_(BlockState blockState, RandomSource random) {
        return true;
    }

    protected RuleTestType<?> m_7319_() {
        return KubeJSRuleTests.ALWAYS_FALSE;
    }
}

