/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.List;
import net.minecraft.client.Minecraft;

@Info(value="Invoked when the debug info is rendered.\n")
public class DebugInfoEventJS
extends ClientEventJS {
    private final List<String> lines;

    public DebugInfoEventJS(List<String> l) {
        this.lines = l;
    }

    @Info(value="Whether the debug info should be rendered.")
    public boolean getShowDebug() {
        return Minecraft.m_91087_().f_91066_.f_92063_;
    }

    @Info(value="The lines of debug info. Mutating this list will change the debug info.")
    public List<String> getLines() {
        return this.lines;
    }
}

