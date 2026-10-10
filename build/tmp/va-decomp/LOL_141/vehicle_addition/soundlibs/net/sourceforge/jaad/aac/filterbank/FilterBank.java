/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.KBDWindows;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.MDCT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.SineWindows;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;

public class FilterBank
implements SineWindows,
KBDWindows {
    public static final int WINDOW_LEN_LONG = 1024;
    public static final int WINDOW_SMALL_LEN_LONG = 960;
    private final float[][] LONG_WINDOWS;
    private final float[][] SHORT_WINDOWS;
    private final int length;
    private final int shortLen;
    private final int mid;
    private final int trans;
    private final MDCT mdctShort;
    private final MDCT mdctLong;
    private final float[] buf;

    public FilterBank(boolean smallFrames) {
        if (smallFrames) {
            this.length = 960;
            this.shortLen = this.length / 8;
            this.LONG_WINDOWS = new float[][]{SINE_960, KBD_960};
            this.SHORT_WINDOWS = new float[][]{SINE_120, KBD_120};
        } else {
            this.length = 1024;
            this.shortLen = this.length / 8;
            this.LONG_WINDOWS = new float[][]{SINE_1024, KBD_1024};
            this.SHORT_WINDOWS = new float[][]{SINE_128, KBD_128};
        }
        this.mid = (this.length - this.shortLen) / 2;
        this.trans = this.shortLen / 2;
        this.mdctShort = new MDCT(this.shortLen * 2);
        this.mdctLong = new MDCT(this.length * 2);
        this.buf = new float[2 * this.length];
    }

    public void process(ICSInfo.WindowSequence windowSequence, int windowShape, int windowShapePrev, float[] in, float[] out, float[] overlap) {
        switch (windowSequence) {
            case ONLY_LONG_SEQUENCE: {
                int i;
                this.mdctLong.process(in, 0, this.buf, 0);
                for (i = 0; i < this.length; ++i) {
                    out[i] = overlap[i] + this.buf[i] * this.LONG_WINDOWS[windowShapePrev][i];
                }
                for (i = 0; i < this.length; ++i) {
                    overlap[i] = this.buf[this.length + i] * this.LONG_WINDOWS[windowShape][this.length - 1 - i];
                }
                break;
            }
            case LONG_START_SEQUENCE: {
                int i;
                this.mdctLong.process(in, 0, this.buf, 0);
                for (i = 0; i < this.length; ++i) {
                    out[i] = overlap[i] + this.buf[i] * this.LONG_WINDOWS[windowShapePrev][i];
                }
                for (i = 0; i < this.mid; ++i) {
                    overlap[i] = this.buf[this.length + i];
                }
                for (i = 0; i < this.shortLen; ++i) {
                    overlap[this.mid + i] = this.buf[this.length + this.mid + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - i - 1];
                }
                for (i = 0; i < this.mid; ++i) {
                    overlap[this.mid + this.shortLen + i] = 0.0f;
                }
                break;
            }
            case EIGHT_SHORT_SEQUENCE: {
                int i;
                for (i = 0; i < 8; ++i) {
                    this.mdctShort.process(in, i * this.shortLen, this.buf, 2 * i * this.shortLen);
                }
                for (i = 0; i < this.mid; ++i) {
                    out[i] = overlap[i];
                }
                for (i = 0; i < this.shortLen; ++i) {
                    out[this.mid + i] = overlap[this.mid + i] + this.buf[i] * this.SHORT_WINDOWS[windowShapePrev][i];
                    out[this.mid + 1 * this.shortLen + i] = overlap[this.mid + this.shortLen * 1 + i] + this.buf[this.shortLen * 1 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 2 + i] * this.SHORT_WINDOWS[windowShape][i];
                    out[this.mid + 2 * this.shortLen + i] = overlap[this.mid + this.shortLen * 2 + i] + this.buf[this.shortLen * 3 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 4 + i] * this.SHORT_WINDOWS[windowShape][i];
                    out[this.mid + 3 * this.shortLen + i] = overlap[this.mid + this.shortLen * 3 + i] + this.buf[this.shortLen * 5 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 6 + i] * this.SHORT_WINDOWS[windowShape][i];
                    if (i >= this.trans) continue;
                    out[this.mid + 4 * this.shortLen + i] = overlap[this.mid + this.shortLen * 4 + i] + this.buf[this.shortLen * 7 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 8 + i] * this.SHORT_WINDOWS[windowShape][i];
                }
                for (i = 0; i < this.shortLen; ++i) {
                    if (i >= this.trans) {
                        overlap[this.mid + 4 * this.shortLen + i - this.length] = this.buf[this.shortLen * 7 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 8 + i] * this.SHORT_WINDOWS[windowShape][i];
                    }
                    overlap[this.mid + 5 * this.shortLen + i - this.length] = this.buf[this.shortLen * 9 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 10 + i] * this.SHORT_WINDOWS[windowShape][i];
                    overlap[this.mid + 6 * this.shortLen + i - this.length] = this.buf[this.shortLen * 11 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 12 + i] * this.SHORT_WINDOWS[windowShape][i];
                    overlap[this.mid + 7 * this.shortLen + i - this.length] = this.buf[this.shortLen * 13 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i] + this.buf[this.shortLen * 14 + i] * this.SHORT_WINDOWS[windowShape][i];
                    overlap[this.mid + 8 * this.shortLen + i - this.length] = this.buf[this.shortLen * 15 + i] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i];
                }
                for (i = 0; i < this.mid; ++i) {
                    overlap[this.mid + this.shortLen + i] = 0.0f;
                }
                break;
            }
            case LONG_STOP_SEQUENCE: {
                int i;
                this.mdctLong.process(in, 0, this.buf, 0);
                for (i = 0; i < this.mid; ++i) {
                    out[i] = overlap[i];
                }
                for (i = 0; i < this.shortLen; ++i) {
                    out[this.mid + i] = overlap[this.mid + i] + this.buf[this.mid + i] * this.SHORT_WINDOWS[windowShapePrev][i];
                }
                for (i = 0; i < this.mid; ++i) {
                    out[this.mid + this.shortLen + i] = overlap[this.mid + this.shortLen + i] + this.buf[this.mid + this.shortLen + i];
                }
                for (i = 0; i < this.length; ++i) {
                    overlap[i] = this.buf[this.length + i] * this.LONG_WINDOWS[windowShape][this.length - 1 - i];
                }
                break;
            }
        }
    }

    public void processLTP(ICSInfo.WindowSequence windowSequence, int windowShape, int windowShapePrev, float[] in, float[] out) {
        switch (windowSequence) {
            case ONLY_LONG_SEQUENCE: {
                for (int i = this.length - 1; i >= 0; --i) {
                    this.buf[i] = in[i] * this.LONG_WINDOWS[windowShapePrev][i];
                    this.buf[i + this.length] = in[i + this.length] * this.LONG_WINDOWS[windowShape][this.length - 1 - i];
                }
                break;
            }
            case LONG_START_SEQUENCE: {
                int i;
                for (i = 0; i < this.length; ++i) {
                    this.buf[i] = in[i] * this.LONG_WINDOWS[windowShapePrev][i];
                }
                for (i = 0; i < this.mid; ++i) {
                    this.buf[i + this.length] = in[i + this.length];
                }
                for (i = 0; i < this.shortLen; ++i) {
                    this.buf[i + this.length + this.mid] = in[i + this.length + this.mid] * this.SHORT_WINDOWS[windowShape][this.shortLen - 1 - i];
                }
                for (i = 0; i < this.mid; ++i) {
                    this.buf[i + this.length + this.mid + this.shortLen] = 0.0f;
                }
                break;
            }
            case LONG_STOP_SEQUENCE: {
                int i;
                for (i = 0; i < this.mid; ++i) {
                    this.buf[i] = 0.0f;
                }
                for (i = 0; i < this.shortLen; ++i) {
                    this.buf[i + this.mid] = in[i + this.mid] * this.SHORT_WINDOWS[windowShapePrev][i];
                }
                for (i = 0; i < this.mid; ++i) {
                    this.buf[i + this.mid + this.shortLen] = in[i + this.mid + this.shortLen];
                }
                for (i = 0; i < this.length; ++i) {
                    this.buf[i + this.length] = in[i + this.length] * this.LONG_WINDOWS[windowShape][this.length - 1 - i];
                }
                break;
            }
        }
        this.mdctLong.processForward(this.buf, out);
    }
}

