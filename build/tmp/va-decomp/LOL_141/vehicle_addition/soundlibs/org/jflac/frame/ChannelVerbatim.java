/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Channel;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import java.io.IOException;

public class ChannelVerbatim
extends Channel {
    private int[] data;

    public ChannelVerbatim(BitInputStream is, Header header, ChannelData channelData, int bps, int wastedBits) throws IOException {
        super(header, wastedBits);
        this.data = channelData.getResidual();
        for (int i = 0; i < header.blockSize; ++i) {
            this.data[i] = is.readRawInt(bps);
        }
        System.arraycopy(this.data, 0, channelData.getOutput(), 0, header.blockSize);
    }

    public String toString() {
        return "ChannelVerbatim: WastedBits=" + this.wastedBits;
    }
}

