/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.client.Minecraft;

public class ScheduledClientEvent
extends ScheduledEvents.ScheduledEvent {
    public final Minecraft client;

    public static ScheduledEvents make(Minecraft client) {
        return new ScheduledEvents(() -> new ScheduledClientEvent(client));
    }

    public ScheduledClientEvent(Minecraft client) {
        this.client = client;
    }
}

