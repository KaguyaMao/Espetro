/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings;

import dev.latvian.mods.kubejs.typings.Param;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Info {
    public String value() default "";

    public Param[] params() default {};
}

