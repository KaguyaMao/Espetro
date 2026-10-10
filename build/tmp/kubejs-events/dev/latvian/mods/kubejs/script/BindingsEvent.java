/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Scriptable
 */
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.Scriptable;

public class BindingsEvent {
    public final ScriptManager manager;
    public final Scriptable scope;

    public BindingsEvent(ScriptManager m, Scriptable s) {
        this.manager = m;
        this.scope = s;
    }

    public ScriptType getType() {
        return this.manager.scriptType;
    }

    public void add(String name, Object value) {
        if (value != null) {
            this.manager.context.addToScope(this.scope, name, value);
        }
    }
}

