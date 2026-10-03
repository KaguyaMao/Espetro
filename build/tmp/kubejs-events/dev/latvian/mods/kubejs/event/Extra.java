/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Extra {
    public static final Extra STRING = new Extra().transformer(Extra::toString);
    public static final Extra REQUIRES_STRING = STRING.copy().required();
    public static final Extra ID = new Extra().transformer(Extra::toResourceLocation);
    public static final Extra REQUIRES_ID = ID.copy().required();
    public static final Extra REGISTRY = new Extra().transformer(Extra::toRegistryKey).identity();
    public static final Extra REQUIRES_REGISTRY = REGISTRY.copy().required();
    public Transformer transformer = Transformer.IDENTITY;
    public boolean identity = false;
    public boolean required = false;
    public Predicate<Object> validator = UtilsJS.ALWAYS_TRUE;
    public Transformer toString = Transformer.IDENTITY;
    public Function<DescriptionContext, TypeDescJS> describeType = context -> TypeDescJS.STRING;

    private static String toString(Object object) {
        if (object == null) {
            return null;
        }
        String s = object.toString();
        return s.isBlank() ? null : s;
    }

    private static ResourceLocation toResourceLocation(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ResourceLocation) {
            ResourceLocation rl = (ResourceLocation)object;
            return rl;
        }
        String s = object.toString();
        return s.isBlank() ? null : ResourceLocation.m_135820_((String)s);
    }

    private static ResourceKey<? extends Registry<?>> toRegistryKey(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ResourceKey) {
            ResourceKey rl = (ResourceKey)object;
            return rl;
        }
        if (object instanceof ResourceLocation) {
            ResourceLocation rl = (ResourceLocation)object;
            return ResourceKey.m_135788_((ResourceLocation)rl);
        }
        String s = object.toString();
        return s.isBlank() ? null : ResourceKey.m_135788_((ResourceLocation)new ResourceLocation(s));
    }

    public Extra copy() {
        Extra t = new Extra();
        t.transformer = this.transformer;
        t.identity = this.identity;
        t.required = this.required;
        t.validator = this.validator;
        t.toString = this.toString;
        return t;
    }

    public Extra transformer(Transformer factory) {
        this.transformer = factory;
        return this;
    }

    public Extra identity() {
        this.identity = true;
        return this;
    }

    public Extra required() {
        this.required = true;
        return this;
    }

    public Extra validator(Predicate<Object> validator) {
        this.validator = validator;
        return this;
    }

    public Extra describeType(Function<DescriptionContext, TypeDescJS> describeType) {
        this.describeType = describeType;
        return this;
    }

    public Extra toString(Transformer factory) {
        this.toString = factory;
        return this;
    }

    @FunctionalInterface
    public static interface Transformer {
        public static final Transformer IDENTITY = o -> o;

        @Nullable
        public Object transform(Object var1);
    }
}

