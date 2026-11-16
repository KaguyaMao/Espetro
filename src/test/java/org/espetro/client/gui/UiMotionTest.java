package org.espetro.client.gui;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class UiMotionTest {
    @Test
    void interpolatesBetweenFramesWithinOneTickAndFinishesAtDuration() {
        AtomicLong clock = new AtomicLong(5_000_000_000L);
        UiMotion.Value value = new UiMotion.Value(0f, clock::get);
        value.to(1f, 160L);
        assertEquals(0f, value.get());
        clock.addAndGet(8_000_000L);
        float firstFrame = value.get();
        clock.addAndGet(8_000_000L);
        assertTrue(firstFrame > 0f);
        assertTrue(value.get() > firstFrame);
        assertFalse(value.isSettled());
        clock.set(5_080_000_000L);
        assertEquals(0.875f, value.get(), 0.00001f);
        clock.set(5_160_000_000L);
        assertEquals(1f, value.get());
        assertTrue(value.isSettled());
    }

    @Test
    void repeatedTargetDoesNotRestartAndRetargetingKeepsCurrentValue() {
        AtomicLong clock = new AtomicLong();
        UiMotion.Value value = new UiMotion.Value(0f, clock::get);
        value.to(1f, 160L);
        clock.set(80_000_000L);
        float beforeRetarget = value.get();
        value.to(1f, 160L);
        assertEquals(beforeRetarget, value.get());
        clock.set(160_000_000L);
        assertTrue(value.isSettled());
        value.snap(0f);
        value.to(1f, 160L);
        clock.set(240_000_000L);
        beforeRetarget = value.get();
        value.to(0f, 160L);
        assertEquals(beforeRetarget, value.get());
        clock.set(248_000_000L);
        assertTrue(value.get() < beforeRetarget);
        assertTrue(value.get() > 0f);
        clock.set(400_000_000L);
        assertEquals(0f, value.get());
    }

    @Test
    void retargetSamplesElapsedTimeEvenWithoutAnIntermediateRenderedFrame() {
        AtomicLong clock = new AtomicLong();
        UiMotion.Value value = new UiMotion.Value(0f, clock::get);
        value.to(1f, 160L);
        clock.set(40_000_000L);
        value.to(0f, 160L);
        assertEquals(0.578125f, value.get(), 0.00001f);
        clock.set(200_000_000L);
        assertEquals(0f, value.get());
        assertTrue(value.isSettled());
    }

    @Test
    void snapAndNonpositiveDurationImmediatelySettle() {
        AtomicLong clock = new AtomicLong();
        UiMotion.Value value = new UiMotion.Value(0f, clock::get);
        value.to(1f, 180L);
        clock.set(40_000_000L);
        value.snap(0.4f);
        assertEquals(0.4f, value.get());
        assertTrue(value.isSettled());
        clock.set(1_000_000_000L);
        assertEquals(0.4f, value.get());
        value.to(0f, 0L);
        assertEquals(0f, value.get());
        value.to(1f, -1L);
        assertEquals(1f, value.get());
        assertTrue(value.isSettled());
        assertEquals(0f, UiMotion.ease(-1f));
        assertEquals(1f, UiMotion.ease(2f));
    }
}
