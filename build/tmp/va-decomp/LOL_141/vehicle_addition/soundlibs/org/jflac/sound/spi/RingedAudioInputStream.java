/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import LOL_141.vehicle_addition.soundlibs.org.jflac.util.RingBuffer;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class RingedAudioInputStream
extends AudioInputStream {
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    protected InputStream in;
    private final byte[] single = new byte[1];
    protected RingBuffer buffer = new RingBuffer();

    private boolean checkIfStillOpen() throws IOException {
        return this.in == null;
    }

    public RingedAudioInputStream(InputStream in, AudioFormat format, long length) {
        this(in, format, length, 2048);
    }

    public RingedAudioInputStream(InputStream in, AudioFormat format, long length, int size) {
        this(in, format, length, size, size);
    }

    public RingedAudioInputStream(InputStream in, AudioFormat format, long length, int size, int presize) {
        super(in, format, length);
        this.in = in;
        if (format.getFrameSize() > 0) {
            this.buffer.resize(format.getFrameSize() * 2);
        }
    }

    protected void fill() throws IOException {
    }

    @Override
    public synchronized int read() throws IOException {
        this.fill();
        if (this.buffer.get(this.single, 0, 1) == -1) {
            return -1;
        }
        return this.single[0] & 0xFF;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (this.checkIfStillOpen()) {
            return -1;
        }
        int frameSize = this.getFormat().getFrameSize();
        int bytesRead = 0;
        len -= len % frameSize;
        while (len > 0) {
            int thisBytesRead;
            int thisLen = len;
            if (thisLen > this.buffer.getAvailable()) {
                thisLen = this.buffer.getAvailable();
            }
            if (thisLen < frameSize) {
                this.fill();
                if (this.buffer.getAvailable() >= frameSize) continue;
                break;
            }
            if ((thisBytesRead = this.buffer.get(b, off, thisLen -= thisLen % frameSize)) < frameSize) break;
            off += thisBytesRead;
            len -= thisBytesRead;
            bytesRead += thisBytesRead;
        }
        if (bytesRead == 0 && this.buffer.isEOF()) {
            return -1;
        }
        return bytesRead;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        if (this.checkIfStillOpen()) {
            throw new IOException("Stream closed");
        }
        throw new IOException("skip not supported");
    }

    @Override
    public synchronized int available() throws IOException {
        if (this.checkIfStillOpen()) {
            throw new IOException("Stream closed");
        }
        if (this.buffer.getAvailable() < this.getFormat().getFrameSize()) {
            this.fill();
        }
        return this.buffer.getAvailable();
    }

    @Override
    public synchronized void mark(int readlimit) {
    }

    @Override
    public synchronized void reset() throws IOException {
        if (this.checkIfStillOpen()) {
            throw new IOException("Stream closed");
        }
        throw new IOException("reset not supported");
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.in == null) {
            return;
        }
        this.in.close();
        this.in = null;
    }
}

