/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.event.level;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.LevelEvent;
import org.jetbrains.annotations.ApiStatus;

public class ChunkEvent
extends LevelEvent {
    private final ChunkAccess chunk;

    public ChunkEvent(ChunkAccess chunk) {
        super(chunk.getWorldForge());
        this.chunk = chunk;
    }

    public ChunkEvent(ChunkAccess chunk, LevelAccessor level) {
        super(level);
        this.chunk = chunk;
    }

    public ChunkAccess getChunk() {
        return this.chunk;
    }

    public static class Unload
    extends ChunkEvent {
        public Unload(ChunkAccess chunk) {
            super(chunk);
        }
    }

    public static class Load
    extends ChunkEvent {
        private final boolean newChunk;

        @ApiStatus.Internal
        public Load(ChunkAccess chunk, boolean newChunk) {
            super(chunk);
            this.newChunk = newChunk;
        }

        public boolean isNewChunk() {
            return this.newChunk;
        }
    }
}

