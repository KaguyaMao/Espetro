/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.GCConstants;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.PQFTables;

class IPQF
implements GCConstants,
PQFTables {
    private final float[] buf = new float[4];
    private final float[][] tmp1 = new float[2][24];
    private final float[][] tmp2 = new float[2][24];

    IPQF() {
    }

    void process(float[][] in, int frameLen, int maxBand, float[] out) {
        int i;
        for (i = 0; i < frameLen; ++i) {
            out[i] = 0.0f;
        }
        for (i = 0; i < frameLen / 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.buf[j] = in[j][i];
            }
            this.performSynthesis(this.buf, out, i * 4);
        }
    }

    private void performSynthesis(float[] in, float[] out, int outOff) {
        float acc;
        int k;
        int n;
        int kk = 12;
        for (n = 0; n < 2; ++n) {
            for (k = 0; k < 23; ++k) {
                this.tmp1[n][k] = this.tmp1[n][k + 1];
                this.tmp2[n][k] = this.tmp2[n][k + 1];
            }
        }
        for (n = 0; n < 2; ++n) {
            int i;
            acc = 0.0f;
            for (i = 0; i < 4; ++i) {
                acc += COEFS_Q0[n][i] * in[i];
            }
            this.tmp1[n][23] = acc;
            acc = 0.0f;
            for (i = 0; i < 4; ++i) {
                acc += COEFS_Q1[n][i] * in[i];
            }
            this.tmp2[n][23] = acc;
        }
        for (n = 0; n < 2; ++n) {
            acc = 0.0f;
            for (k = 0; k < 12; ++k) {
                acc += COEFS_T0[n][k] * this.tmp1[n][23 - 2 * k];
            }
            for (k = 0; k < 12; ++k) {
                acc += COEFS_T1[n][k] * this.tmp2[n][22 - 2 * k];
            }
            out[outOff + n] = acc;
            acc = 0.0f;
            for (k = 0; k < 12; ++k) {
                acc += COEFS_T0[3 - n][k] * this.tmp1[n][23 - 2 * k];
            }
            for (k = 0; k < 12; ++k) {
                acc -= COEFS_T1[3 - n][k] * this.tmp2[n][22 - 2 * k];
            }
            out[outOff + 4 - 1 - n] = acc;
        }
    }
}

