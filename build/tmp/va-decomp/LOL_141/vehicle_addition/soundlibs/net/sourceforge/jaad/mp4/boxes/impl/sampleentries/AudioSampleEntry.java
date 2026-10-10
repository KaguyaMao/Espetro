/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;
import java.io.IOException;

public class AudioSampleEntry
extends SampleEntry {
    private int channelCount;
    private int sampleSize;
    private int sampleRate;

    public AudioSampleEntry(String name) {
        super(name);
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        in.skipBytes(8L);
        this.channelCount = (int)in.readBytes(2);
        this.sampleSize = (int)in.readBytes(2);
        in.skipBytes(2L);
        in.skipBytes(2L);
        this.sampleRate = (int)in.readBytes(2);
        in.skipBytes(2L);
        this.readChildren(in);
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }
}

