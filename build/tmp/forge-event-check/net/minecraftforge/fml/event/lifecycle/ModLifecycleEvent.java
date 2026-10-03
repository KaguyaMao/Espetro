/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.InterModComms
 *  net.minecraftforge.fml.InterModComms$IMCMessage
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.event.IModBusEvent
 */
package net.minecraftforge.fml.event.lifecycle;

import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;

public class ModLifecycleEvent
extends Event
implements IModBusEvent {
    private final ModContainer container;

    public ModLifecycleEvent(ModContainer container) {
        this.container = container;
    }

    public final String description() {
        String cn = ((Object)((Object)this)).getClass().getName();
        return cn.substring(cn.lastIndexOf(46) + 1);
    }

    public Stream<InterModComms.IMCMessage> getIMCStream() {
        return InterModComms.getMessages((String)this.container.getModId());
    }

    public Stream<InterModComms.IMCMessage> getIMCStream(Predicate<String> methodFilter) {
        return InterModComms.getMessages((String)this.container.getModId(), methodFilter);
    }

    ModContainer getContainer() {
        return this.container;
    }

    public String toString() {
        return this.description();
    }
}

