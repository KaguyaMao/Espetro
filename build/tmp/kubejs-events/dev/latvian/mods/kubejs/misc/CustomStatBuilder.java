/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;

public class CustomStatBuilder
extends BuilderBase<ResourceLocation> {
    public CustomStatBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public final RegistryInfo getRegistryType() {
        return RegistryInfo.CUSTOM_STAT;
    }

    @Override
    public ResourceLocation createObject() {
        return this.id;
    }
}

