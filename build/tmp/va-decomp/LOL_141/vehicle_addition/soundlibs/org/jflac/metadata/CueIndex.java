/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import java.io.IOException;

public class CueIndex {
    private static final int CUESHEET_INDEX_OFFSET_LEN = 64;
    private static final int CUESHEET_INDEX_NUMBER_LEN = 8;
    private static final int CUESHEET_INDEX_RESERVED_LEN = 24;
    protected long offset;
    protected byte number;

    public CueIndex(BitInputStream is) throws IOException {
        this.offset = is.readRawULong(64);
        this.number = (byte)is.readRawUInt(8);
        is.skipBitsNoCRC(24);
    }
}

