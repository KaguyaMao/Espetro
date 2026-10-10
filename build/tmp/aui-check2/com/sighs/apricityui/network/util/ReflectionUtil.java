/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 */
package com.sighs.apricityui.network.util;

import com.sighs.apricityui.network.codec.StreamCodec;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.network.FriendlyByteBuf;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static <T> Constructor<T> findCanonicalConstructor(Class<T> clazz, Class<?>[] paramTypes) {
        try {
            Constructor<T> ctor = clazz.getDeclaredConstructor(paramTypes);
            ctor.setAccessible(true);
            return ctor;
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("Canonical constructor not found for record: " + clazz.getName(), e);
        }
    }

    public static <T> T newInstance(Constructor<T> ctor, Object ... args) {
        try {
            return ctor.newInstance(args);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate record: " + ctor.getDeclaringClass().getName(), e);
        }
    }

    public static Object invoke(Method method, Object instance) {
        try {
            return method.invoke(instance, new Object[0]);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to invoke method: " + method.getName(), e);
        }
    }

    public static StreamCodec<FriendlyByteBuf, Object> getStaticFieldAsCodec(Class<?> recordClass, Class<?> holder, Field field) {
        try {
            field.setAccessible(true);
            String errorInfo = "Field " + holder.getName() + "#" + field.getName() + " is not a StreamCodec";
            try {
                MethodHandle getter = MethodHandles.lookup().unreflectGetter(field);
                Object codecObj = getter.invokeWithArguments(new Object[0]);
                if (!(codecObj instanceof StreamCodec)) {
                    throw new IllegalStateException(errorInfo);
                }
                StreamCodec codec = (StreamCodec)codecObj;
                return codec;
            }
            catch (IllegalAccessException e) {
                Object codecObj = field.get(null);
                if (!(codecObj instanceof StreamCodec)) {
                    throw new IllegalStateException(errorInfo);
                }
                StreamCodec codec = (StreamCodec)codecObj;
                return codec;
            }
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to resolve codec field " + holder.getName() + "#" + field.getName() + " for record " + recordClass.getName(), t);
        }
    }

    public static <T> T getStaticField(Class<?> recordClass, Class<?> holder, Field field, Class<T> expectedType) {
        try {
            field.setAccessible(true);
            String errorInfo = "Field " + holder.getName() + "#" + field.getName() + " is not a " + expectedType.getSimpleName();
            try {
                MethodHandle getter = MethodHandles.lookup().unreflectGetter(field);
                Object obj = getter.invokeWithArguments(new Object[0]);
                if (!expectedType.isInstance(obj)) {
                    throw new IllegalStateException(errorInfo);
                }
                return (T)obj;
            }
            catch (IllegalAccessException e) {
                Object obj = field.get(null);
                if (!expectedType.isInstance(obj)) {
                    throw new IllegalStateException(errorInfo);
                }
                return (T)obj;
            }
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to resolve field " + holder.getName() + "#" + field.getName() + " for record " + recordClass.getName(), t);
        }
    }
}

