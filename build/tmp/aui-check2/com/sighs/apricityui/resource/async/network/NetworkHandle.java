/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource.async.network;

import com.sighs.apricityui.task.AbstractAsyncHandler;

public final class NetworkHandle {
    private final String url;
    private volatile long generation;
    private volatile AbstractAsyncHandler.AsyncState state = AbstractAsyncHandler.AsyncState.NEW;
    private volatile Throwable error;
    private volatile long failedAtMs;

    public NetworkHandle(String url, long generation) {
        this.url = url;
        this.generation = generation;
    }

    public String url() {
        return this.url;
    }

    public long generation() {
        return this.generation;
    }

    public AbstractAsyncHandler.AsyncState state() {
        return this.state;
    }

    public Throwable error() {
        return this.error;
    }

    public long failedAtMs() {
        return this.failedAtMs;
    }

    public synchronized void reset(long newGeneration) {
        this.generation = newGeneration;
        this.state = AbstractAsyncHandler.AsyncState.NEW;
        this.error = null;
        this.failedAtMs = 0L;
    }

    public synchronized void markLoading() {
        this.state = AbstractAsyncHandler.AsyncState.LOADING;
    }

    public synchronized void markReady() {
        this.state = AbstractAsyncHandler.AsyncState.READY;
        this.error = null;
    }

    public synchronized void markFailed(Throwable throwable, long nowMs) {
        this.state = AbstractAsyncHandler.AsyncState.FAILED;
        this.error = throwable;
        this.failedAtMs = nowMs;
    }

    public synchronized void markStale() {
        this.state = AbstractAsyncHandler.AsyncState.STALE;
    }
}

