/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class TrackFragmentRunBox
extends FullBox {
    private int sampleCount;
    private boolean dataOffsetPresent;
    private boolean firstSampleFlagsPresent;
    private long dataOffset;
    private long firstSampleFlags;
    private boolean sampleDurationPresent;
    private boolean sampleSizePresent;
    private boolean sampleFlagsPresent;
    private boolean sampleCompositionTimeOffsetPresent;
    private long[] sampleDuration;
    private long[] sampleSize;
    private long[] sampleFlags;
    private long[] sampleCompositionTimeOffset;

    public TrackFragmentRunBox() {
        super("Track Fragment Run Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.sampleCount = (int)in.readBytes(4);
        boolean bl = this.dataOffsetPresent = (this.flags & 1) == 1;
        if (this.dataOffsetPresent) {
            this.dataOffset = in.readBytes(4);
        }
        boolean bl2 = this.firstSampleFlagsPresent = (this.flags & 4) == 4;
        if (this.firstSampleFlagsPresent) {
            this.firstSampleFlags = in.readBytes(4);
        }
        boolean bl3 = this.sampleDurationPresent = (this.flags & 0x100) == 256;
        if (this.sampleDurationPresent) {
            this.sampleDuration = new long[this.sampleCount];
        }
        boolean bl4 = this.sampleSizePresent = (this.flags & 0x200) == 512;
        if (this.sampleSizePresent) {
            this.sampleSize = new long[this.sampleCount];
        }
        boolean bl5 = this.sampleFlagsPresent = (this.flags & 0x400) == 1024;
        if (this.sampleFlagsPresent) {
            this.sampleFlags = new long[this.sampleCount];
        }
        boolean bl6 = this.sampleCompositionTimeOffsetPresent = (this.flags & 0x800) == 2048;
        if (this.sampleCompositionTimeOffsetPresent) {
            this.sampleCompositionTimeOffset = new long[this.sampleCount];
        }
        for (int i = 0; i < this.sampleCount && this.getLeft(in) > 0L; ++i) {
            if (this.sampleDurationPresent) {
                this.sampleDuration[i] = in.readBytes(4);
            }
            if (this.sampleSizePresent) {
                this.sampleSize[i] = in.readBytes(4);
            }
            if (this.sampleFlagsPresent) {
                this.sampleFlags[i] = in.readBytes(4);
            }
            if (!this.sampleCompositionTimeOffsetPresent) continue;
            this.sampleCompositionTimeOffset[i] = in.readBytes(4);
        }
    }

    public int getSampleCount() {
        return this.sampleCount;
    }

    public boolean isDataOffsetPresent() {
        return this.dataOffsetPresent;
    }

    public long getDataOffset() {
        return this.dataOffset;
    }

    public boolean isFirstSampleFlagsPresent() {
        return this.firstSampleFlagsPresent;
    }

    public long getFirstSampleFlags() {
        return this.firstSampleFlags;
    }

    public boolean isSampleDurationPresent() {
        return this.sampleDurationPresent;
    }

    public long[] getSampleDuration() {
        return this.sampleDuration;
    }

    public boolean isSampleSizePresent() {
        return this.sampleSizePresent;
    }

    public long[] getSampleSize() {
        return this.sampleSize;
    }

    public boolean isSampleFlagsPresent() {
        return this.sampleFlagsPresent;
    }

    public long[] getSampleFlags() {
        return this.sampleFlags;
    }

    public boolean isSampleCompositionTimeOffsetPresent() {
        return this.sampleCompositionTimeOffsetPresent;
    }

    public long[] getSampleCompositionTimeOffset() {
        return this.sampleCompositionTimeOffset;
    }
}

