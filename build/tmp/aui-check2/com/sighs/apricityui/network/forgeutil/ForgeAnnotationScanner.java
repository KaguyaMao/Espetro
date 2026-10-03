/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.ModFileScanData
 *  net.minecraftforge.forgespi.language.ModFileScanData$AnnotationData
 *  org.objectweb.asm.Type
 */
package com.sighs.apricityui.network.forgeutil;

import com.sighs.apricityui.util.spi.IAnnotationScanner;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

public final class ForgeAnnotationScanner
implements IAnnotationScanner {
    @Override
    public Set<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotationType, Set<String> basePackages, Predicate<Class<?>> classFilter) {
        LinkedHashSet result = new LinkedHashSet();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Type asmType = Type.getType(annotationType);
        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : scanData.getAnnotations()) {
                String className;
                ElementType target = annotation.targetType();
                if (target != ElementType.TYPE && target != ElementType.METHOD || !annotation.annotationType().equals((Object)asmType) || !this.matchesBasePackages(className = annotation.clazz().getClassName(), basePackages)) continue;
                try {
                    Class<?> clazz = Class.forName(className, false, loader);
                    if (!classFilter.test(clazz)) continue;
                    result.add(clazz);
                }
                catch (ClassNotFoundException | NoClassDefFoundError throwable) {}
            }
        }
        return result;
    }

    private boolean matchesBasePackages(String className, Set<String> basePackages) {
        if (basePackages.isEmpty()) {
            return true;
        }
        for (String base : basePackages) {
            if (!className.startsWith(base + ".") && !className.equals(base)) continue;
            return true;
        }
        return false;
    }
}

