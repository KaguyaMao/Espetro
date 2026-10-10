/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.mixin.common.EventHandlerInvoker;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={EventHandler.class}, remap=false)
public abstract class EventHandlerBridgeMixin {
    public EventHandler cancelable() {
        return ((EventHandlerInvoker)((Object)this)).callHasResult();
    }

    public boolean post(Object extraId, EventJS event) {
        return ((EventHandlerInvoker)((Object)this)).callPost(event, extraId).interruptFalse();
    }

    public boolean post(EventJS event) {
        return ((EventHandlerInvoker)((Object)this)).callPost(event, null).interruptFalse();
    }
}

