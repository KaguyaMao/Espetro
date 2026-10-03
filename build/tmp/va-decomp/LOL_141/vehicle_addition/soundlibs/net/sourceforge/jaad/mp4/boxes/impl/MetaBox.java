/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Box;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxFactory;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class MetaBox
extends FullBox {
    public MetaBox() {
        super("Meta Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.readChildren(in);
    }

    @Override
    protected Box parseBox(MP4InputStream in) throws IOException {
        long offset = in.getOffset();
        long size = in.readBytes(4);
        long type = in.readBytes(4);
        if (this.children.isEmpty() && size == 1751411826L) {
            offset -= 4L;
            type = size;
            size = ((long)this.version & 0xFFFFFFFFL) << 24;
            size += (long)this.flags;
            this.flags = 0;
            this.version = 0;
        }
        return BoxFactory.parseBox(this, offset, size, type, in);
    }
}

