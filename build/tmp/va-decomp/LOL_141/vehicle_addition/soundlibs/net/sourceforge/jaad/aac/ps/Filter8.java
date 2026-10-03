/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter;

public class Filter8
implements Filter {
    private static final float[] p8_13_20 = new float[]{0.0074608293f, 0.02270421f, 0.04546866f, 0.07266114f, 0.098851085f, 0.1179371f, 0.125f};
    private static final float[] p8_13_34 = new float[]{0.015656756f, 0.037527163f, 0.054178912f, 0.08417044f, 0.10307344f, 0.122224525f, 0.125f};
    public static final Filter8 f20 = new Filter8(p8_13_20);
    public static final Filter8 f34 = new Filter8(p8_13_34);
    final float[] filter;

    Filter8(float[] filter) {
        this.filter = filter;
    }

    @Override
    public int resolution() {
        return 8;
    }

    @Override
    public int filter(int frame_len, float[][] buffer, float[][][] result) {
        float[] input_re1 = new float[4];
        float[] input_re2 = new float[4];
        float[] input_im1 = new float[4];
        float[] input_im2 = new float[4];
        float[] x = new float[4];
        for (int i = 0; i < frame_len; ++i) {
            int n;
            input_re1[0] = this.filter[6] * buffer[6 + i][0];
            input_re1[1] = this.filter[5] * (buffer[5 + i][0] + buffer[7 + i][0]);
            input_re1[2] = -(this.filter[0] * (buffer[0 + i][0] + buffer[12 + i][0])) + this.filter[4] * (buffer[4 + i][0] + buffer[8 + i][0]);
            input_re1[3] = -(this.filter[1] * (buffer[1 + i][0] + buffer[11 + i][0])) + this.filter[3] * (buffer[3 + i][0] + buffer[9 + i][0]);
            input_im1[0] = this.filter[5] * (buffer[7 + i][1] - buffer[5 + i][1]);
            input_im1[1] = this.filter[0] * (buffer[12 + i][1] - buffer[0 + i][1]) + this.filter[4] * (buffer[8 + i][1] - buffer[4 + i][1]);
            input_im1[2] = this.filter[1] * (buffer[11 + i][1] - buffer[1 + i][1]) + this.filter[3] * (buffer[9 + i][1] - buffer[3 + i][1]);
            input_im1[3] = this.filter[2] * (buffer[10 + i][1] - buffer[2 + i][1]);
            for (n = 0; n < 4; ++n) {
                x[n] = input_re1[n] - input_im1[3 - n];
            }
            Filter8.DCT3_4_unscaled(x, x);
            result[i][7][0] = x[0];
            result[i][5][0] = x[2];
            result[i][3][0] = x[3];
            result[i][1][0] = x[1];
            for (n = 0; n < 4; ++n) {
                x[n] = input_re1[n] + input_im1[3 - n];
            }
            Filter8.DCT3_4_unscaled(x, x);
            result[i][6][0] = x[1];
            result[i][4][0] = x[3];
            result[i][2][0] = x[2];
            result[i][0][0] = x[0];
            input_im2[0] = this.filter[6] * buffer[6 + i][1];
            input_im2[1] = this.filter[5] * (buffer[5 + i][1] + buffer[7 + i][1]);
            input_im2[2] = -(this.filter[0] * (buffer[0 + i][1] + buffer[12 + i][1])) + this.filter[4] * (buffer[4 + i][1] + buffer[8 + i][1]);
            input_im2[3] = -(this.filter[1] * (buffer[1 + i][1] + buffer[11 + i][1])) + this.filter[3] * (buffer[3 + i][1] + buffer[9 + i][1]);
            input_re2[0] = this.filter[5] * (buffer[7 + i][0] - buffer[5 + i][0]);
            input_re2[1] = this.filter[0] * (buffer[12 + i][0] - buffer[0 + i][0]) + this.filter[4] * (buffer[8 + i][0] - buffer[4 + i][0]);
            input_re2[2] = this.filter[1] * (buffer[11 + i][0] - buffer[1 + i][0]) + this.filter[3] * (buffer[9 + i][0] - buffer[3 + i][0]);
            input_re2[3] = this.filter[2] * (buffer[10 + i][0] - buffer[2 + i][0]);
            for (n = 0; n < 4; ++n) {
                x[n] = input_im2[n] + input_re2[3 - n];
            }
            Filter8.DCT3_4_unscaled(x, x);
            result[i][7][1] = x[0];
            result[i][5][1] = x[2];
            result[i][3][1] = x[3];
            result[i][1][1] = x[1];
            for (n = 0; n < 4; ++n) {
                x[n] = input_im2[n] - input_re2[3 - n];
            }
            Filter8.DCT3_4_unscaled(x, x);
            result[i][6][1] = x[1];
            result[i][4][1] = x[3];
            result[i][2][1] = x[2];
            result[i][0][1] = x[0];
        }
        return this.resolution();
    }

    static void DCT3_4_unscaled(float[] y, float[] x) {
        float f0 = x[2] * 0.70710677f;
        float f1 = x[0] - f0;
        float f2 = x[0] + f0;
        float f3 = x[1] + x[3];
        float f4 = x[1] * 1.306563f;
        float f5 = f3 * -0.9238795f;
        float f6 = x[3] * -0.5411961f;
        float f7 = f4 + f5;
        float f8 = f6 - f5;
        y[3] = f2 - f8;
        y[0] = f2 + f8;
        y[2] = f1 - f7;
        y[1] = f1 + f7;
    }
}

