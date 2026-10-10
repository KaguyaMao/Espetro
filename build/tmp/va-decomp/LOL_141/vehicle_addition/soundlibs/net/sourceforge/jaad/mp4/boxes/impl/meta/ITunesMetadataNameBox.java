/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class ITunesMetadataNameBox
extends FullBox {
    private String metaName;

    public ITunesMetadataNameBox() {
        super("iTunes Metadata Name Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.metaName = in.readString((int)this.getLeft(in));
    }

    public String getMetaName() {
        return this.metaName;
    }
}

