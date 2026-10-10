/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.samplegroupentries;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;

public abstract class SampleGroupDescriptionEntry
extends BoxImpl {
    protected SampleGroupDescriptionEntry(String name) {
        super(name);
    }

    @Override
    public abstract void decode(MP4InputStream var1) throws IOException;
}

