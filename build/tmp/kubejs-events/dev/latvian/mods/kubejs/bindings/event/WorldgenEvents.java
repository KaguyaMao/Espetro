/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.level.gen.AddWorldgenEventJS;
import dev.latvian.mods.kubejs.level.gen.RemoveWorldgenEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;

public interface WorldgenEvents {
    public static final EventGroup GROUP = EventGroup.of("WorldgenEvents");
    public static final EventHandler ADD = GROUP.startup("add", () -> AddWorldgenEventJS.class);
    public static final EventHandler REMOVE = GROUP.startup("remove", () -> RemoveWorldgenEventJS.class);

    public static void post() {
        REMOVE.post(ScriptType.STARTUP, new RemoveWorldgenEventJS());
        ADD.post(ScriptType.STARTUP, new AddWorldgenEventJS());
    }
}

