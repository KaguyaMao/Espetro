/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import java.io.IOException;

public class Unknown
extends Metadata {
    protected byte[] data;

    public Unknown(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        if (length > 0) {
            this.data = new byte[length];
            is.readByteBlockAlignedNoCRC(this.data, length);
        }
    }
}

