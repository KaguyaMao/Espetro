/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.io;

import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.CRC16;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {
    private static final int BITS_PER_BLURB = 8;
    private static final int BITS_PER_BLURB_LOG2 = 3;
    private static final byte BLURB_TOP_BIT_ONE = -128;
    private static final int BUFFER_CHUNK_SIZE = 1024;
    private byte[] buffer = new byte[1024];
    private int putByte = 0;
    private int getByte = 0;
    private int getBit = 0;
    private int availBits = 0;
    private int totalBitsRead = 0;
    private short readCRC16 = 0;
    private InputStream inStream;

    public BitInputStream(InputStream is) {
        this.inStream = is;
    }

    private int readFromStream() throws IOException {
        if (this.getByte > 0 && this.putByte > this.getByte) {
            System.arraycopy(this.buffer, this.getByte, this.buffer, 0, this.putByte - this.getByte);
        }
        this.putByte -= this.getByte;
        this.getByte = 0;
        int bytes = this.buffer.length - this.putByte;
        if ((bytes = this.inStream.read(this.buffer, this.putByte, bytes)) <= 0) {
            throw new EOFException();
        }
        this.putByte += bytes;
        this.availBits += bytes << 3;
        return bytes;
    }

    public void reset() {
        this.getByte = 0;
        this.getBit = 0;
        this.putByte = 0;
        this.availBits = 0;
        this.totalBitsRead = 0;
    }

    public void resetReadCRC16(short seed) {
        this.readCRC16 = seed;
    }

    public short getReadCRC16() {
        return this.readCRC16;
    }

    public boolean isConsumedByteAligned() {
        return (this.getBit & 7) == 0;
    }

    public int bitsLeftForByteAlignment() {
        return 8 - (this.getBit & 7);
    }

    public int getInputBytesUnconsumed() {
        return this.availBits >> 3;
    }

    public void skipBitsNoCRC(int bits) throws IOException {
        int bytesNeeded;
        if (bits == 0) {
            return;
        }
        int bitsToAlign = this.getBit & 7;
        if (bitsToAlign != 0) {
            int bitsToTake = Math.min(8 - bitsToAlign, bits);
            this.readRawUInt(bitsToTake);
            bits -= bitsToTake;
        }
        if ((bytesNeeded = bits / 8) > 0) {
            this.readByteBlockAlignedNoCRC(null, bytesNeeded);
            bits %= 8;
        }
        if (bits > 0) {
            this.readRawUInt(bits);
        }
    }

    public int readBit() throws IOException {
        while (true) {
            if (this.availBits > 0) {
                int val = (this.buffer[this.getByte] & 128 >> this.getBit) != 0 ? 1 : 0;
                ++this.getBit;
                if (this.getBit == 8) {
                    this.readCRC16 = CRC16.update(this.buffer[this.getByte], this.readCRC16);
                    ++this.getByte;
                    this.getBit = 0;
                }
                --this.availBits;
                ++this.totalBitsRead;
                return val;
            }
            this.readFromStream();
        }
    }

    public int readBitToInt(int val) throws IOException {
        while (true) {
            if (this.availBits > 0) {
                val <<= 1;
                val |= (this.buffer[this.getByte] & 128 >> this.getBit) != 0 ? 1 : 0;
                ++this.getBit;
                if (this.getBit == 8) {
                    this.readCRC16 = CRC16.update(this.buffer[this.getByte], this.readCRC16);
                    ++this.getByte;
                    this.getBit = 0;
                }
                --this.availBits;
                ++this.totalBitsRead;
                return val;
            }
            this.readFromStream();
        }
    }

    public int peekBitToInt(int val, int bit) throws IOException {
        while (true) {
            if (bit < this.availBits) {
                val <<= 1;
                val = this.getBit + bit >= 8 ? (val |= (this.buffer[this.getByte + 1] & 128 >> (bit = (this.getBit + bit) % 8)) != 0 ? 1 : 0) : (val |= (this.buffer[this.getByte] & 128 >> this.getBit + bit) != 0 ? 1 : 0);
                return val;
            }
            this.readFromStream();
        }
    }

    public long readBitToLong(long val) throws IOException {
        while (true) {
            if (this.availBits > 0) {
                val <<= 1;
                val |= (this.buffer[this.getByte] & 128 >> this.getBit) != 0 ? 1L : 0L;
                ++this.getBit;
                if (this.getBit == 8) {
                    this.readCRC16 = CRC16.update(this.buffer[this.getByte], this.readCRC16);
                    ++this.getByte;
                    this.getBit = 0;
                }
                --this.availBits;
                ++this.totalBitsRead;
                return val;
            }
            this.readFromStream();
        }
    }

    public int readRawUInt(int bits) throws IOException {
        int val = 0;
        for (int i = 0; i < bits; ++i) {
            val = this.readBitToInt(val);
        }
        return val;
    }

    public int peekRawUInt(int bits) throws IOException {
        int val = 0;
        for (int i = 0; i < bits; ++i) {
            val = this.peekBitToInt(val, i);
        }
        return val;
    }

    public int readRawInt(int bits) throws IOException {
        int val;
        if (bits == 0) {
            return 0;
        }
        int uval = 0;
        for (int i = 0; i < bits; ++i) {
            uval = this.readBitToInt(uval);
        }
        int bitsToleft = 32 - bits;
        if (bitsToleft != 0) {
            val = uval <<= bitsToleft;
            val >>= bitsToleft;
        } else {
            val = uval;
        }
        return val;
    }

    public long readRawULong(int bits) throws IOException {
        long val = 0L;
        for (int i = 0; i < bits; ++i) {
            val = this.readBitToLong(val);
        }
        return val;
    }

    public int readRawIntLittleEndian() throws IOException {
        int x32 = this.readRawUInt(8);
        int x8 = this.readRawUInt(8);
        x32 |= x8 << 8;
        x8 = this.readRawUInt(8);
        x32 |= x8 << 16;
        x8 = this.readRawUInt(8);
        return x32 |= x8 << 24;
    }

    public void readByteBlockAlignedNoCRC(byte[] val, int nvals) throws IOException {
        int destlength = nvals;
        while (nvals > 0) {
            int chunk = Math.min(nvals, this.putByte - this.getByte);
            if (chunk == 0) {
                this.readFromStream();
                continue;
            }
            if (val != null) {
                System.arraycopy(this.buffer, this.getByte, val, destlength - nvals, chunk);
            }
            nvals -= chunk;
            this.getByte += chunk;
            this.availBits -= chunk << 3;
            this.totalBitsRead += chunk << 3;
        }
    }

    public int readUnaryUnsigned() throws IOException {
        int bit;
        int val = 0;
        while ((bit = this.readBit()) == 0) {
            ++val;
        }
        return val;
    }

    public void readRiceSignedBlock(int[] vals, int pos, int nvals, int parameter) throws IOException {
        int availableBits;
        int j;
        byte saveBlurb;
        byte blurb;
        int valI = 0;
        int cbits = 0;
        int uval = 0;
        int msbs = 0;
        int lsbsLeft = 0;
        int state = 0;
        if (nvals == 0) {
            return;
        }
        int i = this.getByte;
        long startBits = this.getByte * 8 + this.getBit;
        if (this.getBit > 0) {
            saveBlurb = blurb = this.buffer[i];
            cbits = this.getBit;
            blurb = (byte)(blurb << cbits);
            while (true) {
                if (state == 0) {
                    if (blurb != 0) {
                        j = 0;
                        while ((blurb & 0xFFFFFF80) == 0) {
                            blurb = (byte)(blurb << 1);
                            ++j;
                        }
                        msbs += j;
                        blurb = (byte)(blurb << 1);
                        uval = 0;
                        lsbsLeft = parameter;
                        ++state;
                        if ((cbits += ++j) != 8) continue;
                        cbits = 0;
                        this.readCRC16 = CRC16.update(saveBlurb, this.readCRC16);
                        break;
                    }
                    msbs += 8 - cbits;
                    cbits = 0;
                    this.readCRC16 = CRC16.update(saveBlurb, this.readCRC16);
                    break;
                }
                availableBits = 8 - cbits;
                if (lsbsLeft >= availableBits) {
                    uval <<= availableBits;
                    uval |= (blurb & 0xFF) >> cbits;
                    cbits = 0;
                    this.readCRC16 = CRC16.update(saveBlurb, this.readCRC16);
                    if (lsbsLeft == availableBits) {
                        vals[pos + valI++] = ((uval |= msbs << parameter) & 1) != 0 ? -(uval >> 1) - 1 : uval >> 1;
                        if (valI == nvals) break;
                        msbs = 0;
                        state = 0;
                    }
                    lsbsLeft -= availableBits;
                    break;
                }
                uval <<= lsbsLeft;
                uval |= (blurb & 0xFF) >> 8 - lsbsLeft;
                blurb = (byte)(blurb << lsbsLeft);
                cbits += lsbsLeft;
                vals[pos + valI++] = ((uval |= msbs << parameter) & 1) != 0 ? -(uval >> 1) - 1 : uval >> 1;
                if (valI == nvals) {
                    --i;
                    break;
                }
                msbs = 0;
                state = 0;
            }
            this.getByte = ++i;
            this.getBit = cbits;
        }
        while (valI < nvals) {
            while (i < this.putByte && valI < nvals) {
                saveBlurb = blurb = this.buffer[i];
                cbits = 0;
                while (true) {
                    if (state == 0) {
                        if (blurb != 0) {
                            j = 0;
                            while ((blurb & 0xFFFFFF80) == 0) {
                                blurb = (byte)(blurb << 1);
                                ++j;
                            }
                            msbs += j;
                            blurb = (byte)(blurb << 1);
                            uval = 0;
                            lsbsLeft = parameter;
                            ++state;
                            if ((cbits += ++j) != 8) continue;
                            cbits = 0;
                            this.readCRC16 = CRC16.update(saveBlurb, this.readCRC16);
                            break;
                        }
                        msbs += 8 - cbits;
                        cbits = 0;
                        this.readCRC16 = CRC16.update(saveBlurb, this.readCRC16);
                        break;
                    }
                    availableBits = 8 - cbits;
                    if (lsbsLeft >= availableBits) {
                        uval <<= availableBits;
                        uval |= (blurb & 0xFF) >> cbits;
                        cbits = 0;
                        this.readCRC16 = CRC16.update(saveBlurb, this.readCRC16);
                        if (lsbsLeft == availableBits) {
                            vals[pos + valI++] = ((uval |= msbs << parameter) & 1) != 0 ? -(uval >> 1) - 1 : uval >> 1;
                            if (valI == nvals) break;
                            msbs = 0;
                            state = 0;
                        }
                        lsbsLeft -= availableBits;
                        break;
                    }
                    uval <<= lsbsLeft;
                    uval |= (blurb & 0xFF) >> 8 - lsbsLeft;
                    blurb = (byte)(blurb << lsbsLeft);
                    cbits += lsbsLeft;
                    vals[pos + valI++] = ((uval |= msbs << parameter) & 1) != 0 ? -(uval >> 1) - 1 : uval >> 1;
                    if (valI == nvals) {
                        --i;
                        break;
                    }
                    msbs = 0;
                    state = 0;
                }
                ++i;
            }
            this.getByte = i;
            this.getBit = cbits;
            if (valI >= nvals) continue;
            long endBits = this.getByte * 8 + this.getBit;
            this.totalBitsRead = (int)((long)this.totalBitsRead + (endBits - startBits));
            this.availBits = (int)((long)this.availBits - (endBits - startBits));
            this.readFromStream();
            i = 0;
            startBits = this.getByte * 8 + this.getBit;
        }
        long endBits = this.getByte * 8 + this.getBit;
        this.totalBitsRead = (int)((long)this.totalBitsRead + (endBits - startBits));
        this.availBits = (int)((long)this.availBits - (endBits - startBits));
    }

    public int readUTF8Int(ByteData raw) throws IOException {
        int i;
        int v = 0;
        int x = this.readRawUInt(8);
        if (raw != null) {
            raw.append((byte)x);
        }
        if ((x & 0x80) == 0) {
            v = x;
            i = 0;
        } else if ((x & 0xC0) != 0 && (x & 0x20) == 0) {
            v = x & 0x1F;
            i = 1;
        } else if ((x & 0xE0) != 0 && (x & 0x10) == 0) {
            v = x & 0xF;
            i = 2;
        } else if ((x & 0xF0) != 0 && (x & 8) == 0) {
            v = x & 7;
            i = 3;
        } else if ((x & 0xF8) != 0 && (x & 4) == 0) {
            v = x & 3;
            i = 4;
        } else if ((x & 0xFC) != 0 && (x & 2) == 0) {
            v = x & 1;
            i = 5;
        } else {
            int val = -1;
            return val;
        }
        while (i > 0) {
            x = this.peekRawUInt(8);
            if ((x & 0x80) == 0 || (x & 0x40) != 0) {
                int val = -1;
                return val;
            }
            x = this.readRawUInt(8);
            if (raw != null) {
                raw.append((byte)x);
            }
            v <<= 6;
            v |= x & 0x3F;
            --i;
        }
        int val = v;
        return val;
    }

    public long readUTF8Long(ByteData raw) throws IOException {
        int i;
        long v = 0L;
        int x = this.readRawUInt(8);
        if (raw != null) {
            raw.append((byte)x);
        }
        if ((x & 0x80) == 0) {
            v = x;
            i = 0;
        } else if ((x & 0xC0) != 0 && (x & 0x20) == 0) {
            v = x & 0x1F;
            i = 1;
        } else if ((x & 0xE0) != 0 && (x & 0x10) == 0) {
            v = x & 0xF;
            i = 2;
        } else if ((x & 0xF0) != 0 && (x & 8) == 0) {
            v = x & 7;
            i = 3;
        } else if ((x & 0xF8) != 0 && (x & 4) == 0) {
            v = x & 3;
            i = 4;
        } else if ((x & 0xFC) != 0 && (x & 2) == 0) {
            v = x & 1;
            i = 5;
        } else if ((x & 0xFE) != 0 && (x & 1) == 0) {
            v = 0L;
            i = 6;
        } else {
            long val = -1L;
            return val;
        }
        while (i > 0) {
            x = this.peekRawUInt(8);
            if ((x & 0x80) == 0 || (x & 0x40) != 0) {
                long val = -1L;
                return val;
            }
            x = this.readRawUInt(8);
            if (raw != null) {
                raw.append((byte)x);
            }
            v <<= 6;
            v |= (long)(x & 0x3F);
            --i;
        }
        long val = v;
        return val;
    }

    public int getTotalBytesRead() {
        return (this.totalBitsRead + 7) / 8;
    }
}

