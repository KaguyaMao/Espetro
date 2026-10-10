/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import javax.sound.sampled.AudioFileFormat;

public class FlacFileFormatType
extends AudioFileFormat.Type {
    public static final AudioFileFormat.Type FLAC = new FlacFileFormatType("FLAC", "flac");

    public FlacFileFormatType(String name, String extension) {
        super(name, extension);
    }
}

