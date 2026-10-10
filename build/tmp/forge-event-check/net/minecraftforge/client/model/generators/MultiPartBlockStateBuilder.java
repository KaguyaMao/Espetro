/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.MultimapBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.properties.Property
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;

public final class MultiPartBlockStateBuilder
implements IGeneratedBlockState {
    private final List<PartBuilder> parts = new ArrayList<PartBuilder>();
    private final Block owner;

    public MultiPartBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    public ConfiguredModel.Builder<PartBuilder> part() {
        return ConfiguredModel.builder(this);
    }

    MultiPartBlockStateBuilder addPart(PartBuilder part) {
        this.parts.add(part);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonArray variants = new JsonArray();
        for (PartBuilder part : this.parts) {
            variants.add((JsonElement)part.toJson());
        }
        JsonObject main = new JsonObject();
        main.add("multipart", (JsonElement)variants);
        return main;
    }

    private static JsonObject toJson(List<PartBuilder.ConditionGroup> conditions, boolean useOr) {
        JsonObject groupJson = new JsonObject();
        JsonArray innerGroupJson = new JsonArray();
        groupJson.add(useOr ? "OR" : "AND", (JsonElement)innerGroupJson);
        for (PartBuilder.ConditionGroup group : conditions) {
            innerGroupJson.add((JsonElement)group.toJson());
        }
        return groupJson;
    }

    private static JsonObject toJson(Multimap<Property<?>, Comparable<?>> conditions, boolean useOr) {
        JsonObject groupJson = new JsonObject();
        for (Map.Entry e : conditions.asMap().entrySet()) {
            StringBuilder activeString = new StringBuilder();
            for (Comparable val : (Collection)e.getValue()) {
                if (activeString.length() > 0) {
                    activeString.append("|");
                }
                activeString.append(((Property)e.getKey()).m_6940_(val));
            }
            groupJson.addProperty(((Property)e.getKey()).m_61708_(), activeString.toString());
        }
        if (useOr) {
            JsonArray innerWhen = new JsonArray();
            for (Map.Entry entry : groupJson.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.add((String)entry.getKey(), (JsonElement)entry.getValue());
                innerWhen.add((JsonElement)obj);
            }
            groupJson = new JsonObject();
            groupJson.add("OR", (JsonElement)innerWhen);
        }
        return groupJson;
    }

    public class PartBuilder {
        public BlockStateProvider.ConfiguredModelList models;
        public boolean useOr;
        public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        public final List<ConditionGroup> nestedConditionGroups = new ArrayList<ConditionGroup>();

        PartBuilder(BlockStateProvider.ConfiguredModelList models) {
            this.models = models;
        }

        public PartBuilder useOr() {
            this.useOr = true;
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T ... values) {
            Preconditions.checkNotNull(prop, (Object)"Property must not be null");
            Preconditions.checkNotNull(values, (Object)"Value list must not be null");
            Preconditions.checkArgument((values.length > 0 ? 1 : 0) != 0, (Object)"Value list must not be empty");
            Preconditions.checkArgument((!this.conditions.containsKey(prop) ? 1 : 0) != 0, (String)"Cannot set condition for property \"%s\" more than once", (Object)prop.m_61708_());
            Preconditions.checkArgument((boolean)this.canApplyTo(MultiPartBlockStateBuilder.this.owner), (String)"IProperty %s is not valid for the block %s", prop, (Object)MultiPartBlockStateBuilder.this.owner);
            Preconditions.checkState((boolean)this.nestedConditionGroups.isEmpty(), (Object)"Can't have normal conditions if there are already nested condition groups");
            this.conditions.putAll(prop, Arrays.asList(values));
            return this;
        }

        public final ConditionGroup nestedGroup() {
            Preconditions.checkState((boolean)this.conditions.isEmpty(), (Object)"Can't have nested condition groups if there are already normal conditions");
            ConditionGroup group = new ConditionGroup();
            this.nestedConditionGroups.add(group);
            return group;
        }

        public MultiPartBlockStateBuilder end() {
            return MultiPartBlockStateBuilder.this;
        }

        JsonObject toJson() {
            JsonObject out = new JsonObject();
            if (!this.conditions.isEmpty()) {
                out.add("when", (JsonElement)MultiPartBlockStateBuilder.toJson(this.conditions, this.useOr));
            } else if (!this.nestedConditionGroups.isEmpty()) {
                out.add("when", (JsonElement)MultiPartBlockStateBuilder.toJson(this.nestedConditionGroups, this.useOr));
            }
            out.add("apply", this.models.toJSON());
            return out;
        }

        public boolean canApplyTo(Block b) {
            return b.m_49965_().m_61092_().containsAll(this.conditions.keySet());
        }

        public class ConditionGroup {
            public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
            public final List<ConditionGroup> nestedConditionGroups = new ArrayList<ConditionGroup>();
            private ConditionGroup parent = null;
            public boolean useOr;

            @SafeVarargs
            public final <T extends Comparable<T>> ConditionGroup condition(Property<T> prop, T ... values) {
                Preconditions.checkNotNull(prop, (Object)"Property must not be null");
                Preconditions.checkNotNull(values, (Object)"Value list must not be null");
                Preconditions.checkArgument((values.length > 0 ? 1 : 0) != 0, (Object)"Value list must not be empty");
                Preconditions.checkArgument((!this.conditions.containsKey(prop) ? 1 : 0) != 0, (String)"Cannot set condition for property \"%s\" more than once", (Object)prop.m_61708_());
                Preconditions.checkArgument((boolean)PartBuilder.this.canApplyTo(MultiPartBlockStateBuilder.this.owner), (String)"IProperty %s is not valid for the block %s", prop, (Object)MultiPartBlockStateBuilder.this.owner);
                Preconditions.checkState((boolean)this.nestedConditionGroups.isEmpty(), (Object)"Can't have normal conditions if there are already nested condition groups");
                this.conditions.putAll(prop, Arrays.asList(values));
                return this;
            }

            public ConditionGroup nestedGroup() {
                Preconditions.checkState((boolean)this.conditions.isEmpty(), (Object)"Can't have nested condition groups if there are already normal conditions");
                ConditionGroup group = new ConditionGroup();
                group.parent = this;
                this.nestedConditionGroups.add(group);
                return group;
            }

            public ConditionGroup endNestedGroup() {
                if (this.parent == null) {
                    throw new IllegalStateException("This condition group is not nested, use end() instead");
                }
                return this.parent;
            }

            public PartBuilder end() {
                if (this.parent != null) {
                    throw new IllegalStateException("This is a nested condition group, use endNestedGroup() instead");
                }
                return PartBuilder.this;
            }

            public ConditionGroup useOr() {
                this.useOr = true;
                return this;
            }

            JsonObject toJson() {
                if (!this.conditions.isEmpty()) {
                    return MultiPartBlockStateBuilder.toJson(this.conditions, this.useOr);
                }
                if (!this.nestedConditionGroups.isEmpty()) {
                    return MultiPartBlockStateBuilder.toJson(this.nestedConditionGroups, this.useOr);
                }
                return new JsonObject();
            }
        }
    }
}

