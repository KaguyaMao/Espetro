/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObject;

@FunctionalInterface
public interface PainterFactory {
    public PainterObject create(Painter var1);
}

