/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class SoundMediaHeaderBox
extends FullBox {
    private double balance;

    public SoundMediaHeaderBox() {
        super("Sound Media Header Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.balance = in.readFixedPoint(8, 8);
        in.skipBytes(2L);
    }

    public double getBalance() {
        return this.balance;
    }
}

