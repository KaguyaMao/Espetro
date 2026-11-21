package org.espetro.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerRateLimiterTest {
    private final AtomicLong now = new AtomicLong(10_000L);
    private final PlayerRateLimiter limiter = new PlayerRateLimiter(1_000L, now::get);
    private final UUID player = UUID.randomUUID();

    @Test
    void firstCallIsAllowed() {
        assertTrue(limiter.tryAcquire(player));
    }

    @Test
    void callWithinIntervalIsBlockedAndAfterIntervalAllowed() {
        assertTrue(limiter.tryAcquire(player));
        now.addAndGet(999L);
        assertFalse(limiter.tryAcquire(player));
        now.addAndGet(1_000L);
        assertTrue(limiter.tryAcquire(player));
    }

    @Test
    void continuousSpamKeepsResettingTheWindow() {
        // 与原先 put-then-compare 写法一致：被拦下的调用也会刷新时间
        assertTrue(limiter.tryAcquire(player));
        for (int i = 0; i < 5; i++) {
            now.addAndGet(600L);
            assertFalse(limiter.tryAcquire(player));
        }
        now.addAndGet(1_000L);
        assertTrue(limiter.tryAcquire(player));
    }

    @Test
    void playersAreIndependent() {
        UUID other = UUID.randomUUID();
        assertTrue(limiter.tryAcquire(player));
        assertTrue(limiter.tryAcquire(other));
        assertFalse(limiter.tryAcquire(player));
    }

    @Test
    void forgetReleasesRecord() {
        limiter.tryAcquire(player);
        assertEquals(1, limiter.trackedCount());
        limiter.forget(player);
        limiter.forget(null);
        assertEquals(0, limiter.trackedCount());
        assertTrue(limiter.tryAcquire(player));
    }
}
