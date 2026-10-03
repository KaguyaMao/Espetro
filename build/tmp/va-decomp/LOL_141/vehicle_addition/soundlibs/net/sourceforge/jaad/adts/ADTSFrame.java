/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.adts;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AudioDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ChannelConfiguration;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Profile;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import java.io.DataInputStream;
import java.io.IOException;

class ADTSFrame
implements AudioDecoderInfo {
    private boolean id;
    private boolean protectionAbsent;
    private boolean privateBit;
    private boolean copy;
    private boolean home;
    private int layer;
    private int profile;
    private int sampleFrequency;
    private int channelConfiguration;
    private boolean copyrightIDBit;
    private boolean copyrightIDStart;
    private int frameLength;
    private int adtsBufferFullness;
    private int rawDataBlockCount;
    private int[] rawDataBlockPosition;
    private int crcCheck;
    private byte[] info;

    ADTSFrame(DataInputStream in) throws IOException {
        this.readHeader(in);
        if (!this.protectionAbsent) {
            this.crcCheck = in.readUnsignedShort();
        }
        if (this.rawDataBlockCount != 0) {
            int i;
            if (!this.protectionAbsent) {
                this.rawDataBlockPosition = new int[this.rawDataBlockCount];
                for (i = 0; i < this.rawDataBlockCount; ++i) {
                    this.rawDataBlockPosition[i] = in.readUnsignedShort();
                }
                this.crcCheck = in.readUnsignedShort();
            }
            for (i = 0; i < this.rawDataBlockCount; ++i) {
                if (this.protectionAbsent) continue;
                this.crcCheck = in.readUnsignedShort();
            }
        }
    }

    private void readHeader(DataInputStream in) throws IOException {
        int i = in.read();
        this.id = (i >> 3 & 1) == 1;
        this.layer = i >> 1 & 3;
        this.protectionAbsent = (i & 1) == 1;
        i = in.read();
        this.profile = (i >> 6 & 3) + 1;
        this.sampleFrequency = i >> 2 & 0xF;
        this.privateBit = (i >> 1 & 1) == 1;
        i = i << 8 | in.read();
        this.channelConfiguration = i >> 6 & 7;
        this.copy = (i >> 5 & 1) == 1;
        this.home = (i >> 4 & 1) == 1;
        this.copyrightIDBit = (i >> 3 & 1) == 1;
        this.copyrightIDStart = (i >> 2 & 1) == 1;
        i = i << 16 | in.readUnsignedShort();
        this.frameLength = i >> 5 & 0x1FFF;
        i = i << 8 | in.read();
        this.adtsBufferFullness = i >> 2 & 0x7FF;
        this.rawDataBlockCount = i & 3;
    }

    int getFrameLength() {
        return this.frameLength - (this.protectionAbsent ? 7 : 9);
    }

    @Override
    public Profile getProfile() {
        return Profile.forInt(this.profile);
    }

    @Override
    public SampleFrequency getSampleFrequency() {
        return SampleFrequency.forInt(this.sampleFrequency);
    }

    @Override
    public ChannelConfiguration getChannelConfiguration() {
        return ChannelConfiguration.forInt(this.channelConfiguration);
    }
}

