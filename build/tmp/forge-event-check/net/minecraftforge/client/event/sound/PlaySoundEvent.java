/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundEngine
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraftforge.client.event.sound.SoundEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class PlaySoundEvent
extends SoundEvent {
    private final String name;
    private final SoundInstance originalSound;
    @Nullable
    private SoundInstance sound;

    @ApiStatus.Internal
    public PlaySoundEvent(SoundEngine manager, SoundInstance sound) {
        super(manager);
        this.originalSound = sound;
        this.name = sound.m_7904_().m_135815_();
        this.setSound(sound);
    }

    public String getName() {
        return this.name;
    }

    public SoundInstance getOriginalSound() {
        return this.originalSound;
    }

    @Nullable
    public SoundInstance getSound() {
        return this.sound;
    }

    public void setSound(@Nullable SoundInstance newSound) {
        this.sound = newSound;
    }
}

