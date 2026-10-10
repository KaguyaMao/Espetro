/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TAudioFileFormat;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class MpegAudioFileFormat
extends TAudioFileFormat {
    public MpegAudioFileFormat(AudioFileFormat.Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes, Map properties) {
        super(type, audioFormat, nLengthInFrames, nLengthInBytes, properties);
    }

    public Map properties() {
        return super.properties();
    }
}

