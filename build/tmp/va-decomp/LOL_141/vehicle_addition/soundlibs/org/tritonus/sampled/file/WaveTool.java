/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file;

import javax.sound.sampled.AudioFormat;

public class WaveTool {
    public static final int WAVE_RIFF_MAGIC = 1380533830;
    public static final int WAVE_WAVE_MAGIC = 1463899717;
    public static final int WAVE_FMT_MAGIC = 1718449184;
    public static final int WAVE_DATA_MAGIC = 1684108385;
    public static final int WAVE_FACT_MAGIC = 1717658484;
    public static final short WAVE_FORMAT_UNSPECIFIED = 0;
    public static final short WAVE_FORMAT_PCM = 1;
    public static final short WAVE_FORMAT_MS_ADPCM = 2;
    public static final short WAVE_FORMAT_ALAW = 6;
    public static final short WAVE_FORMAT_ULAW = 7;
    public static final short WAVE_FORMAT_IMA_ADPCM = 17;
    public static final short WAVE_FORMAT_G723_ADPCM = 20;
    public static final short WAVE_FORMAT_GSM610 = 49;
    public static final short WAVE_FORMAT_G721_ADPCM = 64;
    public static final short WAVE_FORMAT_MPEG = 80;
    public static final int MIN_FMT_CHUNK_LENGTH = 14;
    public static final int MIN_DATA_OFFSET = 42;
    public static final int MIN_FACT_CHUNK_LENGTH = 4;
    public static final int FMT_CHUNK_SIZE = 18;
    public static final int RIFF_CONTAINER_CHUNK_SIZE = 12;
    public static final int CHUNK_HEADER_SIZE = 8;
    public static final int DATA_OFFSET = 46;
    public static AudioFormat.Encoding GSM0610 = new AudioFormat.Encoding("GSM0610");
    public static AudioFormat.Encoding IMA_ADPCM = new AudioFormat.Encoding("IMA_ADPCM");

    public static short getFormatCode(AudioFormat format) {
        AudioFormat.Encoding encoding = format.getEncoding();
        int nSampleSize = format.getSampleSizeInBits();
        boolean frameSizeOK = format.getFrameSize() == -1 || format.getChannels() != -1 || format.getFrameSize() == (nSampleSize + 7) / 8 * format.getChannels();
        boolean signed = encoding.equals(AudioFormat.Encoding.PCM_SIGNED);
        boolean unsigned = encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED);
        if (nSampleSize == 8 && frameSizeOK && (signed || unsigned)) {
            return 1;
        }
        if (nSampleSize > 8 && nSampleSize <= 32 && frameSizeOK && signed) {
            return 1;
        }
        if (encoding.equals(AudioFormat.Encoding.ULAW) && (nSampleSize == -1 || nSampleSize == 8) && frameSizeOK) {
            return 7;
        }
        if (encoding.equals(AudioFormat.Encoding.ALAW) && (nSampleSize == -1 || nSampleSize == 8) && frameSizeOK) {
            return 6;
        }
        if (encoding.equals(new AudioFormat.Encoding("IMA_ADPCM")) && nSampleSize == 4) {
            return 17;
        }
        if (encoding.equals(GSM0610)) {
            return 49;
        }
        return 0;
    }
}

