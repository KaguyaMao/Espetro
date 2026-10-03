/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Huffman;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import java.util.Arrays;

class Envelope {
    boolean dt = false;
    final int[] prev;
    final int[] index;
    private static final int[] MAP20TO34 = new int[]{0, 256, 1, 2, 770, 3, 4, 4, 5, 5, 6, 7, 8, 8, 9, 9, 10, 11, 12, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 18, 19, 19, 19};

    Envelope(int[] prev) {
        this.prev = prev;
        this.index = new int[prev.length];
    }

    void read(BitStream ld, EnvTables tables, int nr_par) {
        this.dt = ld.readBool();
        Huffman.Table huff = tables.table(this.dt);
        for (int n = 0; n < nr_par; ++n) {
            this.index[n] = huff.read(ld);
        }
    }

    void reset() {
        this.dt = false;
        Arrays.fill(this.index, 0);
    }

    void restore() {
        System.arraycopy(this.prev, 0, this.index, 0, this.prev.length);
    }

    void decode(EnvMode mode) {
        if (mode == null) {
            this.reset();
        } else {
            int i;
            int stride = mode.stride();
            int nr_par = mode.nr_par;
            if (this.dt) {
                for (i = 0; i < nr_par; ++i) {
                    int p = this.prev[i * stride];
                    this.index[i] = mode.clip(p + this.index[i]);
                }
            } else {
                int p = this.index[0];
                for (int i2 = 1; i2 < nr_par; ++i2) {
                    this.index[i2] = p = mode.clip(p + this.index[i2]);
                }
            }
            if (stride > 1) {
                for (i = stride * nr_par - 1; i > 0; --i) {
                    this.index[i] = this.index[i / stride];
                }
            }
        }
    }

    void map20To34() {
        for (int l = this.index.length - 1; l > 0; --l) {
            int m = MAP20TO34[l];
            int i = this.index[m & 0xFF];
            m >>= 8;
            int n = 1;
            while (m != 0) {
                i += this.index[m & 0xFF];
                m >>= 8;
                ++n;
            }
            this.index[l] = i /= n;
        }
    }
}

