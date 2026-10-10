/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.FloatSampleBuffer;

public interface FloatSampleInput {
    public void read(FloatSampleBuffer var1);

    public void read(FloatSampleBuffer var1, int var2, int var3);

    public boolean isDone();

    public int getChannels();

    public float getSampleRate();
}

