/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.SMVSpecificBox;

public class SMVDecoderInfo
extends DecoderInfo {
    private SMVSpecificBox box;

    public SMVDecoderInfo(CodecSpecificBox box) {
        this.box = (SMVSpecificBox)box;
    }

    public int getDecoderVersion() {
        return this.box.getDecoderVersion();
    }

    public long getVendor() {
        return this.box.getVendor();
    }

    public int getFramesPerSample() {
        return this.box.getFramesPerSample();
    }
}

