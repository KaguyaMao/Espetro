/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.capturepoint;

public final class CaptureProgressIntegrator {
    public static final double LEGACY_PROGRESS_PER_40_TICKS = 5.0;
    private static final double PROGRESS_PER_TICK = 0.125;

    private CaptureProgressIntegrator() {
    }

    public static double advance(double current, int direction, int elapsedTicks) {
        if (!Double.isFinite(current)) {
            throw new IllegalArgumentException("Capture progress must be finite");
        }
        if (direction < -1 || direction > 1 || elapsedTicks < 0) {
            throw new IllegalArgumentException("Invalid capture integration input");
        }
        return CaptureProgressIntegrator.clamp(current + (double)(direction * elapsedTicks) * 0.125);
    }

    public static int display(double preciseProgress) {
        return (int)Math.floor(CaptureProgressIntegrator.clamp(preciseProgress) + 1.0E-9);
    }

    public static double clamp(double progress) {
        return Math.max(0.0, Math.min(100.0, progress));
    }
}

