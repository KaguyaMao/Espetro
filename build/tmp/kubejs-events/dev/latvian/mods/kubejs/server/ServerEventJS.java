/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 */
package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.MinecraftServer;

public class ServerEventJS
extends EventJS {
    public final MinecraftServer server;

    public ServerEventJS(MinecraftServer s) {
        this.server = s;
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}

