/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util.spi;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Predicate;

public interface IAnnotationScanner {
    public Set<Class<?>> findAnnotatedClasses(Class<? extends Annotation> var1, Set<String> var2, Predicate<Class<?>> var3);
}

