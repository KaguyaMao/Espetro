/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import java.io.IOException;

public class DecoderSpecificInfo
extends Descriptor {
    private byte[] data;

    @Override
    void decode(MP4InputStream in) throws IOException {
        this.data = new byte[this.size];
        in.readBytes(this.data);
    }

    public byte[] getData() {
        return this.data;
    }
}

