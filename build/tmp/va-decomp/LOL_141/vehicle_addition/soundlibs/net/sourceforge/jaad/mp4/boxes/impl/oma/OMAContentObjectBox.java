/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class OMAContentObjectBox
extends FullBox {
    private byte[] data;

    public OMAContentObjectBox() {
        super("OMA Content Object Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int len = (int)in.readBytes(4);
        this.data = new byte[len];
        in.readBytes(this.data);
    }

    public byte[] getData() {
        return this.data;
    }
}

