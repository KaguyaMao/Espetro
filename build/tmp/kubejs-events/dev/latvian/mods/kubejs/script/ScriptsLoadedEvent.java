/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.Event
 *  dev.architectury.event.EventFactory
 */
package dev.latvian.mods.kubejs.script;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public class ScriptsLoadedEvent {
    public static final Event<Runnable> EVENT = EventFactory.createLoop(Runnable.class);
}

