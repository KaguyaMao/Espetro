/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class SchemeTypeBox
extends FullBox {
    public static final long ITUNES_SCHEME = 1769239918L;
    private long schemeType;
    private long schemeVersion;
    private String schemeURI;

    public SchemeTypeBox() {
        super("Scheme Type Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.schemeType = in.readBytes(4);
        this.schemeVersion = in.readBytes(4);
        this.schemeURI = (this.flags & 1) == 1 ? in.readUTFString((int)this.getLeft(in), "UTF-8") : null;
    }

    public long getSchemeType() {
        return this.schemeType;
    }

    public long getSchemeVersion() {
        return this.schemeVersion;
    }

    public String getSchemeURI() {
        return this.schemeURI;
    }
}

