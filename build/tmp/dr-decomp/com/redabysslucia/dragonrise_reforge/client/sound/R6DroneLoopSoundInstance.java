/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 */
package com.redabysslucia.dragonrise_reforge.client.sound;

import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import com.redabysslucia.dragonrise_reforge.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class R6DroneLoopSoundInstance
extends AbstractTickableSoundInstance {
    private final R6DroneEntity drone;
    private final Minecraft client;
    private final boolean fast;

    public R6DroneLoopSoundInstance(R6DroneEntity drone, Minecraft client, boolean fast) {
        super(fast ? (SoundEvent)ModSounds.R6_DRONE_FAST.get() : (SoundEvent)ModSounds.R6_DRONE_MOVING.get(), SoundSource.PLAYERS, client.f_91073_.m_213780_());
        this.drone = drone;
        this.client = client;
        this.fast = fast;
        this.f_119578_ = true;
        this.f_119579_ = 0;
        this.f_119573_ = 0.5f;
        this.f_119574_ = 1.0f;
        this.f_119575_ = drone.m_20185_();
        this.f_119576_ = drone.m_20186_();
        this.f_119577_ = drone.m_20189_();
    }

    public void m_7788_() {
        if (this.drone.m_213877_()) {
            this.m_119609_();
            return;
        }
        this.f_119575_ = this.drone.m_20185_();
        this.f_119576_ = this.drone.m_20186_();
        this.f_119577_ = this.drone.m_20189_();
    }

    public boolean isFast() {
        return this.fast;
    }
}

