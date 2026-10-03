/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file;

import javax.sound.sampled.AudioFileFormat;

public class MpegFileFormatType
extends AudioFileFormat.Type {
    public static final AudioFileFormat.Type MPEG = new MpegFileFormatType("MPEG", "mpeg");
    public static final AudioFileFormat.Type MP3 = new MpegFileFormatType("MP3", "mp3");

    public MpegFileFormatType(String strName, String strExtension) {
        super(strName, strExtension);
    }
}

