/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.FBType;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PSTables;

class Filterbank
implements PSTables {
    public final int len;
    private final float[][] work;
    private final float[][][] buffer;
    private final float[][][] temp;

    Filterbank(int len) {
        this.len = len;
        this.work = new float[this.len + 12][2];
        this.buffer = new float[5][this.len][2];
        this.temp = new float[this.len][12][2];
    }

    void hybrid_analysis(float[][][] X, float[][][] X_hybrid, FBType fbt) {
        int offset = 0;
        for (int band = 0; band < fbt.decay_cutoff; ++band) {
            int i;
            for (i = 0; i < 12; ++i) {
                this.work[i][0] = this.buffer[band][i][0];
                this.work[i][1] = this.buffer[band][i][1];
            }
            for (int n = 0; n < this.len; ++n) {
                this.work[12 + n][0] = X[n + 6][band][0];
                this.work[12 + n][1] = X[n + 6][band][1];
            }
            for (i = 0; i < 12; ++i) {
                this.buffer[band][i][0] = this.work[this.len + i][0];
                this.buffer[band][i][1] = this.work[this.len + i][1];
            }
            Filter f = fbt.filters[band];
            int resolution = f.filter(this.len, this.work, this.temp);
            for (int n = 0; n < this.len; ++n) {
                for (int k = 0; k < f.resolution(); ++k) {
                    X_hybrid[n][offset + k][0] = this.temp[n][k][0];
                    X_hybrid[n][offset + k][1] = this.temp[n][k][1];
                }
            }
            offset += resolution;
        }
        if (fbt != FBType.T34) {
            for (int n = 0; n < this.len; ++n) {
                float[] fArray = X_hybrid[n][3];
                fArray[0] = fArray[0] + X_hybrid[n][4][0];
                float[] fArray2 = X_hybrid[n][3];
                fArray2[1] = fArray2[1] + X_hybrid[n][4][1];
                X_hybrid[n][4][0] = 0.0f;
                X_hybrid[n][4][1] = 0.0f;
                float[] fArray3 = X_hybrid[n][2];
                fArray3[0] = fArray3[0] + X_hybrid[n][5][0];
                float[] fArray4 = X_hybrid[n][2];
                fArray4[1] = fArray4[1] + X_hybrid[n][5][1];
                X_hybrid[n][5][0] = 0.0f;
                X_hybrid[n][5][1] = 0.0f;
            }
        }
    }

    void hybrid_synthesis(float[][][] X, float[][][] X_hybrid, FBType fbt) {
        int offset = 0;
        for (int band = 0; band < fbt.decay_cutoff; ++band) {
            int resolution = fbt.filters[band].resolution();
            for (int n = 0; n < this.len; ++n) {
                X[n][band][0] = 0.0f;
                X[n][band][1] = 0.0f;
                for (int k = 0; k < resolution; ++k) {
                    float[] fArray = X[n][band];
                    fArray[0] = fArray[0] + X_hybrid[n][offset + k][0];
                    float[] fArray2 = X[n][band];
                    fArray2[1] = fArray2[1] + X_hybrid[n][offset + k][1];
                }
            }
            offset += resolution;
        }
    }
}

