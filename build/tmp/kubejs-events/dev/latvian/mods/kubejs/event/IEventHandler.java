/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventJS;

@FunctionalInterface
public interface IEventHandler {
    public Object onEvent(EventJS var1) throws EventExit;
}

