/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FFT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.MDCTTables;

class MDCT
implements MDCTTables {
    private final int N;
    private final int N2;
    private final int N4;
    private final int N8;
    private final float[][] sincos;
    private final FFT fft;
    private final float[][] buf;

    MDCT(int length) {
        this.N = length;
        this.N2 = length >> 1;
        this.N4 = length >> 2;
        this.N8 = length >> 3;
        switch (length) {
            case 2048: {
                this.sincos = MDCT_TABLE_2048;
                break;
            }
            case 256: {
                this.sincos = MDCT_TABLE_128;
                break;
            }
            case 1920: {
                this.sincos = MDCT_TABLE_1920;
                break;
            }
            case 240: {
                this.sincos = MDCT_TABLE_240;
            }
            default: {
                throw new AACException("unsupported MDCT length: " + length);
            }
        }
        this.fft = new FFT(this.N4);
        this.buf = new float[this.N4][2];
    }

    void process(float[] in, int inOff, float[] out, int outOff) {
        int k;
        for (k = 0; k < this.N4; ++k) {
            this.buf[k][1] = in[inOff + 2 * k] * this.sincos[k][0] + in[inOff + this.N2 - 1 - 2 * k] * this.sincos[k][1];
            this.buf[k][0] = in[inOff + this.N2 - 1 - 2 * k] * this.sincos[k][0] - in[inOff + 2 * k] * this.sincos[k][1];
        }
        this.fft.process(this.buf, false);
        for (k = 0; k < this.N4; ++k) {
            float t0 = this.buf[k][0];
            float t1 = this.buf[k][1];
            this.buf[k][1] = t1 * this.sincos[k][0] + t0 * this.sincos[k][1];
            this.buf[k][0] = t0 * this.sincos[k][0] - t1 * this.sincos[k][1];
        }
        for (k = 0; k < this.N8; k += 2) {
            out[outOff + 2 * k] = this.buf[this.N8 + k][1];
            out[outOff + 2 + 2 * k] = this.buf[this.N8 + 1 + k][1];
            out[outOff + 1 + 2 * k] = -this.buf[this.N8 - 1 - k][0];
            out[outOff + 3 + 2 * k] = -this.buf[this.N8 - 2 - k][0];
            out[outOff + this.N4 + 2 * k] = this.buf[k][0];
            out[outOff + this.N4 + 2 + 2 * k] = this.buf[1 + k][0];
            out[outOff + this.N4 + 1 + 2 * k] = -this.buf[this.N4 - 1 - k][1];
            out[outOff + this.N4 + 3 + 2 * k] = -this.buf[this.N4 - 2 - k][1];
            out[outOff + this.N2 + 2 * k] = this.buf[this.N8 + k][0];
            out[outOff + this.N2 + 2 + 2 * k] = this.buf[this.N8 + 1 + k][0];
            out[outOff + this.N2 + 1 + 2 * k] = -this.buf[this.N8 - 1 - k][1];
            out[outOff + this.N2 + 3 + 2 * k] = -this.buf[this.N8 - 2 - k][1];
            out[outOff + this.N2 + this.N4 + 2 * k] = -this.buf[k][1];
            out[outOff + this.N2 + this.N4 + 2 + 2 * k] = -this.buf[1 + k][1];
            out[outOff + this.N2 + this.N4 + 1 + 2 * k] = this.buf[this.N4 - 1 - k][0];
            out[outOff + this.N2 + this.N4 + 3 + 2 * k] = this.buf[this.N4 - 2 - k][0];
        }
    }

    void processForward(float[] in, float[] out) {
        int n;
        int k;
        for (k = 0; k < this.N8; ++k) {
            n = k << 1;
            float t0 = in[this.N - this.N4 - 1 - n] + in[this.N - this.N4 + n];
            float t1 = in[this.N4 + n] - in[this.N4 - 1 - n];
            float[] sc = this.sincos[k];
            this.buf[k][0] = (float)this.N * (t0 * sc[0] + t1 * sc[1]);
            this.buf[k][1] = (float)this.N * (t1 * sc[0] - t0 * sc[1]);
            t0 = in[this.N2 - 1 - n] - in[n];
            t1 = in[this.N2 + n] + in[this.N - 1 - n];
            sc = this.sincos[k + this.N8];
            this.buf[k + this.N8][0] = (float)this.N * (t0 * sc[0] + t1 * sc[1]);
            this.buf[k + this.N8][1] = (float)this.N * (t1 * sc[0] - t0 * sc[1]);
        }
        this.fft.processForward(this.buf);
        for (k = 0; k < this.N4; ++k) {
            n = k << 1;
            float[] sc = this.sincos[k];
            float t0 = this.buf[k][0] * sc[0] + this.buf[k][1] * sc[1];
            float t1 = this.buf[k][1] * sc[0] - this.buf[k][0] * sc[1];
            out[n] = -t0;
            out[this.N2 - 1 - n] = t1;
            out[this.N2 + n] = -t1;
            out[this.N - 1 - n] = t0;
        }
    }
}

