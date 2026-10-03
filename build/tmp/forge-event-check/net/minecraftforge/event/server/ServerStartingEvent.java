/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 */
package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerLifecycleEvent;

public class ServerStartingEvent
extends ServerLifecycleEvent {
    public ServerStartingEvent(MinecraftServer server) {
        super(server);
    }
}

