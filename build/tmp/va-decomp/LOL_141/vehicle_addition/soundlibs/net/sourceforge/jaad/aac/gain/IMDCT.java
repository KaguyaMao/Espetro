/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.FFT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.GCConstants;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.IMDCTTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.Windows;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;

class IMDCT
implements GCConstants,
IMDCTTables,
Windows {
    private static final float[][] LONG_WINDOWS = new float[][]{SINE_256, KBD_256};
    private static final float[][] SHORT_WINDOWS = new float[][]{SINE_32, KBD_32};
    private final int frameLen;
    private final int shortFrameLen;
    private final int lbLong;
    private final int lbShort;
    private final int lbMid;

    IMDCT(int frameLen) {
        this.frameLen = frameLen;
        this.lbLong = frameLen / 4;
        this.shortFrameLen = frameLen / 8;
        this.lbShort = this.shortFrameLen / 4;
        this.lbMid = (this.lbLong - this.lbShort) / 2;
    }

    void process(float[] in, float[] out, int winShape, int winShapePrev, ICSInfo.WindowSequence winSeq) {
        int b;
        float[] buf = new float[this.frameLen];
        if (winSeq.equals((Object)ICSInfo.WindowSequence.EIGHT_SHORT_SEQUENCE)) {
            for (b = 0; b < 4; ++b) {
                for (int j = 0; j < 8; ++j) {
                    for (int i = 0; i < this.lbShort; ++i) {
                        buf[this.lbLong * b + this.lbShort * j + i] = b % 2 == 0 ? in[this.shortFrameLen * j + this.lbShort * b + i] : in[this.shortFrameLen * j + this.lbShort * b + this.lbShort - 1 - i];
                    }
                }
            }
        } else {
            for (b = 0; b < 4; ++b) {
                for (int i = 0; i < this.lbLong; ++i) {
                    buf[this.lbLong * b + i] = b % 2 == 0 ? in[this.lbLong * b + i] : in[this.lbLong * b + this.lbLong - 1 - i];
                }
            }
        }
        for (b = 0; b < 4; ++b) {
            this.process2(buf, out, winSeq, winShape, winShapePrev, b);
        }
    }

    private void process2(float[] in, float[] out, ICSInfo.WindowSequence winSeq, int winShape, int winShapePrev, int band) {
        float[] bufIn = new float[this.lbLong];
        float[] bufOut = new float[this.lbLong * 2];
        float[] window = new float[this.lbLong * 2];
        float[] window1 = new float[this.lbShort * 2];
        float[] window2 = new float[this.lbShort * 2];
        switch (winSeq) {
            case ONLY_LONG_SEQUENCE: {
                int i;
                for (i = 0; i < this.lbLong; ++i) {
                    window[i] = LONG_WINDOWS[winShapePrev][i];
                    window[this.lbLong * 2 - 1 - i] = LONG_WINDOWS[winShape][i];
                }
                break;
            }
            case EIGHT_SHORT_SEQUENCE: {
                int i;
                for (i = 0; i < this.lbShort; ++i) {
                    window1[i] = SHORT_WINDOWS[winShapePrev][i];
                    window1[this.lbShort * 2 - 1 - i] = SHORT_WINDOWS[winShape][i];
                    window2[i] = SHORT_WINDOWS[winShape][i];
                    window2[this.lbShort * 2 - 1 - i] = SHORT_WINDOWS[winShape][i];
                }
                break;
            }
            case LONG_START_SEQUENCE: {
                int i;
                for (i = 0; i < this.lbLong; ++i) {
                    window[i] = LONG_WINDOWS[winShapePrev][i];
                }
                for (i = 0; i < this.lbMid; ++i) {
                    window[i + this.lbLong] = 1.0f;
                }
                for (i = 0; i < this.lbShort; ++i) {
                    window[i + this.lbMid + this.lbLong] = SHORT_WINDOWS[winShape][this.lbShort - 1 - i];
                }
                for (i = 0; i < this.lbMid; ++i) {
                    window[i + this.lbMid + this.lbLong + this.lbShort] = 0.0f;
                }
                break;
            }
            case LONG_STOP_SEQUENCE: {
                int i;
                for (i = 0; i < this.lbMid; ++i) {
                    window[i] = 0.0f;
                }
                for (i = 0; i < this.lbShort; ++i) {
                    window[i + this.lbMid] = SHORT_WINDOWS[winShapePrev][i];
                }
                for (i = 0; i < this.lbMid; ++i) {
                    window[i + this.lbMid + this.lbShort] = 1.0f;
                }
                for (i = 0; i < this.lbLong; ++i) {
                    window[i + this.lbMid + this.lbShort + this.lbMid] = LONG_WINDOWS[winShape][this.lbLong - 1 - i];
                }
                break;
            }
        }
        if (winSeq.equals((Object)ICSInfo.WindowSequence.EIGHT_SHORT_SEQUENCE)) {
            for (j = 0; j < 8; ++j) {
                int k;
                for (k = 0; k < this.lbShort; ++k) {
                    bufIn[k] = in[band * this.lbLong + j * this.lbShort + k];
                }
                if (j == 0) {
                    System.arraycopy(window1, 0, window, 0, this.lbShort * 2);
                } else {
                    System.arraycopy(window2, 0, window, 0, this.lbShort * 2);
                }
                this.imdct(bufIn, bufOut, window, this.lbShort);
                for (k = 0; k < this.lbShort * 2; ++k) {
                    out[band * this.lbLong * 2 + j * this.lbShort * 2 + k] = bufOut[k] / 32.0f;
                }
            }
        } else {
            for (j = 0; j < this.lbLong; ++j) {
                bufIn[j] = in[band * this.lbLong + j];
            }
            this.imdct(bufIn, bufOut, window, this.lbLong);
            for (j = 0; j < this.lbLong * 2; ++j) {
                out[band * this.lbLong * 2 + j] = bufOut[j] / 256.0f;
            }
        }
    }

    private void imdct(float[] in, float[] out, float[] window, int n) {
        int i;
        int i2;
        float[][] table2;
        float[][] table;
        int n2 = n / 2;
        if (n == 256) {
            table = IMDCT_TABLE_256;
            table2 = IMDCT_POST_TABLE_256;
        } else if (n == 32) {
            table = IMDCT_TABLE_32;
            table2 = IMDCT_POST_TABLE_32;
        } else {
            throw new AACException("gain control: unexpected IMDCT length");
        }
        float[] tmp = new float[n];
        for (i2 = 0; i2 < n2; ++i2) {
            tmp[i2] = in[2 * i2];
        }
        for (i2 = n2; i2 < n; ++i2) {
            tmp[i2] = -in[2 * n - 1 - 2 * i2];
        }
        float[][] buf = new float[n2][2];
        for (i = 0; i < n2; ++i) {
            buf[i][0] = table[i][0] * tmp[2 * i] - table[i][1] * tmp[2 * i + 1];
            buf[i][1] = table[i][0] * tmp[2 * i + 1] + table[i][1] * tmp[2 * i];
        }
        FFT.process(buf, n2);
        for (i = 0; i < n2; ++i) {
            tmp[i] = table2[i][0] * buf[i][0] + table2[i][1] * buf[n2 - 1 - i][0] + table2[i][2] * buf[i][1] + table2[i][3] * buf[n2 - 1 - i][1];
            tmp[n - 1 - i] = table2[i][2] * buf[i][0] - table2[i][3] * buf[n2 - 1 - i][0] - table2[i][0] * buf[i][1] + table2[i][1] * buf[n2 - 1 - i][1];
        }
        System.arraycopy(tmp, n2, out, 0, n2);
        for (i = n2; i < n * 3 / 2; ++i) {
            out[i] = -tmp[n * 3 / 2 - 1 - i];
        }
        for (i = n * 3 / 2; i < n * 2; ++i) {
            out[i] = -tmp[i - n * 3 / 2];
        }
        for (i = 0; i < n; ++i) {
            int n3 = i;
            out[n3] = out[n3] * window[i];
        }
    }
}

