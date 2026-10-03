/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class ITunesMetadataMeanBox
extends FullBox {
    private String domain;

    public ITunesMetadataMeanBox() {
        super("iTunes Metadata Mean Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.domain = in.readString((int)this.getLeft(in));
    }

    public String getDomain() {
        return this.domain;
    }
}

