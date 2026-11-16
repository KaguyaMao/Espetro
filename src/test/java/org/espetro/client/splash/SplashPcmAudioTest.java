package org.espetro.client.splash;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SplashPcmAudioTest {
    @Test
    void opensAndPlaysOnRealJavaSoundDeviceWithoutOpenAl() throws Exception {
        assertFalse(SplashWavAudio.isAlReady());
        // Zero PCM samples exercise the real device without playing a test sound to the user.
        try (SplashPcmAudio audio = new SplashPcmAudio(0f)) {
            audio.prewarm();
            audio.start(0);
            assertTrue(audio.started().get(5, TimeUnit.SECONDS), "real audio line must start");
            assertFalse(SplashWavAudio.isAlReady());
        }
    }

    @Test
    void lateDeviceOpeningSeeksToVideoClock() throws Exception {
        AtomicLong clock = new AtomicLong(1000);
        CountDownLatch opening = new CountDownLatch(1);
        CountDownLatch allowOpen = new CountDownLatch(1);
        FakeOutput output = new FakeOutput();
        try (SplashPcmAudio audio = new SplashPcmAudio(() -> {
            opening.countDown();
            assertTrue(allowOpen.await(2, TimeUnit.SECONDS));
            return output;
        }, clock::get)) {
            audio.prewarm();
            assertTrue(opening.await(2, TimeUnit.SECONDS));
            audio.start(0);
            clock.set(1400);
            allowOpen.countDown();
            assertTrue(audio.started().get(2, TimeUnit.SECONDS));
            assertEquals(400_000L, output.offset);
        } finally {
            allowOpen.countDown();
        }
        assertTrue(output.closed.await(2, TimeUnit.SECONDS));
    }

    @Test
    void closingWhileDeviceOpensDoesNotBlockRenderOrStartLateAudio() throws Exception {
        CountDownLatch opening = new CountDownLatch(1);
        CountDownLatch allowOpen = new CountDownLatch(1);
        FakeOutput output = new FakeOutput();
        SplashPcmAudio audio = new SplashPcmAudio(() -> {
            opening.countDown();
            assertTrue(allowOpen.await(2, TimeUnit.SECONDS));
            return output;
        }, () -> 1000L);
        audio.prewarm();
        assertTrue(opening.await(2, TimeUnit.SECONDS));
        CompletableFuture.runAsync(audio::close).get(500, TimeUnit.MILLISECONDS);
        allowOpen.countDown();
        assertFalse(audio.started().get(2, TimeUnit.SECONDS));
        assertTrue(output.closed.await(2, TimeUnit.SECONDS));
        assertEquals(-1L, output.offset);
    }

    private static final class FakeOutput implements SplashPcmAudio.Output {
        final CountDownLatch closed = new CountDownLatch(1);
        volatile long offset = -1;
        public long durationMicros() { return 6_567_000; }
        public void start(long offsetMicros) { offset = offsetMicros; }
        public boolean running() { return closed.getCount() != 0 && offset >= 0; }
        public void close() { closed.countDown(); }
    }
}
