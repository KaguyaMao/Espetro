/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package dev.latvian.mods.kubejs.core;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface LazyComponentKJS
extends Supplier<Component> {
    @Override
    public Component get();
}

