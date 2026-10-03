/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.TAudioFormat;
import java.util.Map;
import javax.sound.sampled.AudioFormat;

public class MpegAudioFormat
extends TAudioFormat {
    public MpegAudioFormat(AudioFormat.Encoding encoding, float nFrequency, int SampleSizeInBits, int nChannels, int FrameSize, float FrameRate, boolean isBigEndian, Map properties) {
        super(encoding, nFrequency, SampleSizeInBits, nChannels, FrameSize, FrameRate, isBigEndian, properties);
    }

    public Map properties() {
        return super.properties();
    }
}

