package org.espetro.client.splash;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Allows preparation in the background, deferring startup apply work until the loading page is visible. */
public final class SplashStartupReloadGate {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SplashStartupReloadGate.class);
    private static final SplashStartupReloadGate INSTANCE = new SplashStartupReloadGate();
    private final CompletableFuture<Void> released = new CompletableFuture<>();
    private boolean claimed;
    private boolean releaseAfterSwap;

    public static <T> CompletableFuture<T> holdStartup(CompletableFuture<T> initial) {
        return INSTANCE.hold(initial);
    }

    public static void finishPlayback() { INSTANCE.requestRelease(); }
    public static void afterSwap() { INSTANCE.releaseAfterSwap(); }

    synchronized <T> CompletableFuture<T> hold(CompletableFuture<T> initial) {
        if (claimed) return initial;
        claimed = true;
        // Fail open even if no rendering hook is reached or third-party startup fails.
        released.completeOnTimeout(null, 20, TimeUnit.SECONDS);
        LOGGER.info("[启动视频] Startup reload apply deferred; preparation remains asynchronous");
        return initial.thenCombine(released, (value, ignored) -> value);
    }

    synchronized void requestRelease() { releaseAfterSwap = true; }

    void releaseAfterSwap() {
        synchronized (this) {
            if (!releaseAfterSwap) return;
            releaseAfterSwap = false;
        }
        // Complete outside the lock: callbacks can enqueue or run reload tasks immediately.
        if (released.complete(null)) LOGGER.info("[启动视频] Loading page presented; startup reload apply released");
    }
}
