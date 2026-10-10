/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox;
import java.io.IOException;

public class ThreeGPPClassificationBox
extends ThreeGPPMetadataBox {
    private long entity;
    private int table;

    public ThreeGPPClassificationBox() {
        super("3GPP Classification Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.decodeCommon(in);
        this.entity = in.readBytes(4);
        this.table = (int)in.readBytes(2);
    }

    public long getEntity() {
        return this.entity;
    }

    public int getTable() {
        return this.table;
    }
}

