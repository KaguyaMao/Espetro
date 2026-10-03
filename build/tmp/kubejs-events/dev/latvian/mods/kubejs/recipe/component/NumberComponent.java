/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.ScriptRuntime
 *  dev.latvian.mods.rhino.Wrapper
 *  net.minecraft.util.Mth
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.util.Mth;

public interface NumberComponent<T extends Number>
extends RecipeComponent<T> {
    public static final IntRange INT = NumberComponent.intRange(0, Integer.MAX_VALUE);
    public static final LongRange LONG = NumberComponent.longRange(0L, Long.MAX_VALUE);
    public static final FloatRange FLOAT = NumberComponent.floatRange(0.0f, Float.POSITIVE_INFINITY);
    public static final DoubleRange DOUBLE = NumberComponent.doubleRange(0.0, Double.POSITIVE_INFINITY);
    public static final IntRange ANY_INT = NumberComponent.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final LongRange ANY_LONG = NumberComponent.longRange(Long.MIN_VALUE, Long.MAX_VALUE);
    public static final FloatRange ANY_FLOAT = NumberComponent.floatRange(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    public static final DoubleRange ANY_DOUBLE = NumberComponent.doubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final DynamicRecipeComponent DYNAMIC_INT = new DynamicRecipeComponent(TypeDescJS.object().add("min", TypeDescJS.NUMBER).add("max", TypeDescJS.NUMBER), (cx, scope, args) -> {
        int min = ScriptRuntime.toInt32((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("min", 0)));
        int max = ScriptRuntime.toInt32((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("max", Integer.MAX_VALUE)));
        return NumberComponent.intRange(min, max);
    });
    public static final DynamicRecipeComponent DYNAMIC_LONG = new DynamicRecipeComponent(TypeDescJS.object().add("min", TypeDescJS.NUMBER).add("max", TypeDescJS.NUMBER), (cx, scope, args) -> {
        double min = ScriptRuntime.toNumber((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("min", 0)));
        double max = ScriptRuntime.toNumber((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("max", Long.MAX_VALUE)));
        return NumberComponent.longRange((long)min, (long)max);
    });
    public static final DynamicRecipeComponent DYNAMIC_FLOAT = new DynamicRecipeComponent(TypeDescJS.object().add("min", TypeDescJS.NUMBER).add("max", TypeDescJS.NUMBER), (cx, scope, args) -> {
        double min = ScriptRuntime.toNumber((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("min", Float.valueOf(0.0f))));
        double max = ScriptRuntime.toNumber((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("max", Float.valueOf(Float.MAX_VALUE))));
        return NumberComponent.floatRange((float)min, (float)max);
    });
    public static final DynamicRecipeComponent DYNAMIC_DOUBLE = new DynamicRecipeComponent(TypeDescJS.object().add("min", TypeDescJS.NUMBER).add("max", TypeDescJS.NUMBER), (cx, scope, args) -> {
        double min = ScriptRuntime.toNumber((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("min", 0)));
        double max = ScriptRuntime.toNumber((Context)cx, (Object)Wrapper.unwrapped((Object)args.getOrDefault("max", Double.MAX_VALUE)));
        return NumberComponent.doubleRange(min, max);
    });

    public static IntRange intRange(int min, int max) {
        return new IntRange(min, max);
    }

    public static LongRange longRange(long min, long max) {
        return new LongRange(min, max);
    }

    public static FloatRange floatRange(float min, float max) {
        return new FloatRange(min, max);
    }

    public static DoubleRange doubleRange(double min, double max) {
        return new DoubleRange(min, max);
    }

    private static Number numberOf(Object from) {
        if (from instanceof Number) {
            Number n = (Number)from;
            return n;
        }
        if (from instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)from;
            return json.getAsNumber();
        }
        if (from instanceof CharSequence) {
            return Double.parseDouble(from.toString());
        }
        throw new IllegalStateException("Expected a number!");
    }

    @Override
    default public String componentType() {
        return "number";
    }

    @Override
    default public Class<?> componentClass() {
        return Number.class;
    }

    @Override
    default public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return TypeDescJS.NUMBER;
    }

    @Override
    default public boolean hasPriority(RecipeJS recipe, Object from) {
        JsonPrimitive json;
        return from instanceof Number || from instanceof JsonPrimitive && (json = (JsonPrimitive)from).isNumber();
    }

    public record IntRange(int min, int max) implements NumberComponent<Integer>
    {
        @Override
        public Class<?> componentClass() {
            return Integer.class;
        }

        public JsonPrimitive write(RecipeJS recipe, Integer value) {
            return new JsonPrimitive((Number)value);
        }

        @Override
        public Integer read(RecipeJS recipe, Object from) {
            return Mth.m_14045_((int)NumberComponent.numberOf(from).intValue(), (int)this.min, (int)this.max);
        }

        public IntRange min(int min) {
            return new IntRange(min, this.max);
        }

        public IntRange max(int max) {
            return new IntRange(this.min, max);
        }

        @Override
        public String toString() {
            return "int";
        }
    }

    public record LongRange(long min, long max) implements NumberComponent<Long>
    {
        @Override
        public Class<?> componentClass() {
            return Long.class;
        }

        public JsonPrimitive write(RecipeJS recipe, Long value) {
            return new JsonPrimitive((Number)value);
        }

        @Override
        public Long read(RecipeJS recipe, Object from) {
            long val = NumberComponent.numberOf(from).longValue();
            return val < this.min ? this.min : Math.min(val, this.max);
        }

        public LongRange min(long min) {
            return new LongRange(min, this.max);
        }

        public LongRange max(long max) {
            return new LongRange(this.min, max);
        }

        @Override
        public String toString() {
            return "long";
        }
    }

    public record FloatRange(float min, float max) implements NumberComponent<Float>
    {
        @Override
        public Class<?> componentClass() {
            return Float.class;
        }

        public JsonPrimitive write(RecipeJS recipe, Float value) {
            return new JsonPrimitive((Number)value);
        }

        @Override
        public Float read(RecipeJS recipe, Object from) {
            return Float.valueOf(Mth.m_14036_((float)NumberComponent.numberOf(from).floatValue(), (float)this.min, (float)this.max));
        }

        public FloatRange min(float min) {
            return new FloatRange(min, this.max);
        }

        public FloatRange max(float max) {
            return new FloatRange(this.min, max);
        }

        @Override
        public String toString() {
            return "float";
        }
    }

    public record DoubleRange(double min, double max) implements NumberComponent<Double>
    {
        @Override
        public Class<?> componentClass() {
            return Double.class;
        }

        public JsonPrimitive write(RecipeJS recipe, Double value) {
            return new JsonPrimitive((Number)value);
        }

        @Override
        public Double read(RecipeJS recipe, Object from) {
            return Mth.m_14008_((double)NumberComponent.numberOf(from).doubleValue(), (double)this.min, (double)this.max);
        }

        public DoubleRange min(double min) {
            return new DoubleRange(min, this.max);
        }

        public DoubleRange max(double max) {
            return new DoubleRange(this.min, max);
        }

        @Override
        public String toString() {
            return "double";
        }
    }
}

