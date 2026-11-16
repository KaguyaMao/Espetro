package org.espetro.client.gui;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/** Frame-sampled motion based on monotonic elapsed time, independent of client ticks. */
public final class UiMotion {
    private UiMotion() { }

    public static float ease(float progress) {
        float remaining = 1f - Math.max(0f, Math.min(1f, progress));
        return 1f - remaining * remaining * remaining;
    }

    public static class Value {
        private final LongSupplier clock;
        private float from;
        private float target;
        private long startedAt;
        private long durationNanos;

        public Value(float initial) {
            this(initial, System::nanoTime);
        }

        Value(float initial, LongSupplier clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
            from = target = initial;
        }

        /** Repeated requests for the same target do not restart an in-flight transition. */
        public void to(float next, long durationMs) {
            if (Float.compare(target, next) == 0) return;
            long now = clock.getAsLong();
            float current = sample(now);
            from = current;
            target = next;
            startedAt = now;
            durationNanos = Math.max(0L, TimeUnit.MILLISECONDS.toNanos(durationMs));
            if (durationNanos == 0L || Float.compare(current, next) == 0) snap(next);
        }

        public float get() {
            return sample(clock.getAsLong());
        }

        public boolean isSettled() {
            return durationNanos == 0L || clock.getAsLong() - startedAt >= durationNanos;
        }

        public void snap(float value) {
            from = target = value;
            durationNanos = 0L;
        }

        private float sample(long now) {
            if (durationNanos == 0L) return target;
            long elapsed = now - startedAt;
            if (elapsed >= durationNanos) return target;
            if (elapsed <= 0L) return from;
            return from + (target - from) * ease(elapsed / (float) durationNanos);
        }
    }
}
