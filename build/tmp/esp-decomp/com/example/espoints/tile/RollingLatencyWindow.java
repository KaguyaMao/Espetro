/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.Arrays;

public final class RollingLatencyWindow {
    private final long[] samples;
    private int cursor;
    private int count;

    public RollingLatencyWindow(int capacity) {
        if (capacity < 1 || capacity > 65536) {
            throw new IllegalArgumentException("Invalid latency window capacity");
        }
        this.samples = new long[capacity];
    }

    public synchronized void record(long nanos) {
        this.samples[this.cursor] = Math.max(0L, nanos);
        this.cursor = (this.cursor + 1) % this.samples.length;
        this.count = Math.min(this.samples.length, this.count + 1);
    }

    public synchronized long percentile(double quantile) {
        if (!Double.isFinite(quantile) || quantile < 0.0 || quantile > 1.0) {
            throw new IllegalArgumentException("Invalid latency quantile");
        }
        if (this.count == 0) {
            return 0L;
        }
        long[] ordered = Arrays.copyOf(this.samples, this.count);
        Arrays.sort(ordered);
        int index = (int)Math.ceil(quantile * (double)ordered.length) - 1;
        return ordered[Math.max(0, Math.min(ordered.length - 1, index))];
    }

    public synchronized int size() {
        return this.count;
    }
}

