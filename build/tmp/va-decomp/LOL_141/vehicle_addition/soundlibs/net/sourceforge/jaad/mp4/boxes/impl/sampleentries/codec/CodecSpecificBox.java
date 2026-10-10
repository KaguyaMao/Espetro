/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;

public abstract class CodecSpecificBox
extends BoxImpl {
    private long vendor;
    private int decoderVersion;

    public CodecSpecificBox(String name) {
        super(name);
    }

    protected void decodeCommon(MP4InputStream in) throws IOException {
        this.vendor = in.readBytes(4);
        this.decoderVersion = in.readByte();
    }

    public long getVendor() {
        return this.vendor;
    }

    public int getDecoderVersion() {
        return this.decoderVersion;
    }
}

