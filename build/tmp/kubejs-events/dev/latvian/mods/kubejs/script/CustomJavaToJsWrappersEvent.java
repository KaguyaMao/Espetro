/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.CustomJavaToJsWrapperProvider
 */
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapperProvider;
import java.util.function.Predicate;

public class CustomJavaToJsWrappersEvent {
    public final ScriptManager manager;

    public CustomJavaToJsWrappersEvent(ScriptManager m) {
        this.manager = m;
    }

    public ScriptType getScriptType() {
        return this.manager.scriptType;
    }

    public <T> void add(Class<T> type, CustomJavaToJsWrapperProvider<T> provider) {
        this.manager.context.addCustomJavaToJsWrapper(type, provider);
    }

    public <T> void add(Predicate<T> predicate, CustomJavaToJsWrapperProvider<T> provider) {
        this.manager.context.addCustomJavaToJsWrapper(predicate, provider);
    }
}

