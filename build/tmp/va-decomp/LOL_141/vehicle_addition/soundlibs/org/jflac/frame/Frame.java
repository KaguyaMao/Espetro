/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Channel;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;

public class Frame {
    public Header header;
    public Channel[] subframes = new Channel[8];
    private short crc;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Frame Header: " + this.header + "\n");
        for (int i = 0; i < this.header.channels; ++i) {
            sb.append("\tFrame Data " + this.subframes[i].toString() + "\n");
        }
        sb.append("\tFrame Footer: " + this.crc);
        return sb.toString();
    }

    public static int getMaxRicePartitionOrderFromBlocksize(int blocksize) {
        int maxRicePartitionOrder = 0;
        while ((blocksize & 1) == 0) {
            ++maxRicePartitionOrder;
            blocksize >>= 1;
        }
        return Math.min(15, maxRicePartitionOrder);
    }

    public short getCRC() {
        return this.crc;
    }

    public void setCRC(short crc) {
        this.crc = crc;
    }
}

