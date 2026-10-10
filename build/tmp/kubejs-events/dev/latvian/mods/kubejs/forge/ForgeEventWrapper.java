/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.IEventBus
 */
package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.forge.ForgeEventConsumer;
import dev.latvian.mods.kubejs.forge.GenericForgeEventConsumer;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.function.Consumer;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public record ForgeEventWrapper(String name, IEventBus eventBus) {
    public Object onEvent(Object eventClass, ForgeEventConsumer consumer) {
        if (!(eventClass instanceof CharSequence) && !(eventClass instanceof Class)) {
            throw new RuntimeException("Invalid syntax! " + this.name + ".onEvent(eventType, function) requires event class and handler");
        }
        if (!KubeJS.getStartupScriptManager().firstLoad) {
            ConsoleJS.STARTUP.warn(this.name + ".onEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
            return null;
        }
        try {
            Class c;
            Class type = eventClass instanceof Class ? (c = (Class)eventClass) : Class.forName(eventClass.toString());
            this.eventBus.addListener(EventPriority.NORMAL, false, type, (Consumer)consumer);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public Object onGenericEvent(Object eventClass, Object genericClass, GenericForgeEventConsumer consumer) {
        if (!(eventClass instanceof CharSequence) && !(eventClass instanceof Class) || !(genericClass instanceof CharSequence) && !(genericClass instanceof Class)) {
            throw new RuntimeException("Invalid syntax! " + this.name + ".onGenericEvent(eventType, genericType, function) requires event class, generic class and handler");
        }
        if (!KubeJS.getStartupScriptManager().firstLoad) {
            ConsoleJS.STARTUP.warn(this.name + ".onGenericEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
            return null;
        }
        try {
            Class c;
            Class c2;
            Class type = eventClass instanceof Class ? (c2 = (Class)eventClass) : Class.forName(eventClass.toString());
            Class genericType = genericClass instanceof Class ? (c = (Class)genericClass) : Class.forName(genericClass.toString());
            this.eventBus.addGenericListener(genericType, EventPriority.NORMAL, false, type, (Consumer)consumer);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}

