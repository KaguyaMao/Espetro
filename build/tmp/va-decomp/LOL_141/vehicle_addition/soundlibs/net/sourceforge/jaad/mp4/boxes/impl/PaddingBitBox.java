/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class PaddingBitBox
extends FullBox {
    private int[] pad1;
    private int[] pad2;

    public PaddingBitBox() {
        super("Padding Bit Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int sampleCount = (int)(in.readBytes(4) + 1L) / 2;
        this.pad1 = new int[sampleCount];
        this.pad2 = new int[sampleCount];
        for (int i = 0; i < sampleCount; ++i) {
            byte b = (byte)in.readByte();
            this.pad1[i] = b >> 4 & 7;
            this.pad2[i] = b & 7;
        }
    }

    public int[] getPad1() {
        return this.pad1;
    }

    public int[] getPad2() {
        return this.pad2;
    }
}

