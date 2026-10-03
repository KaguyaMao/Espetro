/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProgressiveDownloadInformationBox
extends FullBox {
    private Map<Long, Long> pairs = new HashMap<Long, Long>();

    public ProgressiveDownloadInformationBox() {
        super("Progressive Download Information Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        while (this.getLeft(in) > 0L) {
            long rate = in.readBytes(4);
            long initialDelay = in.readBytes(4);
            this.pairs.put(rate, initialDelay);
        }
    }

    public Map<Long, Long> getInformationPairs() {
        return this.pairs;
    }
}

