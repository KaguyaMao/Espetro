/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public interface IForgeRegistryInternal<V>
extends IForgeRegistry<V> {
    public void setSlaveMap(ResourceLocation var1, Object var2);

    public void register(int var1, ResourceLocation var2, V var3);

    public V getValue(int var1);
}

