/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.registries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Deprecated(since="1.21.4", forRemoval=true)
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface ObjectHolder {
    public String registryName();

    public String value();
}

