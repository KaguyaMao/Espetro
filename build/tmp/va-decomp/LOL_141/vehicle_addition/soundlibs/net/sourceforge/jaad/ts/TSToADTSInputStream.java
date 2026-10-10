/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.ts;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TSToADTSInputStream
extends InputStream {
    private static final int TS_PACKET_SIZE = 188;
    private static final int SYNC_BYTE = 71;
    private static final int CHECK_PACKETS_COUNT = 3;
    private final InputStream in;
    private final byte[] payloadBuffer = new byte[188];
    private int audioPid = -1;
    private int payloadPos = 0;
    private int payloadLimit = 0;
    private boolean isSynced = false;

    public TSToADTSInputStream(InputStream in) {
        this.in = in.markSupported() ? in : new BufferedInputStream(in);
    }

    @Override
    public int read() throws IOException {
        while (this.payloadPos >= this.payloadLimit) {
            if (this.readNextTSPacket()) continue;
            return -1;
        }
        return this.payloadBuffer[this.payloadPos++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.payloadPos >= this.payloadLimit && !this.readNextTSPacket()) {
            return -1;
        }
        int available = this.payloadLimit - this.payloadPos;
        int toCopy = Math.min(available, len);
        System.arraycopy(this.payloadBuffer, this.payloadPos, b, off, toCopy);
        this.payloadPos += toCopy;
        return toCopy;
    }

    private boolean readNextTSPacket() throws IOException {
        int streamId;
        int n;
        if (!this.isSynced) {
            this.skipID3Tags();
            if (!this.resync()) {
                return false;
            }
            this.isSynced = true;
        }
        byte[] packet = new byte[188];
        for (int read = 0; read < 188; read += n) {
            n = this.in.read(packet, read, 188 - read);
            if (n != -1) continue;
            return false;
        }
        if ((packet[0] & 0xFF) != 71) {
            this.isSynced = false;
            return true;
        }
        int pid = (packet[1] & 0x1F) << 8 | packet[2] & 0xFF;
        boolean pusi = (packet[1] & 0x40) != 0;
        int adaptation = (packet[3] & 0x30) >> 4;
        if (adaptation == 0 || adaptation == 2) {
            return true;
        }
        int payloadStart = 4;
        if (adaptation == 3) {
            payloadStart += 1 + (packet[4] & 0xFF);
        }
        if (payloadStart >= 188) {
            return true;
        }
        if (this.audioPid == -1 && pusi && payloadStart + 3 < 188 && (packet[payloadStart] & 0xFF) == 0 && (packet[payloadStart + 1] & 0xFF) == 0 && (packet[payloadStart + 2] & 0xFF) == 1 && 192 <= (streamId = packet[payloadStart + 3] & 0xFF) && streamId <= 223) {
            this.audioPid = pid;
        }
        if (pid != this.audioPid || this.audioPid == -1) {
            return true;
        }
        if (pusi && payloadStart + 8 < 188) {
            int pesHeaderDataLen = packet[payloadStart + 8] & 0xFF;
            payloadStart += 9 + pesHeaderDataLen;
        }
        if (payloadStart >= 188) {
            return true;
        }
        int len = 188 - payloadStart;
        System.arraycopy(packet, payloadStart, this.payloadBuffer, 0, len);
        this.payloadLimit = len;
        this.payloadPos = 0;
        return true;
    }

    private void skipID3Tags() throws IOException {
        block0: while (true) {
            int n;
            this.in.mark(10);
            byte[] header = new byte[10];
            for (int readBytes = 0; readBytes < 10; readBytes += n) {
                n = this.in.read(header, readBytes, 10 - readBytes);
                if (n != -1) continue;
                this.in.reset();
                return;
            }
            if (header[0] != 73 || header[1] != 68 || header[2] != 51 || (header[6] & 0x80) != 0 || (header[7] & 0x80) != 0 || (header[8] & 0x80) != 0 || (header[9] & 0x80) != 0) break;
            int size = (header[6] & 0x7F) << 21 | (header[7] & 0x7F) << 14 | (header[8] & 0x7F) << 7 | header[9] & 0x7F;
            long skipped = 0L;
            while (true) {
                long s;
                if (skipped >= (long)size || (s = this.in.skip((long)size - skipped)) <= 0L) continue block0;
                skipped += s;
            }
            break;
        }
        this.in.reset();
    }

    private boolean resync() throws IOException {
        while (true) {
            this.in.mark(565);
            int b = this.in.read();
            if (b == -1) {
                return false;
            }
            if (b != 71) continue;
            if (this.checkSequence()) {
                return true;
            }
            this.in.read();
        }
    }

    private boolean checkSequence() throws IOException {
        byte[] temp = new byte[188];
        boolean valid = true;
        for (int i = 1; i < 3; ++i) {
            int nextByte;
            int n;
            int toSkip = 187;
            for (int readBytes = 0; readBytes < toSkip; readBytes += n) {
                n = this.in.read(temp, readBytes, toSkip - readBytes);
                if (n != -1) continue;
                valid = false;
                break;
            }
            if (!valid || (nextByte = this.in.read()) == -1) break;
            if (nextByte == 71) continue;
            valid = false;
            break;
        }
        this.in.reset();
        return valid;
    }
}

