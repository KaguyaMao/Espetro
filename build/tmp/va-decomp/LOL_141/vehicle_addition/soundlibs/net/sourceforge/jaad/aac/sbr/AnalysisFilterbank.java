/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.DCT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Filterbank;

class AnalysisFilterbank
extends Filterbank {
    AnalysisFilterbank(int channels) {
        super(channels);
    }

    void sbr_qmf_analysis_32(int numTimeSlotsRate, float[] input, float[][][] X, int offset, int kx) {
        float[] u = new float[64];
        float[] in_real = new float[32];
        float[] in_imag = new float[32];
        float[] out_real = new float[32];
        float[] out_imag = new float[32];
        int in = 0;
        for (int l = 0; l < numTimeSlotsRate; ++l) {
            int n;
            for (n = 31; n >= 0; --n) {
                float f = input[in++];
                this.v[this.v_index + n + 320] = f;
                this.v[this.v_index + n] = f;
            }
            for (n = 0; n < 64; ++n) {
                u[n] = this.v[this.v_index + n] * qmf_c[2 * n] + this.v[this.v_index + n + 64] * qmf_c[2 * (n + 64)] + this.v[this.v_index + n + 128] * qmf_c[2 * (n + 128)] + this.v[this.v_index + n + 192] * qmf_c[2 * (n + 192)] + this.v[this.v_index + n + 256] * qmf_c[2 * (n + 256)];
            }
            this.v_index -= 32;
            if (this.v_index < 0) {
                this.v_index = 288;
            }
            in_imag[31] = u[1];
            in_real[0] = u[0];
            for (n = 1; n < 31; ++n) {
                in_imag[31 - n] = u[n + 1];
                in_real[n] = -u[64 - n];
            }
            in_imag[0] = u[32];
            in_real[31] = -u[33];
            DCT.dct4_kernel(in_real, in_imag, out_real, out_imag);
            for (n = 0; n < 16; ++n) {
                if (2 * n + 1 < kx) {
                    X[l + offset][2 * n][0] = 2.0f * out_real[n];
                    X[l + offset][2 * n][1] = 2.0f * out_imag[n];
                    X[l + offset][2 * n + 1][0] = -2.0f * out_imag[31 - n];
                    X[l + offset][2 * n + 1][1] = -2.0f * out_real[31 - n];
                    continue;
                }
                if (2 * n < kx) {
                    X[l + offset][2 * n][0] = 2.0f * out_real[n];
                    X[l + offset][2 * n][1] = 2.0f * out_imag[n];
                } else {
                    X[l + offset][2 * n][0] = 0.0f;
                    X[l + offset][2 * n][1] = 0.0f;
                }
                X[l + offset][2 * n + 1][0] = 0.0f;
                X[l + offset][2 * n + 1][1] = 0.0f;
            }
        }
    }
}

