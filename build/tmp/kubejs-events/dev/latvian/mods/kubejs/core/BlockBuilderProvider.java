/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface BlockBuilderProvider {
    @Nullable
    default public BlockBuilder kjs$getBlockBuilder() {
        throw new NoMixinException();
    }
}

