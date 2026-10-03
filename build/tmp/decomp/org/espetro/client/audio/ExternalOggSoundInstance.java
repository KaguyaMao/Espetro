/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.audio;

import com.mojang.blaze3d.audio.OggAudioStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;

final class ExternalOggSoundInstance
extends AbstractSoundInstance {
    private final Path file;
    private final Sound externalSound;
    private final WeighedSoundEvents resolvedEvent;

    ExternalOggSoundInstance(Path file, SoundSource source, String channelName) {
        super(new ResourceLocation("espetro", "external_audio/" + channelName), source, SoundInstance.m_235150_());
        this.file = file;
        this.f_119573_ = 1.0f;
        this.f_119574_ = 1.0f;
        this.f_119578_ = false;
        this.f_119582_ = true;
        this.f_119580_ = SoundInstance.Attenuation.NONE;
        this.externalSound = new Sound("espetro:external_audio/stream", ConstantFloat.m_146458_(1.0f), ConstantFloat.m_146458_(1.0f), 1, Sound.Type.FILE, true, false, 16);
        this.resolvedEvent = new WeighedSoundEvents(this.f_119572_, null);
        this.resolvedEvent.m_120451_(this.externalSound);
        this.f_119570_ = this.externalSound;
    }

    @Override
    public WeighedSoundEvents m_6775_(SoundManager soundManager) {
        this.f_119570_ = this.externalSound;
        return this.resolvedEvent;
    }

    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new OggAudioStream(Files.newInputStream(this.file, new OpenOption[0]));
            }
            catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, Util.m_183991_());
    }
}

