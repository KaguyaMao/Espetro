/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.ArraySet;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.AudioFormats;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert.TSimpleFormatConversionProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFormat;

public abstract class TMatrixFormatConversionProvider
extends TSimpleFormatConversionProvider {
    private Map m_targetEncodingsFromSourceFormat = new HashMap();
    private Map m_targetFormatsFromSourceFormat = new HashMap();

    protected TMatrixFormatConversionProvider(List sourceFormats, List targetFormats, boolean[][] abConversionPossible) {
        super(sourceFormats, targetFormats);
        for (int nSourceFormat = 0; nSourceFormat < sourceFormats.size(); ++nSourceFormat) {
            AudioFormat sourceFormat = (AudioFormat)sourceFormats.get(nSourceFormat);
            ArraySet<AudioFormat.Encoding> supportedTargetEncodings = new ArraySet<AudioFormat.Encoding>();
            this.m_targetEncodingsFromSourceFormat.put(sourceFormat, supportedTargetEncodings);
            HashMap<AudioFormat.Encoding, ArraySet<AudioFormat>> targetFormatsFromTargetEncodings = new HashMap<AudioFormat.Encoding, ArraySet<AudioFormat>>();
            this.m_targetFormatsFromSourceFormat.put(sourceFormat, targetFormatsFromTargetEncodings);
            for (int nTargetFormat = 0; nTargetFormat < targetFormats.size(); ++nTargetFormat) {
                AudioFormat targetFormat = (AudioFormat)targetFormats.get(nTargetFormat);
                if (!abConversionPossible[nSourceFormat][nTargetFormat]) continue;
                AudioFormat.Encoding targetEncoding = targetFormat.getEncoding();
                supportedTargetEncodings.add(targetEncoding);
                ArraySet<AudioFormat> supportedTargetFormats = (ArraySet<AudioFormat>)targetFormatsFromTargetEncodings.get(targetEncoding);
                if (supportedTargetFormats == null) {
                    supportedTargetFormats = new ArraySet<AudioFormat>();
                    targetFormatsFromTargetEncodings.put(targetEncoding, supportedTargetFormats);
                }
                supportedTargetFormats.add(targetFormat);
            }
        }
    }

    public AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
        for (Map.Entry entry : this.m_targetEncodingsFromSourceFormat.entrySet()) {
            AudioFormat format = (AudioFormat)entry.getKey();
            if (!AudioFormats.matches(format, sourceFormat)) continue;
            Collection targetEncodings = (Collection)entry.getValue();
            return targetEncodings.toArray(EMPTY_ENCODING_ARRAY);
        }
        return EMPTY_ENCODING_ARRAY;
    }

    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        for (Map.Entry entry : this.m_targetFormatsFromSourceFormat.entrySet()) {
            AudioFormat format = (AudioFormat)entry.getKey();
            if (!AudioFormats.matches(format, sourceFormat)) continue;
            Map targetEncodings = (Map)entry.getValue();
            Collection targetFormats = (Collection)targetEncodings.get(targetEncoding);
            if (targetFormats != null) {
                return targetFormats.toArray(EMPTY_FORMAT_ARRAY);
            }
            return EMPTY_FORMAT_ARRAY;
        }
        return EMPTY_FORMAT_ARRAY;
    }
}

