package org.espetro.client.splash;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/** Startup PCM playback, independent of Minecraft's not-yet-loaded OpenAL engine. */
final class SplashPcmAudio implements AutoCloseable {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SplashPcmAudio.class);
    private final ExecutorService worker = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "espetro-splash-pcm");
        thread.setDaemon(true);
        return thread;
    });
    private final OutputFactory factory;
    private final LongSupplier clock;
    private final CompletableFuture<Void> startSignal = new CompletableFuture<>();
    private final CompletableFuture<Boolean> prepared = new CompletableFuture<>();
    private final CompletableFuture<Boolean> started = new CompletableFuture<>();
    private volatile boolean closed;
    private volatile long startedAt;
    private boolean scheduled;

    SplashPcmAudio(float volume) {
        this(() -> openClip(volume), () -> System.nanoTime() / 1_000_000L);
    }

    SplashPcmAudio(OutputFactory factory, LongSupplier clock) {
        this.factory = factory;
        this.clock = clock;
    }

    synchronized void prewarm() {
        if (scheduled || closed) return;
        scheduled = true;
        worker.execute(this::run);
    }

    void start(long elapsedMs) {
        if (closed || startSignal.isDone()) return;
        startedAt = clock.getAsLong() - Math.max(0, elapsedMs);
        startSignal.complete(null);
        prewarm();
    }

    boolean isPrepared() { return prepared.isDone(); }
    CompletableFuture<Boolean> started() { return started; }

    private void run() {
        try (Output output = factory.open()) {
            prepared.complete(true);
            startSignal.get(20, TimeUnit.SECONDS);
            if (closed) return;
            long offsetMicros = Math.max(0, clock.getAsLong() - startedAt) * 1000L;
            if (offsetMicros >= output.durationMicros()) {
                LOGGER.warn("[启动视频] PCM audio expired before start: offsetUs={}, durationUs={}",
                    offsetMicros, output.durationMicros());
                return;
            }
            output.start(offsetMicros);
            // DirectClip starts its mixer thread asynchronously on Windows.
            long startDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
            while (!closed && !output.running() && System.nanoTime() < startDeadline) Thread.sleep(5);
            if (closed) return;
            boolean running = output.running();
            if (!running) throw new IllegalStateException("PCM output did not enter playback state");
            started.complete(true);
            LOGGER.info("[启动视频] PCM audio started: backend=JavaSound, offsetMs={}", offsetMicros / 1000L);
            while (!closed && output.running()) Thread.sleep(10);
        } catch (Exception error) {
            if (!closed) LOGGER.warn("[启动视频] PCM audio failed", error);
        } finally {
            prepared.complete(false);
            started.complete(false);
            worker.shutdown();
        }
    }

    /** Closing never waits for an audio device on the render thread. */
    @Override
    public void close() {
        closed = true;
        startSignal.complete(null);
        worker.shutdown();
    }

    interface Output extends AutoCloseable {
        long durationMicros();
        void start(long offsetMicros);
        boolean running();
        void close();
    }

    @FunctionalInterface
    interface OutputFactory { Output open() throws Exception; }

    private static Output openClip(float volume) throws Exception {
        try (InputStream stream = SplashPcmAudio.class.getResourceAsStream("/assets/espetro/splash/audio.wav")) {
            if (stream == null) throw new IllegalStateException("Missing startup WAV");
            try (AudioInputStream wav = AudioSystem.getAudioInputStream(new java.io.BufferedInputStream(stream))) {
                AudioFormat format = wav.getFormat();
                if (!AudioFormat.Encoding.PCM_SIGNED.equals(format.getEncoding())
                        || format.getSampleSizeInBits() != 16 || format.isBigEndian()) {
                    throw new IllegalStateException("Startup WAV must be little-endian PCM16: " + format);
                }
                byte[] pcm = wav.readAllBytes();
                float gain = Math.max(0f, Math.min(1f, volume));
                for (int i = 0; i + 1 < pcm.length; i += 2) {
                    int sample = (short) ((pcm[i] & 255) | (pcm[i + 1] << 8));
                    int scaled = Math.round(sample * gain);
                    pcm[i] = (byte) scaled;
                    pcm[i + 1] = (byte) (scaled >> 8);
                }
                Clip clip = AudioSystem.getClip();
                try {
                    clip.open(format, pcm, 0, pcm.length);
                } catch (Exception error) {
                    clip.close();
                    throw error;
                }
                return new Output() {
                    public long durationMicros() { return clip.getMicrosecondLength(); }
                    public void start(long offsetMicros) {
                        clip.setMicrosecondPosition(offsetMicros);
                        clip.start();
                    }
                    public boolean running() { return clip.isRunning(); }
                    public void close() { clip.stop(); clip.close(); }
                };
            }
        }
    }
}
