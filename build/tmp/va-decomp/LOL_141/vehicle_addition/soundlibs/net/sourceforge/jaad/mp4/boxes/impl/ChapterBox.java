/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChapterBox
extends FullBox {
    private final Map<Long, String> chapters = new HashMap<Long, String>();

    public ChapterBox() {
        super("Chapter Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        in.skipBytes(4L);
        int count = in.readByte();
        for (int i = 0; i < count; ++i) {
            long timestamp = in.readBytes(8);
            int len = in.readByte();
            String name = in.readString(len);
            this.chapters.put(timestamp, name);
        }
    }

    public Map<Long, String> getChapters() {
        return this.chapters;
    }
}

