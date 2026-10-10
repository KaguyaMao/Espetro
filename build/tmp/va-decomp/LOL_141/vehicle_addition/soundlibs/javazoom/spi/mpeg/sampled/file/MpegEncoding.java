/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file;

import javax.sound.sampled.AudioFormat;

public class MpegEncoding
extends AudioFormat.Encoding {
    public static final AudioFormat.Encoding MPEG1L1 = new MpegEncoding("MPEG1L1");
    public static final AudioFormat.Encoding MPEG1L2 = new MpegEncoding("MPEG1L2");
    public static final AudioFormat.Encoding MPEG1L3 = new MpegEncoding("MPEG1L3");
    public static final AudioFormat.Encoding MPEG2L1 = new MpegEncoding("MPEG2L1");
    public static final AudioFormat.Encoding MPEG2L2 = new MpegEncoding("MPEG2L2");
    public static final AudioFormat.Encoding MPEG2L3 = new MpegEncoding("MPEG2L3");
    public static final AudioFormat.Encoding MPEG2DOT5L1 = new MpegEncoding("MPEG2DOT5L1");
    public static final AudioFormat.Encoding MPEG2DOT5L2 = new MpegEncoding("MPEG2DOT5L2");
    public static final AudioFormat.Encoding MPEG2DOT5L3 = new MpegEncoding("MPEG2DOT5L3");

    public MpegEncoding(String strName) {
        super(strName);
    }
}

