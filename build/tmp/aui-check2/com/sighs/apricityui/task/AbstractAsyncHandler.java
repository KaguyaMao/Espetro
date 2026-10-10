/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.task;

import com.sighs.apricityui.ApricityUI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public abstract class AbstractAsyncHandler<TApplyTask> {
    private static final Map<String, AbstractAsyncHandler<?>> HANDLERS = new ConcurrentHashMap();
    private final String id;
    private final int applyBudgetPerTick;
    private final long applyTimeBudgetNs;
    private final AtomicLong generation = new AtomicLong(1L);
    private final ConcurrentLinkedQueue<TApplyTask> applyQueue = new ConcurrentLinkedQueue();
    private final ThreadPoolExecutor workers;

    protected AbstractAsyncHandler(String id, int maxQueueSize, int applyBudgetPerTick, long applyTimeBudgetNs, String workerThreadName) {
        this.id = id;
        this.applyBudgetPerTick = Math.max(1, applyBudgetPerTick);
        this.applyTimeBudgetNs = Math.max(100000L, applyTimeBudgetNs);
        int workerCount = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        this.workers = new ThreadPoolExecutor(workerCount, workerCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(Math.max(16, maxQueueSize)), AbstractAsyncHandler.createThreadFactory(workerThreadName), new ThreadPoolExecutor.AbortPolicy());
        AbstractAsyncHandler.register(this);
    }

    public final String id() {
        return this.id;
    }

    protected final long currentGeneration() {
        return this.generation.get();
    }

    protected final void enqueueApplyTask(TApplyTask task) {
        if (task == null) {
            return;
        }
        this.applyQueue.offer(task);
    }

    protected final void submitWorker(Runnable task, Consumer<RejectedExecutionException> onRejected) {
        block2: {
            try {
                this.workers.execute(task);
            }
            catch (RejectedExecutionException ex) {
                ApricityUI.LOGGER.error("[AUI Async] worker queue rejected task handler={} queued={}", new Object[]{this.id, this.workers.getQueue().size(), ex});
                if (onRejected == null) break block2;
                onRejected.accept(ex);
            }
        }
    }

    public final void tickApplyQueue() {
        TApplyTask task;
        long startNs = System.nanoTime();
        for (int processed = 0; processed < this.applyBudgetPerTick && System.nanoTime() - startNs < this.applyTimeBudgetNs && (task = this.applyQueue.poll()) != null; ++processed) {
            try {
                this.applyOnMainThread(task, this.currentGeneration());
                continue;
            }
            catch (RuntimeException exception) {
                ApricityUI.LOGGER.error("[AUI Async] apply task failed handler={} processed={}", new Object[]{this.id, processed, exception});
                throw exception;
            }
        }
    }

    public final void clearAndBumpGeneration() {
        TApplyTask task;
        long nextGeneration = this.generation.incrementAndGet();
        this.onBeforeClear(nextGeneration);
        while ((task = this.applyQueue.poll()) != null) {
            this.onDiscardApplyTask(task);
        }
        this.workers.getQueue().clear();
        this.onAfterClear(nextGeneration);
    }

    protected abstract void applyOnMainThread(TApplyTask var1, long var2);

    protected void onBeforeClear(long nextGeneration) {
    }

    protected void onAfterClear(long nextGeneration) {
    }

    protected void onDiscardApplyTask(TApplyTask task) {
    }

    public static void register(AbstractAsyncHandler<?> handler) {
        if (handler == null) {
            return;
        }
        HANDLERS.put(handler.id(), handler);
    }

    public static void unregister(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        HANDLERS.remove(id);
    }

    public static void tickAll() {
        for (AbstractAsyncHandler<?> handler : HANDLERS.values()) {
            handler.tickApplyQueue();
        }
    }

    public static void clearAllAndBumpGeneration() {
        for (AbstractAsyncHandler<?> handler : HANDLERS.values()) {
            handler.clearAndBumpGeneration();
        }
    }

    private static ThreadFactory createThreadFactory(String workerThreadName) {
        AtomicInteger index = new AtomicInteger(0);
        return runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName(workerThreadName + "-" + index.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    public static enum AsyncState {
        NEW,
        LOADING,
        APPLYING,
        READY,
        FAILED,
        STALE;

    }
}

