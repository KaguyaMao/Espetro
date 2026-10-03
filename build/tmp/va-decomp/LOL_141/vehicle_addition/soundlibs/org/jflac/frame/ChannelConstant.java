/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Channel;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import java.io.IOException;

public class ChannelConstant
extends Channel {
    private int value;

    public ChannelConstant(BitInputStream is, Header header, ChannelData channelData, int bps, int wastedBits) throws IOException {
        super(header, wastedBits);
        this.value = is.readRawInt(bps);
        for (int i = 0; i < header.blockSize; ++i) {
            channelData.getOutput()[i] = this.value;
        }
    }

    public String toString() {
        return "ChannelConstant: Value=" + this.value + " WastedBits=" + this.wastedBits;
    }
}

