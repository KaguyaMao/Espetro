/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public interface IForgeRegistryModifiable<V>
extends IForgeRegistry<V> {
    public void clear();

    public V remove(ResourceLocation var1);

    public boolean isLocked();
}

