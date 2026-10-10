/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;

@RemapPrefixForJS(value="kjs$")
public interface BlockStateKJS {
    default public void kjs$setDestroySpeed(float v) {
        throw new NoMixinException();
    }

    default public void kjs$setRequiresTool(boolean v) {
        throw new NoMixinException();
    }

    default public void kjs$setLightEmission(int v) {
        throw new NoMixinException();
    }
}

