/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class FairTileRequestQueue<K> {
    public static final int DEFAULT_PER_PLAYER_LIMIT = 64;
    public static final int DEFAULT_GLOBAL_LIMIT = 4096;
    private final int perPlayerLimit;
    private final int globalLimit;
    private final Map<UUID, LinkedHashSet<K>> pendingByPlayer = new HashMap<UUID, LinkedHashSet<K>>();
    private final Deque<UUID> roundRobin = new ArrayDeque<UUID>();
    private final Set<UUID> scheduledPlayers = new LinkedHashSet<UUID>();
    private int size;

    public FairTileRequestQueue() {
        this(64, 4096);
    }

    public FairTileRequestQueue(int perPlayerLimit, int globalLimit) {
        if (perPlayerLimit < 1 || globalLimit < perPlayerLimit) {
            throw new IllegalArgumentException("Invalid tile queue bounds");
        }
        this.perPlayerLimit = perPlayerLimit;
        this.globalLimit = globalLimit;
    }

    public synchronized OfferResult offer(UUID playerId, K key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        LinkedHashSet playerQueue = this.pendingByPlayer.computeIfAbsent(playerId, ignored -> new LinkedHashSet());
        if (playerQueue.contains(key)) {
            return OfferResult.DUPLICATE;
        }
        if (playerQueue.size() >= this.perPlayerLimit) {
            return OfferResult.PLAYER_FULL;
        }
        if (this.size >= this.globalLimit) {
            if (playerQueue.isEmpty()) {
                this.pendingByPlayer.remove(playerId);
            }
            return OfferResult.GLOBAL_FULL;
        }
        playerQueue.add(key);
        ++this.size;
        this.schedule(playerId);
        return OfferResult.ACCEPTED;
    }

    public synchronized Entry<K> poll() {
        return this.poll(null);
    }

    public synchronized Entry<K> poll(BiFunction<UUID, Set<K>, K> picker) {
        while (!this.roundRobin.isEmpty()) {
            Object key;
            UUID playerId = this.roundRobin.removeFirst();
            this.scheduledPlayers.remove(playerId);
            LinkedHashSet<K> queue = this.pendingByPlayer.get(playerId);
            if (queue == null || queue.isEmpty()) {
                this.pendingByPlayer.remove(playerId);
                continue;
            }
            Object k = key = picker == null ? null : (Object)picker.apply(playerId, queue);
            if (key == null || !queue.contains(key)) {
                key = queue.iterator().next();
            }
            queue.remove(key);
            --this.size;
            if (queue.isEmpty()) {
                this.pendingByPlayer.remove(playerId);
            } else {
                this.schedule(playerId);
            }
            return new Entry<Object>(playerId, key);
        }
        return null;
    }

    public synchronized void defer(Entry<K> entry) {
        if (entry != null) {
            this.offer(entry.playerId(), entry.key());
        }
    }

    public synchronized int removePlayer(UUID playerId) {
        LinkedHashSet<K> removed = this.pendingByPlayer.remove(playerId);
        this.roundRobin.removeIf(playerId::equals);
        this.scheduledPlayers.remove(playerId);
        int count = removed == null ? 0 : removed.size();
        this.size -= count;
        return count;
    }

    public synchronized int removeIf(Predicate<K> predicate) {
        int removed = 0;
        Iterator<Map.Entry<UUID, LinkedHashSet<K>>> iterator = this.pendingByPlayer.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, LinkedHashSet<K>> entry = iterator.next();
            int before = entry.getValue().size();
            entry.getValue().removeIf(predicate);
            removed += before - entry.getValue().size();
            if (!entry.getValue().isEmpty()) continue;
            iterator.remove();
            this.roundRobin.removeIf(entry.getKey()::equals);
            this.scheduledPlayers.remove(entry.getKey());
        }
        this.size -= removed;
        return removed;
    }

    public synchronized void clear() {
        this.pendingByPlayer.clear();
        this.roundRobin.clear();
        this.scheduledPlayers.clear();
        this.size = 0;
    }

    public synchronized int size() {
        return this.size;
    }

    public synchronized int playerSize(UUID playerId) {
        Set queue = this.pendingByPlayer.get(playerId);
        return queue == null ? 0 : queue.size();
    }

    private void schedule(UUID playerId) {
        if (this.scheduledPlayers.add(playerId)) {
            this.roundRobin.addLast(playerId);
        }
    }

    public static enum OfferResult {
        ACCEPTED,
        DUPLICATE,
        PLAYER_FULL,
        GLOBAL_FULL;

    }

    public record Entry<K>(UUID playerId, K key) {
    }
}

