/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.config;

public enum MapImageQuality {
    PERFORMANCE(0, "\u6027\u80fd"),
    BALANCED(2, "\u5e73\u8861"),
    HIGH(3, "\u9ad8\u6e05");

    private final int refinementLevels;
    private final String displayName;

    private MapImageQuality(int refinementLevels, String displayName) {
        this.refinementLevels = refinementLevels;
        this.displayName = displayName;
    }

    public int refinementLevels() {
        return this.refinementLevels;
    }

    public String displayName() {
        return this.displayName;
    }
}

