/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file;

import javax.sound.sampled.AudioFormat;

public class AiffTool {
    public static final int AIFF_FORM_MAGIC = 1179603533;
    public static final int AIFF_AIFF_MAGIC = 1095321158;
    public static final int AIFF_AIFC_MAGIC = 1095321155;
    public static final int AIFF_COMM_MAGIC = 1129270605;
    public static final int AIFF_SSND_MAGIC = 1397968452;
    public static final int AIFF_FVER_MAGIC = 1180058962;
    public static final int AIFF_COMM_UNSPECIFIED = 0;
    public static final int AIFF_COMM_PCM = 1313820229;
    public static final int AIFF_COMM_ULAW = 1970037111;
    public static final int AIFF_COMM_IMA_ADPCM = 1768775988;
    public static final int AIFF_FVER_TIME_STAMP = -1568648896;

    public static int getFormatCode(AudioFormat format) {
        AudioFormat.Encoding encoding = format.getEncoding();
        int nSampleSize = format.getSampleSizeInBits();
        boolean frameSizeOK = format.getFrameSize() == -1 || format.getChannels() != -1 || format.getFrameSize() == nSampleSize / 8 * format.getChannels();
        boolean signed = encoding.equals(AudioFormat.Encoding.PCM_SIGNED);
        boolean unsigned = encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED);
        if (nSampleSize == 8 && frameSizeOK && (signed || unsigned)) {
            return 1313820229;
        }
        if (nSampleSize > 8 && nSampleSize <= 32 && frameSizeOK && signed) {
            return 1313820229;
        }
        if (encoding.equals(AudioFormat.Encoding.ULAW) && nSampleSize == 8 && frameSizeOK) {
            return 1970037111;
        }
        if (encoding.equals(new AudioFormat.Encoding("IMA_ADPCM")) && nSampleSize == 4) {
            return 1768775988;
        }
        return 0;
    }
}

