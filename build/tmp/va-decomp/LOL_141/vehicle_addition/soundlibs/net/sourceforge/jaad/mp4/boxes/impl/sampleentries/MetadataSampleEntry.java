/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.SampleEntry;
import java.io.IOException;

abstract class MetadataSampleEntry
extends SampleEntry {
    private String contentEncoding;

    MetadataSampleEntry(String name) {
        super(name);
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.contentEncoding = in.readUTFString((int)this.getLeft(in), "UTF-8");
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }
}

