/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public interface SampleRate {
    public int getFrequency();

    public SampleRate duplicated();

    public SampleFrequency getNominal();

    public static SampleRate forFrequency(int frequency) {
        SampleFrequency nominalFrequency = SampleFrequency.nominalFrequency(frequency);
        return nominalFrequency.forFrequency(frequency);
    }

    public static SampleRate decode(BitStream in) {
        int index = in.readBits(4);
        if (index != 15) {
            return SampleFrequency.TABLE.get(index);
        }
        int frequency = in.readBits(24);
        return SampleRate.forFrequency(frequency);
    }
}

