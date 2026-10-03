/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  dev.architectury.registry.registries.DeferredRegister
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType
 */
package dev.latvian.mods.kubejs.level.gen.ruletest;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.level.gen.ruletest.AllMatchRuleTest;
import dev.latvian.mods.kubejs.level.gen.ruletest.AlwaysFalseRuleTest;
import dev.latvian.mods.kubejs.level.gen.ruletest.AnyMatchRuleTest;
import dev.latvian.mods.kubejs.level.gen.ruletest.InvertRuleTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public interface KubeJSRuleTests {
    public static final DeferredRegister<RuleTestType<?>> RULE_TEST_TYPES = DeferredRegister.create((String)"kubejs", (ResourceKey)Registries.f_256947_);
    public static final RuleTestType<InvertRuleTest> INVERT = KubeJSRuleTests.register("invert", InvertRuleTest.CODEC);
    public static final RuleTestType<AlwaysFalseRuleTest> ALWAYS_FALSE = KubeJSRuleTests.register("always_false", AlwaysFalseRuleTest.CODEC);
    public static final RuleTestType<AllMatchRuleTest> ALL_MATCH = KubeJSRuleTests.register("all_match", AllMatchRuleTest.CODEC);
    public static final RuleTestType<AnyMatchRuleTest> ANY_MATCH = KubeJSRuleTests.register("any_match", AnyMatchRuleTest.CODEC);

    public static <P extends RuleTest> RuleTestType<P> register(String id, Codec<P> codec) {
        RuleTestType type = () -> codec;
        RULE_TEST_TYPES.register(id, () -> type);
        return type;
    }

    public static void init() {
        RULE_TEST_TYPES.register();
    }
}

