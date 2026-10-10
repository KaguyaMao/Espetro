/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;
import java.io.InputStream;

class MP4ResourceInputStream
extends MP4InputStream {
    private final String resource;
    private final ClassLoader resourceLoader;
    private volatile boolean closed = false;
    private InputStream in = null;
    private long offset;

    MP4ResourceInputStream(ClassLoader resourceLoader, String resource) throws IOException {
        this.resourceLoader = resourceLoader;
        this.resource = resource;
        this.ensureStreamAvailable();
    }

    @Override
    public int read() throws IOException {
        this.ensureOpen();
        int i = this.in.read();
        if (i >= 0) {
            ++this.offset;
        }
        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.ensureOpen();
        int i = this.in.read(b, off, len);
        if (i > 0) {
            this.offset += (long)i;
        }
        return i;
    }

    @Override
    public long skip(long n) throws IOException {
        this.ensureOpen();
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
        this.checkNotClosed();
        if (pos < 0L) {
            throw new IndexOutOfBoundsException("negative position: " + pos);
        }
        long bytesToSkip = pos - this.offset;
        if (bytesToSkip >= 0L) {
            this.ensureStreamAvailable();
            this.skipBytes(bytesToSkip);
        } else {
            this.in.close();
            this.in = null;
            this.ensureStreamAvailable();
            this.skipBytes(pos);
        }
    }

    @Override
    public boolean seekSupported() {
        return true;
    }

    @Override
    public boolean hasLeft() throws IOException {
        this.ensureOpen();
        return this.in.available() > 0;
    }

    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.in.close();
    }

    private void checkNotClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Already closed");
        }
    }

    private void ensureStreamAvailable() throws IOException {
        if (this.in == null) {
            this.in = this.resourceLoader.getResourceAsStream(this.resource);
            if (this.in == null) {
                throw new IOException("Couldn't read resource '" + this.resource + "' with ClassLoader '" + String.valueOf(this.resourceLoader) + "'");
            }
            this.offset = 0L;
        }
    }

    private void ensureOpen() throws IOException {
        this.checkNotClosed();
        this.ensureStreamAvailable();
    }

    @Override
    public int available() throws IOException {
        this.ensureOpen();
        return this.in.available();
    }
}

