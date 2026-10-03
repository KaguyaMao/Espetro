/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.typings;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={})
public @interface Param {
    public String name() default "";

    public String value() default "";
}

