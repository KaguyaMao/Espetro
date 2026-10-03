/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.WrappedException
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.IEventHandler;
import dev.latvian.mods.rhino.WrappedException;
import org.jetbrains.annotations.Nullable;

public class EventHandlerContainer {
    public final Object extraId;
    public final IEventHandler handler;
    public final String source;
    public final int line;
    EventHandlerContainer child;

    public static boolean isEmpty(@Nullable EventHandlerContainer[] array) {
        if (array == null) {
            return true;
        }
        for (EventHandlerContainer c : array) {
            if (c == null) continue;
            return false;
        }
        return true;
    }

    public EventHandlerContainer(Object extraId, IEventHandler handler, String source, int line) {
        this.extraId = extraId;
        this.handler = handler;
        this.source = source;
        this.line = line;
    }

    public EventResult handle(EventJS event, EventExceptionHandler exh) throws EventExit {
        EventHandlerContainer itr = this;
        do {
            try {
                itr.handler.onEvent(event);
            }
            catch (EventExit exit) {
                throw exit;
            }
            catch (Throwable ex) {
                Throwable throwable = ex;
                while (throwable instanceof WrappedException) {
                    WrappedException e = (WrappedException)throwable;
                    throwable = e.getWrappedException();
                }
                if (throwable instanceof EventExit) {
                    EventExit exit = (EventExit)throwable;
                    throw exit;
                }
                if (exh != null && (throwable = exh.handle(event, itr, throwable)) == null) continue;
                throw EventResult.Type.ERROR.exit(throwable);
            }
        } while ((itr = itr.child) != null);
        return EventResult.PASS;
    }

    public void add(Object extraId, IEventHandler handler, String source, int line) {
        EventHandlerContainer itr = this;
        while (itr.child != null) {
            itr = itr.child;
        }
        itr.child = new EventHandlerContainer(extraId, handler, source, line);
    }

    public String toString() {
        return "Event Handler (" + this.source + ":" + this.line + ")";
    }
}

