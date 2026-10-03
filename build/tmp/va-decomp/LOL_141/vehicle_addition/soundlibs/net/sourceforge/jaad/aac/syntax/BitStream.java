/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ByteArrayBitStream;

public interface BitStream {
    public int getPosition();

    public int getBitsLeft();

    public int readBits(int var1);

    default public byte readByte(int n) {
        if (n > 8) {
            throw new IllegalArgumentException();
        }
        return (byte)this.readBits(n);
    }

    default public short readShort(int n) {
        if (n > 16) {
            throw new IllegalArgumentException();
        }
        return (short)this.readBits(n);
    }

    public int readBit();

    public boolean readBool();

    public int peekBits(int var1);

    public void skipBits(int var1);

    public void skipBit();

    public void byteAlign();

    public BitStream readSubStream(int var1);

    public static BitStream open(byte[] data) {
        return new ByteArrayBitStream(data);
    }
}

