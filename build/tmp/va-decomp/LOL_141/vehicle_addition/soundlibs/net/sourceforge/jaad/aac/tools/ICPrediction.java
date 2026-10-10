/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICPrediction {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.tools.ICPrediction");
    private static final float SF_SCALE = -9.765625E-4f;
    private static final float INV_SF_SCALE = -1024.0f;
    private static final int MAX_PREDICTORS = 672;
    private static final float A = 0.953125f;
    private static final float ALPHA = 0.90625f;
    private boolean predictorReset;
    private int predictorResetGroup;
    private boolean[] predictionUsed;
    private PredictorState[] states = new PredictorState[672];

    public ICPrediction() {
        this.resetAllPredictors();
    }

    public void decode(BitStream in, int maxSFB, SampleFrequency sf) {
        int predictorCount = sf.getPredictorCount();
        this.predictorReset = in.readBool();
        if (this.predictorReset) {
            this.predictorResetGroup = in.readBits(5);
        }
        int maxPredSFB = sf.getMaximalPredictionSFB();
        int length = Math.min(maxSFB, maxPredSFB);
        this.predictionUsed = new boolean[length];
        for (int sfb = 0; sfb < length; ++sfb) {
            this.predictionUsed[sfb] = in.readBool();
        }
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.log(Level.WARNING, "ICPrediction: maxSFB={0}, maxPredSFB={1}", new Integer[]{maxSFB, maxPredSFB});
        }
    }

    public void process(ICSInfo info, float[] data) {
        if (info.isEightShortFrame()) {
            this.resetAllPredictors();
        } else {
            int len = info.getSFB();
            int[] swbOffsets = info.getSWBOffsets();
            for (int sfb = 0; sfb < len; ++sfb) {
                for (int k = swbOffsets[sfb]; k < swbOffsets[sfb + 1]; ++k) {
                    this.predict(data, k, this.predictionUsed[sfb]);
                }
            }
            if (this.predictorReset) {
                this.resetPredictorGroup(this.predictorResetGroup);
            }
        }
    }

    private void resetPredictState(int index) {
        if (this.states[index] == null) {
            this.states[index] = new PredictorState();
        }
        this.states[index].r0 = 0.0f;
        this.states[index].r1 = 0.0f;
        this.states[index].cor0 = 0.0f;
        this.states[index].cor1 = 0.0f;
        this.states[index].var0 = 16256.0f;
        this.states[index].var1 = 16256.0f;
    }

    private void resetAllPredictors() {
        for (int i = 0; i < this.states.length; ++i) {
            this.resetPredictState(i);
        }
    }

    private void resetPredictorGroup(int group) {
        for (int i = group - 1; i < this.states.length; i += 30) {
            this.resetPredictState(i);
        }
    }

    private void predict(float[] data, int off, boolean output) {
        if (this.states[off] == null) {
            this.states[off] = new PredictorState();
        }
        PredictorState state = this.states[off];
        float r0 = state.r0;
        float r1 = state.r1;
        float cor0 = state.cor0;
        float cor1 = state.cor1;
        float var0 = state.var0;
        float var1 = state.var1;
        float k1 = var0 > 1.0f ? cor0 * this.even(0.953125f / var0) : 0.0f;
        float k2 = var1 > 1.0f ? cor1 * this.even(0.953125f / var1) : 0.0f;
        float pv = this.round(k1 * r0 + k2 * r1);
        if (output) {
            int n = off;
            data[n] = data[n] + pv * -9.765625E-4f;
        }
        float e0 = data[off] * -1024.0f;
        float e1 = e0 - k1 * r0;
        state.cor1 = this.trunc(0.90625f * cor1 + r1 * e1);
        state.var1 = this.trunc(0.90625f * var1 + 0.5f * (r1 * r1 + e1 * e1));
        state.cor0 = this.trunc(0.90625f * cor0 + r0 * e0);
        state.var0 = this.trunc(0.90625f * var0 + 0.5f * (r0 * r0 + e0 * e0));
        state.r1 = this.trunc(0.953125f * (r0 - k1 * e0));
        state.r0 = this.trunc(0.953125f * e0);
    }

    private float round(float pf) {
        return Float.intBitsToFloat(Float.floatToIntBits(pf) + 32768 & 0xFFFF0000);
    }

    private float even(float pf) {
        int i = Float.floatToIntBits(pf);
        i = i + Short.MAX_VALUE + (i & 1) & 0xFFFF0000;
        return Float.intBitsToFloat(i);
    }

    private float trunc(float pf) {
        return Float.intBitsToFloat(Float.floatToIntBits(pf) & 0xFFFF0000);
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.WARNING);
        }
    }

    private static final class PredictorState {
        float cor0 = 0.0f;
        float cor1 = 0.0f;
        float var0 = 0.0f;
        float var1 = 0.0f;
        float r0 = 1.0f;
        float r1 = 1.0f;

        private PredictorState() {
        }
    }
}

