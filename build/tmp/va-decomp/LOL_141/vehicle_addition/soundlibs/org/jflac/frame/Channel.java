/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;

public abstract class Channel {
    public static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE = 0;
    public static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE2 = 1;
    public static final int ENTROPY_CODING_METHOD_TYPE_LEN = 2;
    public static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE_ORDER_LEN = 4;
    protected Header header;
    protected int wastedBits;

    protected Channel(Header header, int wastedBits) {
        this.header = header;
        this.wastedBits = wastedBits;
    }

    public int getWastedBits() {
        return this.wastedBits;
    }
}

