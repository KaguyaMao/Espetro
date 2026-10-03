/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class OMAURLBox
extends FullBox {
    private String content;

    public OMAURLBox(String name) {
        super(name);
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        byte[] b = new byte[(int)this.getLeft(in)];
        in.readBytes(b);
        this.content = new String(b, "UTF-8");
    }

    public String getContent() {
        return this.content;
    }
}

