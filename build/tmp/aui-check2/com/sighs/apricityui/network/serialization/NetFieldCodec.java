/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.network.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.RECORD_COMPONENT})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NetFieldCodec {
    public Class<?> holder();

    public String field() default "STREAM_CODEC";
}

