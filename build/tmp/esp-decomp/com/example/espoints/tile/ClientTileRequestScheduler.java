/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ClientTileRequestScheduler<K> {
    public static final int DEFAULT_DESIRED_LIMIT = 256;
    public static final int DEFAULT_OUTSTANDING_LIMIT = 32;
    private static final long BASE_RETRY_MILLIS = 500L;
    private static final long MAX_RETRY_MILLIS = 8000L;
    private final int desiredLimit;
    private final int outstandingLimit;
    private final LinkedHashSet<K> desired = new LinkedHashSet();
    private final Map<K, Attempt> outstanding = new HashMap<K, Attempt>();
    private final Set<K> processing = new HashSet<K>();
    private final Map<K, Attempt> retry = new HashMap<K, Attempt>();

    public ClientTileRequestScheduler() {
        this(256, 32);
    }

    public ClientTileRequestScheduler(int desiredLimit, int outstandingLimit) {
        if (desiredLimit < 1 || outstandingLimit < 1 || outstandingLimit > desiredLimit || outstandingLimit > 64) {
            throw new IllegalArgumentException("Invalid client tile request bounds");
        }
        this.desiredLimit = desiredLimit;
        this.outstandingLimit = outstandingLimit;
    }

    public synchronized void updateDesired(List<K> ordered) {
        this.desired.clear();
        if (ordered != null) {
            for (K key2 : ordered) {
                if (key2 == null || this.desired.size() >= this.desiredLimit) continue;
                this.desired.add(key2);
            }
        }
        this.outstanding.keySet().removeIf(key -> !this.desired.contains(key));
        this.processing.removeIf(key -> !this.desired.contains(key));
        this.retry.keySet().removeIf(key -> !this.desired.contains(key));
    }

    public synchronized void addDesired(K key) {
        if (key != null && this.desired.size() < this.desiredLimit) {
            this.desired.add(key);
        }
    }

    public synchronized List<K> poll(long nowMillis, int maximum) {
        this.expire(nowMillis);
        int available = Math.min(Math.max(0, maximum), this.outstandingLimit - this.outstanding.size());
        if (available == 0) {
            return List.of();
        }
        ArrayList result = new ArrayList(available);
        for (Object key : this.desired) {
            Attempt previous;
            if (result.size() >= available) break;
            if (this.outstanding.containsKey(key) || this.processing.contains(key) || (previous = this.retry.get(key)) != null && nowMillis < previous.retryAtMillis) continue;
            int attempt = previous == null ? 1 : previous.number + 1;
            long retryAt = ClientTileRequestScheduler.safeAdd(nowMillis, ClientTileRequestScheduler.retryDelay(key, attempt));
            Attempt next = new Attempt(attempt, retryAt);
            this.outstanding.put(key, next);
            this.retry.put(key, next);
            result.add(key);
        }
        return List.copyOf(result);
    }

    public synchronized void complete(K key) {
        this.outstanding.remove(key);
        this.processing.remove(key);
        this.retry.remove(key);
        this.desired.remove(key);
    }

    public synchronized void received(K key) {
        this.outstanding.remove(key);
        if (this.desired.contains(key)) {
            this.processing.add(key);
        }
    }

    public synchronized void reject(K key, long nowMillis) {
        int number;
        Attempt attempt = this.outstanding.remove(key);
        this.processing.remove(key);
        int n = number = attempt == null ? 1 : attempt.number;
        if (this.desired.contains(key)) {
            Attempt previous = this.retry.get(key);
            if (previous != null) {
                number = Math.max(number, previous.number);
            }
            this.retry.put(key, new Attempt(number, ClientTileRequestScheduler.safeAdd(nowMillis, ClientTileRequestScheduler.retryDelay(key, number))));
        } else {
            this.retry.remove(key);
        }
    }

    public synchronized void clear() {
        this.desired.clear();
        this.outstanding.clear();
        this.processing.clear();
        this.retry.clear();
    }

    public synchronized int desiredSize() {
        return this.desired.size();
    }

    public synchronized int outstandingSize() {
        return this.outstanding.size();
    }

    public synchronized int processingSize() {
        return this.processing.size();
    }

    public synchronized boolean isOutstanding(K key) {
        return this.outstanding.containsKey(key);
    }

    public synchronized boolean isDesired(K key) {
        return this.desired.contains(key);
    }

    public synchronized boolean isProcessing(K key) {
        return this.processing.contains(key);
    }

    private void expire(long nowMillis) {
        this.outstanding.entrySet().removeIf(entry -> nowMillis >= ((Attempt)entry.getValue()).retryAtMillis);
    }

    private static long retryDelay(Object key, int attempt) {
        int shift = Math.min(4, Math.max(0, attempt - 1));
        long exponential = Math.min(8000L, 500L << shift);
        long jitter = Math.floorMod(key.hashCode(), 127);
        return Math.min(8000L, exponential + jitter);
    }

    private static long safeAdd(long value, long increment) {
        return value > Long.MAX_VALUE - increment ? Long.MAX_VALUE : value + increment;
    }

    private record Attempt(int number, long retryAtMillis) {
    }
}

