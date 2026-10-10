/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.GCConstants;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.IMDCT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.IPQF;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;

public class GainControl
implements GCConstants {
    private final int frameLen;
    private final int lbLong;
    private final int lbShort;
    private final IMDCT imdct;
    private final IPQF ipqf;
    private final float[] buffer1;
    private final float[] function;
    private final float[][] buffer2;
    private final float[][] overlap;
    private int maxBand;
    private int[][][] level;
    private int[][][] levelPrev;
    private int[][][] location;
    private int[][][] locationPrev;

    public GainControl(int frameLen) {
        this.frameLen = frameLen;
        this.lbLong = frameLen / 4;
        this.lbShort = this.lbLong / 8;
        this.imdct = new IMDCT(frameLen);
        this.ipqf = new IPQF();
        this.levelPrev = new int[0][][];
        this.locationPrev = new int[0][][];
        this.buffer1 = new float[frameLen / 2];
        this.buffer2 = new float[4][this.lbLong];
        this.function = new float[this.lbLong * 2];
        this.overlap = new float[4][this.lbLong * 2];
    }

    public void decode(BitStream in, ICSInfo.WindowSequence winSeq) {
        int locBits;
        int wdLen;
        this.maxBand = in.readBits(2) + 1;
        int locBits2 = 0;
        switch (winSeq) {
            case ONLY_LONG_SEQUENCE: {
                wdLen = 1;
                locBits = 5;
                locBits2 = 5;
                break;
            }
            case EIGHT_SHORT_SEQUENCE: {
                wdLen = 8;
                locBits = 2;
                locBits2 = 2;
                break;
            }
            case LONG_START_SEQUENCE: {
                wdLen = 2;
                locBits = 4;
                locBits2 = 2;
                break;
            }
            case LONG_STOP_SEQUENCE: {
                wdLen = 2;
                locBits = 4;
                locBits2 = 5;
                break;
            }
            default: {
                return;
            }
        }
        this.level = new int[this.maxBand][wdLen][];
        this.location = new int[this.maxBand][wdLen][];
        for (int bd = 1; bd < this.maxBand; ++bd) {
            for (int wd = 0; wd < wdLen; ++wd) {
                int len = in.readBits(3);
                this.level[bd][wd] = new int[len];
                this.location[bd][wd] = new int[len];
                for (int k = 0; k < len; ++k) {
                    this.level[bd][wd][k] = in.readBits(4);
                    int bits = wd == 0 ? locBits : locBits2;
                    this.location[bd][wd][k] = in.readBits(bits);
                }
            }
        }
    }

    public void process(float[] data, int winShape, int winShapePrev, ICSInfo.WindowSequence winSeq) {
        this.imdct.process(data, this.buffer1, winShape, winShapePrev, winSeq);
        for (int i = 0; i < 4; ++i) {
            this.compensate(this.buffer1, this.buffer2, winSeq, i);
        }
        this.ipqf.process(this.buffer2, this.frameLen, this.maxBand, data);
    }

    private void compensate(float[] in, float[][] out, ICSInfo.WindowSequence winSeq, int band) {
        if (winSeq.equals((Object)ICSInfo.WindowSequence.EIGHT_SHORT_SEQUENCE)) {
            for (int k = 0; k < 8; ++k) {
                int b;
                int a;
                int j;
                this.calculateFunctionData(this.lbShort * 2, band, winSeq, k);
                for (j = 0; j < this.lbShort * 2; ++j) {
                    int n = a = band * this.lbLong * 2 + k * this.lbShort * 2 + j;
                    in[n] = in[n] * this.function[j];
                }
                for (j = 0; j < this.lbShort; ++j) {
                    a = j + this.lbLong * 7 / 16 + this.lbShort * k;
                    b = band * this.lbLong * 2 + k * this.lbShort * 2 + j;
                    float[] fArray = this.overlap[band];
                    int n = a;
                    fArray[n] = fArray[n] + in[b];
                }
                for (j = 0; j < this.lbShort; ++j) {
                    a = j + this.lbLong * 7 / 16 + this.lbShort * (k + 1);
                    b = band * this.lbLong * 2 + k * this.lbShort * 2 + this.lbShort + j;
                    this.overlap[band][a] = in[b];
                }
                this.locationPrev[band][0] = Utils.copyOf(this.location[band][k]);
                this.levelPrev[band][0] = Utils.copyOf(this.level[band][k]);
            }
            System.arraycopy(this.overlap[band], 0, out[band], 0, this.lbLong);
            System.arraycopy(this.overlap[band], this.lbLong, this.overlap[band], 0, this.lbLong);
        } else {
            int j;
            this.calculateFunctionData(this.lbLong * 2, band, winSeq, 0);
            for (j = 0; j < this.lbLong * 2; ++j) {
                int n = band * this.lbLong * 2 + j;
                in[n] = in[n] * this.function[j];
            }
            for (j = 0; j < this.lbLong; ++j) {
                out[band][j] = this.overlap[band][j] + in[band * this.lbLong * 2 + j];
            }
            for (j = 0; j < this.lbLong; ++j) {
                this.overlap[band][j] = in[band * this.lbLong * 2 + this.lbLong + j];
            }
            int lastBlock = winSeq.equals((Object)ICSInfo.WindowSequence.ONLY_LONG_SEQUENCE) ? 1 : 0;
            this.locationPrev[band][0] = Utils.copyOf(this.location[band][lastBlock]);
            this.levelPrev[band][0] = Utils.copyOf(this.level[band][lastBlock]);
        }
    }

    private void calculateFunctionData(int samples, int band, ICSInfo.WindowSequence winSeq, int blockID) {
        int i;
        int[] locA = new int[10];
        float[] levA = new float[10];
        float[] modFunc = new float[samples];
        float[] buf1 = new float[samples / 2];
        float[] buf2 = new float[samples / 2];
        float[] buf3 = new float[samples / 2];
        int maxLocGain0 = 0;
        int maxLocGain1 = 0;
        int maxLocGain2 = 0;
        switch (winSeq) {
            case ONLY_LONG_SEQUENCE: 
            case EIGHT_SHORT_SEQUENCE: {
                maxLocGain0 = maxLocGain1 = samples / 2;
                maxLocGain2 = 0;
                break;
            }
            case LONG_START_SEQUENCE: {
                maxLocGain0 = samples / 2;
                maxLocGain1 = samples * 7 / 32;
                maxLocGain2 = samples / 16;
                break;
            }
            case LONG_STOP_SEQUENCE: {
                maxLocGain0 = samples / 16;
                maxLocGain1 = samples * 7 / 32;
                maxLocGain2 = samples / 2;
            }
        }
        this.calculateFMD(band, 0, true, maxLocGain0, samples, locA, levA, buf1);
        int block = winSeq.equals((Object)ICSInfo.WindowSequence.EIGHT_SHORT_SEQUENCE) ? blockID : 0;
        float secLevel = this.calculateFMD(band, block, false, maxLocGain1, samples, locA, levA, buf2);
        if (winSeq.equals((Object)ICSInfo.WindowSequence.LONG_START_SEQUENCE) || winSeq.equals((Object)ICSInfo.WindowSequence.LONG_STOP_SEQUENCE)) {
            this.calculateFMD(band, 1, false, maxLocGain2, samples, locA, levA, buf3);
        }
        int flatLen = 0;
        if (winSeq.equals((Object)ICSInfo.WindowSequence.LONG_STOP_SEQUENCE)) {
            flatLen = samples / 2 - maxLocGain0 - maxLocGain1;
            for (i = 0; i < flatLen; ++i) {
                modFunc[i] = 1.0f;
            }
        }
        if (winSeq.equals((Object)ICSInfo.WindowSequence.ONLY_LONG_SEQUENCE) || winSeq.equals((Object)ICSInfo.WindowSequence.EIGHT_SHORT_SEQUENCE)) {
            levA[0] = 1.0f;
        }
        for (i = 0; i < maxLocGain0; ++i) {
            modFunc[i + flatLen] = levA[0] * secLevel * buf1[i];
        }
        for (i = 0; i < maxLocGain1; ++i) {
            modFunc[i + flatLen + maxLocGain0] = levA[0] * buf2[i];
        }
        if (winSeq.equals((Object)ICSInfo.WindowSequence.LONG_START_SEQUENCE)) {
            for (i = 0; i < maxLocGain2; ++i) {
                modFunc[i + maxLocGain0 + maxLocGain1] = buf3[i];
            }
            flatLen = samples / 2 - maxLocGain1 - maxLocGain2;
            for (i = 0; i < flatLen; ++i) {
                modFunc[i + maxLocGain0 + maxLocGain1 + maxLocGain2] = 1.0f;
            }
        } else if (winSeq.equals((Object)ICSInfo.WindowSequence.LONG_STOP_SEQUENCE)) {
            for (i = 0; i < maxLocGain2; ++i) {
                modFunc[i + flatLen + maxLocGain0 + maxLocGain1] = buf3[i];
            }
        }
        for (i = 0; i < samples; ++i) {
            this.function[i] = 1.0f / modFunc[i];
        }
    }

    private float calculateFMD(int bd, int wd, boolean prev, int maxLocGain, int samples, int[] loc, float[] lev, float[] fmd) {
        int i;
        int[] m = new int[samples / 2];
        int[] lct = prev ? this.locationPrev[bd][wd] : this.location[bd][wd];
        int[] lvl = prev ? this.levelPrev[bd][wd] : this.level[bd][wd];
        int length = lct.length;
        for (int i2 = 0; i2 < length; ++i2) {
            loc[i2 + 1] = 8 * lct[i2];
            int lngain = this.getGainChangePointID(lvl[i2]);
            lev[i2 + 1] = lngain < 0 ? 1.0f / (float)Math.pow(2.0, -lngain) : (float)Math.pow(2.0, lngain);
        }
        loc[0] = 0;
        lev[0] = length == 0 ? 1.0f : lev[1];
        float secLevel = lev[0];
        loc[length + 1] = maxLocGain;
        lev[length + 1] = 1.0f;
        for (i = 0; i < maxLocGain; ++i) {
            m[i] = 0;
            for (int j = 0; j <= length + 1; ++j) {
                if (loc[j] > i) continue;
                m[i] = j;
            }
        }
        for (i = 0; i < maxLocGain; ++i) {
            fmd[i] = i >= loc[m[i]] && i <= loc[m[i]] + 7 ? this.interpolateGain(lev[m[i]], lev[m[i] + 1], i - loc[m[i]]) : lev[m[i] + 1];
        }
        return secLevel;
    }

    private int getGainChangePointID(int lngain) {
        for (int i = 0; i < 16; ++i) {
            if (lngain != LN_GAIN[i]) continue;
            return i;
        }
        return 0;
    }

    private float interpolateGain(float alev0, float alev1, int iloc) {
        float a0 = (float)(Math.log(alev0) / Math.log(2.0));
        float a1 = (float)(Math.log(alev1) / Math.log(2.0));
        return (float)Math.pow(2.0, ((float)(8 - iloc) * a0 + (float)iloc * a1) / 8.0f);
    }
}

