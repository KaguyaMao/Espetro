/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.Channel
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundEngine
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.client.event.sound.SoundEvent;
import org.jetbrains.annotations.ApiStatus;

public class PlayStreamingSourceEvent
extends SoundEvent.SoundSourceEvent {
    @ApiStatus.Internal
    public PlayStreamingSourceEvent(SoundEngine engine, SoundInstance sound, Channel channel) {
        super(engine, sound, channel);
    }
}

