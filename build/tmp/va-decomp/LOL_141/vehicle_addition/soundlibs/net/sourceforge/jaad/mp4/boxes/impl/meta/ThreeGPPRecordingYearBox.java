/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class ThreeGPPRecordingYearBox
extends FullBox {
    private int year;

    public ThreeGPPRecordingYearBox() {
        super("3GPP Recording Year Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.year = (int)in.readBytes(2);
    }

    public int getYear() {
        return this.year;
    }
}

