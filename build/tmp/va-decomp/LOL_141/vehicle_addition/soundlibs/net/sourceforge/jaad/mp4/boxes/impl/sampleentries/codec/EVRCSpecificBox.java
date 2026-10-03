/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import java.io.IOException;

public class EVRCSpecificBox
extends CodecSpecificBox {
    private int framesPerSample;

    public EVRCSpecificBox() {
        super("EVCR Specific Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.decodeCommon(in);
        this.framesPerSample = in.readByte();
    }

    public int getFramesPerSample() {
        return this.framesPerSample;
    }
}

