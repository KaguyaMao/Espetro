/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class ColorParameterBox
extends FullBox {
    private long colorParameterType;
    private int primariesIndex;
    private int transferFunctionIndex;
    private int matrixIndex;

    public ColorParameterBox() {
        super("Color Parameter Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.colorParameterType = in.readBytes(4);
        this.primariesIndex = (int)in.readBytes(2);
        this.transferFunctionIndex = (int)in.readBytes(2);
        this.matrixIndex = (int)in.readBytes(2);
    }
}

