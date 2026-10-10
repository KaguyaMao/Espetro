/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.world;

import java.util.Locale;

public enum WorldWindowDisplayPrecision {
    AUTO,
    FULL,
    REDUCED,
    MINIMAL;


    public static WorldWindowDisplayPrecision parse(String value) {
        if (value == null || value.isBlank()) {
            return AUTO;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            return WorldWindowDisplayPrecision.valueOf(normalized);
        }
        catch (IllegalArgumentException ignored) {
            return AUTO;
        }
    }

    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

