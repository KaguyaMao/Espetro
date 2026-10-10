/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.sounds.SoundEngine
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event.sound;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class SoundEngineLoadEvent
extends SoundEvent
implements IModBusEvent {
    @ApiStatus.Internal
    public SoundEngineLoadEvent(SoundEngine manager) {
        super(manager);
    }
}

