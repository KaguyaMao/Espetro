/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 */
package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder
extends BuilderBase<SoundEvent> {
    public SoundEventBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public final RegistryInfo getRegistryType() {
        return RegistryInfo.SOUND_EVENT;
    }

    @Override
    public SoundEvent createObject() {
        return SoundEvent.m_262824_((ResourceLocation)this.id);
    }
}

