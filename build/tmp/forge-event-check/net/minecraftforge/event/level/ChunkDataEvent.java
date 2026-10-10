/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkStatus$ChunkType
 */
package net.minecraftforge.event.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.event.level.ChunkEvent;

public class ChunkDataEvent
extends ChunkEvent {
    private final CompoundTag data;

    public ChunkDataEvent(ChunkAccess chunk, CompoundTag data) {
        super(chunk);
        this.data = data;
    }

    public ChunkDataEvent(ChunkAccess chunk, LevelAccessor world, CompoundTag data) {
        super(chunk, world);
        this.data = data;
    }

    public CompoundTag getData() {
        return this.data;
    }

    public static class Save
    extends ChunkDataEvent {
        public Save(ChunkAccess chunk, LevelAccessor world, CompoundTag data) {
            super(chunk, world, data);
        }
    }

    public static class Load
    extends ChunkDataEvent {
        private ChunkStatus.ChunkType status;

        public Load(ChunkAccess chunk, CompoundTag data, ChunkStatus.ChunkType status) {
            super(chunk, data);
            this.status = status;
        }

        public ChunkStatus.ChunkType getStatus() {
            return this.status;
        }
    }
}

