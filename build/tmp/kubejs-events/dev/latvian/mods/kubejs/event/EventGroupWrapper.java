/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import java.util.HashMap;
import java.util.Set;

public class EventGroupWrapper
extends HashMap<String, BaseFunction> {
    private final ScriptType scriptType;
    private final EventGroup group;

    public EventGroupWrapper(ScriptType scriptType, EventGroup group) {
        this.scriptType = scriptType;
        this.group = group;
    }

    @Override
    public BaseFunction get(Object key) {
        String keyString = String.valueOf(key);
        EventHandler handler = this.group.getHandlers().get(keyString);
        if (handler == null) {
            this.scriptType.console.error("Unknown event '%s.%s'!".formatted(this.group.name, keyString));
            return new BaseFunction();
        }
        return handler;
    }

    @Override
    public boolean containsKey(Object key) {
        return true;
    }

    @Override
    public Set<String> keySet() {
        return this.group.getHandlers().keySet();
    }
}

