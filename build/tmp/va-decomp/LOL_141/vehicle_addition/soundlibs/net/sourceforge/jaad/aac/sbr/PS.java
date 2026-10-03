/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public interface PS {
    public boolean isDataAvailable();

    public void decode(BitStream var1);

    public void process(float[][][] var1, float[][][] var2);
}

