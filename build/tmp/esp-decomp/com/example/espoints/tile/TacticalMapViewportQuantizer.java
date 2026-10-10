/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

public final class TacticalMapViewportQuantizer {
    public static final int LABEL_PIXELS = 4;

    private TacticalMapViewportQuantizer() {
    }

    public static long quantize(double world, double span, int screenPixels, int stepPixels) {
        double worldPerPixel = screenPixels <= 0 ? 1.0 : span / (double)screenPixels;
        double step = Math.max(worldPerPixel * (double)Math.max(1, stepPixels), 1.0E-6);
        return Math.round(world / step);
    }

    public static LabelKey labelKey(long revision, double minX, double minZ, double maxX, double maxZ, double spanX, double spanZ, int width, int height, boolean compact, boolean showLabels) {
        return new LabelKey(revision, TacticalMapViewportQuantizer.quantize(minX, spanX, width, 4), TacticalMapViewportQuantizer.quantize(minZ, spanZ, height, 4), TacticalMapViewportQuantizer.quantize(maxX, spanX, width, 4), TacticalMapViewportQuantizer.quantize(maxZ, spanZ, height, 4), width, height, compact, showLabels);
    }

    public record LabelKey(long revision, long minX, long minZ, long maxX, long maxZ, int width, int height, boolean compact, boolean showLabels) {
    }
}

