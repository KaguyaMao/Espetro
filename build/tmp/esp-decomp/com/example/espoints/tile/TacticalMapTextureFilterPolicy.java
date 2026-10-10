/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

public final class TacticalMapTextureFilterPolicy {
    private TacticalMapTextureFilterPolicy() {
    }

    public static boolean useLinearFiltering(int level, int maximumLevel) {
        if (level < 0 || maximumLevel < 0 || level > maximumLevel) {
            throw new IllegalArgumentException("Invalid tactical map LOD");
        }
        return maximumLevel > 0 && level == maximumLevel;
    }

    public static boolean useLinearFiltering(int level, int maximumLevel, double scaleX, double scaleY) {
        if (TacticalMapTextureFilterPolicy.useLinearFiltering(level, maximumLevel)) {
            return true;
        }
        if (!Double.isFinite(scaleX) || !Double.isFinite(scaleY) || scaleX <= 0.0 || scaleY <= 0.0) {
            throw new IllegalArgumentException("Invalid tactical map texture scale");
        }
        return !TacticalMapTextureFilterPolicy.isExactIntegerRatio(scaleX) || !TacticalMapTextureFilterPolicy.isExactIntegerRatio(scaleY);
    }

    private static boolean isExactIntegerRatio(double scale) {
        double ratio = scale >= 1.0 ? scale : 1.0 / scale;
        return Math.abs(ratio - Math.rint(ratio)) <= 1.0E-6;
    }
}

