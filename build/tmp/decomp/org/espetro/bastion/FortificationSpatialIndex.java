/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

final class FortificationSpatialIndex {
    private final Map<Key, Set<UUID>> chunks = new HashMap<Key, Set<UUID>>();
    private final Map<UUID, Entry> entries = new HashMap<UUID, Entry>();

    FortificationSpatialIndex() {
    }

    void clear() {
        this.chunks.clear();
        this.entries.clear();
    }

    void put(UUID id, String dimension, AABB box) {
        this.remove(id);
        Entry entry = new Entry(dimension, box, FortificationSpatialIndex.coveredChunks(box));
        this.entries.put(id, entry);
        for (long chunk : entry.chunks) {
            this.chunks.computeIfAbsent(new Key(dimension, chunk), ignored -> new HashSet()).add(id);
        }
    }

    void remove(UUID id) {
        Entry previous = this.entries.remove(id);
        if (previous == null) {
            return;
        }
        for (long chunk : previous.chunks) {
            Key key = new Key(previous.dimension, chunk);
            Set<UUID> bucket = this.chunks.get(key);
            if (bucket == null) continue;
            bucket.remove(id);
            if (!bucket.isEmpty()) continue;
            this.chunks.remove(key);
        }
    }

    List<UUID> query(String dimension, AABB bounds) {
        HashSet<UUID> result = new HashSet<UUID>();
        for (long chunk : FortificationSpatialIndex.coveredChunks(bounds)) {
            Set<UUID> bucket = this.chunks.get(new Key(dimension, chunk));
            if (bucket == null) continue;
            for (UUID id : bucket) {
                Entry entry = this.entries.get(id);
                if (entry == null || !entry.box.m_82381_(bounds)) continue;
                result.add(id);
            }
        }
        return List.copyOf(result);
    }

    int size() {
        return this.entries.size();
    }

    private static List<Long> coveredChunks(AABB box) {
        int minX = new ChunkPos((BlockPos)BlockPos.m_274561_((double)box.f_82288_, (double)box.f_82289_, (double)box.f_82290_)).f_45578_;
        int maxX = new ChunkPos((BlockPos)BlockPos.m_274561_((double)Math.nextDown((double)box.f_82291_), (double)box.f_82292_, (double)box.f_82293_)).f_45578_;
        int minZ = new ChunkPos((BlockPos)BlockPos.m_274561_((double)box.f_82288_, (double)box.f_82289_, (double)box.f_82290_)).f_45579_;
        int maxZ = new ChunkPos((BlockPos)BlockPos.m_274561_((double)box.f_82291_, (double)box.f_82292_, (double)Math.nextDown((double)box.f_82293_))).f_45579_;
        ArrayList<Long> result = new ArrayList<Long>((maxX - minX + 1) * (maxZ - minZ + 1));
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                result.add(ChunkPos.m_45589_(x, z));
            }
        }
        return result;
    }

    private record Entry(String dimension, AABB box, List<Long> chunks) {
    }

    private record Key(String dimension, long chunk) {
    }
}

