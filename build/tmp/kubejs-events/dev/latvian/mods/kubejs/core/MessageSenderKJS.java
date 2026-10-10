/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.network.chat.Component
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;

@RemapPrefixForJS(value="kjs$")
public interface MessageSenderKJS {
    default public Component kjs$getName() {
        throw new NoMixinException();
    }

    default public Component kjs$getDisplayName() {
        return this.kjs$getName();
    }

    default public void kjs$tell(Component message) {
        throw new NoMixinException();
    }

    default public void kjs$setStatusMessage(Component message) {
    }

    default public int kjs$runCommand(String command) {
        throw new NoMixinException();
    }

    default public int kjs$runCommandSilent(String command) {
        return this.kjs$runCommand(command);
    }
}

