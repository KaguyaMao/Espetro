package org.espetro.client.splash;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/** Draws after queued rendering and immediately before glfwSwapBuffers, without replacing any screen. */
public final class SplashEarlyRenderer {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SplashEarlyRenderer.class);

    private static final long FADE_OUT_MS = 400L;
    private static final long PREPARE_TIMEOUT_MS = 15_000L;
    private static final long PLAYBACK_GRACE_MS = 9_000L;
    private static final SplashEarlyRenderer INSTANCE = new SplashEarlyRenderer();

    private SplashVideoAssets assets;
    private SplashPlaybackTimeline timeline;
    private SplashVideoBlitter blitter;
    private final SplashPcmAudio audio = new SplashPcmAudio(0.8F);
    private boolean finished;
    private boolean failed;
    private boolean hookLogged;
    private long preparingAt;
    private int uploadedIndex = -1;
    private int presentedFrames;

    private SplashEarlyRenderer() { }

    public static SplashEarlyRenderer get() { return INSTANCE; }

    public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) INSTANCE.tick(minecraft);
    }

    /** Tick is only a fallback watchdog; it must not start the video clock. */
    public void tick(Minecraft minecraft) {
        if (assets != null && timedOut(Util.getMillis())) {
            fail("Playback/decoder timeout", null);
        }
        if (failed && !SplashVideoController.wasPlayedByScreen()
                && minecraft.getOverlay() == null) {
            SplashVideoController.tryFallback(minecraft);
        }
    }

    private boolean shouldStart(Minecraft minecraft) {
        return !SplashVideoController.wasPlayedByScreen() && minecraft.level == null
            && (minecraft.screen == null || minecraft.getOverlay() != null);
    }

    private boolean prepare(long now) {
        assets = SplashVideoAssets.openClasspath();
        if (assets == null) return false;
        timeline = new SplashPlaybackTimeline(assets.frameCount(), assets.fps(),
            assets.durationMs(), FADE_OUT_MS);
        blitter = new SplashVideoBlitter();
        preparingAt = now;
        audio.prewarm();
        assets.prewarm();
        return true;
    }

    /** This is only called from the single, pre-swap RenderSystem hook. */
    public void renderOverlay(Minecraft minecraft) {
        if (finished || failed) return;
        if (!hookLogged) {
            hookLogged = true;
            LOGGER.info(
                "[启动视频] hook=beforeSwap, screen={}, overlay={}",
                minecraft.screen == null ? "null" : minecraft.screen.getClass().getSimpleName(),
                minecraft.getOverlay() == null ? "null" : minecraft.getOverlay().getClass().getSimpleName());
        }
        if (SplashVideoController.wasPlayedByScreen() && assets == null) return;
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        if (width <= 0 || height <= 0
                || GLFW.glfwGetWindowAttrib(minecraft.getWindow().getWindow(), GLFW.GLFW_ICONIFIED) != 0) return;
        long now = Util.getMillis();
        try {
            if (assets == null) {
                if (!shouldStart(minecraft)) return;
                if (!prepare(now)) {
                    fail("Classpath video assets unavailable", null);
                    return;
                }
            }
            if (timedOut(now)) {
                fail("Playback/decoder timeout: " + assets.lastError(), null);
                return;
            }
            if (timeline.complete(now)) {
                stop();
                return;
            }
            int wanted = timeline.wantedFrame(now);
            if (uploadedIndex < wanted || uploadedIndex < 0) {
                SplashVideoAssets.DecodedFrame frame = assets.takeFrame(wanted);
                if (frame != null) {
                    try (NativeImage image = frame.image()) {
                        blitter.upload(image);
                        uploadedIndex = frame.index();
                    }
                }
            }
            if (uploadedIndex < 0) return;
            if (!timeline.started() && !audio.isPrepared() && now - preparingAt < 2000L) return;
            // NDC shader: no GUI projection, shader reload, or near-plane dependency.
            blitter.draw(width, height, timeline.opacity(now));
            boolean first = !timeline.started();
            timeline.presented(now, uploadedIndex);
            presentedFrames++;
            if (first) {
                audio.start(0);
                SplashVideoController.markPlayed();
                LOGGER.info(
                    "[启动视频] 加载期叠加开始: {} 帧 @ {} fps, hook=beforeSwap, framebuffer={}x{}, frame={}",
                    assets.frameCount(), assets.fps(), width, height, uploadedIndex);
            } else if (presentedFrames % 60 == 0) {
                LOGGER.info("[启动视频] presented={}, frame={}, elapsedMs={}",
                    presentedFrames, uploadedIndex, timeline.elapsedMs(now));
            }
        } catch (Throwable error) {
            fail("Pre-swap video draw failed", error);
        }
    }

    private boolean timedOut(long now) {
        return timeline != null && (timeline.started()
            ? timeline.elapsedMs(now) > assets.durationMs() + PLAYBACK_GRACE_MS
            : now - preparingAt > PREPARE_TIMEOUT_MS);
    }

    private void fail(String reason, Throwable error) {
        failed = true;
        LOGGER.warn("[启动视频] {}: {}", reason,
            error == null ? "fallback enabled" : error.toString());
        stop();
    }

    private void stop() {
        if (finished) return;
        finished = true;
        audio.close();
        if (assets != null) {
            assets.close();
            assets = null;
        }
        if (blitter != null) {
            try { blitter.close(); } catch (Throwable ignored) { }
            blitter = null;
        }
        LOGGER.info("[启动视频] 加载期叠加结束: presented={}, lastFrame={}, failed={}",
            presentedFrames, uploadedIndex, failed);
        SplashStartupReloadGate.finishPlayback();
    }

    /** Preparation also owns the startup slot, preventing a duplicate title-screen player. */
    public boolean isRunning() { return assets != null && !finished && !failed; }

}
