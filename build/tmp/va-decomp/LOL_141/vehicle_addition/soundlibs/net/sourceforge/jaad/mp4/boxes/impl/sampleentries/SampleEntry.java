/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;

public abstract class SampleEntry
extends BoxImpl {
    private long dataReferenceIndex;

    protected SampleEntry(String name) {
        super(name);
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        in.skipBytes(6L);
        this.dataReferenceIndex = in.readBytes(2);
    }

    public long getDataReferenceIndex() {
        return this.dataReferenceIndex;
    }
}

