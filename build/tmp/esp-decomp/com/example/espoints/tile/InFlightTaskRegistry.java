/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class InFlightTaskRegistry<K, V> {
    private final Map<K, CompletableFuture<V>> tasks = new ConcurrentHashMap<K, CompletableFuture<V>>();

    public CompletableFuture<V> getOrStart(K key, Supplier<CompletableFuture<V>> starter) {
        CompletableFuture task = this.tasks.computeIfAbsent(key, ignored -> (CompletableFuture)starter.get());
        task.whenComplete((result, error) -> this.tasks.remove(key, task));
        return task;
    }

    public void clear() {
        this.tasks.values().forEach(task -> task.cancel(true));
        this.tasks.clear();
    }

    public int size() {
        return this.tasks.size();
    }
}

