/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class ProgressiveTileReadiness<K, V> {
    private final long generation;
    private final Map<K, CompletableFuture<V>> futures = new ConcurrentHashMap<K, CompletableFuture<V>>();
    private final Set<K> owners = ConcurrentHashMap.newKeySet();
    private volatile Throwable terminalFailure;

    public ProgressiveTileReadiness(long generation) {
        this.generation = generation;
    }

    public CompletableFuture<V> future(K key) {
        CompletableFuture future = this.futures.computeIfAbsent(key, ignored -> new CompletableFuture());
        Throwable failure = this.terminalFailure;
        if (failure != null) {
            future.completeExceptionally(failure);
        }
        return future;
    }

    public boolean claim(long expectedGeneration, K key) {
        return expectedGeneration == this.generation && this.terminalFailure == null && !this.future(key).isDone() && this.owners.add(key);
    }

    public void release(K key) {
        this.owners.remove(key);
    }

    public boolean publish(long expectedGeneration, K key, V value) {
        if (expectedGeneration != this.generation || this.terminalFailure != null || !this.owners.contains(key)) {
            return false;
        }
        return this.future(key).complete(value);
    }

    public boolean isReady(K key) {
        CompletableFuture<V> future = this.futures.get(key);
        return future != null && future.isDone() && !future.isCompletedExceptionally();
    }

    public void fail(Throwable error) {
        if (this.terminalFailure != null) {
            return;
        }
        this.terminalFailure = error;
        this.owners.clear();
        for (CompletableFuture<V> future : this.futures.values()) {
            future.completeExceptionally(error);
        }
    }

    public int ownerCount() {
        return this.owners.size();
    }
}

