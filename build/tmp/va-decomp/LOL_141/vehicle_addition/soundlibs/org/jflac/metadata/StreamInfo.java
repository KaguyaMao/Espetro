/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;

public class StreamInfo
extends Metadata {
    private static final int STREAMINFO_MIN_BLOCK_SIZE_LEN = 16;
    private static final int STREAMINFO_MAX_BLOCK_SIZE_LEN = 16;
    private static final int STREAMINFO_MIN_FRAME_SIZE_LEN = 24;
    private static final int STREAMINFO_MAX_FRAME_SIZE_LEN = 24;
    private static final int STREAMINFO_SAMPLE_RATE_LEN = 20;
    private static final int STREAMINFO_CHANNELS_LEN = 3;
    private static final int STREAMINFO_BITS_PER_SAMPLE_LEN = 5;
    private static final int STREAMINFO_TOTAL_SAMPLES_LEN = 36;
    private static final int STREAMINFO_MD5SUM_LEN = 128;
    private byte[] md5sum = new byte[16];
    private int minBlockSize;
    private int maxBlockSize;
    private int minFrameSize;
    private int maxFrameSize;
    private int sampleRate;
    private int channels;
    private int bitsPerSample;
    private long totalSamples;

    public StreamInfo(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        int usedBits = 0;
        this.minBlockSize = is.readRawUInt(16);
        usedBits += 16;
        this.maxBlockSize = is.readRawUInt(16);
        usedBits += 16;
        this.minFrameSize = is.readRawUInt(24);
        usedBits += 24;
        this.maxFrameSize = is.readRawUInt(24);
        usedBits += 24;
        this.sampleRate = is.readRawUInt(20);
        usedBits += 20;
        this.channels = is.readRawUInt(3) + 1;
        usedBits += 3;
        this.bitsPerSample = is.readRawUInt(5) + 1;
        usedBits += 5;
        this.totalSamples = is.readRawULong(36);
        usedBits += 36;
        is.readByteBlockAlignedNoCRC(this.md5sum, 16);
        is.readByteBlockAlignedNoCRC(null, length -= (usedBits += 128) / 8);
    }

    public void write(BitOutputStream os, boolean isLast) throws IOException {
        os.writeRawUInt(isLast, 1);
        os.writeRawUInt(0, 7);
        os.writeRawUInt(this.calcLength(), 24);
        os.writeRawUInt(this.minBlockSize, 16);
        os.writeRawUInt(this.maxBlockSize, 16);
        os.writeRawUInt(this.minFrameSize, 24);
        os.writeRawUInt(this.maxFrameSize, 24);
        os.writeRawUInt(this.sampleRate, 20);
        os.writeRawUInt(this.channels - 1, 3);
        os.writeRawULong(this.bitsPerSample - 1, 5);
        os.writeRawULong(this.totalSamples, 36);
        os.writeByteBlock(this.md5sum, this.md5sum.length);
        os.flushByteAligned();
    }

    public int calcLength() {
        int bits = 144 + this.md5sum.length * 8;
        return (bits + 7) / 8;
    }

    public boolean compatiable(StreamInfo info) {
        if (this.sampleRate != info.sampleRate) {
            return false;
        }
        if (this.channels != info.channels) {
            return false;
        }
        return this.bitsPerSample == info.bitsPerSample;
    }

    public String toString() {
        return "StreamInfo: BlockSize=" + this.minBlockSize + "-" + this.maxBlockSize + " FrameSize=" + this.minFrameSize + "-" + this.maxFrameSize + " SampleRate=" + this.sampleRate + " Channels=" + this.channels + " BPS=" + this.bitsPerSample + " TotalSamples=" + this.totalSamples;
    }

    public int getMaxBlockSize() {
        return this.maxBlockSize;
    }

    public int getMinBlockSize() {
        return this.minBlockSize;
    }

    public long getTotalSamples() {
        return this.totalSamples;
    }

    public void setTotalSamples(long totalSamples) {
        this.totalSamples = totalSamples;
    }

    public void addTotalSamples(long totalSamples) {
        this.totalSamples += totalSamples;
    }

    public int getMaxFrameSize() {
        return this.maxFrameSize;
    }

    public int getMinFrameSize() {
        return this.minFrameSize;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public AudioFormat getAudioFormat() {
        return new AudioFormat(this.sampleRate, this.bitsPerSample, this.channels, this.bitsPerSample > 8, false);
    }

    public int getBitsPerSample() {
        return this.bitsPerSample;
    }

    public int getChannels() {
        return this.channels;
    }
}

