/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter;

public class Filter4
implements Filter {
    private static final float[] p4_13_34 = new float[]{-0.059082113f, -0.048714984f, 0.0f, 0.07778724f, 0.16486304f, 0.23279856f, 0.25f};
    public static final Filter4 f = new Filter4(p4_13_34);
    final float[] filter;

    Filter4(float[] filter) {
        this.filter = filter;
    }

    @Override
    public int resolution() {
        return 4;
    }

    @Override
    public int filter(int frame_len, float[][] buffer, float[][][] result) {
        float[] input_re1 = new float[2];
        float[] input_re2 = new float[2];
        float[] input_im1 = new float[2];
        float[] input_im2 = new float[2];
        for (int i = 0; i < frame_len; ++i) {
            input_re1[0] = -(this.filter[2] * (buffer[i + 2][0] + buffer[i + 10][0])) + this.filter[6] * buffer[i + 6][0];
            input_re1[1] = -0.70710677f * (this.filter[1] * (buffer[i + 1][0] + buffer[i + 11][0]) + this.filter[3] * (buffer[i + 3][0] + buffer[i + 9][0]) - this.filter[5] * (buffer[i + 5][0] + buffer[i + 7][0]));
            input_im1[0] = this.filter[0] * (buffer[i + 0][1] - buffer[i + 12][1]) - this.filter[4] * (buffer[i + 4][1] - buffer[i + 8][1]);
            input_im1[1] = 0.70710677f * (this.filter[1] * (buffer[i + 1][1] - buffer[i + 11][1]) - this.filter[3] * (buffer[i + 3][1] - buffer[i + 9][1]) - this.filter[5] * (buffer[i + 5][1] - buffer[i + 7][1]));
            input_re2[0] = this.filter[0] * (buffer[i + 0][0] - buffer[i + 12][0]) - this.filter[4] * (buffer[i + 4][0] - buffer[i + 8][0]);
            input_re2[1] = 0.70710677f * (this.filter[1] * (buffer[i + 1][0] - buffer[i + 11][0]) - this.filter[3] * (buffer[i + 3][0] - buffer[i + 9][0]) - this.filter[5] * (buffer[i + 5][0] - buffer[i + 7][0]));
            input_im2[0] = -(this.filter[2] * (buffer[i + 2][1] + buffer[i + 10][1])) + this.filter[6] * buffer[i + 6][1];
            input_im2[1] = -0.70710677f * (this.filter[1] * (buffer[i + 1][1] + buffer[i + 11][1]) + this.filter[3] * (buffer[i + 3][1] + buffer[i + 9][1]) - this.filter[5] * (buffer[i + 5][1] + buffer[i + 7][1]));
            result[i][0][0] = input_re1[0] + input_re1[1] + input_im1[0] + input_im1[1];
            result[i][0][1] = -input_re2[0] - input_re2[1] + input_im2[0] + input_im2[1];
            result[i][1][0] = input_re1[0] - input_re1[1] - input_im1[0] + input_im1[1];
            result[i][1][1] = input_re2[0] - input_re2[1] + input_im2[0] - input_im2[1];
            result[i][2][0] = input_re1[0] - input_re1[1] + input_im1[0] - input_im1[1];
            result[i][2][1] = -input_re2[0] + input_re2[1] + input_im2[0] - input_im2[1];
            result[i][3][0] = input_re1[0] + input_re1[1] - input_im1[0] - input_im1[1];
            result[i][3][1] = input_re2[0] + input_re2[1] + input_im2[0] + input_im2[1];
        }
        return this.resolution();
    }
}

