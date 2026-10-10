/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

public final class FboHandle {
    private final Object impl;
    public final int width;
    public final int height;

    FboHandle(Object impl, int width, int height) {
        this.impl = impl;
        this.width = width;
        this.height = height;
    }

    public static FboHandle of(Object impl, int width, int height) {
        return new FboHandle(impl, width, height);
    }

    public <T> T as() {
        return (T)this.impl;
    }
}

