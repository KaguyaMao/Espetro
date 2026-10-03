/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleRate;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.List;

public enum SampleFrequency implements SampleRate
{
    SF_96000(0, 96000, new int[]{33, 512}, new int[]{31, 9}),
    SF_88200(1, 88200, new int[]{33, 512}, new int[]{31, 9}),
    SF_64000(2, 64000, new int[]{38, 664}, new int[]{34, 10}),
    SF_48000(3, 48000, new int[]{40, 672}, new int[]{40, 14}),
    SF_44100(4, 44100, new int[]{40, 672}, new int[]{42, 14}),
    SF_32000(5, 32000, new int[]{40, 672}, new int[]{51, 14}),
    SF_24000(6, 24000, new int[]{41, 652}, new int[]{46, 14}),
    SF_22050(7, 22050, new int[]{41, 652}, new int[]{46, 14}),
    SF_16000(8, 16000, new int[]{37, 664}, new int[]{42, 14}),
    SF_12000(9, 12000, new int[]{37, 664}, new int[]{42, 14}),
    SF_11025(10, 11025, new int[]{37, 664}, new int[]{42, 14}),
    SF_8000(11, 8000, new int[]{34, 664}, new int[]{39, 14});

    public static final SampleFrequency SF_NONE;
    public static final int ESCAPE_INDEX = 15;
    public static final List<SampleFrequency> TABLE;
    private final int index;
    private final int frequency;
    private final int[] prediction;
    private final int[] maxTNS_SFB;

    public static SampleFrequency forInt(int i) {
        if (i >= 0 && i < TABLE.size()) {
            return TABLE.get(i);
        }
        return SF_NONE;
    }

    @Override
    public SampleRate forFrequency(int freq) {
        if (freq == this.frequency) {
            return this;
        }
        return new SampleRate(){

            @Override
            public int getFrequency() {
                return SampleFrequency.this.frequency;
            }

            @Override
            public SampleFrequency getNominal() {
                return SampleFrequency.this;
            }

            @Override
            public SampleRate duplicated() {
                SampleFrequency duplicate = SampleFrequency.this.duplicated();
                return duplicate == SF_NONE ? SF_NONE : duplicate.forFrequency(2 * SampleFrequency.this.frequency);
            }
        };
    }

    public static SampleFrequency nominalFrequency(int freq) {
        SampleFrequency result = null;
        float dev = Float.POSITIVE_INFINITY;
        for (SampleFrequency sf : SampleFrequency.values()) {
            float d = sf.getDeviationTo(freq);
            if (d == 0.0f) {
                return sf;
            }
            if (d < dev) {
                result = sf;
                dev = d;
            }
            if (sf.frequency < freq) break;
        }
        return result;
    }

    private SampleFrequency(int index, int freqency, int[] prediction, int[] maxTNS_SFB) {
        this.index = index;
        this.frequency = freqency;
        this.prediction = prediction;
        this.maxTNS_SFB = maxTNS_SFB;
    }

    @Override
    public SampleFrequency getNominal() {
        return this;
    }

    public static SampleFrequency decode(BitStream in) {
        int sfIndex = in.readBits(4);
        return SampleFrequency.forInt(sfIndex);
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public SampleFrequency duplicated() {
        return this.index < 3 ? SF_NONE : TABLE.get(this.index - 3);
    }

    public float getDeviationTo(int frequency) {
        return ((float)frequency - (float)this.frequency) / (float)this.frequency;
    }

    public int getMaximalPredictionSFB() {
        return this.prediction[0];
    }

    public int getPredictorCount() {
        return this.prediction[1];
    }

    public int getMaximalTNS_SFB(boolean shortWindow) {
        return this.maxTNS_SFB[shortWindow ? 1 : 0];
    }

    public String toString() {
        return Integer.toString(this.frequency);
    }

    static {
        SF_NONE = null;
        TABLE = Utils.listOf(SampleFrequency.values());
    }
}

