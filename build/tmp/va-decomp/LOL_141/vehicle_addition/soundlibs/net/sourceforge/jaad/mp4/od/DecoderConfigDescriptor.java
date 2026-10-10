/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import java.io.IOException;

public class DecoderConfigDescriptor
extends Descriptor {
    private int objectProfile;
    private int streamType;
    private int decodingBufferSize;
    private boolean upstream;
    private long maxBitRate;
    private long averageBitRate;

    @Override
    void decode(MP4InputStream in) throws IOException {
        this.objectProfile = in.readByte();
        int x = in.readByte();
        this.streamType = x >> 2 & 0x3F;
        this.upstream = (x >> 1 & 1) == 1;
        this.decodingBufferSize = (int)in.readBytes(3);
        this.maxBitRate = in.readBytes(4);
        this.averageBitRate = in.readBytes(4);
        this.readChildren(in);
    }

    public int getObjectProfile() {
        return this.objectProfile;
    }

    public int getStreamType() {
        return this.streamType;
    }

    public boolean isUpstream() {
        return this.upstream;
    }

    public int getDecodingBufferSize() {
        return this.decodingBufferSize;
    }

    public long getMaxBitRate() {
        return this.maxBitRate;
    }

    public long getAverageBitRate() {
        return this.averageBitRate;
    }
}

