/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitOutputStream;
import java.io.IOException;

public class SeekPoint {
    private static final int SEEKPOINT_SAMPLE_NUMBER_LEN = 64;
    private static final int SEEKPOINT_STREAM_OFFSET_LEN = 64;
    private static final int SEEKPOINT_FRAME_SAMPLES_LEN = 16;
    protected long sampleNumber;
    protected long streamOffset;
    protected int frameSamples;

    public SeekPoint(BitInputStream is) throws IOException {
        this.sampleNumber = is.readRawULong(64);
        this.streamOffset = is.readRawULong(64);
        this.frameSamples = is.readRawUInt(16);
    }

    public SeekPoint(long sampleNumber, long streamOffset, int frameSamples) {
        this.sampleNumber = sampleNumber;
        this.streamOffset = streamOffset;
        this.frameSamples = frameSamples;
    }

    public void write(BitOutputStream os) throws IOException {
        os.writeRawULong(this.sampleNumber, 64);
        os.writeRawULong(this.streamOffset, 64);
        os.writeRawUInt(this.frameSamples, 16);
    }

    public String toString() {
        return "sampleNumber=" + this.sampleNumber + " streamOffset=" + this.streamOffset + " frameSamples=" + this.frameSamples;
    }

    public int getFrameSamples() {
        return this.frameSamples;
    }

    public long getSampleNumber() {
        return this.sampleNumber;
    }

    public long getStreamOffset() {
        return this.streamOffset;
    }

    public void setStreamOffset(long streamOffset) {
        this.streamOffset = streamOffset;
    }
}

