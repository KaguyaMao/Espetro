/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.CircularBuffer;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

abstract class AsynchronousAudioInputStream
extends AudioInputStream
implements CircularBuffer.Trigger {
    private byte[] singleByte;
    protected final CircularBuffer buffer = new CircularBuffer(this);

    AsynchronousAudioInputStream(InputStream in, AudioFormat format, long length) throws IOException {
        super(in, format, length);
    }

    @Override
    public int read() throws IOException {
        if (this.singleByte == null) {
            this.singleByte = new byte[1];
        }
        int i = this.buffer.read(this.singleByte, 0, 1) == -1 ? -1 : this.singleByte[0] & 0xFF;
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.buffer.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.buffer.read(b, off, len);
    }

    @Override
    public long skip(long len) throws IOException {
        int l;
        byte[] b = new byte[l];
        for (l = (int)len; l > 0; l -= this.buffer.read(b, 0, l)) {
        }
        return len;
    }

    @Override
    public int available() throws IOException {
        return this.buffer.availableRead();
    }

    @Override
    public void close() throws IOException {
        this.buffer.close();
        super.close();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int limit) {
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("mark not supported");
    }
}

