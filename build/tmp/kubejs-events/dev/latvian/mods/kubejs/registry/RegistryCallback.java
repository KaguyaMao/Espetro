/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.registry;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public interface RegistryCallback<T> {
    public void accept(ResourceLocation var1, Supplier<T> var2);
}

