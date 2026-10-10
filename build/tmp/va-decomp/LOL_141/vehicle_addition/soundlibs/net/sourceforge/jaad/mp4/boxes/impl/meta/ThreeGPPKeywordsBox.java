/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox;
import java.io.IOException;

public class ThreeGPPKeywordsBox
extends ThreeGPPMetadataBox {
    private String[] keywords;

    public ThreeGPPKeywordsBox() {
        super("3GPP Keywords Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.decodeCommon(in);
        int count = in.readByte();
        this.keywords = new String[count];
        for (int i = 0; i < count; ++i) {
            int len = in.readByte();
            this.keywords[i] = in.readUTFString(len);
        }
    }

    public String[] getKeywords() {
        return this.keywords;
    }
}

