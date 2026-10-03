/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.time.temporal.TemporalAmount;

@RemapPrefixForJS(value="kjs$")
public interface MinecraftEnvironmentKJS
extends MessageSenderKJS {
    public ScheduledEvents kjs$getScheduledEvents();

    default public ScheduledEvents.ScheduledEvent kjs$schedule(TemporalAmount timer, ScheduledEvents.Callback callback) {
        return this.kjs$getScheduledEvents().schedule(timer, false, callback);
    }

    default public ScheduledEvents.ScheduledEvent kjs$scheduleInTicks(long ticks, ScheduledEvents.Callback callback) {
        return this.kjs$getScheduledEvents().schedule(new TickDuration(ticks), false, callback);
    }

    default public ScheduledEvents.ScheduledEvent kjs$scheduleRepeating(TemporalAmount timer, ScheduledEvents.Callback callback) {
        return this.kjs$getScheduledEvents().schedule(timer, false, callback);
    }

    default public ScheduledEvents.ScheduledEvent kjs$scheduleRepeatingInTicks(long ticks, ScheduledEvents.Callback callback) {
        return this.kjs$getScheduledEvents().schedule(new TickDuration(ticks), true, callback);
    }
}

