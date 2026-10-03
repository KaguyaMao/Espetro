/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;

public class ServerLifecycleEvent
extends Event {
    protected final MinecraftServer server;

    public ServerLifecycleEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}

