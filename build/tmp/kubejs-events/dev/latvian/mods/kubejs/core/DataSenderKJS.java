/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface DataSenderKJS {
    default public void kjs$sendData(String channel, @Nullable CompoundTag data) {
        throw new NoMixinException();
    }

    default public void kjs$sendData(String channel) {
        this.kjs$sendData(channel, null);
    }
}

