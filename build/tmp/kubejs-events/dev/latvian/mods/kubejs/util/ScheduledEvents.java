/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaObject
 *  dev.latvian.mods.rhino.RhinoException
 *  dev.latvian.mods.rhino.ScriptRuntime
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.ScriptableObject
 *  dev.latvian.mods.rhino.Undefined
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Undefined;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ScheduledEvents {
    public final Supplier<ScheduledEvent> factory;
    public final LinkedList<ScheduledEvent> events;
    public final LinkedList<ScheduledEvent> futureEvents;
    public final AtomicInteger nextId;
    public long currentMillis;
    public long currentTick;

    public ScheduledEvents(Supplier<ScheduledEvent> factory) {
        this.factory = factory;
        this.events = new LinkedList();
        this.futureEvents = new LinkedList();
        this.nextId = new AtomicInteger(0);
        this.currentMillis = 0L;
        this.currentTick = 0L;
    }

    public ScheduledEvent schedule(TemporalAmount timer, boolean repeating, Callback callback) {
        if (timer instanceof TickDuration) {
            TickDuration duration = (TickDuration)timer;
            return this.schedule(duration.ticks(), true, repeating, callback);
        }
        if (timer instanceof Duration) {
            Duration duration = (Duration)timer;
            return this.schedule(duration.toMillis(), false, repeating, callback);
        }
        throw new IllegalArgumentException("Unsupported TemporalAmount: " + String.valueOf(timer));
    }

    public ScheduledEvent schedule(long timer, boolean ofTicks, boolean repeating, Callback callback) {
        ScheduledEvent e = new ScheduledEvent();
        e.scheduledEvents = this;
        e.id = this.nextId.incrementAndGet();
        e.ofTicks = ofTicks;
        e.repeating = repeating;
        e.timer = timer;
        e.callback = callback;
        e.reschedule();
        this.futureEvents.add(e);
        return e;
    }

    public void tickAll(long nowTicks) {
        this.currentMillis = System.currentTimeMillis();
        this.currentTick = nowTicks;
        if (!this.futureEvents.isEmpty()) {
            this.events.addAll(this.futureEvents);
            this.futureEvents.clear();
        }
        if (!this.events.isEmpty()) {
            this.events.removeIf(ScheduledEvent.TICK);
        }
    }

    public void clear(int id) {
        for (ScheduledEvent event : this.events) {
            if (event.id != id) continue;
            event.callback = null;
            break;
        }
        for (ScheduledEvent event : this.futureEvents) {
            if (event.id != id) continue;
            event.callback = null;
            return;
        }
    }

    @FunctionalInterface
    public static interface Callback {
        public void onCallback(ScheduledEvent var1);
    }

    public static class ScheduledEvent {
        private static final Predicate<ScheduledEvent> TICK = ScheduledEvent::tick;
        public ScheduledEvents scheduledEvents;
        public int id;
        public boolean ofTicks;
        public boolean repeating;
        public long timer;
        public long endTime;
        public transient Callback callback;

        public ScheduledEvent reschedule() {
            this.endTime = (this.ofTicks ? this.scheduledEvents.currentTick : this.scheduledEvents.currentMillis) + this.timer;
            return this;
        }

        public ScheduledEvent reschedule(long timer) {
            this.timer = timer;
            return this.reschedule();
        }

        public void clear() {
            this.callback = null;
        }

        private boolean tick() {
            if (this.callback == null) {
                return true;
            }
            if ((this.ofTicks ? this.scheduledEvents.currentTick : this.scheduledEvents.currentMillis) >= this.endTime) {
                try {
                    this.callback.onCallback(this);
                }
                catch (RhinoException ex) {
                    ConsoleJS.SERVER.error("Error occurred while handling scheduled event callback: " + ex.getMessage());
                }
                catch (Throwable ex) {
                    ex.printStackTrace();
                }
                if (this.repeating) {
                    this.reschedule();
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    public static class TimeoutJSFunction
    extends BaseFunction {
        public final ScheduledEvents scheduledEvents;
        public final boolean clear;
        public final boolean interval;

        public TimeoutJSFunction(ScheduledEvents scheduledEvents, boolean clear, boolean interval) {
            this.scheduledEvents = scheduledEvents;
            this.clear = clear;
            this.interval = interval;
        }

        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            if (this.clear) {
                this.scheduledEvents.clear(ScriptRuntime.toInt32((Context)cx, (Object)args[0]));
                return Undefined.instance;
            }
            TemporalAmount timer = (TemporalAmount)Context.jsToJava((Context)cx, (Object)args[1], TemporalAmount.class);
            Callback callback = (Callback)NativeJavaObject.createInterfaceAdapter((Context)cx, Callback.class, (ScriptableObject)((ScriptableObject)args[0]));
            return this.scheduledEvents.schedule((TemporalAmount)timer, (boolean)this.interval, (Callback)callback).id;
        }
    }
}

