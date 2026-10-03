/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;
import java.io.IOException;

public class RTPHintSampleEntry
extends SampleEntry {
    private int hintTrackVersion;
    private int highestCompatibleVersion;
    private long maxPacketSize;

    public RTPHintSampleEntry() {
        super("RTP Hint Sample Entry");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.hintTrackVersion = (int)in.readBytes(2);
        this.highestCompatibleVersion = (int)in.readBytes(2);
        this.maxPacketSize = in.readBytes(4);
    }

    public long getMaxPacketSize() {
        return this.maxPacketSize;
    }
}

