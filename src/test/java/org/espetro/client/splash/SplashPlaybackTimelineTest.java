package org.espetro.client.splash;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SplashPlaybackTimelineTest {
    private SplashPlaybackTimeline timeline() {
        return new SplashPlaybackTimeline(394, 60, 6567, 400);
    }

    @Test
    void decodingWaitDoesNotConsumePlaybackTime() {
        SplashPlaybackTimeline t = timeline();
        assertEquals(0, t.wantedFrame(60_000));
        assertEquals(0, t.elapsedMs(60_000));
        assertFalse(t.started());
        assertFalse(t.complete(60_000));
        t.presented(60_000, 0);
        assertTrue(t.started());
        assertEquals(0, t.wantedFrame(60_016));
        assertEquals(1, t.wantedFrame(60_017));
        assertEquals(60, t.wantedFrame(61_000));
        assertEquals(393, t.wantedFrame(70_000));
    }

    @Test
    void staleDecodedFrameCannotKeepVideoOverLoadingPageAfterDeadline() {
        SplashPlaybackTimeline t = timeline();
        t.presented(1000, 0);
        t.presented(7567, 392);
        assertEquals(1f, t.opacity(7567));
        assertEquals(0.5f, t.opacity(7767));
        assertFalse(t.complete(7966));
        assertTrue(t.complete(7967));
        assertEquals(0f, t.opacity(9000));
        assertTrue(t.complete(50_000));
    }

    @Test
    void lastFrameMustRemainUntilFullVideoDuration() {
        SplashPlaybackTimeline t = timeline();
        t.presented(1000, 0);
        t.presented(7550, 393);
        assertFalse(t.complete(7550));
        assertEquals(1f, t.opacity(7550));
        t.presented(7567, 393);
        t.presented(7767, 393); // repeated presentations must not restart the fade
        assertEquals(0.5f, t.opacity(7767));
        assertTrue(t.complete(7967));
    }
}
