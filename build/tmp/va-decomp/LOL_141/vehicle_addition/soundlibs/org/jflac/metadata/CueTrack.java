/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.CueIndex;
import java.io.IOException;

public class CueTrack {
    private static final int CUESHEET_TRACK_OFFSET_LEN = 64;
    private static final int CUESHEET_TRACK_NUMBER_LEN = 8;
    private static final int CUESHEET_TRACK_ISRC_LEN = 96;
    private static final int CUESHEET_TRACK_TYPE_LEN = 1;
    private static final int CUESHEET_TRACK_PRE_EMPHASIS_LEN = 1;
    private static final int CUESHEET_TRACK_RESERVED_LEN = 110;
    private static final int CUESHEET_TRACK_NUM_INDICES_LEN = 8;
    protected long offset;
    protected byte number;
    protected byte[] isrc = new byte[13];
    protected int type;
    protected int preEmphasis;
    protected byte numIndices;
    protected CueIndex[] indices;

    public CueTrack(BitInputStream is) throws IOException {
        this.offset = is.readRawULong(64);
        this.number = (byte)is.readRawUInt(8);
        is.readByteBlockAlignedNoCRC(this.isrc, 12);
        this.type = is.readRawUInt(1);
        this.preEmphasis = is.readRawUInt(1);
        is.skipBitsNoCRC(110);
        this.numIndices = (byte)is.readRawUInt(8);
        if (this.numIndices > 0) {
            this.indices = new CueIndex[this.numIndices];
            for (int j = 0; j < this.numIndices; ++j) {
                this.indices[j] = new CueIndex(is);
            }
        }
    }
}

