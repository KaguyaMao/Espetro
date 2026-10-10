/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.io;

import LOL_141.vehicle_addition.soundlibs.org.jflac.util.CRC16;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.CRC8;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream {
    private static final int BITS_PER_BLURB = 8;
    private static final long[] MASK32 = new long[]{0L, 1L, 3L, 7L, 15L, 31L, 63L, 127L, 255L, 511L, 1023L, 2047L, 4095L, 8191L, 16383L, 32767L, 65535L, 131071L, 262143L, 524287L, 1048575L, 0x1FFFFFL, 0x3FFFFFL, 0x7FFFFFL, 0xFFFFFFL, 0x1FFFFFFL, 0x3FFFFFFL, 0x7FFFFFFL, 0xFFFFFFFL, 0x1FFFFFFFL, 0x3FFFFFFFL, Integer.MAX_VALUE, -1L, 0x1FFFFFFFFL, 0x3FFFFFFFFL, 0x7FFFFFFFFL, 0xFFFFFFFFFL, 0x1FFFFFFFFFL, 0x3FFFFFFFFFL, 0x7FFFFFFFFFL, 0xFFFFFFFFFFL, 0x1FFFFFFFFFFL, 0x3FFFFFFFFFFL, 0x7FFFFFFFFFFL, 0xFFFFFFFFFFFL, 0x1FFFFFFFFFFFL, 0x3FFFFFFFFFFFL, 0x7FFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFL, 0x3FFFFFFFFFFFFL, 0x7FFFFFFFFFFFFL, 0xFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFL, 0x3FFFFFFFFFFFFFL, 0x7FFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFFL, 0x3FFFFFFFFFFFFFFL, 0x7FFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFL, 0x1FFFFFFFFFFFFFFFL, 0x3FFFFFFFFFFFFFFFL, Long.MAX_VALUE, -1L};
    private byte[] buffer = new byte[0];
    private int outCapacity = 0;
    private int outBlurbs = 0;
    private int outBits = 0;
    private int totalBits = 0;
    private int consumedBlurbs = 0;
    private int consumedBits = 0;
    private int totalConsumedBits = 0;
    private short readCRC16 = 0;
    private OutputStream os;

    private boolean resize(int newCapacity) {
        if (this.outCapacity >= newCapacity) {
            return true;
        }
        byte[] newBuffer = new byte[newCapacity];
        System.arraycopy(this.buffer, 0, newBuffer, 0, Math.min(this.outBlurbs + (this.outBits != 0 ? 1 : 0), newCapacity));
        if (newCapacity < this.outBlurbs + (this.outBits != 0 ? 1 : 0)) {
            this.outBlurbs = newCapacity;
            this.outBits = 0;
            this.totalBits = newCapacity << 3;
        }
        if (newCapacity < this.consumedBlurbs + (this.consumedBits != 0 ? 1 : 0)) {
            this.consumedBlurbs = newCapacity;
            this.consumedBits = 0;
            this.totalConsumedBits = newCapacity << 3;
        }
        this.buffer = newBuffer;
        this.outCapacity = newCapacity;
        return true;
    }

    private boolean grow(int minBlurbsToAdd) {
        int newCapacity = Math.max(this.outCapacity * 2, this.outCapacity + minBlurbsToAdd);
        return this.resize(newCapacity);
    }

    private boolean ensureSize(int bitsToAdd) {
        if (this.outCapacity << 3 < this.totalBits + bitsToAdd) {
            return this.grow((bitsToAdd >> 3) + 2);
        }
        return true;
    }

    public BitOutputStream(OutputStream os) {
        this.os = os;
    }

    public BitOutputStream() {
    }

    public boolean concatenateAligned(BitOutputStream src) {
        int bitsToAdd = src.totalBits - src.totalConsumedBits;
        if (bitsToAdd == 0) {
            return true;
        }
        if (this.outBits != src.consumedBits) {
            return false;
        }
        if (!this.ensureSize(bitsToAdd)) {
            return false;
        }
        if (this.outBits == 0) {
            System.arraycopy(src.buffer, src.consumedBlurbs, this.buffer, this.outBlurbs, src.outBlurbs - src.consumedBlurbs + (src.outBits != 0 ? 1 : 0));
        } else if (this.outBits + bitsToAdd > 8) {
            int n = this.outBlurbs;
            this.buffer[n] = (byte)(this.buffer[n] << 8 - this.outBits);
            int n2 = this.outBlurbs;
            this.buffer[n2] = (byte)(this.buffer[n2] | src.buffer[src.consumedBlurbs] & (1 << 8 - this.outBits) - 1);
            System.arraycopy(src.buffer, src.consumedBlurbs + 1, this.buffer, this.outBlurbs + 11, src.outBlurbs - src.consumedBlurbs - 1 + (src.outBits != 0 ? 1 : 0));
        } else {
            int n = this.outBlurbs;
            this.buffer[n] = (byte)(this.buffer[n] << bitsToAdd);
            int n3 = this.outBlurbs;
            this.buffer[n3] = (byte)(this.buffer[n3] | src.buffer[src.consumedBlurbs] & (1 << bitsToAdd) - 1);
        }
        this.outBits = src.outBits;
        this.totalBits += bitsToAdd;
        this.outBlurbs = this.totalBits / 8;
        return true;
    }

    public void resetReadCRC16(short seed) {
        this.readCRC16 = seed;
    }

    public short getReadCRC16() {
        return this.readCRC16;
    }

    public short getWriteCRC16() {
        return CRC16.calc(this.buffer, this.outBlurbs);
    }

    public byte getWriteCRC8() {
        return CRC8.calc(this.buffer, this.outBlurbs);
    }

    public boolean isByteAligned() {
        return (this.outBits & 7) == 0;
    }

    public boolean isConsumedByteAligned() {
        return (this.consumedBits & 7) == 0;
    }

    public int bitsLeftForByteAlignment() {
        return 8 - (this.consumedBits & 7);
    }

    public int getInputBytesUnconsumed() {
        return this.totalBits - this.totalConsumedBits >> 3;
    }

    public void writeZeroes(int bits) throws IOException {
        if (bits == 0) {
            return;
        }
        if (!this.ensureSize(bits)) {
            throw new IOException("Memory Allocation Error");
        }
        this.totalBits += bits;
        while (bits > 0) {
            int n = Math.min(8 - this.outBits, bits);
            int n2 = this.outBlurbs++;
            this.buffer[n2] = (byte)(this.buffer[n2] << n);
            bits -= n;
            this.outBits += n;
            if (this.outBits != 8) continue;
            this.outBits = 0;
        }
    }

    public void writeRawUInt(boolean val, int bits) throws IOException {
        this.writeRawUInt(val ? 1 : 0, bits);
    }

    public void writeRawUInt(int val, int bits) throws IOException {
        if (bits == 0) {
            return;
        }
        if (this.outCapacity << 3 < this.totalBits + bits && !this.ensureSize(bits)) {
            throw new IOException("Memory allocation error");
        }
        if (bits < 32) {
            val &= ~(-1 << bits);
        }
        this.totalBits += bits;
        while (bits > 0) {
            int k;
            int n = 8 - this.outBits;
            if (n == 8) {
                if (bits < 8) {
                    this.buffer[this.outBlurbs] = (byte)val;
                    this.outBits = bits;
                    break;
                }
                if (bits == 8) {
                    this.buffer[this.outBlurbs++] = (byte)val;
                    break;
                }
                k = bits - 8;
                this.buffer[this.outBlurbs++] = (byte)(val >> k);
                val &= ~(-1 << k);
                bits -= 8;
                continue;
            }
            if (bits <= n) {
                int n2 = this.outBlurbs;
                this.buffer[n2] = (byte)(this.buffer[n2] << bits);
                int n3 = this.outBlurbs++;
                this.buffer[n3] = (byte)(this.buffer[n3] | val);
                if (bits == n) {
                    this.outBits = 0;
                    break;
                }
                this.outBits += bits;
                break;
            }
            k = bits - n;
            int n4 = this.outBlurbs;
            this.buffer[n4] = (byte)(this.buffer[n4] << n);
            int n5 = this.outBlurbs++;
            this.buffer[n5] = (byte)(this.buffer[n5] | val >> k);
            val &= ~(-1 << k);
            bits -= n;
            this.outBits = 0;
        }
    }

    public void writeRawInt(int val, int bits) throws IOException {
        this.writeRawUInt(val, bits);
    }

    public void writeRawULong(long val, int bits) throws IOException {
        if (bits == 0) {
            return;
        }
        if (!this.ensureSize(bits)) {
            throw new IOException("Memory Allocate Error");
        }
        val &= MASK32[bits];
        this.totalBits += bits;
        while (bits > 0) {
            if (this.outBits == 0) {
                if (bits < 8) {
                    this.buffer[this.outBlurbs] = (byte)val;
                    this.outBits = bits;
                    break;
                }
                if (bits == 8) {
                    this.buffer[this.outBlurbs++] = (byte)val;
                    break;
                }
                int k = bits - 8;
                this.buffer[this.outBlurbs++] = (byte)(val >> k);
                val &= -1L << k ^ 0xFFFFFFFFFFFFFFFFL;
                bits -= 8;
                continue;
            }
            int n = Math.min(8 - this.outBits, bits);
            int k = bits - n;
            int n2 = this.outBlurbs;
            this.buffer[n2] = (byte)(this.buffer[n2] << n);
            int n3 = this.outBlurbs++;
            this.buffer[n3] = (byte)((long)this.buffer[n3] | val >> k);
            val &= -1L << k ^ 0xFFFFFFFFFFFFFFFFL;
            bits -= n;
            this.outBits += n;
            if (this.outBits != 8) continue;
            this.outBits = 0;
        }
    }

    public void writeRawUIntLittleEndian(int val) throws IOException {
        this.writeRawUInt(val, 8);
        this.writeRawUInt(val >> 8, 8);
        this.writeRawUInt(val >> 16, 8);
        this.writeRawUInt(val >> 24, 8);
    }

    public void writeByteBlock(byte[] vals, int nvals) throws IOException {
        for (int i = 0; i < nvals; ++i) {
            this.writeRawUInt(vals[i], 8);
        }
    }

    public void writeUnaryUnsigned(int val) throws IOException {
        if (val < 32) {
            this.writeRawUInt(1, ++val);
        } else if (val < 64) {
            this.writeRawULong(1L, ++val);
        } else {
            this.writeZeroes(val);
            this.writeRawUInt(1, 1);
        }
    }

    public int riceBits(int val, int parameter) {
        int uval = val < 0 ? (-(++val) << 1) + 1 : val << 1;
        int msbs = uval >> parameter;
        return 1 + parameter + msbs;
    }

    public void writeRiceSigned(int val, int parameter) throws IOException {
        int uval = val < 0 ? (-(++val) << 1) + 1 : val << 1;
        int msbs = uval >> parameter;
        int interestingBits = 1 + parameter;
        int totalBits = interestingBits + msbs;
        int pattern = 1 << parameter;
        pattern |= uval & (1 << parameter) - 1;
        if (totalBits <= 32) {
            this.writeRawUInt(pattern, totalBits);
        } else {
            this.writeZeroes(msbs);
            this.writeRawUInt(pattern, interestingBits);
        }
    }

    public void writeUTF8UInt(int val) throws IOException {
        if (val < 128) {
            this.writeRawUInt(val, 8);
        } else if (val < 2048) {
            this.writeRawUInt(0xC0 | val >> 6, 8);
            this.writeRawUInt(0x80 | val & 0x3F, 8);
        } else if (val < 65536) {
            this.writeRawUInt(0xE0 | val >> 12, 8);
            this.writeRawUInt(0x80 | val >> 6 & 0x3F, 8);
            this.writeRawUInt(0x80 | val & 0x3F, 8);
        } else if (val < 0x200000) {
            this.writeRawUInt(0xF0 | val >> 18, 8);
            this.writeRawUInt(0x80 | val >> 12 & 0x3F, 8);
            this.writeRawUInt(0x80 | val >> 6 & 0x3F, 8);
            this.writeRawUInt(0x80 | val & 0x3F, 8);
        } else if (val < 0x4000000) {
            this.writeRawUInt(0xF8 | val >> 24, 8);
            this.writeRawUInt(0x80 | val >> 18 & 0x3F, 8);
            this.writeRawUInt(0x80 | val >> 12 & 0x3F, 8);
            this.writeRawUInt(0x80 | val >> 6 & 0x3F, 8);
            this.writeRawUInt(0x80 | val & 0x3F, 8);
        } else {
            this.writeRawUInt(0xFC | val >> 30, 8);
            this.writeRawUInt(0x80 | val >> 24 & 0x3F, 8);
            this.writeRawUInt(0x80 | val >> 18 & 0x3F, 8);
            this.writeRawUInt(0x80 | val >> 12 & 0x3F, 8);
            this.writeRawUInt(0x80 | val >> 6 & 0x3F, 8);
            this.writeRawUInt(0x80 | val & 0x3F, 8);
        }
    }

    public void writeUTF8ULong(long val) throws IOException {
        if (val < 128L) {
            this.writeRawUInt((int)val, 8);
        } else if (val < 2048L) {
            this.writeRawUInt(0xC0 | (int)(val >> 6), 8);
            this.writeRawUInt(0x80 | (int)(val & 0x3FL), 8);
        } else if (val < 65536L) {
            this.writeRawUInt(0xE0 | (int)(val >> 12), 8);
            this.writeRawUInt(0x80 | (int)(val >> 6 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val & 0x3FL), 8);
        } else if (val < 0x200000L) {
            this.writeRawUInt(0xF0 | (int)(val >> 18), 8);
            this.writeRawUInt(0x80 | (int)(val >> 12 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 6 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val & 0x3FL), 8);
        } else if (val < 0x4000000L) {
            this.writeRawUInt(0xF8 | (int)(val >> 24), 8);
            this.writeRawUInt(0x80 | (int)(val >> 18 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 12 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 6 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val & 0x3FL), 8);
        } else if (val < Integer.MIN_VALUE) {
            this.writeRawUInt(0xFC | (int)(val >> 30), 8);
            this.writeRawUInt(0x80 | (int)(val >> 24 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 18 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 12 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 6 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val & 0x3FL), 8);
        } else {
            this.writeRawUInt(254, 8);
            this.writeRawUInt(0x80 | (int)(val >> 30 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 24 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 18 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 12 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val >> 6 & 0x3FL), 8);
            this.writeRawUInt(0x80 | (int)(val & 0x3FL), 8);
        }
    }

    public void zeroPadToByteBoundary() throws IOException {
        if ((this.outBits & 7) != 0) {
            this.writeZeroes(8 - (this.outBits & 7));
        }
    }

    public void flushByteAligned() throws IOException {
        this.zeroPadToByteBoundary();
        if (this.outBlurbs == 0) {
            return;
        }
        this.os.write(this.buffer, 0, this.outBlurbs);
        this.outBlurbs = 0;
    }

    public int getTotalBits() {
        return this.totalBits;
    }

    public int getTotalBlurbs() {
        return (this.totalBits + 7) / 8;
    }
}

