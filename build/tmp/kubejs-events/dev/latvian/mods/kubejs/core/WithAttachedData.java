/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.rhino.util.RemapForJS;

public interface WithAttachedData<T>
extends MessageSenderKJS {
    @RemapForJS(value="getData")
    public AttachedData<T> kjs$getData();
}

