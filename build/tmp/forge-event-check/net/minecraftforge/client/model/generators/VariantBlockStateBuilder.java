/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import org.jetbrains.annotations.Nullable;

public class VariantBlockStateBuilder
implements IGeneratedBlockState {
    private final Block owner;
    private final Map<PartialBlockstate, BlockStateProvider.ConfiguredModelList> models = new LinkedHashMap<PartialBlockstate, BlockStateProvider.ConfiguredModelList>();
    private final Set<BlockState> coveredStates = new HashSet<BlockState>();

    VariantBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    public Map<PartialBlockstate, BlockStateProvider.ConfiguredModelList> getModels() {
        return this.models;
    }

    public Block getOwner() {
        return this.owner;
    }

    @Override
    public JsonObject toJson() {
        ArrayList missingStates = Lists.newArrayList((Iterable)this.owner.m_49965_().m_61056_());
        missingStates.removeAll(this.coveredStates);
        Preconditions.checkState((boolean)missingStates.isEmpty(), (String)"Blockstate for block %s does not cover all states. Missing: %s", (Object)this.owner, (Object)missingStates);
        JsonObject variants = new JsonObject();
        this.getModels().entrySet().stream().sorted(Map.Entry.comparingByKey(PartialBlockstate.comparingByProperties())).forEach(entry -> variants.add(((PartialBlockstate)entry.getKey()).toString(), ((BlockStateProvider.ConfiguredModelList)entry.getValue()).toJSON()));
        JsonObject main = new JsonObject();
        main.add("variants", (JsonElement)variants);
        return main;
    }

    public VariantBlockStateBuilder addModels(PartialBlockstate state, ConfiguredModel ... models) {
        Preconditions.checkNotNull((Object)state, (Object)"state must not be null");
        Preconditions.checkArgument((models.length > 0 ? 1 : 0) != 0, (Object)"Cannot set models to empty array");
        Preconditions.checkArgument((state.getOwner() == this.owner ? 1 : 0) != 0, (String)"Cannot set models for a different block. Found: %s, Current: %s", (Object)state.getOwner(), (Object)this.owner);
        if (!this.models.containsKey(state)) {
            Preconditions.checkArgument((boolean)this.disjointToAll(state), (Object)"Cannot set models for a state for which a partial match has already been configured");
            this.models.put(state, new BlockStateProvider.ConfiguredModelList(models));
            for (BlockState fullState : this.owner.m_49965_().m_61056_()) {
                if (!state.test(fullState)) continue;
                this.coveredStates.add(fullState);
            }
        } else {
            this.models.compute(state, ($, cml) -> cml.append(models));
        }
        return this;
    }

    public VariantBlockStateBuilder setModels(PartialBlockstate state, ConfiguredModel ... model) {
        Preconditions.checkArgument((!this.models.containsKey(state) ? 1 : 0) != 0, (String)"Cannot set models for a state that has already been configured: %s", (Object)state);
        this.addModels(state, model);
        return this;
    }

    private boolean disjointToAll(PartialBlockstate newState) {
        return this.coveredStates.stream().noneMatch(newState);
    }

    public PartialBlockstate partialState() {
        return new PartialBlockstate(this.owner, this);
    }

    public VariantBlockStateBuilder forAllStates(Function<BlockState, ConfiguredModel[]> mapper) {
        return this.forAllStatesExcept(mapper, new Property[0]);
    }

    public VariantBlockStateBuilder forAllStatesExcept(Function<BlockState, ConfiguredModel[]> mapper, Property<?> ... ignored) {
        HashSet<PartialBlockstate> seen = new HashSet<PartialBlockstate>();
        for (BlockState fullState : this.owner.m_49965_().m_61056_()) {
            LinkedHashMap propertyValues = Maps.newLinkedHashMap((Map)fullState.m_61148_());
            for (Property<?> p : ignored) {
                propertyValues.remove(p);
            }
            PartialBlockstate partialState = new PartialBlockstate(this.owner, propertyValues, this);
            if (!seen.add(partialState)) continue;
            this.setModels(partialState, mapper.apply(fullState));
        }
        return this;
    }

    public static class PartialBlockstate
    implements Predicate<BlockState> {
        private final Block owner;
        private final SortedMap<Property<?>, Comparable<?>> setStates;
        @Nullable
        private final VariantBlockStateBuilder outerBuilder;

        PartialBlockstate(Block owner, @Nullable VariantBlockStateBuilder outerBuilder) {
            this(owner, (Map<Property<?>, Comparable<?>>)ImmutableMap.of(), outerBuilder);
        }

        PartialBlockstate(Block owner, Map<Property<?>, Comparable<?>> setStates, @Nullable VariantBlockStateBuilder outerBuilder) {
            this.owner = owner;
            this.outerBuilder = outerBuilder;
            for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
                Property<?> prop = entry.getKey();
                Comparable<?> value = entry.getValue();
                Preconditions.checkArgument((boolean)owner.m_49965_().m_61092_().contains(prop), (String)"Property %s not found on block %s", entry, (Object)this.owner);
                Preconditions.checkArgument((boolean)prop.m_6908_().contains(value), (String)"%s is not a valid value for %s", value, prop);
            }
            this.setStates = Maps.newTreeMap(Comparator.comparing(Property::m_61708_));
            this.setStates.putAll(setStates);
        }

        public <T extends Comparable<T>> PartialBlockstate with(Property<T> prop, T value) {
            Preconditions.checkArgument((!this.setStates.containsKey(prop) ? 1 : 0) != 0, (String)"Property %s has already been set", prop);
            HashMap newState = new HashMap(this.setStates);
            newState.put(prop, value);
            return new PartialBlockstate(this.owner, newState, this.outerBuilder);
        }

        private void checkValidOwner() {
            Preconditions.checkNotNull((Object)this.outerBuilder, (Object)"Partial blockstate must have a valid owner to perform this action");
        }

        public ConfiguredModel.Builder<VariantBlockStateBuilder> modelForState() {
            this.checkValidOwner();
            return ConfiguredModel.builder(this.outerBuilder, this);
        }

        public PartialBlockstate addModels(ConfiguredModel ... models) {
            this.checkValidOwner();
            this.outerBuilder.addModels(this, models);
            return this;
        }

        public VariantBlockStateBuilder setModels(ConfiguredModel ... models) {
            this.checkValidOwner();
            return this.outerBuilder.setModels(this, models);
        }

        public PartialBlockstate partialState() {
            this.checkValidOwner();
            return this.outerBuilder.partialState();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            PartialBlockstate that = (PartialBlockstate)o;
            return this.owner.equals(that.owner) && this.setStates.equals(that.setStates);
        }

        public int hashCode() {
            return Objects.hash(this.owner, this.setStates);
        }

        public Block getOwner() {
            return this.owner;
        }

        public SortedMap<Property<?>, Comparable<?>> getSetStates() {
            return this.setStates;
        }

        @Override
        public boolean test(BlockState blockState) {
            if (blockState.m_60734_() != this.getOwner()) {
                return false;
            }
            for (Map.Entry<Property<?>, Comparable<?>> entry : this.setStates.entrySet()) {
                if (blockState.m_61143_(entry.getKey()) == entry.getValue()) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder ret = new StringBuilder();
            for (Map.Entry<Property<?>, Comparable<?>> entry : this.setStates.entrySet()) {
                if (ret.length() > 0) {
                    ret.append(',');
                }
                ret.append(entry.getKey().m_61708_()).append('=').append(entry.getKey().m_6940_(entry.getValue()));
            }
            return ret.toString();
        }

        public static Comparator<PartialBlockstate> comparingByProperties() {
            return (s1, s2) -> {
                TreeSet propUniverse = new TreeSet(s1.getSetStates().comparator().reversed());
                propUniverse.addAll(s1.getSetStates().keySet());
                propUniverse.addAll(s2.getSetStates().keySet());
                for (Property property : propUniverse) {
                    int cmp;
                    Comparable val1 = (Comparable)s1.getSetStates().get(property);
                    Comparable val2 = (Comparable)s2.getSetStates().get(property);
                    if (val1 == null && val2 != null) {
                        return -1;
                    }
                    if (val2 == null && val1 != null) {
                        return 1;
                    }
                    if (val1 == null || val2 == null || (cmp = val1.compareTo(val2)) == 0) continue;
                    return cmp;
                }
                return 0;
            };
        }
    }
}

