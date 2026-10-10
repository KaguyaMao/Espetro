/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import java.io.IOException;

public class Padding
extends Metadata {
    private int length;

    public Padding(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        this.length = length;
        is.readByteBlockAlignedNoCRC(null, length);
    }

    public String toString() {
        return "Padding (Length=" + this.length + ")";
    }
}

