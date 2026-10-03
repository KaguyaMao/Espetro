/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.FixedPredictor;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Channel;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyCodingMethod;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyPartitionedRice;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import java.io.IOException;

public class ChannelFixed
extends Channel {
    private static final int MAX_FIXED_ORDER = 4;
    private EntropyCodingMethod entropyCodingMethod;
    private int order;
    private int[] warmup = new int[4];
    private int[] residual;

    public ChannelFixed(BitInputStream is, Header header, ChannelData channelData, int bps, int wastedBits, int order) throws IOException {
        super(header, wastedBits);
        this.residual = channelData.getResidual();
        this.order = order;
        for (int u = 0; u < order; ++u) {
            this.warmup[u] = is.readRawInt(bps);
        }
        int type = is.readRawUInt(2);
        switch (type) {
            case 0: 
            case 1: {
                int u32 = is.readRawUInt(4);
                EntropyPartitionedRice pr = new EntropyPartitionedRice();
                this.entropyCodingMethod = pr;
                pr.order = u32;
                pr.contents = channelData.getPartitionedRiceContents();
                pr.readResidual(is, order, pr.order, header, channelData.getResidual(), type == 1);
                break;
            }
            default: {
                throw new IOException("STREAM_DECODER_UNPARSEABLE_STREAM");
            }
        }
        System.arraycopy(this.warmup, 0, channelData.getOutput(), 0, order);
        FixedPredictor.restoreSignal(this.residual, header.blockSize - order, order, channelData.getOutput(), order);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("FLACSubframe_Fixed: Order=" + this.order + " PartitionOrder=" + ((EntropyPartitionedRice)this.entropyCodingMethod).order + " WastedBits=" + this.wastedBits);
        for (int i = 0; i < this.order; ++i) {
            sb.append(" warmup[" + i + "]=" + this.warmup[i]);
        }
        return sb.toString();
    }
}

