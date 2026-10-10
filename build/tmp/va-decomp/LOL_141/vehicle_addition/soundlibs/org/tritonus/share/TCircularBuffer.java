/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;

public class TCircularBuffer {
    private boolean m_bBlockingRead;
    private boolean m_bBlockingWrite;
    private byte[] m_abData;
    private int m_nSize;
    private long m_lReadPos;
    private long m_lWritePos;
    private Trigger m_trigger;
    private boolean m_bOpen;

    public TCircularBuffer(int nSize, boolean bBlockingRead, boolean bBlockingWrite, Trigger trigger) {
        this.m_bBlockingRead = bBlockingRead;
        this.m_bBlockingWrite = bBlockingWrite;
        this.m_nSize = nSize;
        this.m_abData = new byte[this.m_nSize];
        this.m_lReadPos = 0L;
        this.m_lWritePos = 0L;
        this.m_trigger = trigger;
        this.m_bOpen = true;
    }

    public void close() {
        this.m_bOpen = false;
    }

    private boolean isOpen() {
        return this.m_bOpen;
    }

    public int availableRead() {
        return (int)(this.m_lWritePos - this.m_lReadPos);
    }

    public int availableWrite() {
        return this.m_nSize - this.availableRead();
    }

    private int getReadPos() {
        return (int)(this.m_lReadPos % (long)this.m_nSize);
    }

    private int getWritePos() {
        return (int)(this.m_lWritePos % (long)this.m_nSize);
    }

    public int read(byte[] abData) {
        return this.read(abData, 0, abData.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int read(byte[] abData, int nOffset, int nLength) {
        if (TDebug.TraceCircularBuffer) {
            TDebug.out(">TCircularBuffer.read(): called.");
            this.dumpInternalState();
        }
        if (!this.isOpen()) {
            if (this.availableRead() > 0) {
                nLength = Math.min(nLength, this.availableRead());
                if (TDebug.TraceCircularBuffer) {
                    TDebug.out("reading rest in closed buffer, length: " + nLength);
                }
            } else {
                if (TDebug.TraceCircularBuffer) {
                    TDebug.out("< not open. returning -1.");
                }
                return -1;
            }
        }
        TCircularBuffer tCircularBuffer = this;
        synchronized (tCircularBuffer) {
            if (this.m_trigger != null && this.availableRead() < nLength) {
                if (TDebug.TraceCircularBuffer) {
                    TDebug.out("executing trigger.");
                }
                this.m_trigger.execute();
            }
            if (!this.m_bBlockingRead) {
                nLength = Math.min(this.availableRead(), nLength);
            }
            int nRemainingBytes = nLength;
            while (nRemainingBytes > 0) {
                while (this.availableRead() == 0) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        if (!TDebug.TraceAllExceptions) continue;
                        TDebug.out(e);
                    }
                }
                int nAvailable = Math.min(this.availableRead(), nRemainingBytes);
                while (nAvailable > 0) {
                    int nToRead = Math.min(nAvailable, this.m_nSize - this.getReadPos());
                    System.arraycopy(this.m_abData, this.getReadPos(), abData, nOffset, nToRead);
                    this.m_lReadPos += (long)nToRead;
                    nOffset += nToRead;
                    nAvailable -= nToRead;
                    nRemainingBytes -= nToRead;
                }
                this.notifyAll();
            }
            if (TDebug.TraceCircularBuffer) {
                TDebug.out("After read:");
                this.dumpInternalState();
                TDebug.out("< completed. Read " + nLength + " bytes");
            }
            return nLength;
        }
    }

    public int write(byte[] abData) {
        return this.write(abData, 0, abData.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int write(byte[] abData, int nOffset, int nLength) {
        if (TDebug.TraceCircularBuffer) {
            TDebug.out(">TCircularBuffer.write(): called; nLength: " + nLength);
            this.dumpInternalState();
        }
        TCircularBuffer tCircularBuffer = this;
        synchronized (tCircularBuffer) {
            if (TDebug.TraceCircularBuffer) {
                TDebug.out("entered synchronized block.");
            }
            if (!this.m_bBlockingWrite) {
                nLength = Math.min(this.availableWrite(), nLength);
            }
            int nRemainingBytes = nLength;
            while (nRemainingBytes > 0) {
                while (this.availableWrite() == 0) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        if (!TDebug.TraceAllExceptions) continue;
                        TDebug.out(e);
                    }
                }
                int nAvailable = Math.min(this.availableWrite(), nRemainingBytes);
                while (nAvailable > 0) {
                    int nToWrite = Math.min(nAvailable, this.m_nSize - this.getWritePos());
                    System.arraycopy(abData, nOffset, this.m_abData, this.getWritePos(), nToWrite);
                    this.m_lWritePos += (long)nToWrite;
                    nOffset += nToWrite;
                    nAvailable -= nToWrite;
                    nRemainingBytes -= nToWrite;
                }
                this.notifyAll();
            }
            if (TDebug.TraceCircularBuffer) {
                TDebug.out("After write:");
                this.dumpInternalState();
                TDebug.out("< completed. Wrote " + nLength + " bytes");
            }
            return nLength;
        }
    }

    private void dumpInternalState() {
        TDebug.out("m_lReadPos  = " + this.m_lReadPos + " ^= " + this.getReadPos());
        TDebug.out("m_lWritePos = " + this.m_lWritePos + " ^= " + this.getWritePos());
        TDebug.out("availableRead()  = " + this.availableRead());
        TDebug.out("availableWrite() = " + this.availableWrite());
    }

    public static interface Trigger {
        public void execute();
    }
}

