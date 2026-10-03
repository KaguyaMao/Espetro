/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.client.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MusicBufferedInputStream
extends BufferedInputStream {
    public MusicBufferedInputStream(InputStream in) {
        super(in);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            return super.read(b, off, len);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

