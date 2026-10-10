/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Filterbank;
import java.util.Arrays;

abstract class SynthesisFilterbank
extends Filterbank {
    SynthesisFilterbank(int channels) {
        super(channels);
    }

    @Override
    public void reset() {
        Arrays.fill(this.v, 0.0f);
    }

    abstract void synthesis(int var1, float[][][] var2, float[] var3);
}

