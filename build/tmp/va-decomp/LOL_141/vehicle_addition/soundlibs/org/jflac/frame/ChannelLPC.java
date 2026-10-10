/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.LPCPredictor;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Channel;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyCodingMethod;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyPartitionedRice;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.BitMath;
import java.io.IOException;

public class ChannelLPC
extends Channel {
    private static final int SUBFRAME_LPC_QLP_COEFF_PRECISION_LEN = 4;
    private static final int SUBFRAME_LPC_QLP_SHIFT_LEN = 5;
    private static final int MAX_LPC_ORDER = 32;
    private EntropyCodingMethod entropyCodingMethod;
    private int order;
    private int qlpCoeffPrecision;
    private int quantizationLevel;
    private int[] qlpCoeff = new int[32];
    private int[] warmup = new int[32];

    public ChannelLPC(BitInputStream is, Header header, ChannelData channelData, int bps, int wastedBits, int order) throws IOException {
        super(header, wastedBits);
        this.order = order;
        for (int u = 0; u < order; ++u) {
            this.warmup[u] = is.readRawInt(bps);
        }
        int u32 = is.readRawUInt(4);
        if (u32 == 15) {
            throw new IOException("STREAM_DECODER_ERROR_STATUS_LOST_SYNC");
        }
        this.qlpCoeffPrecision = u32 + 1;
        this.quantizationLevel = is.readRawInt(5);
        for (int u = 0; u < order; ++u) {
            this.qlpCoeff[u] = is.readRawInt(this.qlpCoeffPrecision);
        }
        int codingType = is.readRawUInt(2);
        switch (codingType) {
            case 0: 
            case 1: {
                this.entropyCodingMethod = new EntropyPartitionedRice();
                ((EntropyPartitionedRice)this.entropyCodingMethod).order = is.readRawUInt(4);
                ((EntropyPartitionedRice)this.entropyCodingMethod).contents = channelData.getPartitionedRiceContents();
                break;
            }
            default: {
                throw new IOException("STREAM_DECODER_UNPARSEABLE_STREAM");
            }
        }
        if (this.entropyCodingMethod instanceof EntropyPartitionedRice) {
            ((EntropyPartitionedRice)this.entropyCodingMethod).readResidual(is, order, ((EntropyPartitionedRice)this.entropyCodingMethod).order, header, channelData.getResidual(), codingType == 1);
        }
        System.arraycopy(this.warmup, 0, channelData.getOutput(), 0, order);
        if (bps + this.qlpCoeffPrecision + BitMath.ilog2(order) <= 32) {
            if (bps <= 16 && this.qlpCoeffPrecision <= 16) {
                LPCPredictor.restoreSignal(channelData.getResidual(), header.blockSize - order, this.qlpCoeff, order, this.quantizationLevel, channelData.getOutput(), order);
            } else {
                LPCPredictor.restoreSignal(channelData.getResidual(), header.blockSize - order, this.qlpCoeff, order, this.quantizationLevel, channelData.getOutput(), order);
            }
        } else {
            LPCPredictor.restoreSignalWide(channelData.getResidual(), header.blockSize - order, this.qlpCoeff, order, this.quantizationLevel, channelData.getOutput(), order);
        }
    }

    public String toString() {
        int i;
        StringBuffer sb = new StringBuffer("ChannelLPC: Order=" + this.order + " WastedBits=" + this.wastedBits);
        sb.append(" qlpCoeffPrecision=" + this.qlpCoeffPrecision + " quantizationLevel=" + this.quantizationLevel);
        sb.append("\n\t\tqlpCoeff: ");
        for (i = 0; i < this.order; ++i) {
            sb.append(this.qlpCoeff[i] + " ");
        }
        sb.append("\n\t\tWarmup: ");
        for (i = 0; i < this.order; ++i) {
            sb.append(this.warmup[i] + " ");
        }
        sb.append("\n\t\tParameter: ");
        for (i = 0; i < 1 << ((EntropyPartitionedRice)this.entropyCodingMethod).order; ++i) {
            sb.append(((EntropyPartitionedRice)this.entropyCodingMethod).contents.parameters[i] + " ");
        }
        return sb.toString();
    }
}

