/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.registry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface KJSBindings {
    public String value() default "";

    public String modId() default "";

    public boolean isClient() default false;
}

