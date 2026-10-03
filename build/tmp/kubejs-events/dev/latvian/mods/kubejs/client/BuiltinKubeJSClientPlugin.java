/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.client.Minecraft;

public class BuiltinKubeJSClientPlugin
extends KubeJSPlugin {
    @Override
    public void clientInit() {
        Painter.INSTANCE.registerBuiltinObjects();
    }

    @Override
    public void registerEvents() {
        ClientEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Client", Minecraft.m_91087_());
        event.add("Painter", Painter.INSTANCE);
        if (event.getType().isClient()) {
            ScheduledEvents se = Minecraft.m_91087_().kjs$getScheduledEvents();
            event.add("setTimeout", (Object)new ScheduledEvents.TimeoutJSFunction(se, false, false));
            event.add("clearTimeout", (Object)new ScheduledEvents.TimeoutJSFunction(se, true, false));
            event.add("setInterval", (Object)new ScheduledEvents.TimeoutJSFunction(se, false, true));
            event.add("clearInterval", (Object)new ScheduledEvents.TimeoutJSFunction(se, true, true));
        }
    }
}

