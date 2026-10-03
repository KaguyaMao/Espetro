/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter;

public class Filter2
implements Filter {
    private static float[] p2_13_20 = new float[]{0.0f, 0.018994875f, 0.0f, -0.072931394f, 0.0f, 0.30596632f, 0.5f};
    public static final Filter2 f = new Filter2(p2_13_20);
    final float[] filter;

    Filter2(float[] filter) {
        this.filter = filter;
    }

    @Override
    public int resolution() {
        return 2;
    }

    @Override
    public int filter(int frame_len, float[][] buffer, float[][][] result) {
        for (int i = 0; i < frame_len; ++i) {
            float r0 = this.filter[0] * (buffer[0 + i][0] + buffer[12 + i][0]);
            float r1 = this.filter[1] * (buffer[1 + i][0] + buffer[11 + i][0]);
            float r2 = this.filter[2] * (buffer[2 + i][0] + buffer[10 + i][0]);
            float r3 = this.filter[3] * (buffer[3 + i][0] + buffer[9 + i][0]);
            float r4 = this.filter[4] * (buffer[4 + i][0] + buffer[8 + i][0]);
            float r5 = this.filter[5] * (buffer[5 + i][0] + buffer[7 + i][0]);
            float r6 = this.filter[6] * buffer[6 + i][0];
            float i0 = this.filter[0] * (buffer[0 + i][1] + buffer[12 + i][1]);
            float i1 = this.filter[1] * (buffer[1 + i][1] + buffer[11 + i][1]);
            float i2 = this.filter[2] * (buffer[2 + i][1] + buffer[10 + i][1]);
            float i3 = this.filter[3] * (buffer[3 + i][1] + buffer[9 + i][1]);
            float i4 = this.filter[4] * (buffer[4 + i][1] + buffer[8 + i][1]);
            float i5 = this.filter[5] * (buffer[5 + i][1] + buffer[7 + i][1]);
            float i6 = this.filter[6] * buffer[6 + i][1];
            result[i][0][0] = r0 + r1 + r2 + r3 + r4 + r5 + r6;
            result[i][0][1] = i0 + i1 + i2 + i3 + i4 + i5 + i6;
            result[i][1][0] = r0 - r1 + r2 - r3 + r4 - r5 + r6;
            result[i][1][1] = i0 - i1 + i2 - i3 + i4 - i5 + i6;
        }
        return this.resolution();
    }
}

