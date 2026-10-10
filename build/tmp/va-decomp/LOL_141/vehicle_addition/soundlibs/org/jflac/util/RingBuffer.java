/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

public class RingBuffer {
    protected static final int DEFAULT_BUFFER_SIZE = 2048;
    protected volatile int bufferSize = 0;
    protected byte[] buffer = null;
    protected volatile int putHere = 0;
    protected volatile int getHere = 0;
    protected volatile boolean eof = false;
    protected Object signal = new Object();

    public RingBuffer(int size) {
        this.bufferSize = size;
        this.buffer = new byte[size];
    }

    public RingBuffer() {
        this(2048);
    }

    public int size() {
        return this.buffer.length;
    }

    public void resize(int newSize) {
        if (this.bufferSize >= newSize) {
            return;
        }
        byte[] newBuffer = new byte[newSize];
        System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
        this.buffer = newBuffer;
        this.bufferSize = newSize;
    }

    public int putAvailable() {
        if (this.putHere == this.getHere) {
            return this.bufferSize - 1;
        }
        if (this.putHere < this.getHere) {
            return this.getHere - this.putHere - 1;
        }
        return this.bufferSize - (this.putHere - this.getHere) - 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void empty() {
        Object object = this.signal;
        synchronized (object) {
            this.putHere = 0;
            this.getHere = 0;
            this.signal.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(byte[] data, int offset, int len) {
        if (len == 0) {
            return;
        }
        Object object = this.signal;
        synchronized (object) {
            while (this.putAvailable() < len) {
                try {
                    this.signal.wait(1000L);
                }
                catch (Exception e) {
                    System.out.println("Put.Signal.wait:" + e);
                }
            }
            if (this.putHere >= this.getHere) {
                int l = Math.min(len, this.bufferSize - this.putHere);
                System.arraycopy(data, offset, this.buffer, this.putHere, l);
                this.putHere += l;
                if (this.putHere >= this.bufferSize) {
                    this.putHere = 0;
                }
                if (len > l) {
                    this.put(data, offset + l, len - l);
                }
            } else {
                int l = Math.min(len, this.getHere - this.putHere - 1);
                System.arraycopy(data, offset, this.buffer, this.putHere, l);
                this.putHere += l;
                if (this.putHere >= this.bufferSize) {
                    this.putHere = 0;
                }
            }
            this.signal.notify();
        }
    }

    public int getAvailable() {
        if (this.putHere == this.getHere) {
            return 0;
        }
        if (this.putHere < this.getHere) {
            return this.bufferSize - (this.getHere - this.putHere);
        }
        return this.putHere - this.getHere;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int get(byte[] data, int offset, int len) {
        if (len == 0) {
            return 0;
        }
        int dataLen = 0;
        Object object = this.signal;
        synchronized (object) {
            while (this.getAvailable() <= 0) {
                if (this.eof) {
                    return -1;
                }
                try {
                    this.signal.wait(1000L);
                }
                catch (Exception e) {
                    System.out.println("Get.Signal.wait:" + e);
                }
            }
            len = Math.min(len, this.getAvailable());
            if (this.getHere < this.putHere) {
                int l = Math.min(len, this.putHere - this.getHere);
                System.arraycopy(this.buffer, this.getHere, data, offset, l);
                this.getHere += l;
                if (this.getHere >= this.bufferSize) {
                    this.getHere = 0;
                }
                dataLen = l;
            } else {
                int l = Math.min(len, this.bufferSize - this.getHere);
                System.arraycopy(this.buffer, this.getHere, data, offset, l);
                this.getHere += l;
                if (this.getHere >= this.bufferSize) {
                    this.getHere = 0;
                }
                dataLen = l;
                if (len > l) {
                    dataLen += this.get(data, offset + l, len - l);
                }
            }
            this.signal.notify();
        }
        return dataLen;
    }

    public boolean isEOF() {
        return this.eof;
    }

    public void setEOF(boolean eof) {
        this.eof = eof;
    }

    public static void main(String[] args) {
        RingBuffer r = new RingBuffer(9);
        byte[] b = "ABCDEFG".getBytes();
        byte[] g = new byte[2];
        System.out.println("Start");
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
        r.put(b, 0, 3);
        r.get(g, 0, 2);
        System.out.println(new String(g));
    }
}

