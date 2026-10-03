/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.LevelAccessor
 */
package net.minecraftforge.event.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;

public class SleepFinishedTimeEvent
extends LevelEvent {
    private long newTime;
    private final long minTime;

    public SleepFinishedTimeEvent(ServerLevel level, long newTime, long minTime) {
        super((LevelAccessor)level);
        this.newTime = newTime;
        this.minTime = minTime;
    }

    public long getNewTime() {
        return this.newTime;
    }

    public boolean setTimeAddition(long newTimeIn) {
        if (this.minTime > newTimeIn) {
            return false;
        }
        this.newTime = newTimeIn;
        return true;
    }
}

