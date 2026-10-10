/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.OggAudioStream
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.client.sounds.SoundBufferLibrary
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.client.audio;

import LOL_141.vehicle_addition.api.netease.NetEaseResolver;
import LOL_141.vehicle_addition.client.audio.RadioAudioStream;
import LOL_141.vehicle_addition.client.audio.SuperbwarfateSoundEvents;
import LOL_141.vehicle_addition.client.event.RadioHudRenderer;
import LOL_141.vehicle_addition.radio.NetEaseSongManager;
import LOL_141.vehicle_addition.radio.RadioConfig;
import com.mojang.blaze3d.audio.OggAudioStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.sound.sampled.AudioFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RadioSoundInstance
extends AbstractTickableSoundInstance {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation ERROR_SOUND = new ResourceLocation("vehicle_addition", "sounds/error.ogg");
    private final URL streamUrl;
    private final boolean looping;
    private final boolean followPlayer;
    private volatile boolean cancelled = false;
    private long songId = -1L;
    private long skipMs = 0L;

    public RadioSoundInstance(URL streamUrl, boolean looping) {
        this(streamUrl, looping, true);
    }

    public RadioSoundInstance(URL streamUrl, boolean looping, boolean relative) {
        super((SoundEvent)SuperbwarfateSoundEvents.RADIO.get(), RadioConfig.getSoundCategory(), SoundInstance.m_235150_());
        this.streamUrl = streamUrl;
        this.looping = looping;
        this.followPlayer = relative;
        this.f_119582_ = relative;
        this.f_119573_ = 1.0f;
    }

    public static RadioSoundInstance fromUrlPositional(String url, float x, float y, float z, boolean positional) throws MalformedURLException {
        RadioSoundInstance instance = new RadioSoundInstance(new URL(url), true, !positional);
        instance.setPosition(x, y, z);
        instance.f_119582_ = !positional;
        return instance;
    }

    public void setPosition(float x, float y, float z) {
        this.f_119575_ = x;
        this.f_119576_ = y;
        this.f_119577_ = z;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public void setSkipMs(long skipMs) {
        this.skipMs = Math.max(0L, skipMs);
    }

    public void m_7788_() {
        if (this.followPlayer) {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ != null) {
                this.setPosition((float)mc.f_91074_.m_20185_(), (float)mc.f_91074_.m_20186_(), (float)mc.f_91074_.m_20189_());
            }
        }
    }

    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.cancelled) {
                return new SilentAudioStream();
            }
            try {
                long byteOffset = this.computeByteOffset(this.skipMs);
                RadioAudioStream stream = new RadioAudioStream(this.streamUrl, byteOffset);
                if (this.cancelled) {
                    RadioSoundInstance.closeQuietly(stream);
                    return new SilentAudioStream();
                }
                return stream;
            }
            catch (Exception e) {
                LOGGER.error("Failed to open radio stream {}: {}", (Object)this.streamUrl, (Object)e.getMessage());
                if (this.songId > 0L) {
                    try {
                        LOGGER.info("Re-resolving expired url for song {}", (Object)this.songId);
                        String newUrl = NetEaseResolver.resolvePlayUrl(this.songId);
                        if (newUrl != null && !newUrl.isEmpty() && !newUrl.equals(this.streamUrl.toString())) {
                            NetEaseSongManager.refreshSongUrl(this.songId, newUrl);
                            RadioAudioStream retry = new RadioAudioStream(new URL(newUrl), this.computeByteOffset(this.skipMs));
                            if (this.cancelled) {
                                RadioSoundInstance.closeQuietly(retry);
                                return new SilentAudioStream();
                            }
                            return retry;
                        }
                    }
                    catch (Exception ex) {
                        LOGGER.error("Re-resolve failed for song {}", (Object)this.songId, (Object)ex);
                    }
                }
                RadioHudRenderer.show((Component)Component.m_237115_((String)"message.vehicle_addition.play_error"));
                try {
                    InputStream inputstream = Minecraft.m_91087_().m_91098_().m_215595_(ERROR_SOUND);
                    return new OggAudioStream(inputstream);
                }
                catch (IOException ioexception) {
                    throw new CompletionException(ioexception);
                }
            }
        }, Util.m_183991_());
    }

    private static void closeQuietly(AudioStream stream) {
        try {
            stream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private long computeByteOffset(long ms) {
        if (ms <= 0L) {
            return 0L;
        }
        long bytes = ms * 40000L / 1000L;
        return bytes / 1044L * 1044L;
    }

    public static class SilentAudioStream
    implements AudioStream {
        private final AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, false);

        public AudioFormat m_6206_() {
            return this.format;
        }

        public ByteBuffer m_7118_(int size) {
            return null;
        }

        public void close() {
        }
    }
}

