/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class SampleScaleBox
extends FullBox {
    private boolean constrained;
    private int scaleMethod;
    private int displayCenterX;
    private int displayCenterY;

    public SampleScaleBox() {
        super("Sample Scale Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.constrained = (in.readByte() & 1) == 1;
        this.scaleMethod = in.readByte();
        this.displayCenterX = (int)in.readBytes(2);
        this.displayCenterY = (int)in.readBytes(2);
    }

    public boolean isConstrained() {
        return this.constrained;
    }

    public int getDisplayCenterX() {
        return this.displayCenterX;
    }

    public int getDisplayCenterY() {
        return this.displayCenterY;
    }

    public int getScaleMethod() {
        return this.scaleMethod;
    }
}

