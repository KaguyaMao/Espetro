/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;
import java.io.IOException;

public class MPEGSampleEntry
extends SampleEntry {
    public MPEGSampleEntry() {
        super("MPEG Sample Entry");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.readChildren(in);
    }
}

