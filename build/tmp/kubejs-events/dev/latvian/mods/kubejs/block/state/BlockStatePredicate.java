/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  net.minecraft.Util
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block.state;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.level.gen.ruletest.AllMatchRuleTest;
import dev.latvian.mods.kubejs.level.gen.ruletest.AlwaysFalseRuleTest;
import dev.latvian.mods.kubejs.level.gen.ruletest.AnyMatchRuleTest;
import dev.latvian.mods.kubejs.level.gen.ruletest.InvertRuleTest;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Nullable;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public interface BlockStatePredicate
extends Predicate<BlockState>,
ReplacementMatch {
    public static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

    @Override
    public boolean test(BlockState var1);

    default public boolean testBlock(Block block) {
        return this.test(block.m_49966_());
    }

    @Nullable
    default public RuleTest asRuleTest() {
        return null;
    }

    public static BlockStatePredicate fromString(String s) {
        if (s.equals("*")) {
            return Simple.ALL;
        }
        if (s.equals("-")) {
            return Simple.NONE;
        }
        if (s.startsWith("#")) {
            return new TagMatch(Tags.block(new ResourceLocation(s.substring(1))));
        }
        if (s.indexOf(91) != -1) {
            BlockState state = UtilsJS.parseBlockState(s);
            if (state != Blocks.f_50016_.m_49966_()) {
                return new StateMatch(state);
            }
        } else {
            Block block = RegistryInfo.BLOCK.getValue(new ResourceLocation(s));
            if (block != Blocks.f_50016_) {
                return new BlockMatch(block);
            }
        }
        return Simple.NONE;
    }

    public static BlockStatePredicate of(Object o) {
        if (o == null || o == Simple.ALL) {
            return Simple.ALL;
        }
        if (o == Simple.NONE) {
            return Simple.NONE;
        }
        List<?> list = ListJS.orSelf(o);
        if (list.isEmpty()) {
            return Simple.NONE;
        }
        if (list.size() > 1) {
            ArrayList<BlockStatePredicate> predicates = new ArrayList<BlockStatePredicate>();
            for (Object o1 : list) {
                BlockStatePredicate p = BlockStatePredicate.of(o1);
                if (p == Simple.ALL) {
                    return Simple.ALL;
                }
                if (p == Simple.NONE) continue;
                predicates.add(p);
            }
            return predicates.isEmpty() ? Simple.NONE : (predicates.size() == 1 ? (BlockStatePredicate)predicates.get(0) : new OrMatch(predicates));
        }
        Map<?, ?> map = MapJS.of(o);
        if (map != null) {
            if (map.isEmpty()) {
                return Simple.ALL;
            }
            ArrayList<BlockStatePredicate> predicates = new ArrayList<BlockStatePredicate>();
            if (map.get("or") != null) {
                predicates.add(BlockStatePredicate.of(map.get("or")));
            }
            if (map.get("not") != null) {
                predicates.add(new NotMatch(BlockStatePredicate.of(map.get("not"))));
            }
            return new AndMatch(predicates);
        }
        return BlockStatePredicate.ofSingle(list.get(0));
    }

    public static RuleTest ruleTestOf(Object o) {
        BlockStatePredicate bsp;
        if (o instanceof RuleTest) {
            RuleTest rule = (RuleTest)o;
            return rule;
        }
        if (o instanceof BlockStatePredicate && (bsp = (BlockStatePredicate)o).asRuleTest() != null) {
            return bsp.asRuleTest();
        }
        return (RuleTest)Optional.ofNullable(NBTUtils.toTagCompound((Object)o)).map(tag -> RuleTest.f_74307_.parse((DynamicOps)NbtOps.f_128958_, tag)).flatMap(DataResult::result).or(() -> Optional.ofNullable(BlockStatePredicate.of(o).asRuleTest())).orElseThrow(() -> new IllegalArgumentException("Could not parse valid rule test from " + String.valueOf(o) + "!"));
    }

    private static BlockStatePredicate ofSingle(Object o) {
        if (o instanceof BlockStatePredicate) {
            BlockStatePredicate bsp = (BlockStatePredicate)o;
            return bsp;
        }
        if (o instanceof Block) {
            Block block = (Block)o;
            return new BlockMatch(block);
        }
        if (o instanceof BlockState) {
            BlockState state = (BlockState)o;
            return new StateMatch(state);
        }
        if (o instanceof TagKey) {
            TagKey tag = (TagKey)o;
            return new TagMatch((TagKey<Block>)tag);
        }
        Pattern pattern = UtilsJS.parseRegex(o);
        return pattern == null ? BlockStatePredicate.fromString(o.toString()) : new RegexMatch(pattern);
    }

    default public Collection<BlockState> getBlockStates() {
        HashSet<BlockState> states = new HashSet<BlockState>();
        for (BlockState state : UtilsJS.getAllBlockStates()) {
            if (!this.test(state)) continue;
            states.add(state);
        }
        return states;
    }

    default public Collection<Block> getBlocks() {
        HashSet<Block> blocks = new HashSet<Block>();
        for (BlockState state : this.getBlockStates()) {
            blocks.add(state.m_60734_());
        }
        return blocks;
    }

    default public Set<ResourceLocation> getBlockIds() {
        LinkedHashSet<ResourceLocation> set = new LinkedHashSet<ResourceLocation>();
        for (Block block : this.getBlocks()) {
            ResourceLocation blockId = RegistryInfo.BLOCK.getId(block);
            if (blockId == null) continue;
            set.add(blockId);
        }
        return set;
    }

    default public boolean check(List<OreConfiguration.TargetBlockState> targetStates) {
        for (OreConfiguration.TargetBlockState state : targetStates) {
            if (!this.test(state.f_161033_)) continue;
            return true;
        }
        return false;
    }

    public static enum Simple implements BlockStatePredicate
    {
        ALL(true),
        NONE(false);

        private final boolean match;

        private Simple(boolean match) {
            this.match = match;
        }

        @Override
        public boolean test(BlockState state) {
            return this.match;
        }

        @Override
        public boolean testBlock(Block block) {
            return this.match;
        }

        @Override
        public RuleTest asRuleTest() {
            return this.match ? AlwaysTrueTest.f_73954_ : AlwaysFalseRuleTest.INSTANCE;
        }

        @Override
        public Collection<BlockState> getBlockStates() {
            return this.match ? UtilsJS.getAllBlockStates() : List.of();
        }
    }

    public record TagMatch(TagKey<Block> tag) implements BlockStatePredicate
    {
        @Override
        public boolean test(BlockState state) {
            return state.m_204336_(this.tag);
        }

        @Override
        public boolean testBlock(Block block) {
            return block.m_204297_().m_203656_(this.tag);
        }

        @Override
        public Collection<Block> getBlocks() {
            return (Collection)Util.m_137469_(new LinkedHashSet(), set -> {
                for (Holder holder : BuiltInRegistries.f_256975_.m_206058_(this.tag)) {
                    set.add((Block)holder.m_203334_());
                }
            });
        }

        @Override
        public RuleTest asRuleTest() {
            return new TagMatchTest(this.tag);
        }
    }

    public record StateMatch(BlockState state) implements BlockStatePredicate
    {
        @Override
        public boolean test(BlockState s) {
            return this.state == s;
        }

        @Override
        public boolean testBlock(Block block) {
            return this.state.m_60734_() == block;
        }

        @Override
        public Collection<Block> getBlocks() {
            return Collections.singleton(this.state.m_60734_());
        }

        @Override
        public Collection<BlockState> getBlockStates() {
            return Collections.singleton(this.state);
        }

        @Override
        public Set<ResourceLocation> getBlockIds() {
            ResourceLocation blockId = RegistryInfo.BLOCK.getId(this.state.m_60734_());
            return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
        }

        @Override
        public RuleTest asRuleTest() {
            return new BlockStateMatchTest(this.state);
        }
    }

    public record BlockMatch(Block block) implements BlockStatePredicate
    {
        @Override
        public boolean test(BlockState state) {
            return state.m_60713_(this.block);
        }

        @Override
        public boolean testBlock(Block block) {
            return this.block == block;
        }

        @Override
        public Collection<Block> getBlocks() {
            return Collections.singleton(this.block);
        }

        @Override
        public Collection<BlockState> getBlockStates() {
            return this.block.m_49965_().m_61056_();
        }

        @Override
        public Set<ResourceLocation> getBlockIds() {
            ResourceLocation blockId = RegistryInfo.BLOCK.getId(this.block);
            return blockId == null ? Collections.emptySet() : Collections.singleton(blockId);
        }

        @Override
        public RuleTest asRuleTest() {
            return new BlockMatchTest(this.block);
        }
    }

    public record OrMatch(List<BlockStatePredicate> list) implements BlockStatePredicate
    {
        @Override
        public boolean test(BlockState state) {
            for (BlockStatePredicate predicate : this.list) {
                if (!predicate.test(state)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean testBlock(Block block) {
            for (BlockStatePredicate predicate : this.list) {
                if (!predicate.testBlock(block)) continue;
                return true;
            }
            return false;
        }

        @Override
        public Collection<Block> getBlocks() {
            HashSet<Block> set = new HashSet<Block>();
            for (BlockStatePredicate predicate : this.list) {
                set.addAll(predicate.getBlocks());
            }
            return set;
        }

        @Override
        public Collection<BlockState> getBlockStates() {
            HashSet<BlockState> set = new HashSet<BlockState>();
            for (BlockStatePredicate predicate : this.list) {
                set.addAll(predicate.getBlockStates());
            }
            return set;
        }

        @Override
        public Set<ResourceLocation> getBlockIds() {
            LinkedHashSet<ResourceLocation> set = new LinkedHashSet<ResourceLocation>();
            for (BlockStatePredicate predicate : this.list) {
                set.addAll(predicate.getBlockIds());
            }
            return set;
        }

        @Override
        public RuleTest asRuleTest() {
            AnyMatchRuleTest test = new AnyMatchRuleTest();
            for (BlockStatePredicate predicate : this.list) {
                test.rules.add(predicate.asRuleTest());
            }
            return test;
        }
    }

    public static final class NotMatch
    implements BlockStatePredicate {
        private final BlockStatePredicate predicate;
        private final Collection<BlockState> cachedStates;

        public NotMatch(BlockStatePredicate predicate) {
            this.predicate = predicate;
            this.cachedStates = new LinkedHashSet<BlockState>();
            for (Map.Entry<ResourceKey<Block>, Block> entry : RegistryInfo.BLOCK.entrySet()) {
                for (BlockState state : entry.getValue().m_49965_().m_61056_()) {
                    if (predicate.test(state)) continue;
                    this.cachedStates.add(state);
                }
            }
        }

        @Override
        public boolean test(BlockState state) {
            return !this.predicate.test(state);
        }

        @Override
        public boolean testBlock(Block block) {
            return !this.predicate.testBlock(block);
        }

        @Override
        public Collection<Block> getBlocks() {
            HashSet<Block> set = new HashSet<Block>();
            for (BlockState blockState : this.getBlockStates()) {
                set.add(blockState.m_60734_());
            }
            return set;
        }

        @Override
        public Collection<BlockState> getBlockStates() {
            return this.cachedStates;
        }

        @Override
        public Set<ResourceLocation> getBlockIds() {
            HashSet<ResourceLocation> set = new HashSet<ResourceLocation>();
            for (Block block : this.getBlocks()) {
                set.add(RegistryInfo.BLOCK.getId(block));
            }
            return set;
        }

        @Override
        public RuleTest asRuleTest() {
            return new InvertRuleTest(this.predicate.asRuleTest());
        }
    }

    public static final class AndMatch
    implements BlockStatePredicate {
        private final List<BlockStatePredicate> list;
        private final Collection<BlockState> cachedStates;

        public AndMatch(List<BlockStatePredicate> list) {
            this.list = list;
            this.cachedStates = new LinkedHashSet<BlockState>();
            for (Map.Entry<ResourceKey<Block>, Block> entry : RegistryInfo.BLOCK.entrySet()) {
                for (BlockState state : entry.getValue().m_49965_().m_61056_()) {
                    boolean match = true;
                    for (BlockStatePredicate predicate : list) {
                        if (predicate.test(state)) continue;
                        match = false;
                        break;
                    }
                    if (!match) continue;
                    this.cachedStates.add(state);
                }
            }
        }

        @Override
        public boolean test(BlockState state) {
            for (BlockStatePredicate predicate : this.list) {
                if (predicate.test(state)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean testBlock(Block block) {
            for (BlockStatePredicate predicate : this.list) {
                if (predicate.testBlock(block)) continue;
                return false;
            }
            return true;
        }

        @Override
        public Collection<Block> getBlocks() {
            HashSet<Block> set = new HashSet<Block>();
            for (BlockState blockState : this.getBlockStates()) {
                set.add(blockState.m_60734_());
            }
            return set;
        }

        @Override
        public Collection<BlockState> getBlockStates() {
            return this.cachedStates;
        }

        @Override
        public RuleTest asRuleTest() {
            AllMatchRuleTest test = new AllMatchRuleTest();
            for (BlockStatePredicate predicate : this.list) {
                test.rules.add(predicate.asRuleTest());
            }
            return test;
        }
    }

    public static final class RegexMatch
    implements BlockStatePredicate {
        public final Pattern pattern;
        private final LinkedHashSet<Block> matchedBlocks;

        public RegexMatch(Pattern p) {
            this.pattern = p;
            this.matchedBlocks = new LinkedHashSet();
            for (Map.Entry<ResourceKey<Block>, Block> entry : RegistryInfo.BLOCK.entrySet()) {
                Block block = entry.getValue();
                if (this.matchedBlocks.contains(block) || !this.pattern.matcher(RegistryInfo.BLOCK.getId(block).toString()).find()) continue;
                this.matchedBlocks.add(block);
            }
        }

        @Override
        public boolean test(BlockState state) {
            return this.matchedBlocks.contains(state.m_60734_());
        }

        @Override
        public boolean testBlock(Block block) {
            return this.matchedBlocks.contains(block);
        }

        @Override
        public Collection<Block> getBlocks() {
            return this.matchedBlocks;
        }

        @Override
        public RuleTest asRuleTest() {
            AnyMatchRuleTest test = new AnyMatchRuleTest();
            for (Block block : this.matchedBlocks) {
                test.rules.add((RuleTest)new BlockMatchTest(block));
            }
            return test;
        }
    }
}

