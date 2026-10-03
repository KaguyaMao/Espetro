/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Utils;
import java.io.IOException;

public class ThreeGPPMetadataBox
extends FullBox {
    private String languageCode;
    private String data;

    public ThreeGPPMetadataBox(String name) {
        super(name);
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.decodeCommon(in);
        this.data = in.readUTFString((int)this.getLeft(in));
    }

    protected void decodeCommon(MP4InputStream in) throws IOException {
        super.decode(in);
        this.languageCode = Utils.getLanguageCode(in.readBytes(2));
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public String getData() {
        return this.data;
    }
}

