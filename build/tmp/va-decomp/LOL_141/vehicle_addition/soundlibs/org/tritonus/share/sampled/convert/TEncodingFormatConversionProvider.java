/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.ArraySet;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert.TSimpleFormatConversionProvider;
import java.util.Collection;
import javax.sound.sampled.AudioFormat;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TEncodingFormatConversionProvider
extends TSimpleFormatConversionProvider {
    protected TEncodingFormatConversionProvider(Collection<AudioFormat> sourceFormats, Collection<AudioFormat> targetFormats) {
        super(sourceFormats, targetFormats);
    }

    @Override
    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        if (TDebug.TraceAudioConverter) {
            TDebug.out(">TEncodingFormatConversionProvider.getTargetFormats(AudioFormat.Encoding, AudioFormat):");
            TDebug.out("checking if conversion possible");
            TDebug.out("from: " + sourceFormat);
            TDebug.out("to: " + targetEncoding);
        }
        if (this.isConversionSupported(targetEncoding, sourceFormat)) {
            ArraySet<AudioFormat> result = new ArraySet<AudioFormat>();
            for (AudioFormat targetFormat : this.getCollectionTargetFormats()) {
                targetFormat = this.replaceNotSpecified(sourceFormat, targetFormat);
                result.add(targetFormat);
            }
            if (TDebug.TraceAudioConverter) {
                TDebug.out("< returning " + result.size() + " elements.");
            }
            return result.toArray(EMPTY_FORMAT_ARRAY);
        }
        if (TDebug.TraceAudioConverter) {
            TDebug.out("< returning empty array.");
        }
        return EMPTY_FORMAT_ARRAY;
    }
}

