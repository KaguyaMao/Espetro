/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;

class UnknownBox
extends BoxImpl {
    UnknownBox() {
        super("unknown");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
    }
}

