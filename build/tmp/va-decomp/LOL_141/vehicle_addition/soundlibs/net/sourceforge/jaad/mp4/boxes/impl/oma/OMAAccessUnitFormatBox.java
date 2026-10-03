/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class OMAAccessUnitFormatBox
extends FullBox {
    private boolean selectiveEncrypted;
    private int keyIndicatorLength;
    private int initialVectorLength;

    public OMAAccessUnitFormatBox() {
        super("OMA DRM Access Unit Format Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.selectiveEncrypted = (in.readByte() >> 7 & 1) == 1;
        this.keyIndicatorLength = in.readByte();
        this.initialVectorLength = in.readByte();
    }

    public boolean isSelectiveEncrypted() {
        return this.selectiveEncrypted;
    }

    public int getKeyIndicatorLength() {
        return this.keyIndicatorLength;
    }

    public int getInitialVectorLength() {
        return this.initialVectorLength;
    }
}

