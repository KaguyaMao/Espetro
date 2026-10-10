/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Frame;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;

public interface FrameListener {
    public void processMetadata(Metadata var1);

    public void processFrame(Frame var1);

    public void processError(String var1);
}

