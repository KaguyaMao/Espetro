/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ScriptPackInfo {
    public final String namespace;
    public final Component displayName;
    public final List<ScriptFileInfo> scripts;
    public final String pathStart;

    public ScriptPackInfo(String n, String p) {
        this.namespace = n;
        this.scripts = new ArrayList<ScriptFileInfo>();
        this.pathStart = p;
        this.displayName = Component.m_237113_((String)this.namespace);
    }
}

