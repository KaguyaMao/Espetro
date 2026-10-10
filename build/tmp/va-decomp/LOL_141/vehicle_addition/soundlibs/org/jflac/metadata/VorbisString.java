/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class VorbisString {
    protected byte[] entry;

    public VorbisString(BitInputStream is) throws IOException {
        int elen = is.readRawIntLittleEndian();
        if (elen == 0) {
            return;
        }
        this.entry = new byte[elen];
        is.readByteBlockAlignedNoCRC(this.entry, this.entry.length);
    }

    public String toString() {
        String s;
        try {
            s = new String(this.entry, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            s = "";
        }
        return s;
    }
}

