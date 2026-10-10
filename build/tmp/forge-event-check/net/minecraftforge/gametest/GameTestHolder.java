/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.gametest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface GameTestHolder {
    public String value() default "minecraft";
}

