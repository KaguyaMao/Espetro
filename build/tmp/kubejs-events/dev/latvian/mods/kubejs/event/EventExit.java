/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.event.EventResult;

public class EventExit
extends Exception {
    public final EventResult result;

    public EventExit(EventResult result) {
        super("result", null, false, false);
        this.result = result;
    }
}

