/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

import java.io.DataOutput;
import java.io.IOException;

public class LittleEndianDataOutput
implements DataOutput {
    private DataOutput out;

    public LittleEndianDataOutput(DataOutput out) {
        this.out = out;
    }

    @Override
    public void writeDouble(double arg0) throws IOException {
        this.out.writeDouble(arg0);
    }

    @Override
    public void writeFloat(float arg0) throws IOException {
        this.out.writeFloat(arg0);
    }

    @Override
    public void write(int arg0) throws IOException {
        this.out.write(arg0);
    }

    @Override
    public void writeByte(int arg0) throws IOException {
        this.out.writeByte(arg0);
    }

    @Override
    public void writeChar(int arg0) throws IOException {
        this.out.writeChar(arg0);
    }

    @Override
    public void writeInt(int arg0) throws IOException {
        this.out.writeByte(arg0 & 0xFF);
        this.out.writeByte(arg0 >> 8 & 0xFF);
        this.out.writeByte(arg0 >> 16 & 0xFF);
        this.out.writeByte(arg0 >> 24 & 0xFF);
    }

    @Override
    public void writeShort(int arg0) throws IOException {
        this.out.writeByte(arg0 & 0xFF);
        this.out.writeByte(arg0 >> 8 & 0xFF);
    }

    @Override
    public void writeLong(long arg0) throws IOException {
        this.out.writeByte((int)arg0 & 0xFF);
        this.out.writeByte((int)(arg0 >> 8) & 0xFF);
        this.out.writeByte((int)(arg0 >> 16) & 0xFF);
        this.out.writeByte((int)(arg0 >> 24) & 0xFF);
        this.out.writeByte((int)(arg0 >> 32) & 0xFF);
        this.out.writeByte((int)(arg0 >> 40) & 0xFF);
        this.out.writeByte((int)(arg0 >> 48) & 0xFF);
        this.out.writeByte((int)(arg0 >> 56) & 0xFF);
    }

    @Override
    public void writeBoolean(boolean arg0) throws IOException {
        this.out.writeBoolean(arg0);
    }

    @Override
    public void write(byte[] arg0) throws IOException {
        this.out.write(arg0);
    }

    @Override
    public void write(byte[] arg0, int arg1, int arg2) throws IOException {
        this.out.write(arg0, arg1, arg2);
    }

    @Override
    public void writeBytes(String arg0) throws IOException {
        this.out.writeBytes(arg0);
    }

    @Override
    public void writeChars(String arg0) throws IOException {
        this.out.writeChars(arg0);
    }

    @Override
    public void writeUTF(String arg0) throws IOException {
        this.out.writeUTF(arg0);
    }
}

