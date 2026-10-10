/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class ChunkOffsetBox
extends FullBox {
    private long[] chunks;

    public ChunkOffsetBox() {
        super("Chunk Offset Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int len = this.type == 1668232756L ? 8 : 4;
        int entryCount = (int)in.readBytes(4);
        this.chunks = new long[entryCount];
        for (int i = 0; i < entryCount; ++i) {
            this.chunks[i] = in.readBytes(len);
        }
    }

    public long[] getChunks() {
        return this.chunks;
    }
}

