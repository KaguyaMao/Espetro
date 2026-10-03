/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error.HCR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public class BitsBuffer {
    int bufa;
    int bufb;
    int len = 0;

    public int getLength() {
        return this.len;
    }

    public int showBits(int bits) {
        if (bits == 0) {
            return 0;
        }
        if (this.len <= 32) {
            if (this.len >= bits) {
                return this.bufa >> this.len - bits & -1 >> 32 - bits;
            }
            return this.bufa << bits - this.len & -1 >> 32 - bits;
        }
        if (this.len - bits < 32) {
            return (this.bufb & -1 >> 64 - this.len) << bits - this.len + 32 | this.bufa >> this.len - bits;
        }
        return this.bufb >> this.len - bits - 32 & -1 >> 32 - bits;
    }

    public boolean flushBits(int bits) {
        boolean b;
        this.len -= bits;
        if (this.len < 0) {
            this.len = 0;
            b = false;
        } else {
            b = true;
        }
        return b;
    }

    public int getBits(int n) {
        int i = this.showBits(n);
        if (!this.flushBits(n)) {
            i = -1;
        }
        return i;
    }

    public int getBit() {
        int i = this.showBits(1);
        if (!this.flushBits(1)) {
            i = -1;
        }
        return i;
    }

    public void rewindReverse() {
        if (this.len == 0) {
            return;
        }
        int[] i = HCR.rewindReverse64(this.bufb, this.bufa, this.len);
        this.bufb = i[0];
        this.bufa = i[1];
    }

    public void concatBits(BitsBuffer a) {
        int bh;
        int bl;
        if (a.len == 0) {
            return;
        }
        int al = a.bufa;
        int ah = a.bufb;
        if (this.len > 32) {
            bl = this.bufa;
            bh = this.bufb & (1 << this.len - 32) - 1;
            ah = al << this.len - 32;
            al = 0;
        } else {
            bl = this.bufa & (1 << this.len) - 1;
            bh = 0;
            ah = ah << this.len | al >> 32 - this.len;
            al <<= this.len;
        }
        this.bufa = bl | al;
        this.bufb = bh | ah;
        this.len += a.len;
    }

    public void readSegment(int segwidth, BitStream in) {
        this.len = segwidth;
        if (segwidth > 32) {
            this.bufb = in.readBits(segwidth - 32);
            this.bufa = in.readBits(32);
        } else {
            this.bufa = in.readBits(segwidth);
            this.bufb = 0;
        }
    }
}

