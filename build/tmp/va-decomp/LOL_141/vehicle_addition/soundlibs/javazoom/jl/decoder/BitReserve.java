/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder;

final class BitReserve {
    private static final int BUFSIZE = 32768;
    private static final int BUFSIZE_MASK = Short.MAX_VALUE;
    private int offset = 0;
    private int totbit = 0;
    private int buf_byte_idx = 0;
    private final int[] buf = new int[32768];
    private int buf_bit_idx;

    BitReserve() {
    }

    public int hsstell() {
        return this.totbit;
    }

    public int hgetbits(int N) {
        this.totbit += N;
        int val = 0;
        int pos = this.buf_byte_idx;
        if (pos + N < 32768) {
            while (N-- > 0) {
                val <<= 1;
                val |= this.buf[pos++] != 0 ? 1 : 0;
            }
        } else {
            while (N-- > 0) {
                val <<= 1;
                val |= this.buf[pos] != 0 ? 1 : 0;
                pos = pos + 1 & Short.MAX_VALUE;
            }
        }
        this.buf_byte_idx = pos;
        return val;
    }

    public int hget1bit() {
        ++this.totbit;
        int val = this.buf[this.buf_byte_idx];
        this.buf_byte_idx = this.buf_byte_idx + 1 & Short.MAX_VALUE;
        return val;
    }

    public void hputbuf(int val) {
        int ofs = this.offset;
        this.buf[ofs++] = val & 0x80;
        this.buf[ofs++] = val & 0x40;
        this.buf[ofs++] = val & 0x20;
        this.buf[ofs++] = val & 0x10;
        this.buf[ofs++] = val & 8;
        this.buf[ofs++] = val & 4;
        this.buf[ofs++] = val & 2;
        this.buf[ofs++] = val & 1;
        this.offset = ofs == 32768 ? 0 : ofs;
    }

    public void rewindNbits(int N) {
        this.totbit -= N;
        this.buf_byte_idx -= N;
        if (this.buf_byte_idx < 0) {
            this.buf_byte_idx += 32768;
        }
    }

    public void rewindNbytes(int N) {
        int bits = N << 3;
        this.totbit -= bits;
        this.buf_byte_idx -= bits;
        if (this.buf_byte_idx < 0) {
            this.buf_byte_idx += 32768;
        }
    }
}

