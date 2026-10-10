/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.ModFileScanData
 *  net.minecraftforge.forgespi.language.ModFileScanData$AnnotationData
 *  org.objectweb.asm.Type
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.ApricityUI;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

public final class ReflectionUtils {
    private static final Set<String> SCAN_PACKAGES = new LinkedHashSet<String>();

    public static void addScanPackage(String basePackage) {
        if (basePackage != null && !basePackage.isBlank()) {
            SCAN_PACKAGES.add(basePackage);
        }
    }

    public static void addScanPackages(String ... basePackages) {
        if (basePackages != null) {
            for (String p : basePackages) {
                ReflectionUtils.addScanPackage(p);
            }
        }
    }

    private static boolean isPackageAllowed(String className) {
        if (SCAN_PACKAGES.isEmpty()) {
            return true;
        }
        for (String p : SCAN_PACKAGES) {
            if (!className.startsWith(p + ".")) continue;
            return true;
        }
        return false;
    }

    public static Class<?> getRawType(Type type, Class<?> fallback) {
        Class<?> rawType = ReflectionUtils.getRawType(type);
        return rawType != null ? rawType : fallback;
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            Class aClass = (Class)type;
            return aClass;
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            return ReflectionUtils.getRawType(genericArrayType.getGenericComponentType());
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            return ReflectionUtils.getRawType(parameterizedType.getRawType());
        }
        return null;
    }

    public static <A extends Annotation> void findAnnotationClasses(Class<A> annotationClass, @Nullable Predicate<Map<String, Object>> annotationPredicate, Consumer<Class<?>> consumer, Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (!annotationType.equals((Object)annotation.annotationType()) || annotation.targetType() != ElementType.TYPE || annotationPredicate != null && !annotationPredicate.test(annotation.annotationData())) continue;
                try {
                    String className = annotation.memberName();
                    if (!ReflectionUtils.isPackageAllowed(className)) continue;
                    consumer.accept(Class.forName(className, false, ReflectionUtils.class.getClassLoader()));
                }
                catch (Throwable throwable) {
                    ApricityUI.LOGGER.error("Failed to load class for notation: {}", (Object)annotation.memberName(), (Object)throwable);
                }
            }
        }
        onFinished.run();
    }

    public static <A extends Annotation> void findAnnotationStaticField(Class<A> annotationClass, @Nullable Predicate<Map<String, Object>> annotationPredicate, BiConsumer<Field, Object> consumer, Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (!annotationType.equals((Object)annotation.annotationType()) || annotation.targetType() != ElementType.FIELD || annotationPredicate != null && !annotationPredicate.test(annotation.annotationData())) continue;
                org.objectweb.asm.Type clazz = annotation.clazz();
                String fieldName = annotation.memberName();
                try {
                    String className = annotation.clazz().getClassName();
                    if (!ReflectionUtils.isPackageAllowed(className)) continue;
                    Field field = Class.forName(className).getDeclaredField(fieldName);
                    if (Modifier.isStatic(field.getModifiers())) {
                        consumer.accept(field, field.get(null));
                        continue;
                    }
                    ApricityUI.LOGGER.error("Field is not static for notation: {} in {}", (Object)fieldName, (Object)clazz);
                }
                catch (Throwable throwable) {
                    ApricityUI.LOGGER.error("Failed to load static field for notation: {} in {}", new Object[]{fieldName, clazz, throwable});
                }
            }
        }
        onFinished.run();
    }

    public static <A extends Annotation> void findAnnotationStaticMethod(Class<A> annotationClass, @Nullable Predicate<Map<String, Object>> annotationPredicate, Consumer<Method> consumer, Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (!annotationType.equals((Object)annotation.annotationType()) || annotation.targetType() != ElementType.METHOD || annotationPredicate != null && !annotationPredicate.test(annotation.annotationData())) continue;
                org.objectweb.asm.Type clazz = annotation.clazz();
                String methodFullDesc = annotation.memberName();
                String methodName = methodFullDesc.substring(0, methodFullDesc.indexOf(40));
                String methodDesc = methodFullDesc.substring(methodFullDesc.indexOf(40));
                try {
                    String className = annotation.clazz().getClassName();
                    if (!ReflectionUtils.isPackageAllowed(className)) continue;
                    for (Method method : Class.forName(className).getDeclaredMethods()) {
                        if (!method.getName().equals(methodName) || !methodDesc.equals(org.objectweb.asm.Type.getMethodDescriptor((Method)method))) continue;
                        if (Modifier.isStatic(method.getModifiers())) {
                            consumer.accept(method);
                            continue;
                        }
                        ApricityUI.LOGGER.error("Method is not static for notation: {} in {}", (Object)methodDesc, (Object)clazz);
                    }
                }
                catch (Throwable throwable) {
                    ApricityUI.LOGGER.error("Failed to load static method for notation: {} in {}", new Object[]{methodDesc, clazz, throwable});
                }
            }
        }
        onFinished.run();
    }
}

