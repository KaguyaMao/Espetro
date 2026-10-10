/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;

public interface PCMProcessor {
    public void processStreamInfo(StreamInfo var1);

    public void processPCM(ByteData var1);
}

