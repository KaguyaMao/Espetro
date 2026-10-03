/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import java.io.IOException;

public class H263SpecificBox
extends CodecSpecificBox {
    private int level;
    private int profile;

    public H263SpecificBox() {
        super("H.263 Specific Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.decodeCommon(in);
        this.level = in.readByte();
        this.profile = in.readByte();
    }

    public int getLevel() {
        return this.level;
    }

    public int getProfile() {
        return this.profile;
    }
}

