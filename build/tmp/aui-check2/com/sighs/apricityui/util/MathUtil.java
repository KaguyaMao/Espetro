/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

public final class MathUtil {
    private MathUtil() {
    }

    public static float normalizeAngle(float angle) {
        float normalized = angle % 360.0f;
        return normalized < 0.0f ? normalized + 360.0f : normalized;
    }

    public static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}

