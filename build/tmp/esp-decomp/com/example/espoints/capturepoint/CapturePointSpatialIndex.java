/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.ChunkPos
 */
package com.example.espoints.capturepoint;

import com.example.espoints.capturepoint.CapturePoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

final class CapturePointSpatialIndex {
    private static final long MAX_CHUNKS_PER_POINT = 65536L;
    private final Map<Long, List<CapturePoint>> byChunk = new HashMap<Long, List<CapturePoint>>();
    private List<CapturePoint> oversized = List.of();

    CapturePointSpatialIndex() {
    }

    void rebuild(Collection<CapturePoint> points) {
        this.byChunk.clear();
        ArrayList<CapturePoint> large = new ArrayList<CapturePoint>();
        for (CapturePoint point : points) {
            int minChunkX = Math.min(point.getPos1().m_123341_(), point.getPos2().m_123341_()) >> 4;
            int maxChunkX = Math.max(point.getPos1().m_123341_(), point.getPos2().m_123341_()) >> 4;
            int minChunkZ = Math.min(point.getPos1().m_123343_(), point.getPos2().m_123343_()) >> 4;
            int maxChunkZ = Math.max(point.getPos1().m_123343_(), point.getPos2().m_123343_()) >> 4;
            long chunkCount = (long)maxChunkX - (long)minChunkX + 1L;
            if ((chunkCount *= (long)maxChunkZ - (long)minChunkZ + 1L) <= 0L || chunkCount > 65536L) {
                large.add(point);
                continue;
            }
            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                    this.byChunk.computeIfAbsent(ChunkPos.m_45589_((int)chunkX, (int)chunkZ), ignored -> new ArrayList()).add(point);
                }
            }
        }
        this.oversized = List.copyOf(large);
    }

    List<CapturePoint> candidates(BlockPos pos) {
        List<CapturePoint> local = this.byChunk.get(ChunkPos.m_45589_((int)(pos.m_123341_() >> 4), (int)(pos.m_123343_() >> 4)));
        if (this.oversized.isEmpty()) {
            return local == null ? List.of() : local;
        }
        if (local == null || local.isEmpty()) {
            return this.oversized;
        }
        ArrayList<CapturePoint> result = new ArrayList<CapturePoint>(local.size() + this.oversized.size());
        result.addAll(local);
        result.addAll(this.oversized);
        return result;
    }
}

