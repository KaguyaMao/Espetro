/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;

public class OriginalFormatBox
extends BoxImpl {
    private long originalFormat;

    public OriginalFormatBox() {
        super("Original Format Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.originalFormat = in.readBytes(4);
    }

    public long getOriginalFormat() {
        return this.originalFormat;
    }
}

