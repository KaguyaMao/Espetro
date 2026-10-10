/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4DefaultInputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4MarkResetInputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4RAFInputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4ResourceInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;

public abstract class MP4InputStream
extends InputStream {
    public static final String UTF8 = "UTF-8";
    public static final String UTF16 = "UTF-16";
    static final int MAX_BUFFER_SIZE = 0x7FFFFFF7;
    private static final int BYTE_ORDER_MASK = 65279;

    public static MP4InputStream open(ClassLoader resourceLoader, String resource) throws IOException {
        return new MP4ResourceInputStream(resourceLoader, resource);
    }

    public static MP4InputStream open(InputStream in) {
        return in.markSupported() ? new MP4MarkResetInputStream(in) : new MP4DefaultInputStream(in);
    }

    public static MP4InputStream open(RandomAccessFile in) {
        return new MP4RAFInputStream(in);
    }

    public int readByte() throws IOException {
        int i = this.read();
        if (i == -1) {
            throw new EOFException();
        }
        return i;
    }

    public void readBytes(byte[] b, int off, int len) throws IOException {
        int i;
        for (int read = 0; read < len; read += i) {
            i = this.read(b, off + read, len - read);
            if (i >= 0) continue;
            throw new EOFException();
        }
    }

    public long readBytes(int n) throws IOException {
        if (n < 1 || n > 8) {
            throw new IndexOutOfBoundsException("invalid number of bytes to read: " + n);
        }
        byte[] b = new byte[n];
        this.readBytes(b, 0, n);
        long result = 0L;
        for (int i = 0; i < n; ++i) {
            result = result << 8 | (long)(b[i] & 0xFF);
        }
        return result;
    }

    public void readBytes(byte[] b) throws IOException {
        this.readBytes(b, 0, b.length);
    }

    public String readString(int n) throws IOException {
        int pos;
        int i = -1;
        char[] c = new char[n];
        for (pos = 0; pos < n; ++pos) {
            i = this.readByte();
            c[pos] = (char)i;
        }
        return new String(c, 0, pos);
    }

    public String readUTFString(int max, String encoding) throws IOException {
        return new String(this.readTerminated(max, 0), Charset.forName(encoding));
    }

    public String readUTFString(int max) throws IOException {
        byte[] bom = new byte[2];
        this.readBytes(bom, 0, 2);
        if (bom[0] == 0 || bom[1] == 0) {
            return "";
        }
        int i = bom[0] << 8 | bom[1];
        byte[] b = this.readTerminated(max - 2, 0);
        byte[] b2 = new byte[b.length + bom.length];
        System.arraycopy(bom, 0, b2, 0, bom.length);
        System.arraycopy(b, 0, b2, bom.length, b.length);
        return new String(b2, Charset.forName(i == 65279 ? UTF16 : UTF8));
    }

    public byte[] readTerminated(int max, int terminator) throws IOException {
        byte[] b = new byte[max];
        int pos = 0;
        int i = 0;
        do {
            if ((i = this.readByte()) == -1 || i == terminator) continue;
            b[pos++] = (byte)i;
        } while (pos < max && i != -1 && i != terminator);
        return Arrays.copyOf(b, pos);
    }

    public double readFixedPoint(int m, int n) throws IOException {
        int bits = m + n;
        if (bits % 8 != 0) {
            throw new IllegalArgumentException("number of bits is not a multiple of 8: " + (m + n));
        }
        long l = this.readBytes(bits / 8);
        double x = Math.pow(2.0, n);
        double d = (double)l / x;
        return d;
    }

    public void skipBytes(long n) throws IOException {
        for (long l = 0L; l < n; l += this.skip((int)(n - l))) {
        }
    }

    public abstract long getOffset() throws IOException;

    public abstract void seek(long var1) throws IOException;

    public abstract boolean seekSupported();

    public abstract boolean hasLeft() throws IOException;

    @Override
    public abstract void close() throws IOException;

    @Override
    public abstract int read() throws IOException;

    @Override
    public abstract int read(byte[] var1, int var2, int var3) throws IOException;

    @Override
    public abstract long skip(long var1) throws IOException;

    @Override
    public abstract int available() throws IOException;
}

