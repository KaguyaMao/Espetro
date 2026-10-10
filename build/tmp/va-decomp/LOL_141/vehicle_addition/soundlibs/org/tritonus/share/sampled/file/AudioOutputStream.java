/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

public interface AudioOutputStream {
    public AudioFormat getFormat();

    public long getLength();

    public int write(byte[] var1, int var2, int var3) throws IOException;

    public void close() throws IOException;
}

