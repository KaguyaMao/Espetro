/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Profile;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FilterBank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Logger;

public class LTPrediction {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.LTPrediction");
    public static final int MAX_LTP_SFB = 40;
    private static final float[] CODEBOOK = new float[]{0.570829f, 0.696616f, 0.813004f, 0.911304f, 0.9849f, 1.067894f, 1.194601f, 1.369533f};
    private boolean isPresent = false;
    private final int frameLength;
    private final int[] states;
    private int coef;
    private int lag;
    private int lastBand;
    private boolean lagUpdate;
    private boolean[] shortUsed;
    private boolean[] shortLagPresent;
    private boolean[] longUsed;
    private int[] shortLag;

    public LTPrediction(int frameLength) {
        this.frameLength = frameLength;
        this.states = new int[4 * frameLength];
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void decode(BitStream in, ICSInfo info, Profile profile) {
        this.lag = 0;
        this.isPresent = in.readBool();
        if (!this.isPresent) {
            return;
        }
        if (profile.equals((Object)Profile.AAC_LD)) {
            this.lagUpdate = in.readBool();
            if (this.lagUpdate) {
                this.lag = in.readBits(10);
            }
        } else {
            this.lag = in.readBits(11);
        }
        if (this.lag > this.frameLength << 1) {
            throw new AACException("LTP lag too large: " + this.lag);
        }
        this.coef = in.readBits(3);
        int windowCount = info.getWindowCount();
        if (info.isEightShortFrame()) {
            this.shortUsed = new boolean[windowCount];
            this.shortLagPresent = new boolean[windowCount];
            this.shortLag = new int[windowCount];
            for (int w = 0; w < windowCount; ++w) {
                this.shortUsed[w] = in.readBool();
                if (!this.shortUsed[w]) continue;
                this.shortLagPresent[w] = in.readBool();
                if (!this.shortLagPresent[w]) continue;
                this.shortLag[w] = in.readBits(4);
            }
        } else {
            this.lastBand = Math.min(info.getMaxSFB(), 40);
            this.longUsed = new boolean[this.lastBand];
            for (int i = 0; i < this.lastBand; ++i) {
                this.longUsed[i] = in.readBool();
            }
        }
    }

    public void process(ICStream ics, FilterBank filterBank) {
        if (!this.isPresent) {
            return;
        }
        float[] data = ics.getInvQuantData();
        ICSInfo info = ics.getInfo();
        if (!info.isEightShortFrame()) {
            int samples = this.frameLength << 1;
            float[] in = new float[2048];
            float[] out = new float[2048];
            for (int i = 0; i < samples; ++i) {
                in[i] = (float)this.states[samples + i - this.lag] * CODEBOOK[this.coef];
            }
            filterBank.processLTP(info.getWindowSequence(), info.getWindowShape(1), info.getWindowShape(0), in, out);
            ics.processTNS(out);
            int[] swbOffsets = info.getSWBOffsets();
            int swbOffsetMax = info.getSWBOffsetMax();
            for (int sfb = 0; sfb < this.lastBand; ++sfb) {
                if (!this.longUsed[sfb]) continue;
                int low = swbOffsets[sfb];
                int high = Math.min(swbOffsets[sfb + 1], swbOffsetMax);
                for (int bin = low; bin < high; ++bin) {
                    int n = bin;
                    data[n] = data[n] + out[bin];
                }
            }
        }
    }

    public void updateState(float[] time, float[] overlap, Profile profile) {
        if (profile.equals((Object)Profile.AAC_LD)) {
            for (int i = 0; i < this.frameLength; ++i) {
                this.states[i] = this.states[i + this.frameLength];
                this.states[this.frameLength + i] = this.states[i + this.frameLength * 2];
                this.states[this.frameLength * 2 + i] = Math.round(time[i]);
                this.states[this.frameLength * 3 + i] = Math.round(overlap[i]);
            }
        } else {
            for (int i = 0; i < this.frameLength; ++i) {
                this.states[i] = this.states[i + this.frameLength];
                this.states[this.frameLength + i] = Math.round(time[i]);
                this.states[this.frameLength * 2 + i] = Math.round(overlap[i]);
            }
        }
        this.isPresent = false;
    }

    public static boolean isLTPProfile(Profile profile) {
        return profile.equals((Object)Profile.AAC_LTP) || profile.equals((Object)Profile.ER_AAC_LTP) || profile.equals((Object)Profile.AAC_LD);
    }

    public void copyOf(LTPrediction ltp) {
        System.arraycopy(ltp.states, 0, this.states, 0, this.states.length);
        this.coef = ltp.coef;
        this.lag = ltp.lag;
        this.lastBand = ltp.lastBand;
        this.lagUpdate = ltp.lagUpdate;
        this.shortUsed = Utils.copyOf(ltp.shortUsed);
        this.shortLagPresent = Utils.copyOf(ltp.shortLagPresent);
        this.shortLag = Utils.copyOf(ltp.shortLag);
        this.longUsed = Utils.copyOf(ltp.longUsed);
    }
}

