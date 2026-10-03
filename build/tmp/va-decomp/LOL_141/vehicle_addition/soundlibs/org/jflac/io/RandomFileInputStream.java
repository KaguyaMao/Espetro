/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomFileInputStream
extends InputStream {
    protected RandomAccessFile randomFile = null;

    public RandomFileInputStream(File file) throws FileNotFoundException {
        this.randomFile = new RandomAccessFile(file, "r");
    }

    public RandomFileInputStream(String fileName) throws FileNotFoundException {
        this.randomFile = new RandomAccessFile(fileName, "r");
    }

    public RandomFileInputStream(RandomAccessFile randomFile) {
        this.randomFile = randomFile;
    }

    @Override
    public int read() throws IOException {
        return this.randomFile.read();
    }

    @Override
    public synchronized void reset() {
        System.out.println("RandomFileInputStream: reset");
    }

    @Override
    public void close() throws IOException {
        this.randomFile.close();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int arg0) {
        System.out.println("RandomFileInputStream: mark");
    }

    @Override
    public long skip(long bytes) throws IOException {
        long pos = this.randomFile.getFilePointer();
        this.randomFile.seek(pos + bytes);
        return this.randomFile.getFilePointer() - pos;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return this.randomFile.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int pos, int bytes) throws IOException {
        return this.randomFile.read(buffer, pos, bytes);
    }

    public void seek(long pos) throws IOException {
        this.randomFile.seek(pos);
    }

    public long getLength() throws IOException {
        return this.randomFile.length();
    }
}

