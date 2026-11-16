package org.espetro.client.splash;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 启动视频的帧资源（assets/espetro/splash/frames/0001.jpg ... + meta.json）。
 *
 * <p>帧在后台线程池解码，主线程 {@link #take(int)} 只取「<= 目标帧的最近一帧」，
 * 解码跟不上时自动丢帧而不是卡住画面。</p>
 */
public final class SplashVideoAssets implements AutoCloseable {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SplashVideoAssets.class);


    private static final String DIR = "splash/frames/";
    private static final String META = "splash/meta.json";
    private static final int LOOKAHEAD = 12;
    private static final int MAX_BUFFERED = 20;

    private final int frameCount;
    private final int fps;
    private final int durationMs;
    /** true 时直接从 jar（classpath）读，不经过资源包管理器——资源加载期只能用这条路。 */
    private final boolean classpathOnly;

    private final ExecutorService pool;
    private final Map<Integer, NativeImage> ready = new ConcurrentHashMap<>();
    private final Object bufferLock = new Object();
    private int nextSchedule;
    private int pendingDecodes;
    private volatile boolean closed;
    private volatile String lastError;

    private SplashVideoAssets(int frameCount, int fps, int durationMs, boolean classpathOnly) {
        this.frameCount = frameCount;
        this.fps = fps;
        this.durationMs = durationMs;
        this.classpathOnly = classpathOnly;
        this.pool = Executors.newFixedThreadPool(4, runnable -> {
            Thread thread = new Thread(runnable, "espetro-splash-decode");
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        });
    }

    public static boolean available() {
        try (InputStream stream = openAsset(META, true)) {
            if (stream != null) return true;
        } catch (Exception ignored) { }
        Minecraft mc = Minecraft.getInstance();
        try {
            Optional<Resource> resource = mc.getResourceManager()
                .getResource(new ResourceLocation("espetro", META));
            return resource.isPresent();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** 直接从 jar 里打开资源；找不到或出错返回 null。 */
    private static InputStream openAsset(String path, boolean classpath) {
        if (classpath) {
            try {
                ClassLoader loader = SplashVideoAssets.class.getClassLoader();
                InputStream stream = loader.getResourceAsStream("assets/espetro/" + path);
                if (stream != null) {
                    return stream;
                }
            } catch (Throwable ignored) {
            }
            return null;
        }
        try {
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager()
                .getResource(new ResourceLocation("espetro", path));
            return resource.map(res -> {
                try {
                    return res.open();
                } catch (Throwable error) {
                    return null;
                }
            }).orElse(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static SplashVideoAssets open() {
        return open(false);
    }

    /**
     * 资源加载期专用：只从 jar 读，完全不碰资源包管理器
     * （加载进度中 {@code getResource} 可能拿到空快照或抛异常）。
     */
    public static SplashVideoAssets openClasspath() {
        return open(true);
    }

    private static SplashVideoAssets open(boolean classpathOnly) {
        try {
            InputStream metaStream = openAsset(META, classpathOnly);
            if (metaStream == null) {
                LOGGER.warn("[启动视频] 找不到 {}", META);
                return null;
            }
            JsonObject meta;
            try (InputStream stream = metaStream;
                 InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                meta = JsonParser.parseReader(reader).getAsJsonObject();
            }
            int frames = meta.has("frames") ? meta.get("frames").getAsInt() : 0;
            int fps = meta.has("fps") ? meta.get("fps").getAsInt() : 60;
            int duration = meta.has("durationMs") ? meta.get("durationMs").getAsInt() : 0;
            if (frames <= 0 || fps <= 0) {
                LOGGER.warn("[启动视频] meta 非法: frames={}, fps={}", frames, fps);
                return null;
            }
            if (duration <= 0) {
                duration = (int) Math.ceil(frames * 1000.0D / fps);
            }
            LOGGER.info("[启动视频] 资源就绪: {} 帧 @ {} fps / {} ms, 来源={}",
                frames, fps, duration, classpathOnly ? "classpath" : "resourcepack");
            return new SplashVideoAssets(frames, fps, duration, classpathOnly);
        } catch (Throwable error) {
            LOGGER.warn("[启动视频] 启动视频资源不可用，跳过播放: {}", error.toString());
            return null;
        }
    }

    public int frameCount() {
        return frameCount;
    }

    public int fps() {
        return fps;
    }

    public int durationMs() {
        return durationMs;
    }

    /** 已解码缓存的帧数（诊断用）。 */
    public int buffered() {
        return ready.size();
    }

    /** 打开屏幕时立刻开始解码前若干帧，减少开局空帧。 */
    public void prewarm() {
        scheduleFrom(0);
    }

    public String lastError() {
        return lastError;
    }

    /**
     * 取「不晚于 {@code index} 的最近一帧」；尚未解码时返回 null，不提前显示未来帧。
     * 返回后该帧的所有权交给调用方（用完需 close）。
     */
    public NativeImage take(int index) {
        DecodedFrame frame = takeFrame(index);
        return frame == null ? null : frame.image();
    }

    /** Actual decoded index, so the renderer never mistakes an old frame for the final frame. */
    public record DecodedFrame(int index, NativeImage image) { }

    public DecodedFrame takeFrame(int index) {
        synchronized (bufferLock) {
            if (closed) return null;
            int best = -1;
            for (Integer key : ready.keySet()) {
                if (key <= index && key > best) best = key;
            }
            for (Integer key : ready.keySet()) {
                if (key < best) {
                    NativeImage stale = ready.remove(key);
                    if (stale != null) stale.close();
                }
            }
            NativeImage image = best < 0 ? null : ready.remove(best);
            scheduleFrom(index);
            return image == null ? null : new DecodedFrame(best, image);
        }
    }

    private void scheduleFrom(int index) {
        synchronized (bufferLock) {
            if (closed) return;
            int from = Math.max(nextSchedule, index);
            int slots = MAX_BUFFERED - ready.size() - pendingDecodes;
            int to = Math.min(frameCount - 1, Math.min(index + LOOKAHEAD, from + slots - 1));
            if (from > to) return;
            nextSchedule = to + 1;
            for (int i = from; i <= to; i++) {
                final int frame = i;
                pendingDecodes++;
                try {
                    pool.execute(() -> {
                        try { decodeInto(frame); }
                        finally { synchronized (bufferLock) { pendingDecodes--; } }
                    });
                } catch (Throwable ignored) {
                    pendingDecodes--;
                }
            }
        }
    }

    private void decodeInto(int index) {
        if (closed) {
            return;
        }
        try {
            String path = DIR + String.format("%04d.jpg", index + 1);
            try (InputStream stream = openAsset(path, this.classpathOnly)) {
                if (stream == null) {
                    lastError = "缺少帧资源 assets/espetro/" + path;
                    return;
                }
                NativeImage image = NativeImage.read(stream);
                synchronized (bufferLock) {
                    if (closed) image.close();
                    else ready.put(index, image);
                }
            }
        } catch (Throwable error) {
            lastError = "帧 " + index + " 解码失败: " + error;
        }
    }

    @Override
    public void close() {
        synchronized (bufferLock) {
            closed = true;
            pool.shutdownNow();
            for (NativeImage image : ready.values()) {
                try { image.close(); } catch (Throwable ignored) { }
            }
            ready.clear();
        }
    }
}
