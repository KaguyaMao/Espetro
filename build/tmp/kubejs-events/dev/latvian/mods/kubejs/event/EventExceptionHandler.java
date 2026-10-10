/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.event.EventHandlerContainer;
import dev.latvian.mods.kubejs.event.EventJS;

@FunctionalInterface
public interface EventExceptionHandler {
    public Throwable handle(EventJS var1, EventHandlerContainer var2, Throwable var3);
}

