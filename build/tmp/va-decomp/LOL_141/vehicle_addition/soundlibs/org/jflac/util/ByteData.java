/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

public class ByteData {
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private byte[] data;
    private int len;

    public ByteData(int maxSpace) {
        if (maxSpace <= 0) {
            maxSpace = 256;
        }
        this.data = new byte[maxSpace];
        this.len = 0;
    }

    public void append(byte b) {
        this.data[this.len++] = b;
    }

    public byte[] getData() {
        return this.data;
    }

    public byte getData(int idx) {
        return this.data[idx];
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        if (len > this.data.length) {
            len = this.data.length;
        }
        this.len = len;
    }
}

