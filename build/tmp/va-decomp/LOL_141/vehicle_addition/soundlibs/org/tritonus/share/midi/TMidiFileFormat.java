/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.midi;

import javax.sound.midi.MidiFileFormat;

public class TMidiFileFormat
extends MidiFileFormat {
    private int m_nTrackCount;

    public TMidiFileFormat(int nType, float fDivisionType, int nResolution, int nByteLength, long lMicrosecondLength, int nTrackCount) {
        super(nType, fDivisionType, nResolution, nByteLength, lMicrosecondLength);
        this.m_nTrackCount = nTrackCount;
    }

    public int getTrackCount() {
        return this.m_nTrackCount;
    }
}

