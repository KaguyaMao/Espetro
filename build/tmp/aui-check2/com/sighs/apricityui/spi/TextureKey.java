/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

public record TextureKey(String value) {
    public TextureKey(String value) {
        this.value = value = value == null ? "" : value;
    }

    public static TextureKey of(String value) {
        return new TextureKey(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}

