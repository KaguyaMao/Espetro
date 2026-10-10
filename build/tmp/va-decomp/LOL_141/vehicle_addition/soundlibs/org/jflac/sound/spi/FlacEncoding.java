/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import javax.sound.sampled.AudioFormat;

public class FlacEncoding
extends AudioFormat.Encoding {
    public static final FlacEncoding FLAC = new FlacEncoding("FLAC");

    public FlacEncoding(String name) {
        super(name);
    }
}

