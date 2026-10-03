/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter;

public class Filter12
implements Filter {
    private static final float[] p12_13_34 = new float[]{0.0408118f, 0.03812811f, 0.051449083f, 0.06399831f, 0.07428314f, 0.08100348f, 0.083333336f};
    public static final Filter12 f = new Filter12(p12_13_34);
    final float[] filter;

    Filter12(float[] filter) {
        this.filter = filter;
    }

    @Override
    public int resolution() {
        return 12;
    }

    @Override
    public int filter(int frame_len, float[][] buffer, float[][][] result) {
        float[] input_re1 = new float[6];
        float[] input_re2 = new float[6];
        float[] input_im1 = new float[6];
        float[] input_im2 = new float[6];
        float[] out_re1 = new float[6];
        float[] out_re2 = new float[6];
        float[] out_im1 = new float[6];
        float[] out_im2 = new float[6];
        for (int i = 0; i < frame_len; ++i) {
            int n;
            for (n = 0; n < 6; ++n) {
                if (n == 0) {
                    input_re1[0] = buffer[6 + i][0] * this.filter[6];
                    input_re2[0] = buffer[6 + i][1] * this.filter[6];
                } else {
                    input_re1[6 - n] = (buffer[n + i][0] + buffer[12 - n + i][0]) * this.filter[n];
                    input_re2[6 - n] = (buffer[n + i][1] + buffer[12 - n + i][1]) * this.filter[n];
                }
                input_im2[n] = (buffer[n + i][0] - buffer[12 - n + i][0]) * this.filter[n];
                input_im1[n] = (buffer[n + i][1] - buffer[12 - n + i][1]) * this.filter[n];
            }
            Filter12.DCT3_6_unscaled(out_re1, input_re1);
            Filter12.DCT3_6_unscaled(out_re2, input_re2);
            Filter12.DCT3_6_unscaled(out_im1, input_im1);
            Filter12.DCT3_6_unscaled(out_im2, input_im2);
            for (n = 0; n < 6; n += 2) {
                result[i][n][0] = out_re1[n] - out_im1[n];
                result[i][n][1] = out_re2[n] + out_im2[n];
                result[i][n + 1][0] = out_re1[n + 1] + out_im1[n + 1];
                result[i][n + 1][1] = out_re2[n + 1] - out_im2[n + 1];
                result[i][10 - n][0] = out_re1[n + 1] - out_im1[n + 1];
                result[i][10 - n][1] = out_re2[n + 1] + out_im2[n + 1];
                result[i][11 - n][0] = out_re1[n] + out_im1[n];
                result[i][11 - n][1] = out_re2[n] - out_im2[n];
            }
        }
        return this.resolution();
    }

    static void DCT3_6_unscaled(float[] y, float[] x) {
        float f0 = x[3] * 0.70710677f;
        float f1 = x[0] + f0;
        float f2 = x[0] - f0;
        float f3 = (x[1] - x[5]) * 0.70710677f;
        float f4 = x[2] * 0.8660254f + x[4] * 0.5f;
        float f5 = f4 - x[4];
        float f6 = x[1] * 0.9659258f + x[5] * 0.25881904f;
        float f7 = f6 - f3;
        y[0] = f1 + f6 + f4;
        y[1] = f2 + f3 - x[4];
        y[2] = f7 + f2 - f5;
        y[3] = f1 - f7 - f5;
        y[4] = f1 - f3 - x[4];
        y[5] = f2 - f6 + f4;
    }
}

