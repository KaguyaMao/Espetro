/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ObjectArrays
 *  com.google.gson.JsonObject
 *  net.minecraft.client.resources.model.BlockModelRotation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import org.jetbrains.annotations.Nullable;

public final class ConfiguredModel {
    public static final int DEFAULT_WEIGHT = 1;
    public final ModelFile model;
    public final int rotationX;
    public final int rotationY;
    public final boolean uvLock;
    public final int weight;

    private static IntStream validRotations() {
        return IntStream.range(0, 4).map(i -> i * 90);
    }

    public static ConfiguredModel[] allYRotations(ModelFile model, int x, boolean uvlock) {
        return ConfiguredModel.allYRotations(model, x, uvlock, 1);
    }

    public static ConfiguredModel[] allYRotations(ModelFile model, int x, boolean uvlock, int weight) {
        return (ConfiguredModel[])ConfiguredModel.validRotations().mapToObj(y -> new ConfiguredModel(model, x, y, uvlock, weight)).toArray(ConfiguredModel[]::new);
    }

    public static ConfiguredModel[] allRotations(ModelFile model, boolean uvlock) {
        return ConfiguredModel.allRotations(model, uvlock, 1);
    }

    public static ConfiguredModel[] allRotations(ModelFile model, boolean uvlock, int weight) {
        return (ConfiguredModel[])ConfiguredModel.validRotations().mapToObj(x -> ConfiguredModel.allYRotations(model, x, uvlock, weight)).flatMap(Arrays::stream).toArray(ConfiguredModel[]::new);
    }

    public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock, int weight) {
        Preconditions.checkNotNull((Object)model);
        this.model = model;
        ConfiguredModel.checkRotation(rotationX, rotationY);
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.uvLock = uvLock;
        ConfiguredModel.checkWeight(weight);
        this.weight = weight;
    }

    public ConfiguredModel(ModelFile model, int rotationX, int rotationY, boolean uvLock) {
        this(model, rotationX, rotationY, uvLock, 1);
    }

    public ConfiguredModel(ModelFile model) {
        this(model, 0, 0, false);
    }

    static void checkRotation(int rotationX, int rotationY) {
        Preconditions.checkArgument((BlockModelRotation.m_119153_((int)rotationX, (int)rotationY) != null ? 1 : 0) != 0, (String)"Invalid model rotation x=%d, y=%d", (int)rotationX, (int)rotationY);
    }

    static void checkWeight(int weight) {
        Preconditions.checkArgument((weight >= 1 ? 1 : 0) != 0, (String)"Model weight must be greater than or equal to 1. Found: %d", (int)weight);
    }

    JsonObject toJSON(boolean includeWeight) {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("model", this.model.getLocation().toString());
        if (this.rotationX != 0) {
            modelJson.addProperty("x", (Number)this.rotationX);
        }
        if (this.rotationY != 0) {
            modelJson.addProperty("y", (Number)this.rotationY);
        }
        if (this.uvLock) {
            modelJson.addProperty("uvlock", Boolean.valueOf(this.uvLock));
        }
        if (includeWeight && this.weight != 1) {
            modelJson.addProperty("weight", (Number)this.weight);
        }
        return modelJson;
    }

    public static Builder<?> builder() {
        return new Builder();
    }

    static Builder<VariantBlockStateBuilder> builder(VariantBlockStateBuilder outer, VariantBlockStateBuilder.PartialBlockstate state) {
        return new Builder<VariantBlockStateBuilder>(models -> outer.setModels(state, (ConfiguredModel)models), (List<ConfiguredModel>)ImmutableList.of());
    }

    static Builder<MultiPartBlockStateBuilder.PartBuilder> builder(MultiPartBlockStateBuilder outer) {
        return new Builder<MultiPartBlockStateBuilder.PartBuilder>(models -> {
            MultiPartBlockStateBuilder multiPartBlockStateBuilder = outer;
            Objects.requireNonNull(multiPartBlockStateBuilder);
            MultiPartBlockStateBuilder.PartBuilder ret = new MultiPartBlockStateBuilder.PartBuilder(multiPartBlockStateBuilder, new BlockStateProvider.ConfiguredModelList((ConfiguredModel)models));
            outer.addPart(ret);
            return ret;
        }, (List<ConfiguredModel>)ImmutableList.of());
    }

    public static class Builder<T> {
        private ModelFile model;
        @Nullable
        private final Function<ConfiguredModel[], T> callback;
        private final List<ConfiguredModel> otherModels;
        private int rotationX;
        private int rotationY;
        private boolean uvLock;
        private int weight = 1;

        Builder() {
            this(null, (List<ConfiguredModel>)ImmutableList.of());
        }

        Builder(@Nullable Function<ConfiguredModel[], T> callback, List<ConfiguredModel> otherModels) {
            this.callback = callback;
            this.otherModels = otherModels;
        }

        public Builder<T> modelFile(ModelFile model) {
            Preconditions.checkNotNull((Object)model, (Object)"Model must not be null");
            this.model = model;
            return this;
        }

        public Builder<T> rotationX(int value) {
            ConfiguredModel.checkRotation(value, this.rotationY);
            this.rotationX = value;
            return this;
        }

        public Builder<T> rotationY(int value) {
            ConfiguredModel.checkRotation(this.rotationX, value);
            this.rotationY = value;
            return this;
        }

        public Builder<T> uvLock(boolean value) {
            this.uvLock = value;
            return this;
        }

        public Builder<T> weight(int value) {
            ConfiguredModel.checkWeight(value);
            this.weight = value;
            return this;
        }

        public ConfiguredModel buildLast() {
            return new ConfiguredModel(this.model, this.rotationX, this.rotationY, this.uvLock, this.weight);
        }

        public ConfiguredModel[] build() {
            return (ConfiguredModel[])ObjectArrays.concat((Object[])this.otherModels.toArray(new ConfiguredModel[0]), (Object)this.buildLast());
        }

        public T addModel() {
            Preconditions.checkNotNull(this.callback, (Object)"Cannot use addModel() without an owning builder present");
            return this.callback.apply(this.build());
        }

        public Builder<T> nextModel() {
            return new Builder<T>(this.callback, Arrays.asList(this.build()));
        }
    }
}

