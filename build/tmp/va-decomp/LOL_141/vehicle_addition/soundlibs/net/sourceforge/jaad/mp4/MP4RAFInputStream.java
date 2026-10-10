/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

class MP4RAFInputStream
extends MP4InputStream {
    private final RandomAccessFile fin;

    MP4RAFInputStream(RandomAccessFile fin) {
        this.fin = fin;
    }

    @Override
    public int read() throws IOException {
        return this.fin.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.fin.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.fin.skipBytes((int)Math.min(Integer.MAX_VALUE, n));
    }

    @Override
    public long getOffset() throws IOException {
        return this.fin.getFilePointer();
    }

    @Override
    public void seek(long pos) throws IOException {
        this.fin.seek(pos);
    }

    @Override
    public boolean seekSupported() {
        return true;
    }

    @Override
    public boolean hasLeft() throws IOException {
        return this.fin.getFilePointer() < this.fin.length() - 1L;
    }

    @Override
    public void close() throws IOException {
        this.fin.close();
    }

    @Override
    public int available() throws IOException {
        return (int)Math.min(Integer.MAX_VALUE, this.fin.length() - this.fin.getFilePointer() - 1L);
    }
}

