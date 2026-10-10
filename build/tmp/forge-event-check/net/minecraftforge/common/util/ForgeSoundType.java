/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.block.SoundType
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.util;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

public class ForgeSoundType
extends SoundType {
    private final Supplier<SoundEvent> breakSound;
    private final Supplier<SoundEvent> stepSound;
    private final Supplier<SoundEvent> placeSound;
    private final Supplier<SoundEvent> hitSound;
    private final Supplier<SoundEvent> fallSound;

    public ForgeSoundType(float volumeIn, float pitchIn, Supplier<SoundEvent> breakSoundIn, Supplier<SoundEvent> stepSoundIn, Supplier<SoundEvent> placeSoundIn, Supplier<SoundEvent> hitSoundIn, Supplier<SoundEvent> fallSoundIn) {
        super(volumeIn, pitchIn, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null, (SoundEvent)null);
        this.breakSound = breakSoundIn;
        this.stepSound = stepSoundIn;
        this.placeSound = placeSoundIn;
        this.hitSound = hitSoundIn;
        this.fallSound = fallSoundIn;
    }

    @NotNull
    public SoundEvent m_56775_() {
        return this.breakSound.get();
    }

    @NotNull
    public SoundEvent m_56776_() {
        return this.stepSound.get();
    }

    @NotNull
    public SoundEvent m_56777_() {
        return this.placeSound.get();
    }

    @NotNull
    public SoundEvent m_56778_() {
        return this.hitSound.get();
    }

    @NotNull
    public SoundEvent m_56779_() {
        return this.fallSound.get();
    }
}

