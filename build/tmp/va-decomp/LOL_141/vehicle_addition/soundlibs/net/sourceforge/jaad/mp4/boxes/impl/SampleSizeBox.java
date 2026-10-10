/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;
import java.util.Arrays;

public class SampleSizeBox
extends FullBox {
    private long sampleCount;
    private long[] sampleSizes;

    public SampleSizeBox() {
        super("Sample Size Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        int sampleSize;
        boolean compact;
        super.decode(in);
        boolean bl = compact = this.type == 1937013298L;
        if (compact) {
            in.skipBytes(3L);
            sampleSize = in.readByte();
        } else {
            sampleSize = (int)in.readBytes(4);
        }
        this.sampleCount = in.readBytes(4);
        this.sampleSizes = new long[(int)this.sampleCount];
        if (compact) {
            if (sampleSize == 4) {
                int i = 0;
                while ((long)i < this.sampleCount) {
                    int x = in.readByte();
                    this.sampleSizes[i] = x >> 4 & 0xF;
                    this.sampleSizes[i + 1] = x & 0xF;
                    i += 2;
                }
            } else {
                this.readSizes(in, sampleSize / 8);
            }
        } else if (sampleSize == 0) {
            this.readSizes(in, 4);
        } else {
            Arrays.fill(this.sampleSizes, (long)sampleSize);
        }
    }

    private void readSizes(MP4InputStream in, int len) throws IOException {
        int i = 0;
        while ((long)i < this.sampleCount) {
            this.sampleSizes[i] = in.readBytes(len);
            ++i;
        }
    }

    public int getSampleCount() {
        return (int)this.sampleCount;
    }

    public long[] getSampleSizes() {
        return this.sampleSizes;
    }
}

