/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource.async.style;

import com.sighs.apricityui.task.AbstractAsyncHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class StyleHandle {
    private final UUID documentId;
    private final long generation;
    private final ConcurrentHashMap<Integer, CssEntry> cssEntries = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, Boolean> requestedFonts = new ConcurrentHashMap();
    private final AtomicInteger pendingCount = new AtomicInteger(0);
    private volatile AbstractAsyncHandler.AsyncState state = AbstractAsyncHandler.AsyncState.NEW;
    private volatile long failedAtMs;

    public StyleHandle(UUID documentId, long generation) {
        this.documentId = documentId;
        this.generation = generation;
    }

    public UUID documentId() {
        return this.documentId;
    }

    public long generation() {
        return this.generation;
    }

    public AbstractAsyncHandler.AsyncState state() {
        return this.state;
    }

    public long failedAtMs() {
        return this.failedAtMs;
    }

    public int pendingCount() {
        return this.pendingCount.get();
    }

    public void putCssEntry(int order, CssEntry entry) {
        if (entry == null) {
            return;
        }
        this.cssEntries.put(order, entry);
    }

    public List<Map.Entry<Integer, CssEntry>> snapshotCssEntries() {
        ArrayList<Map.Entry<Integer, CssEntry>> entries = new ArrayList<Map.Entry<Integer, CssEntry>>(this.cssEntries.entrySet());
        entries.sort(Map.Entry.comparingByKey());
        return entries;
    }

    public boolean tryReserveFont(String fontKey) {
        if (fontKey == null || fontKey.isBlank()) {
            return false;
        }
        return this.requestedFonts.putIfAbsent(fontKey, Boolean.TRUE) == null;
    }

    public synchronized void queueTask() {
        if (this.state == AbstractAsyncHandler.AsyncState.STALE) {
            return;
        }
        this.pendingCount.incrementAndGet();
        this.state = AbstractAsyncHandler.AsyncState.LOADING;
    }

    public synchronized void markApplying() {
        if (this.state == AbstractAsyncHandler.AsyncState.STALE) {
            return;
        }
        this.state = AbstractAsyncHandler.AsyncState.APPLYING;
    }

    public synchronized void completeTask(boolean failed) {
        int left;
        if (this.state == AbstractAsyncHandler.AsyncState.STALE) {
            return;
        }
        if (failed) {
            this.failedAtMs = System.currentTimeMillis();
        }
        if ((left = this.pendingCount.decrementAndGet()) < 0) {
            this.pendingCount.set(0);
            left = 0;
        }
        if (left > 0) {
            this.state = AbstractAsyncHandler.AsyncState.LOADING;
            return;
        }
        if (failed && this.cssEntries.isEmpty()) {
            this.state = AbstractAsyncHandler.AsyncState.FAILED;
            return;
        }
        this.state = AbstractAsyncHandler.AsyncState.READY;
    }

    public synchronized void markReadyIfIdle() {
        if (this.state == AbstractAsyncHandler.AsyncState.STALE) {
            return;
        }
        if (this.pendingCount.get() == 0) {
            this.state = AbstractAsyncHandler.AsyncState.READY;
        }
    }

    public synchronized void markStale() {
        this.state = AbstractAsyncHandler.AsyncState.STALE;
        this.pendingCount.set(0);
    }

    public record CssEntry(String contextPath, String cssText) {
    }
}

