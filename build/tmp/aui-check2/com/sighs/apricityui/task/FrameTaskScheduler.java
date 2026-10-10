/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.task;

import com.sighs.apricityui.ApricityUI;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public final class FrameTaskScheduler {
    private static final long DEFAULT_BUDGET_NS = 2000000L;
    private static final Queue<FrameTask> tasks = new ArrayDeque<FrameTask>();
    private static final Queue<DeferredTask> deferredTasks = new PriorityQueue<DeferredTask>(Comparator.comparingLong(DeferredTask::targetFrame));
    private static long frameIndex;

    private FrameTaskScheduler() {
    }

    public static void schedule(FrameTask task) {
        if (task == null) {
            return;
        }
        tasks.add(task);
    }

    public static void scheduleAfterFrames(int frames, FrameTask task) {
        if (task == null) {
            return;
        }
        long targetFrame = frameIndex + (long)Math.max(1, frames);
        deferredTasks.add(new DeferredTask(targetFrame, task));
    }

    public static void tick() {
        ++frameIndex;
        while (!deferredTasks.isEmpty() && deferredTasks.peek().targetFrame() <= frameIndex) {
            tasks.add(deferredTasks.poll().task());
        }
        if (tasks.isEmpty()) {
            return;
        }
        long deadlineNs = System.nanoTime() + 2000000L;
        while (!tasks.isEmpty()) {
            boolean done;
            FrameTask task = tasks.peek();
            try {
                done = task.runUntil(deadlineNs);
            }
            catch (Exception exception) {
                done = true;
                ApricityUI.LOGGER.error("[AUI Scheduler] frame task failed frame={}", (Object)frameIndex, (Object)exception);
            }
            if (done) {
                tasks.poll();
            }
            if (System.nanoTime() < deadlineNs) continue;
            break;
        }
    }

    private record DeferredTask(long targetFrame, FrameTask task) {
    }

    @FunctionalInterface
    public static interface FrameTask {
        public boolean runUntil(long var1);
    }
}

