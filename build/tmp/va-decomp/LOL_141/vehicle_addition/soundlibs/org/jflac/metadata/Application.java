/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import java.io.IOException;

public class Application
extends Metadata {
    private static final int APPLICATION_ID_LEN = 32;
    private byte[] id = new byte[4];
    private byte[] data;

    public Application(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        is.readByteBlockAlignedNoCRC(this.id, 4);
        if ((length -= 4) > 0) {
            this.data = new byte[length];
            is.readByteBlockAlignedNoCRC(this.data, length);
        }
    }
}

