/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi.Flac2PcmAudioInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi.FlacEncoding;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

public class FlacFormatConversionProvider
extends FormatConversionProvider {
    private static final boolean DEBUG = false;
    private static final boolean HAS_ENCODING = false;

    @Override
    public AudioFormat.Encoding[] getSourceEncodings() {
        AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[]{FlacEncoding.FLAC};
        return encodings;
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings() {
        AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[]{FlacEncoding.PCM_SIGNED};
        return encodings;
    }

    private boolean isBitSizeOK(AudioFormat format, boolean notSpecifiedOK) {
        int bitSize = format.getSampleSizeInBits();
        return notSpecifiedOK && bitSize == -1 || bitSize == 8 || bitSize == 16 || bitSize == 24;
    }

    private boolean isChannelsOK(AudioFormat format, boolean notSpecifiedOK) {
        int channels = format.getChannels();
        return notSpecifiedOK && channels == -1 || channels == 1 || channels == 2;
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
        boolean bitSizeOK = this.isBitSizeOK(sourceFormat, true);
        boolean channelsOK = this.isChannelsOK(sourceFormat, true);
        if (bitSizeOK && channelsOK && sourceFormat.getEncoding().equals(FlacEncoding.FLAC)) {
            AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED};
            return encodings;
        }
        AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[]{};
        return encodings;
    }

    @Override
    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        return this.getTargetFormats(targetEncoding, sourceFormat, true);
    }

    private AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat, boolean notSpecifiedOK) {
        boolean bitSizeOK = this.isBitSizeOK(sourceFormat, notSpecifiedOK);
        boolean channelsOK = this.isChannelsOK(sourceFormat, notSpecifiedOK);
        if (bitSizeOK && channelsOK && sourceFormat.getEncoding().equals(FlacEncoding.FLAC) && targetEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            AudioFormat[] formats = new AudioFormat[]{new AudioFormat(sourceFormat.getSampleRate(), sourceFormat.getSampleSizeInBits(), sourceFormat.getChannels(), true, false)};
            return formats;
        }
        AudioFormat[] formats = new AudioFormat[]{};
        return formats;
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream) {
        AudioFormat[] formats = this.getTargetFormats(targetEncoding, sourceStream.getFormat(), false);
        if (formats != null && formats.length > 0) {
            return this.getAudioInputStream(formats[0], sourceStream);
        }
        throw new IllegalArgumentException("conversion not supported");
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        AudioFormat[] formats = this.getTargetFormats(targetFormat.getEncoding(), sourceFormat, false);
        if (formats != null && formats.length > 0) {
            if (sourceFormat.equals(targetFormat)) {
                return sourceStream;
            }
            if (sourceFormat.getChannels() == targetFormat.getChannels() && sourceFormat.getSampleSizeInBits() == targetFormat.getSampleSizeInBits() && !targetFormat.isBigEndian() && sourceFormat.getEncoding().equals(FlacEncoding.FLAC) && targetFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
                return new Flac2PcmAudioInputStream(sourceStream, targetFormat, -1L);
            }
            if (sourceFormat.getChannels() == targetFormat.getChannels() && sourceFormat.getSampleSizeInBits() == targetFormat.getSampleSizeInBits() && sourceFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && targetFormat.getEncoding().equals(FlacEncoding.FLAC)) {
                throw new IllegalArgumentException("FLAC encoder not yet implemented");
            }
            throw new IllegalArgumentException("unable to convert " + sourceFormat.toString() + " to " + targetFormat.toString());
        }
        throw new IllegalArgumentException("conversion not supported");
    }
}

