/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 */
package com.sighs.apricityui.network.serialization;

import com.sighs.apricityui.network.codec.StreamCodec;
import com.sighs.apricityui.network.serialization.ComponentIO;
import com.sighs.apricityui.network.serialization.CustomCodecResolver;
import com.sighs.apricityui.network.util.ReflectionUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import net.minecraft.network.FriendlyByteBuf;

public final class NetworkRecordCodecBuilder {
    private NetworkRecordCodecBuilder() {
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> build(final Class<T> recordClass) {
        final RecordComponent[] components = recordClass.getRecordComponents();
        Class[] parameterTypes = new Class[components.length];
        final Method[] accessors = new Method[components.length];
        final StreamCodec[] customCodecs = new StreamCodec[components.length];
        Type[] genericTypes = new Type[components.length];
        final ComponentIO.ComponentPlan[] plans = new ComponentIO.ComponentPlan[components.length];
        for (int i = 0; i < components.length; ++i) {
            RecordComponent component = components[i];
            parameterTypes[i] = component.getType();
            accessors[i] = component.getAccessor();
            accessors[i].setAccessible(true);
            customCodecs[i] = CustomCodecResolver.resolve(recordClass, component);
            genericTypes[i] = component.getGenericType();
            if (customCodecs[i] != null) continue;
            plans[i] = ComponentIO.planOf(parameterTypes[i], genericTypes[i]);
        }
        final Constructor<T> constructor = ReflectionUtil.findCanonicalConstructor(recordClass, parameterTypes);
        return new StreamCodec<FriendlyByteBuf, T>(){

            @Override
            public T decode(FriendlyByteBuf buf) {
                Object[] values = new Object[components.length];
                for (int i = 0; i < components.length; ++i) {
                    StreamCodec codec = customCodecs[i];
                    values[i] = codec != null ? codec.decode(buf) : ComponentIO.decodeWithPlan(buf, plans[i], 0, components[i].getName(), recordClass);
                }
                return ReflectionUtil.newInstance(constructor, values);
            }

            @Override
            public void encode(FriendlyByteBuf buf, T value) {
                for (int i = 0; i < components.length; ++i) {
                    Object fieldValue = ReflectionUtil.invoke(accessors[i], value);
                    StreamCodec codec = customCodecs[i];
                    if (codec != null) {
                        codec.encode(buf, fieldValue);
                        continue;
                    }
                    ComponentIO.encodeWithPlan(buf, plans[i], fieldValue, 0, components[i].getName(), recordClass);
                }
            }
        };
    }
}

