/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.EOSException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ByteArrayBitStream
implements BitStream {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.BitStream");
    private static final int WORD_BITS = 32;
    private static final int WORD_BYTES = 4;
    private static final int BYTE_MASK = 255;
    private byte[] buffer;
    private int length;
    private int pos;
    private int cache;
    protected int bitsCached;
    protected int position;

    private void log(Level level, String message, int arg) {
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, String.format(message, this.position, arg));
        }
    }

    public String toString() {
        if (this.buffer == null) {
            return "[]";
        }
        return String.format("[%d;%d]", this.getPosition(), this.length);
    }

    ByteArrayBitStream() {
    }

    ByteArrayBitStream(ByteArrayBitStream other, int size) {
        if (other.getBitsLeft() < size) {
            throw new EOSException("stream overrun");
        }
        this.length = other.position + size;
        this.buffer = other.buffer;
        this.pos = other.pos;
        this.cache = other.cache;
        this.bitsCached = other.bitsCached;
        this.position = other.position;
        other.skipBits(size);
    }

    ByteArrayBitStream(byte[] data) {
        this.setData(data);
    }

    public void destroy() {
        this.reset();
        this.buffer = null;
    }

    public final void setData(byte[] data) {
        this.reset();
        int size = data.length;
        this.length = 8 * size;
        int shift = size % 4;
        this.bitsCached = 8 * shift;
        for (int i = 0; i < shift; ++i) {
            byte c = data[i];
            this.cache <<= 8;
            this.cache |= 0xFF & c;
        }
        if (this.buffer == null || this.buffer.length != (size -= shift)) {
            this.buffer = new byte[size];
        }
        System.arraycopy(data, shift, this.buffer, 0, this.buffer.length);
    }

    @Override
    public BitStream readSubStream(int n) {
        if (this.getBitsLeft() < n) {
            throw new EOSException("stream overrun");
        }
        return new ByteArrayBitStream(this, n);
    }

    @Override
    public void byteAlign() {
        this.log(Level.FINER, "@%d byteAlign: %d", this.position);
        int toFlush = this.bitsCached & 7;
        if (toFlush > 0) {
            this.skipBits(toFlush);
        }
    }

    public final void reset() {
        this.pos = 0;
        this.length = 0;
        this.bitsCached = 0;
        this.cache = 0;
        this.position = 0;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public int getBitsLeft() {
        return this.length - this.position;
    }

    protected int readCache(boolean peek) {
        if (this.pos > this.buffer.length - 4) {
            throw new EOSException("end of stream");
        }
        int i = (this.buffer[this.pos] & 0xFF) << 24 | (this.buffer[this.pos + 1] & 0xFF) << 16 | (this.buffer[this.pos + 2] & 0xFF) << 8 | this.buffer[this.pos + 3] & 0xFF;
        if (!peek) {
            this.pos += 4;
        }
        return i;
    }

    @Override
    public int readBits(int n) {
        int result;
        this.log(n == 0 ? Level.FINEST : Level.FINER, "@%d readBits: %d", n);
        if (this.getBitsLeft() < n) {
            throw new EOSException("stream overrun");
        }
        if (this.bitsCached >= n) {
            this.bitsCached -= n;
            result = this.cache >> this.bitsCached & ByteArrayBitStream.maskBits(n);
            this.position += n;
        } else {
            this.position += n;
            int c = this.cache & ByteArrayBitStream.maskBits(this.bitsCached);
            int left = n - this.bitsCached;
            this.cache = this.readCache(false);
            this.bitsCached = 32 - left;
            result = this.cache >> this.bitsCached & ByteArrayBitStream.maskBits(left) | c << left;
        }
        return result;
    }

    @Override
    public int readBit() {
        int i;
        this.log(Level.FINER, "@%d readBit: %d", 1);
        if (this.getBitsLeft() < 1) {
            throw new EOSException("stream overrun");
        }
        if (this.bitsCached > 0) {
            --this.bitsCached;
            i = this.cache >> this.bitsCached & 1;
        } else {
            this.cache = this.readCache(false);
            this.bitsCached = 31;
            i = this.cache >> this.bitsCached & 1;
        }
        ++this.position;
        return i;
    }

    @Override
    public boolean readBool() {
        return (this.readBit() & 1) != 0;
    }

    @Override
    public int peekBits(int n) {
        int ret;
        this.log(Level.FINER, "@%d peekBits: %d", n);
        if (this.getBitsLeft() < n) {
            throw new EOSException("stream overrun");
        }
        if (this.bitsCached >= n) {
            ret = this.cache >> this.bitsCached - n & ByteArrayBitStream.maskBits(n);
        } else {
            int c = this.cache & ByteArrayBitStream.maskBits(this.bitsCached);
            ret = this.readCache(true) >> 32 - (n -= this.bitsCached) & ByteArrayBitStream.maskBits(n) | c << n;
        }
        return ret;
    }

    public int peekBit() {
        int ret;
        this.log(Level.FINER, "@%d peekBit: %d", 1);
        if (this.getBitsLeft() < 1) {
            throw new EOSException("stream overrun");
        }
        if (this.bitsCached > 0) {
            ret = this.cache >> this.bitsCached - 1 & 1;
        } else {
            int word = this.readCache(true);
            ret = word >> 31 & 1;
        }
        return ret;
    }

    @Override
    public void skipBits(int n) {
        this.log(Level.FINER, "@%d skipBits: %d", n);
        if (this.getBitsLeft() < n) {
            throw new EOSException("stream overrun");
        }
        this.position += n;
        if (n <= this.bitsCached) {
            this.bitsCached -= n;
        } else {
            n -= this.bitsCached;
            while (n >= 32) {
                n -= 32;
                this.readCache(false);
            }
            if (n > 0) {
                this.cache = this.readCache(false);
                this.bitsCached = 32 - n;
            } else {
                this.cache = 0;
                this.bitsCached = 0;
            }
        }
    }

    @Override
    public void skipBit() {
        this.log(Level.FINER, "@%d skipBit: %d", 1);
        if (this.getBitsLeft() < 1) {
            throw new EOSException("end of stream");
        }
        ++this.position;
        if (this.bitsCached > 0) {
            --this.bitsCached;
        } else {
            this.cache = this.readCache(false);
            this.bitsCached = 31;
        }
    }

    public static int maskBits(int n) {
        int i = n == 32 ? -1 : (1 << n) - 1;
        return i;
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.WARNING);
        }
    }
}

