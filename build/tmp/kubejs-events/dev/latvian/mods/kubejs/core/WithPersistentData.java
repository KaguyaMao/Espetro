/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  net.minecraft.nbt.CompoundTag
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.nbt.CompoundTag;

public interface WithPersistentData
extends MessageSenderKJS {
    @RemapForJS(value="getPersistentData")
    default public CompoundTag kjs$getPersistentData() {
        throw new NoMixinException();
    }
}

