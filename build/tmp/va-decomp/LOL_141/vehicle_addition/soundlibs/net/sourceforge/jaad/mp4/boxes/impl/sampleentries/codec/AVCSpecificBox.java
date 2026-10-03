/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import java.io.IOException;

public class AVCSpecificBox
extends CodecSpecificBox {
    private int configurationVersion;
    private int profile;
    private int level;
    private int lengthSize;
    private byte profileCompatibility;
    private byte[][] sequenceParameterSetNALUnit;
    private byte[][] pictureParameterSetNALUnit;

    public AVCSpecificBox() {
        super("AVC Specific Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        int len;
        this.configurationVersion = in.readByte();
        this.profile = in.readByte();
        this.profileCompatibility = (byte)in.readByte();
        this.level = in.readByte();
        this.lengthSize = (in.readByte() & 3) + 1;
        int sequenceParameterSets = in.readByte() & 0x1F;
        this.sequenceParameterSetNALUnit = new byte[sequenceParameterSets][];
        for (int i = 0; i < sequenceParameterSets; ++i) {
            len = (int)in.readBytes(2);
            this.sequenceParameterSetNALUnit[i] = new byte[len];
            in.readBytes(this.sequenceParameterSetNALUnit[i]);
        }
        int pictureParameterSets = in.readByte();
        this.pictureParameterSetNALUnit = new byte[pictureParameterSets][];
        for (int i = 0; i < pictureParameterSets; ++i) {
            len = (int)in.readBytes(2);
            this.pictureParameterSetNALUnit[i] = new byte[len];
            in.readBytes(this.pictureParameterSetNALUnit[i]);
        }
    }

    public int getConfigurationVersion() {
        return this.configurationVersion;
    }

    public int getProfile() {
        return this.profile;
    }

    public byte getProfileCompatibility() {
        return this.profileCompatibility;
    }

    public int getLevel() {
        return this.level;
    }

    public int getLengthSize() {
        return this.lengthSize;
    }

    public byte[][] getSequenceParameterSetNALUnits() {
        return this.sequenceParameterSetNALUnit;
    }

    public byte[][] getPictureParameterSetNALUnits() {
        return this.pictureParameterSetNALUnit;
    }
}

