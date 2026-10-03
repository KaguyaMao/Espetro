/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.sounds.AudioStream
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.BufferUtils
 */
package LOL_141.vehicle_addition.client.audio;

import LOL_141.vehicle_addition.client.api.AudioStreamHandlerManager;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.minecraft.client.sounds.AudioStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

public class RadioAudioStream
implements AudioStream {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ExecutorService AUDIO_STREAM_EXECUTOR = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "MusicRadio-AudioStream-Downloader");
        t.setDaemon(true);
        return t;
    });
    private final AudioInputStream stream;
    private final int frameSize;
    private final byte[] frame;
    private final int streamingBufferSize;
    private final ConcurrentLinkedQueue<ByteBuffer> audioDataQueue = new ConcurrentLinkedQueue();
    private final AtomicBoolean loading = new AtomicBoolean(false);
    private volatile Throwable failed;

    public RadioAudioStream(URL url) throws UnsupportedAudioFileException, IOException {
        this(url, 0L);
    }

    public RadioAudioStream(URL url, long byteOffset) throws UnsupportedAudioFileException, IOException {
        AudioInputStream originalInputStream = AudioStreamHandlerManager.handle(url, byteOffset);
        AudioFormat originalFormat = originalInputStream.getFormat();
        AudioFormat targetFormat = this.getTargetPCMAudioFormat(originalFormat);
        AudioInputStream targetInputStream = AudioSystem.getAudioInputStream(targetFormat, originalInputStream);
        targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, originalFormat.getSampleRate(), 16, 2, 4, originalFormat.getSampleRate(), false);
        this.stream = AudioSystem.getAudioInputStream(targetFormat, targetInputStream);
        this.frameSize = this.stream.getFormat().getFrameSize();
        this.frame = new byte[this.frameSize];
        this.streamingBufferSize = RadioAudioStream.calculateBufferSize(this.stream.getFormat(), 1);
        this.pumpBuffers(4);
    }

    private static int calculateBufferSize(AudioFormat format, int seconds) {
        float bytesPerSample = (float)format.getSampleSizeInBits() / 8.0f;
        int channels = format.getChannels();
        float sampleRate = format.getSampleRate();
        return (int)((float)seconds * bytesPerSample * (float)channels * sampleRate);
    }

    private void pumpBuffers(int readCount) {
        try {
            for (int i = 0; i < readCount; ++i) {
                int count;
                ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int)this.streamingBufferSize);
                int bytesRead = 0;
                do {
                    if ((count = this.stream.read(this.frame)) == -1) continue;
                    byteBuffer.put(this.frame, 0, count);
                } while (count != -1 && (bytesRead += this.frameSize) < this.streamingBufferSize);
                if (byteBuffer.position() > 0) {
                    byteBuffer.flip();
                    this.audioDataQueue.offer(byteBuffer);
                }
                if (count != -1) {
                    continue;
                }
                break;
            }
        }
        catch (Throwable e) {
            LOGGER.error("Failed to read audio stream", e);
            this.failed = e;
            try {
                this.stream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private AudioFormat getTargetPCMAudioFormat(AudioFormat originalFormat) {
        int sampleSizeInBits = originalFormat.getSampleSizeInBits();
        if (sampleSizeInBits == -1) {
            sampleSizeInBits = 16;
        }
        int frameSize = sampleSizeInBits / 8 * originalFormat.getChannels();
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, originalFormat.getSampleRate(), sampleSizeInBits, originalFormat.getChannels(), frameSize, originalFormat.getSampleRate(), false);
    }

    public AudioFormat m_6206_() {
        return this.stream.getFormat();
    }

    private void loadAudioData() {
        if (this.failed == null && this.audioDataQueue.size() < 4 && this.loading.compareAndSet(false, true)) {
            AUDIO_STREAM_EXECUTOR.submit(() -> {
                try {
                    this.pumpBuffers(2);
                }
                finally {
                    this.loading.set(false);
                }
            });
        }
    }

    public ByteBuffer m_7118_(int size) {
        ByteBuffer buffer;
        this.loadAudioData();
        if ((float)size / (float)this.streamingBufferSize > (float)this.audioDataQueue.size() || size <= 0) {
            return null;
        }
        int bytesToRead = size;
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int)size);
        while ((buffer = this.audioDataQueue.peek()) != null) {
            if (buffer.remaining() <= bytesToRead) {
                bytesToRead -= buffer.remaining();
                byteBuffer.put(buffer);
                this.audioDataQueue.poll();
            } else {
                int oldLimit = buffer.limit();
                buffer.limit(buffer.position() + bytesToRead);
                byteBuffer.put(buffer);
                buffer.limit(oldLimit);
                bytesToRead = 0;
            }
            if (bytesToRead > 0) continue;
        }
        byteBuffer.flip();
        return byteBuffer;
    }

    public void close() throws IOException {
        this.stream.close();
    }
}

