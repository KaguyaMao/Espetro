/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;

public record BuilderType<T>(String type, Class<? extends BuilderBase<? extends T>> builderClass, BuilderFactory factory) {
}

