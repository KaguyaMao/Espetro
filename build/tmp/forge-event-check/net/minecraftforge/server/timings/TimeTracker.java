/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.MapMaker
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package net.minecraftforge.server.timings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.timings.ForgeTimings;

public class TimeTracker<T> {
    public static final TimeTracker<BlockEntity> BLOCK_ENTITY_UPDATE = new TimeTracker();
    public static final TimeTracker<Entity> ENTITY_UPDATE = new TimeTracker();
    private boolean enabled;
    private int trackingDuration;
    private Map<T, int[]> timings = new MapMaker().weakKeys().makeMap();
    private WeakReference<T> currentlyTracking;
    private long trackTime;
    private long timing;

    public ImmutableList<ForgeTimings<T>> getTimingData() {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Map.Entry<T, int[]> entry : this.timings.entrySet()) {
            builder.add(new ForgeTimings<T>(entry.getKey(), Arrays.copyOfRange(entry.getValue(), 0, 99)));
        }
        return builder.build();
    }

    public void reset() {
        this.enabled = false;
        this.trackTime = 0L;
        this.timings.clear();
    }

    public void trackEnd(T tracking) {
        if (!this.enabled) {
            return;
        }
        this.trackEnd(tracking, System.nanoTime());
    }

    public void enable(int duration) {
        this.trackingDuration = duration;
        this.enabled = true;
    }

    public void trackStart(T toTrack) {
        if (!this.enabled) {
            return;
        }
        this.trackStart(toTrack, System.nanoTime());
    }

    private void trackEnd(T object, long nanoTime) {
        if (this.currentlyTracking == null || this.currentlyTracking.get() != object) {
            this.currentlyTracking = null;
            return;
        }
        int[] timings = this.timings.computeIfAbsent(object, k -> new int[101]);
        int idx = timings[100] = (timings[100] + 1) % 100;
        timings[idx] = (int)(nanoTime - this.timing);
    }

    private void trackStart(T toTrack, long nanoTime) {
        if (this.trackTime == 0L) {
            this.trackTime = nanoTime;
        } else if (this.trackTime + TimeUnit.NANOSECONDS.convert(this.trackingDuration, TimeUnit.SECONDS) < nanoTime) {
            this.enabled = false;
            this.trackTime = 0L;
        }
        this.currentlyTracking = new WeakReference<T>(toTrack);
        this.timing = nanoTime;
    }
}

