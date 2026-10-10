/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.net.NetworkEventJS;

public interface NetworkEvents {
    public static final EventGroup GROUP = EventGroup.of("NetworkEvents");
    public static final EventHandler DATA_RECEIVED = GROUP.common("dataReceived", () -> NetworkEventJS.class).extra(Extra.REQUIRES_STRING).hasResult();
}

