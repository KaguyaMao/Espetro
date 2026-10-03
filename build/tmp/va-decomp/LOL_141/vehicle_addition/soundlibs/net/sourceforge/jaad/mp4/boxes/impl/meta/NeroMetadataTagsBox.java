/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NeroMetadataTagsBox
extends BoxImpl {
    private final Map<String, String> pairs = new HashMap<String, String>();

    public NeroMetadataTagsBox() {
        super("Nero Metadata Tags Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        in.skipBytes(12L);
        while (this.getLeft(in) > 0L && in.readByte() == 128) {
            in.skipBytes(2L);
            String key = in.readUTFString((int)this.getLeft(in), "UTF-8");
            in.skipBytes(4L);
            int len = in.readByte();
            String val = in.readUTFString(len, "UTF-8");
            this.pairs.put(key, val);
        }
    }

    public Map<String, String> getPairs() {
        return this.pairs;
    }
}

