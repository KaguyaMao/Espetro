/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.convert;

import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.MpegEncoding;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.Encodings;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert.TEncodingFormatConversionProvider;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class MpegFormatConversionProvider
extends TEncodingFormatConversionProvider {
    private static final AudioFormat.Encoding MP3 = Encodings.getEncoding("MP3");
    private static final AudioFormat.Encoding PCM_SIGNED = Encodings.getEncoding("PCM_SIGNED");
    private static final AudioFormat[] INPUT_FORMATS = new AudioFormat[]{new AudioFormat(MP3, -1.0f, -1, 1, -1, -1.0f, false), new AudioFormat(MP3, -1.0f, -1, 1, -1, -1.0f, true), new AudioFormat(MP3, -1.0f, -1, 2, -1, -1.0f, false), new AudioFormat(MP3, -1.0f, -1, 2, -1, -1.0f, true)};
    private static final AudioFormat[] OUTPUT_FORMATS = new AudioFormat[]{new AudioFormat(PCM_SIGNED, -1.0f, 16, 1, 2, -1.0f, false), new AudioFormat(PCM_SIGNED, -1.0f, 16, 1, 2, -1.0f, true), new AudioFormat(PCM_SIGNED, -1.0f, 16, 2, 4, -1.0f, false), new AudioFormat(PCM_SIGNED, -1.0f, 16, 2, 4, -1.0f, true)};

    public MpegFormatConversionProvider() {
        super(Arrays.asList(INPUT_FORMATS), Arrays.asList(OUTPUT_FORMATS));
        if (TDebug.TraceAudioConverter) {
            TDebug.out(">MpegFormatConversionProvider()");
        }
    }

    public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream audioInputStream) {
        if (TDebug.TraceAudioConverter) {
            TDebug.out(">MpegFormatConversionProvider.getAudioInputStream(AudioFormat targetFormat, AudioInputStream audioInputStream):");
        }
        return new DecodedMpegAudioInputStream(targetFormat, audioInputStream);
    }

    public boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat) {
        AudioFormat.Encoding enc;
        boolean conversion;
        if (TDebug.TraceAudioConverter) {
            TDebug.out(">MpegFormatConversionProvider.isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat):");
            TDebug.out("checking if conversion possible");
            TDebug.out("from: " + sourceFormat);
            TDebug.out("to: " + targetFormat);
        }
        if (!(conversion = super.isConversionSupported(targetFormat, sourceFormat)) && (enc = sourceFormat.getEncoding()) instanceof MpegEncoding && (sourceFormat.getFrameRate() != -1.0f || sourceFormat.getFrameSize() != -1)) {
            conversion = true;
        }
        return conversion;
    }
}

