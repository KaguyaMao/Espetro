package org.espetro.client.splash;

/** Playback starts with a presented image; slow decoding cannot extend its ending. */
final class SplashPlaybackTimeline {
    private final int frames;
    private final int fps;
    private final long durationMs;
    private final long fadeMs;
    private boolean started;
    private long startedAt;

    SplashPlaybackTimeline(int frames, int fps, long durationMs, long fadeMs) {
        if (frames <= 0 || fps <= 0 || durationMs <= 0 || fadeMs <= 0) {
            throw new IllegalArgumentException("Invalid splash timeline");
        }
        this.frames = frames;
        this.fps = fps;
        this.durationMs = durationMs;
        this.fadeMs = fadeMs;
    }

    int wantedFrame(long now) {
        return (int) Math.min(frames - 1L, elapsedMs(now) * fps / 1000L);
    }

    long elapsedMs(long now) {
        return started ? Math.max(0L, now - startedAt) : 0L;
    }

    void presented(long now, int frame) {
        if (!started) {
            started = true;
            startedAt = now;
        }
    }

    float opacity(long now) {
        return started ? Math.max(0f, 1f - Math.max(0L, elapsedMs(now) - durationMs) / (float) fadeMs) : 1f;
    }

    boolean complete(long now) {
        return started && elapsedMs(now) >= durationMs + fadeMs;
    }

    boolean started() {
        return started;
    }
}
