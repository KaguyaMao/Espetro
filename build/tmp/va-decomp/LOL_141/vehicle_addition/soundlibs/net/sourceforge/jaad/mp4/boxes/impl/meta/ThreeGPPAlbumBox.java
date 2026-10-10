/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox;
import java.io.IOException;

public class ThreeGPPAlbumBox
extends ThreeGPPMetadataBox {
    private int trackNumber;

    public ThreeGPPAlbumBox() {
        super("3GPP Album Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.trackNumber = this.getLeft(in) > 0L ? in.readByte() : -1;
    }

    public int getTrackNumber() {
        return this.trackNumber;
    }
}

