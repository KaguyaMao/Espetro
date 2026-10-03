/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.CueTrack;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Violation;
import java.io.IOException;

public class CueSheet
extends Metadata {
    private static final int CUESHEET_MEDIA_CATALOG_NUMBER_LEN = 1024;
    private static final int CUESHEET_LEAD_IN_LEN = 64;
    private static final int CUESHEET_IS_CD_LEN = 1;
    private static final int CUESHEET_RESERVED_LEN = 2071;
    private static final int CUESHEET_NUM_TRACKS_LEN = 8;
    protected byte[] mediaCatalogNumber = new byte[129];
    protected long leadIn = 0L;
    protected boolean isCD = false;
    protected int numTracks = 0;
    protected CueTrack[] tracks;

    public CueSheet(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        is.readByteBlockAlignedNoCRC(this.mediaCatalogNumber, 128);
        this.leadIn = is.readRawULong(64);
        this.isCD = is.readRawUInt(1) != 0;
        is.skipBitsNoCRC(2071);
        this.numTracks = is.readRawUInt(8);
        if (this.numTracks > 0) {
            this.tracks = new CueTrack[this.numTracks];
            for (int i = 0; i < this.numTracks; ++i) {
                this.tracks[i] = new CueTrack(is);
            }
        }
    }

    void isLegal(boolean checkCdDaSubset) throws Violation {
        if (checkCdDaSubset) {
            if (this.leadIn < 88200L) {
                throw new Violation("CD-DA cue sheet must have a lead-in length of at least 2 seconds");
            }
            if (this.leadIn % 588L != 0L) {
                throw new Violation("CD-DA cue sheet lead-in length must be evenly divisible by 588 samples");
            }
        }
        if (this.numTracks == 0) {
            throw new Violation("cue sheet must have at least one track (the lead-out)");
        }
        if (checkCdDaSubset && this.tracks[this.numTracks - 1].number != 170) {
            throw new Violation("CD-DA cue sheet must have a lead-out track number 170 (0xAA)");
        }
        for (int i = 0; i < this.numTracks; ++i) {
            if (this.tracks[i].number == 0) {
                throw new Violation("cue sheet may not have a track number 0");
            }
            if (checkCdDaSubset && (this.tracks[i].number < 1 || this.tracks[i].number > 99) && this.tracks[i].number != 170) {
                throw new Violation("CD-DA cue sheet track number must be 1-99 or 170");
            }
            if (checkCdDaSubset && this.tracks[i].offset % 588L != 0L) {
                throw new Violation("CD-DA cue sheet track offset must be evenly divisible by 588 samples");
            }
            if (i < this.numTracks - 1) {
                if (this.tracks[i].numIndices == 0) {
                    throw new Violation("cue sheet track must have at least one index point");
                }
                if (this.tracks[i].indices[0].number > 1) {
                    throw new Violation("cue sheet track's first index number must be 0 or 1");
                }
            }
            for (int j = 0; j < this.tracks[i].numIndices; ++j) {
                if (checkCdDaSubset && this.tracks[i].indices[j].offset % 588L != 0L) {
                    throw new Violation("CD-DA cue sheet track index offset must be evenly divisible by 588 samples");
                }
                if (j <= 0 || this.tracks[i].indices[j].number == this.tracks[i].indices[j - 1].number + 1) continue;
                throw new Violation("cue sheet track index numbers must increase by 1");
            }
        }
    }
}

