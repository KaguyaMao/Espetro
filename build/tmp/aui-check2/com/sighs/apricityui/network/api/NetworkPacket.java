/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.network.api;

import com.sighs.apricityui.network.api.Side;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface NetworkPacket {
    public String modId();

    public String id();

    public Side side() default Side.BOTH;

    public int priority() default 1000;

    public int chunkThreshold() default 0;
}

