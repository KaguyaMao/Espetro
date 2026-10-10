/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.ChunkPos
 */
package net.minecraftforge.common.util;

import java.util.Comparator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class CenterChunkPosComparator
implements Comparator<ChunkPos> {
    private int x;
    private int z;

    public CenterChunkPosComparator(ServerPlayer entityplayer) {
        this.x = (int)entityplayer.m_20185_() >> 4;
        this.z = (int)entityplayer.m_20189_() >> 4;
    }

    @Override
    public int compare(ChunkPos a, ChunkPos b) {
        if (a.equals((Object)b)) {
            return 0;
        }
        int ax = a.f_45578_ - this.x;
        int bx = b.f_45578_ - this.x;
        int az = a.f_45579_ - this.z;
        int bz = b.f_45579_ - this.z;
        int result = (ax - bx) * (ax + bx) + (az - bz) * (az + bz);
        if (result != 0) {
            return result;
        }
        if (ax < 0) {
            if (bx < 0) {
                return bz - az;
            }
            return -1;
        }
        if (bx < 0) {
            return 1;
        }
        return az - bz;
    }
}

