/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class FDItemInformationBox
extends FullBox {
    public FDItemInformationBox() {
        super("FD Item Information Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int entryCount = (int)in.readBytes(2);
        this.readChildren(in, entryCount);
        this.readChildren(in);
    }
}

