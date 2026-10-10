/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface CraftingContainerKJS {
    @Nullable
    default public AbstractContainerMenu kjs$getMenu() {
        throw new NoMixinException();
    }
}

