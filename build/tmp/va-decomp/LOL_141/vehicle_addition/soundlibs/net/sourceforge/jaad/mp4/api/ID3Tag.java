/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.ID3Frame;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ID3Tag {
    private static final int ID3_TAG = 0x494433;
    private static final int SUPPORTED_VERSION = 4;
    private final List<ID3Frame> frames = new ArrayList<ID3Frame>();
    private final int tag;
    private final int flags;
    private final int len;

    ID3Tag(DataInputStream in) throws IOException {
        this.tag = in.read() << 16 | in.read() << 8 | in.read();
        int majorVersion = in.read();
        in.read();
        this.flags = in.read();
        this.len = ID3Tag.readSynch(in);
        if (this.tag == 0x494433 && majorVersion <= 4) {
            if ((this.flags & 0x40) == 64) {
                int extSize = ID3Tag.readSynch(in);
                in.skipBytes(extSize - 6);
            }
            int left = this.len;
            while (left > 0) {
                ID3Frame frame = new ID3Frame(in);
                this.frames.add(frame);
                left = (int)((long)left - frame.getSize());
            }
        }
    }

    public List<ID3Frame> getFrames() {
        return Collections.unmodifiableList(this.frames);
    }

    static int readSynch(DataInputStream in) throws IOException {
        int x = 0;
        for (int i = 0; i < 4; ++i) {
            x |= in.read() & 0x7F;
        }
        return x;
    }
}

