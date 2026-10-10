/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;

public class PixelAspectRatioBox
extends BoxImpl {
    private long hSpacing;
    private long vSpacing;

    public PixelAspectRatioBox() {
        super("Pixel Aspect Ratio Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.hSpacing = in.readBytes(4);
        this.vSpacing = in.readBytes(4);
    }

    public long getHorizontalSpacing() {
        return this.hSpacing;
    }

    public long getVerticalSpacing() {
        return this.vSpacing;
    }
}

