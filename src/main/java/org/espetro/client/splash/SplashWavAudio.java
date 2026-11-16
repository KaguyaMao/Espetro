package org.espetro.client.splash;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

/**
 * 兜底音频播放：直接读 WAV（PCM16）并用 LWJGL OpenAL 播，
 * 不经过 {@code sounds.json} / SoundManager 注册表。
 *
 * <p>这条路径是给「sounds.json 条目在客户端注册表里查不到」时用的，
 * 与 Minecraft 自己的声音引擎共用同一个 OpenAL 上下文，所以不需要额外初始化。</p>
 */
public final class SplashWavAudio {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SplashWavAudio.class);


    private static final ResourceLocation WAV =
        new ResourceLocation("espetro", "splash/audio.wav");

    private int source = -1;
    private int buffer = -1;
    private boolean playing;

    /**
     * OpenAL 是否已经由 Minecraft 自己初始化完成。
     *
     * <p>资源加载期碰 {@code AL10} 会触发 lwjgl 的静态初始化并失败，而失败后的
     * {@code AL$ICDStatic} 会在整个 JVM 生命周期里保持损坏状态，接着原版
     * {@code SoundEngine} 初始化时就会抛 {@code NoClassDefFoundError} 直接崩游戏。
     * 因此这里必须等 Forge 的 {@code SoundEngineLoadEvent} 之后才允许访问 OpenAL。</p>
     */
    private static volatile boolean alReady;

    public static void setAlReady(boolean loaded) {
        alReady = loaded;
    }

    public static boolean isAlReady() {
        return alReady;
    }

    public boolean play(float volume) {
        return play(volume, 0.0F);
    }

    public boolean play(float volume, float offsetSeconds) {
        if (this.playing) {
            return true;
        }
        if (!alReady) {
            return false;
        }
        try {
            Wav wav = readWav();
            if (wav == null) {
                return false;
            }
            this.buffer = AL10.alGenBuffers();
            AL10.alBufferData(this.buffer, wav.format, wav.pcm, wav.sampleRate);
            this.source = AL10.alGenSources();
            AL10.alSourcei(this.source, AL10.AL_BUFFER, this.buffer);
            AL10.alSourcef(this.source, AL10.AL_GAIN, volume);
            AL10.alSourcei(this.source, AL10.AL_LOOPING, AL10.AL_FALSE);
            if (offsetSeconds > 0f) {
                AL10.alSourcef(this.source, AL11.AL_SEC_OFFSET,
                    Math.min(offsetSeconds, Math.max(0f, wav.durationSeconds - 0.025F)));
            }
            AL10.alSourcePlay(this.source);
            this.playing = true;
            return true;
        } catch (Throwable error) {
            LOGGER.warn("[启动视频] 直连 OpenAL 播放失败: {}", error.toString());
            release();
            return false;
        }
    }

    public boolean isPlaying() {
        if (!this.playing) {
            return false;
        }
        try {
            return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public void stop() {
        if (!this.playing) {
            return;
        }
        try {
            AL10.alSourceStop(this.source);
        } catch (Throwable ignored) {
        }
        release();
    }

    private void release() {
        this.playing = false;
        if (this.source != -1) {
            try {
                IntBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder())
                    .asIntBuffer();
                AL10.alDeleteSources(this.source);
            } catch (Throwable ignored) {
            }
            this.source = -1;
        }
        if (this.buffer != -1) {
            try {
                AL10.alDeleteBuffers(this.buffer);
            } catch (Throwable ignored) {
            }
            this.buffer = -1;
        }
    }

    /**
     * 优先直接从 jar 读：资源加载期资源包还没就绪，{@code getResourceManager().open()}
     * 可能拿不到文件。读不到再退回资源包（允许资源包替换音频）。
     */
    private static byte[] readBytes() {
        try {
            InputStream stream = SplashWavAudio.class.getClassLoader()
                .getResourceAsStream("assets/espetro/splash/audio.wav");
            if (stream != null) {
                try (InputStream in = stream) {
                    return in.readAllBytes();
                }
            }
        } catch (Throwable ignored) {
        }
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(WAV)) {
            return stream.readAllBytes();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Wav readWav() throws Exception {
        byte[] bytes = readBytes();
        if (bytes == null) {
            return null;
        }
        {
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            if (bytes.length < 44
                || buffer.get(0) != 'R' || buffer.get(1) != 'I'
                || buffer.get(2) != 'F' || buffer.get(3) != 'F') {
                return null;
            }
            int sampleRate = 44100;
            int channels = 2;
            int bits = 16;
            int dataOffset = -1;
            int dataLength = 0;
            int cursor = 12;
            while (cursor + 8 <= bytes.length) {
                int id = cursor;
                int size = buffer.getInt(cursor + 4);
                String tag = "" + (char) bytes[id] + (char) bytes[id + 1]
                    + (char) bytes[id + 2] + (char) bytes[id + 3];
                if ("fmt ".equals(tag)) {
                    channels = buffer.getShort(cursor + 10) & 0xFFFF;
                    sampleRate = buffer.getInt(cursor + 12);
                    bits = buffer.getShort(cursor + 22) & 0xFFFF;
                } else if ("data".equals(tag)) {
                    dataOffset = cursor + 8;
                    dataLength = Math.min(size, bytes.length - dataOffset);
                    break;
                }
                cursor += 8 + size + (size & 1);
            }
            if (dataOffset < 0 || dataLength <= 0) {
                return null;
            }
            int format = channels == 1
                ? (bits == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16)
                : (bits == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16);
            ByteBuffer pcm = ByteBuffer.allocateDirect(dataLength).order(ByteOrder.nativeOrder());
            pcm.put(bytes, dataOffset, dataLength).flip();
            float durationSeconds = dataLength / (float) (sampleRate * channels * (bits / 8));
            return new Wav(pcm, format, sampleRate, durationSeconds);
        }
    }

    private record Wav(ByteBuffer pcm, int format, int sampleRate, float durationSeconds) {
    }
}
