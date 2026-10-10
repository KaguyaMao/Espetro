/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.huffman;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.huffman.Codebooks;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public class Huffman
implements Codebooks {
    private static final boolean[] UNSIGNED = new boolean[]{false, false, true, true, false, false, true, true, true, true, true};
    private static final int QUAD_LEN = 4;
    private static final int PAIR_LEN = 2;

    private Huffman() {
    }

    private static int findOffset(BitStream in, int[][] table) {
        int j;
        int off = 0;
        int len = table[off][0];
        for (int cw = in.readBits(len); cw != table[off][1]; cw |= in.readBits(j)) {
            j = table[++off][0] - len;
            len = table[off][0];
            cw <<= j;
        }
        return off;
    }

    private static void signValues(BitStream in, int[] data, int off, int len) {
        for (int i = off; i < off + len; ++i) {
            if (data[i] == 0 || !in.readBool()) continue;
            data[i] = -data[i];
        }
    }

    private static int getEscape(BitStream in, int s) {
        boolean neg = s < 0;
        int i = 4;
        while (in.readBool()) {
            ++i;
        }
        int j = in.readBits(i) | 1 << i;
        return neg ? -j : j;
    }

    public static int decodeScaleFactor(BitStream in) {
        int offset = Huffman.findOffset(in, HCB_SF);
        return HCB_SF[offset][2];
    }

    public static void decodeSpectralData(BitStream in, int cb, int[] data, int off) {
        int[][] HCB2 = CODEBOOKS[cb - 1];
        int offset = Huffman.findOffset(in, HCB2);
        data[off] = HCB2[offset][2];
        data[off + 1] = HCB2[offset][3];
        if (cb < 5) {
            data[off + 2] = HCB2[offset][4];
            data[off + 3] = HCB2[offset][5];
        }
        if (cb < 11) {
            if (UNSIGNED[cb - 1]) {
                Huffman.signValues(in, data, off, cb < 5 ? 4 : 2);
            }
        } else if (cb == 11 || cb > 15) {
            Huffman.signValues(in, data, off, cb < 5 ? 4 : 2);
            if (Math.abs(data[off]) == 16) {
                data[off] = Huffman.getEscape(in, data[off]);
            }
            if (Math.abs(data[off + 1]) == 16) {
                data[off + 1] = Huffman.getEscape(in, data[off + 1]);
            }
        } else {
            throw new AACException("Huffman: unknown spectral codebook: " + cb);
        }
    }
}

