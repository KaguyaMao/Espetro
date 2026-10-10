/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.DCT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SynthesisFilterbank;

class SynthesisFilterbank64
extends SynthesisFilterbank {
    SynthesisFilterbank64() {
        super(64);
    }

    @Override
    void synthesis(int numTimeSlotsRate, float[][][] X, float[] output) {
        float[] in_real1 = new float[32];
        float[] in_imag1 = new float[32];
        float[] out_real1 = new float[32];
        float[] out_imag1 = new float[32];
        float[] in_real2 = new float[32];
        float[] in_imag2 = new float[32];
        float[] out_real2 = new float[32];
        float[] out_imag2 = new float[32];
        float scale = 0.015625f;
        int out = 0;
        for (int l = 0; l < numTimeSlotsRate; ++l) {
            float[][] pX = X[l];
            in_imag1[31] = scale * pX[1][0];
            in_real1[0] = scale * pX[0][0];
            in_imag2[31] = scale * pX[62][1];
            in_real2[0] = scale * pX[63][1];
            for (int k = 1; k < 31; ++k) {
                in_imag1[31 - k] = scale * pX[2 * k + 1][0];
                in_real1[k] = scale * pX[2 * k][0];
                in_imag2[31 - k] = scale * pX[63 - (2 * k + 1)][1];
                in_real2[k] = scale * pX[63 - 2 * k][1];
            }
            in_imag1[0] = scale * pX[63][0];
            in_real1[31] = scale * pX[62][0];
            in_imag2[0] = scale * pX[0][1];
            in_real2[31] = scale * pX[1][1];
            DCT.dct4_kernel(in_real1, in_imag1, out_real1, out_imag1);
            DCT.dct4_kernel(in_real2, in_imag2, out_real2, out_imag2);
            int pring_buffer_1 = this.v_index;
            int pring_buffer_3 = pring_buffer_1 + 1280;
            for (int n = 0; n < 32; ++n) {
                float f = out_real2[n] - out_real1[n];
                this.v[pring_buffer_3 + 2 * n] = f;
                this.v[pring_buffer_1 + 2 * n] = f;
                float f2 = out_real2[n] + out_real1[n];
                this.v[pring_buffer_3 + 127 - 2 * n] = f2;
                this.v[pring_buffer_1 + 127 - 2 * n] = f2;
                float f3 = out_imag2[31 - n] + out_imag1[31 - n];
                this.v[pring_buffer_3 + 2 * n + 1] = f3;
                this.v[pring_buffer_1 + 2 * n + 1] = f3;
                float f4 = out_imag2[31 - n] - out_imag1[31 - n];
                this.v[pring_buffer_3 + 127 - (2 * n + 1)] = f4;
                this.v[pring_buffer_1 + 127 - (2 * n + 1)] = f4;
            }
            pring_buffer_1 = this.v_index;
            for (int k = 0; k < 64; ++k) {
                output[out++] = this.v[pring_buffer_1 + k + 0] * qmf_c[k + 0] + this.v[pring_buffer_1 + k + 192] * qmf_c[k + 64] + this.v[pring_buffer_1 + k + 256] * qmf_c[k + 128] + this.v[pring_buffer_1 + k + 448] * qmf_c[k + 192] + this.v[pring_buffer_1 + k + 512] * qmf_c[k + 256] + this.v[pring_buffer_1 + k + 704] * qmf_c[k + 320] + this.v[pring_buffer_1 + k + 768] * qmf_c[k + 384] + this.v[pring_buffer_1 + k + 960] * qmf_c[k + 448] + this.v[pring_buffer_1 + k + 1024] * qmf_c[k + 512] + this.v[pring_buffer_1 + k + 1216] * qmf_c[k + 576];
            }
            this.v_index -= 128;
            if (this.v_index >= 0) continue;
            this.v_index = 1152;
        }
    }
}

