/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error.RVLCTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;

public class RVLC
implements RVLCTables {
    private static final int ESCAPE_FLAG = 7;

    public void decode(BitStream in, ICStream ics, int[][] scaleFactors) {
        int bits = ics.getInfo().isEightShortFrame() ? 11 : 9;
        boolean sfConcealment = in.readBool();
        int revGlobalGain = in.readBits(8);
        int rvlcSFLen = in.readBits(bits);
        ICSInfo info = ics.getInfo();
        int windowGroupCount = info.getWindowGroupCount();
        int maxSFB = info.getMaxSFB();
        Object sfbCB = null;
        int sf = ics.getGlobalGain();
        int intensityPosition = 0;
        int noiseEnergy = sf - 90 - 256;
        boolean intensityUsed = false;
        boolean noiseUsed = false;
        for (int g = 0; g < windowGroupCount; ++g) {
            block6: for (int sfb = 0; sfb < maxSFB; ++sfb) {
                switch (sfbCB[g][sfb]) {
                    case 0: {
                        scaleFactors[g][sfb] = 0;
                        continue block6;
                    }
                    case 14: 
                    case 15: {
                        if (!intensityUsed) {
                            intensityUsed = true;
                        }
                        scaleFactors[g][sfb] = intensityPosition += this.decodeHuffman(in);
                        continue block6;
                    }
                    case 13: {
                        if (noiseUsed) {
                            scaleFactors[g][sfb] = noiseEnergy += this.decodeHuffman(in);
                            continue block6;
                        }
                        noiseUsed = true;
                        noiseEnergy = this.decodeHuffman(in);
                        continue block6;
                    }
                    default: {
                        scaleFactors[g][sfb] = sf += this.decodeHuffman(in);
                    }
                }
            }
        }
        int lastIntensityPosition = 0;
        if (intensityUsed) {
            lastIntensityPosition = this.decodeHuffman(in);
        }
        noiseUsed = false;
        if (in.readBool()) {
            this.decodeEscapes(in, ics, scaleFactors);
        }
    }

    private void decodeEscapes(BitStream in, ICStream ics, int[][] scaleFactors) {
        ICSInfo info = ics.getInfo();
        int windowGroupCount = info.getWindowGroupCount();
        int maxSFB = info.getMaxSFB();
        Object sfbCB = null;
        int escapesLen = in.readBits(8);
        boolean noiseUsed = false;
        for (int g = 0; g < windowGroupCount; ++g) {
            for (int sfb = 0; sfb < maxSFB; ++sfb) {
                if (sfbCB[g][sfb] == 13 && !noiseUsed) {
                    noiseUsed = true;
                    continue;
                }
                if (Math.abs((int)sfbCB[g][sfb]) != 7) continue;
                int val = this.decodeHuffmanEscape(in);
                if (sfbCB[g][sfb] == -7) {
                    int[] nArray = scaleFactors[g];
                    int n = sfb;
                    nArray[n] = nArray[n] - val;
                    continue;
                }
                int[] nArray = scaleFactors[g];
                int n = sfb;
                nArray[n] = nArray[n] + val;
            }
        }
    }

    private int decodeHuffman(BitStream in) {
        int j;
        int off = 0;
        int i = RVLC_BOOK[off][1];
        for (int cw = in.readBits(i); cw != RVLC_BOOK[off][2] && i < 10; cw |= in.readBits(j)) {
            j = RVLC_BOOK[++off][1] - i;
            i += j;
            cw <<= j;
        }
        return RVLC_BOOK[off][0];
    }

    private int decodeHuffmanEscape(BitStream in) {
        int j;
        int off = 0;
        int i = ESCAPE_BOOK[off][1];
        for (int cw = in.readBits(i); cw != ESCAPE_BOOK[off][2] && i < 21; cw |= in.readBits(j)) {
            j = ESCAPE_BOOK[++off][1] - i;
            i += j;
            cw <<= j;
        }
        return ESCAPE_BOOK[off][0];
    }
}

