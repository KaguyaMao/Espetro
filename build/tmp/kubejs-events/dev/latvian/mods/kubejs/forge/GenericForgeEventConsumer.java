/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.GenericEvent
 */
package dev.latvian.mods.kubejs.forge;

import java.util.function.Consumer;
import net.minecraftforge.eventbus.api.GenericEvent;

@FunctionalInterface
public interface GenericForgeEventConsumer
extends Consumer<GenericEvent<?>> {
}

