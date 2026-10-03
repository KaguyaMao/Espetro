/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificBox;

public class H263DecoderInfo
extends DecoderInfo {
    private H263SpecificBox box;

    public H263DecoderInfo(CodecSpecificBox box) {
        this.box = (H263SpecificBox)box;
    }

    public int getDecoderVersion() {
        return this.box.getDecoderVersion();
    }

    public long getVendor() {
        return this.box.getVendor();
    }

    public int getLevel() {
        return this.box.getLevel();
    }

    public int getProfile() {
        return this.box.getProfile();
    }
}

