/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class EncoderBox
extends FullBox {
    private String data;

    public EncoderBox() {
        super("Encoder Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        if (this.parent.getType() == 1768715124L) {
            this.readChildren(in);
        } else {
            super.decode(in);
            this.data = in.readString((int)this.getLeft(in));
        }
    }

    public String getData() {
        return this.data;
    }
}

