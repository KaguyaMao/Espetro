/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;

public final class WeightedLruCache<K, V> {
    private final LinkedHashMap<K, V> values = new LinkedHashMap(16, 0.75f, true);
    private final ToLongFunction<V> weigh;
    private long maximumWeight;
    private long weight;

    public WeightedLruCache(long maximumWeight, ToLongFunction<V> weigh) {
        this.maximumWeight = Math.max(0L, maximumWeight);
        this.weigh = weigh;
    }

    public synchronized V get(K key) {
        return this.values.get(key);
    }

    public synchronized List<V> put(K key, V value) {
        ArrayList<V> evicted = new ArrayList<V>();
        V previous = this.values.put(key, value);
        if (previous != null) {
            this.weight -= this.safeWeight(previous);
            evicted.add(previous);
        }
        this.weight += this.safeWeight(value);
        this.evictOverBudget(evicted);
        return evicted;
    }

    private void evictOverBudget(List<V> evicted) {
        while (this.weight > this.maximumWeight && !this.values.isEmpty()) {
            Map.Entry<K, V> eldest = this.values.entrySet().iterator().next();
            this.values.remove(eldest.getKey());
            this.weight -= this.safeWeight(eldest.getValue());
            evicted.add(eldest.getValue());
        }
    }

    public synchronized List<V> clear() {
        ArrayList<V> removed = new ArrayList<V>(this.values.values());
        this.values.clear();
        this.weight = 0L;
        return removed;
    }

    public synchronized int size() {
        return this.values.size();
    }

    public synchronized long weight() {
        return this.weight;
    }

    public synchronized List<V> setMaximumWeight(long maximumWeight) {
        this.maximumWeight = Math.max(0L, maximumWeight);
        ArrayList evicted = new ArrayList();
        this.evictOverBudget(evicted);
        return evicted;
    }

    private long safeWeight(V value) {
        return Math.max(0L, this.weigh.applyAsLong(value));
    }
}

