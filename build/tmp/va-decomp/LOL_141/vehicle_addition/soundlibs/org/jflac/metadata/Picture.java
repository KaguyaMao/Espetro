/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Picture
extends Metadata {
    private int pictureType;
    private int mimeTypeByteCount;
    private String mimeString;
    private int descStringByteCount;
    private String descString;
    private int picPixelWidth;
    private int picPixelHeight;
    private int picBitsPerPixel;
    private int picColorCount;
    private int picByteCount;
    protected byte[] image;

    public Picture(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        int usedBits = 0;
        this.pictureType = is.readRawUInt(32);
        usedBits += 32;
        this.mimeTypeByteCount = is.readRawUInt(32);
        usedBits += 32;
        byte[] data = new byte[this.mimeTypeByteCount];
        is.readByteBlockAlignedNoCRC(data, this.mimeTypeByteCount);
        usedBits += this.mimeTypeByteCount * 8;
        this.mimeString = new String(data);
        this.descStringByteCount = is.readRawUInt(32);
        usedBits += 32;
        if (this.descStringByteCount != 0) {
            data = new byte[this.descStringByteCount];
            is.readByteBlockAlignedNoCRC(data, this.descStringByteCount);
            try {
                this.descString = new String(data, "UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
            usedBits += this.descStringByteCount * 8;
        } else {
            this.descString = "";
        }
        this.picPixelWidth = is.readRawUInt(32);
        usedBits += 32;
        this.picPixelHeight = is.readRawUInt(32);
        usedBits += 32;
        this.picBitsPerPixel = is.readRawUInt(32);
        usedBits += 32;
        this.picColorCount = is.readRawUInt(32);
        usedBits += 32;
        this.picByteCount = is.readRawUInt(32);
        usedBits += 32;
        this.image = new byte[this.picByteCount];
        is.readByteBlockAlignedNoCRC(this.image, this.picByteCount);
        is.readByteBlockAlignedNoCRC(null, length -= (usedBits += this.picByteCount * 8) / 8);
    }

    public String toString() {
        return "Picture:  Type=" + this.pictureType + " MIME type=" + this.mimeString + " Description=\"" + this.descString + "\" Pixels (WxH)=" + this.picPixelWidth + "x" + this.picPixelHeight + " Color Depth=" + this.picBitsPerPixel + " Color Count=" + this.picColorCount + " Picture Size (bytes)=" + this.picByteCount;
    }
}

