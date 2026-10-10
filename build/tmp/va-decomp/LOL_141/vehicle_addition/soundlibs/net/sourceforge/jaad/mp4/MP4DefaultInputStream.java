/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;
import java.io.InputStream;

class MP4DefaultInputStream
extends MP4InputStream {
    private final InputStream in;
    private long offset;

    MP4DefaultInputStream(InputStream in) {
        this.in = in;
        this.offset = 0L;
    }

    @Override
    public int read() throws IOException {
        int i = this.in.read();
        if (i >= 0) {
            ++this.offset;
        }
        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = this.in.read(b, off, len);
        if (i > 0) {
            this.offset += (long)i;
        }
        return i;
    }

    @Override
    public long skip(long n) throws IOException {
        long i = this.in.skip(n);
        if (i > 0L) {
            this.offset += i;
        }
        return i;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public void seek(long pos) throws IOException {
        throw new IOException("could not seek");
    }

    @Override
    public boolean seekSupported() {
        return false;
    }

    @Override
    public boolean hasLeft() throws IOException {
        return this.in.available() > 0;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }
}

