/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

public final class RenderHandle {
    private final Object impl;

    private RenderHandle(Object impl) {
        this.impl = impl;
    }

    public static RenderHandle of(Object impl) {
        return new RenderHandle(impl);
    }

    public <T> T as() {
        return (T)this.impl;
    }
}

