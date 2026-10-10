/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FFTTables;
import java.util.Arrays;

class FFT
implements FFTTables {
    private final int length;
    private final float[][] roots;
    private final float[][] rev;

    FFT(int length) {
        this.length = length;
        switch (length) {
            case 64: {
                this.roots = FFT_TABLE_64;
                break;
            }
            case 512: {
                this.roots = FFT_TABLE_512;
                break;
            }
            case 60: {
                this.roots = FFT_TABLE_60;
                break;
            }
            case 480: {
                this.roots = FFT_TABLE_480;
                break;
            }
            default: {
                throw new AACException("unexpected FFT length: " + length);
            }
        }
        this.rev = new float[length][2];
    }

    void processForward(float[][] in) {
        this.process(in, true);
    }

    public static void dump(float[][] in) {
        for (int i = 0; i < in.length; ++i) {
            float[] c = in[i];
            System.out.format("cpx(%f, %f),\n", Float.valueOf(c[0]), Float.valueOf(c[1]));
        }
    }

    void process(float[][] in, boolean forward) {
        int i;
        int ii = 0;
        for (i = 0; i < this.length; ++i) {
            int k;
            this.rev[i][0] = in[ii][0];
            this.rev[i][1] = in[ii][1];
            for (k = this.length >> 1; ii >= k && k > 0; ii -= k, k >>= 1) {
            }
            ii += k;
        }
        for (i = 0; i < this.length; ++i) {
            in[i][0] = this.rev[i][0];
            in[i][1] = this.rev[i][1];
        }
        for (i = 0; i < this.length; i += 4) {
            float aRe = in[i][0] + in[i + 1][0];
            float aIm = in[i][1] + in[i + 1][1];
            float bRe = in[i + 2][0] + in[i + 3][0];
            float bIm = in[i + 2][1] + in[i + 3][1];
            float cRe = in[i][0] - in[i + 1][0];
            float cIm = in[i][1] - in[i + 1][1];
            float dRe = in[i + 2][0] - in[i + 3][0];
            float dIm = in[i + 2][1] - in[i + 3][1];
            in[i][0] = aRe + bRe;
            in[i][1] = aIm + bIm;
            in[i + 2][0] = aRe - bRe;
            in[i + 2][1] = aIm - bIm;
            float e1Re = cRe - dIm;
            float e1Im = cIm + dRe;
            float e2Re = cRe + dIm;
            float e2Im = cIm - dRe;
            if (forward) {
                in[i + 1][0] = e2Re;
                in[i + 1][1] = e2Im;
                in[i + 3][0] = e1Re;
                in[i + 3][1] = e1Im;
                continue;
            }
            in[i + 1][0] = e1Re;
            in[i + 1][1] = e1Im;
            in[i + 3][0] = e2Re;
            in[i + 3][1] = e2Im;
        }
        int imOff = forward ? 2 : 1;
        for (int i2 = 4; i2 < this.length; i2 <<= 1) {
            int shift = i2 << 1;
            int m = this.length / shift;
            for (int j = 0; j < this.length; j += shift) {
                for (int k = 0; k < i2; ++k) {
                    int km = k * m;
                    float rootRe = this.roots[km][0];
                    float rootIm = this.roots[km][imOff];
                    float[] v0 = in[j + k];
                    float[] v1 = in[i2 + k + j];
                    float zRe = v1[0] * rootRe - v1[1] * rootIm;
                    float zIm = v1[0] * rootIm + v1[1] * rootRe;
                    v1[0] = v0[0] - zRe;
                    v1[1] = v0[1] - zIm;
                    v0[0] = v0[0] + zRe;
                    v0[1] = v0[1] + zIm;
                }
            }
        }
    }

    public static float[][] copyOf(float[][] array) {
        float[][] result = new float[array.length][2];
        for (int i = 0; i < array.length; ++i) {
            result[i] = Arrays.copyOf(array[i], array[i].length);
        }
        return result;
    }
}

