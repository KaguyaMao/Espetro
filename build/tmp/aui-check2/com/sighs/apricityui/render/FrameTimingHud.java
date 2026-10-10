/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.render.RenderBatchStats;
import com.sighs.apricityui.spi.AuiServices;
import java.util.Arrays;
import java.util.Locale;

public final class FrameTimingHud {
    private static final int SAMPLE_COUNT = 120;
    private static final long[] SAMPLES = new long[120];
    private static int sampleIndex = 0;
    private static int sampleSize = 0;
    private static boolean frameActive = false;
    private static long frameElapsedNs = 0L;

    private FrameTimingHud() {
    }

    public static void beginFrame() {
        if (!FrameTimingHud.isEnabled()) {
            FrameTimingHud.clear();
            return;
        }
        frameActive = true;
        frameElapsedNs = 0L;
        RenderBatchStats.beginFrame();
    }

    public static void record(long elapsedNs) {
        if (!FrameTimingHud.isEnabled()) {
            FrameTimingHud.clear();
            return;
        }
        if (elapsedNs <= 0L) {
            return;
        }
        if (frameActive) {
            frameElapsedNs += elapsedNs;
            return;
        }
        FrameTimingHud.pushSample(elapsedNs);
    }

    public static void endFrame() {
        if (!FrameTimingHud.isEnabled()) {
            FrameTimingHud.clear();
            return;
        }
        if (frameActive) {
            if (frameElapsedNs > 0L) {
                FrameTimingHud.pushSample(frameElapsedNs);
            }
            frameActive = false;
            frameElapsedNs = 0L;
            RenderBatchStats.endFrame();
        }
    }

    private static void pushSample(long elapsedNs) {
        FrameTimingHud.SAMPLES[FrameTimingHud.sampleIndex] = elapsedNs;
        sampleIndex = (sampleIndex + 1) % 120;
        if (sampleSize < 120) {
            ++sampleSize;
        }
    }

    public static String frameStatsText() {
        if (sampleSize == 0) {
            return null;
        }
        long min = Long.MAX_VALUE;
        long max = 0L;
        long sum = 0L;
        for (int i = 0; i < sampleSize; ++i) {
            long value = SAMPLES[i];
            if (value <= 0L) continue;
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
        }
        if (min == Long.MAX_VALUE) {
            return null;
        }
        double avg = (double)sum / (double)sampleSize;
        return String.format(Locale.ROOT, "max %.2f ms  min %.2f ms  avg %.2f ms  g %d img %d imm %d", FrameTimingHud.toMillis(max), FrameTimingHud.toMillis(min), FrameTimingHud.toMillis(avg), RenderBatchStats.lastGraphFlushes(), RenderBatchStats.lastImageFlushes(), RenderBatchStats.lastImmediateImageFlushes());
    }

    private static double toMillis(long nanos) {
        return (double)nanos / 1000000.0;
    }

    private static double toMillis(double nanos) {
        return nanos / 1000000.0;
    }

    public static boolean isEnabled() {
        try {
            return AuiServices.config().frameTimingHud();
        }
        catch (IllegalStateException ignored) {
            return false;
        }
    }

    private static void clear() {
        frameActive = false;
        frameElapsedNs = 0L;
        if (sampleSize == 0 && sampleIndex == 0) {
            return;
        }
        Arrays.fill(SAMPLES, 0L);
        sampleIndex = 0;
        sampleSize = 0;
    }
}

