/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.BitstreamErrors;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.BitstreamException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Crc16;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Header;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class Bitstream
implements BitstreamErrors {
    static byte INITIAL_SYNC = 0;
    static byte STRICT_SYNC = 1;
    private static final int BUFFER_INT_SIZE = 433;
    private final int[] framebuffer = new int[433];
    private int framesize;
    private byte[] frame_bytes = new byte[1732];
    private int wordpointer;
    private int bitindex;
    private int syncword;
    private int header_pos = 0;
    private boolean single_ch_mode;
    private final int[] bitmask = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE, 65535, 131071};
    private final PushbackInputStream source;
    private final Header header = new Header();
    private final byte[] syncbuf = new byte[4];
    private Crc16[] crc = new Crc16[1];
    private byte[] rawid3v2 = null;
    private boolean firstframe = true;

    public Bitstream(InputStream in) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        in = new BufferedInputStream(in);
        this.loadID3v2(in);
        this.firstframe = true;
        this.source = new PushbackInputStream(in, 1732);
        this.closeFrame();
    }

    public int header_pos() {
        return this.header_pos;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadID3v2(InputStream in) {
        int size = -1;
        try {
            in.mark(10);
            this.header_pos = size = this.readID3v2Header(in);
        }
        catch (IOException iOException) {
        }
        finally {
            try {
                in.reset();
            }
            catch (IOException iOException) {}
        }
        try {
            if (size > 0) {
                this.rawid3v2 = new byte[size];
                in.read(this.rawid3v2, 0, this.rawid3v2.length);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private int readID3v2Header(InputStream in) throws IOException {
        byte[] id3header = new byte[4];
        int size = -10;
        in.read(id3header, 0, 3);
        if (id3header[0] == 73 && id3header[1] == 68 && id3header[2] == 51) {
            in.read(id3header, 0, 3);
            byte majorVersion = id3header[0];
            byte revision = id3header[1];
            in.read(id3header, 0, 4);
            size = (id3header[0] << 21) + (id3header[1] << 14) + (id3header[2] << 7) + id3header[3];
        }
        return size + 10;
    }

    public InputStream getRawID3v2() {
        if (this.rawid3v2 == null) {
            return null;
        }
        ByteArrayInputStream bain = new ByteArrayInputStream(this.rawid3v2);
        return bain;
    }

    public void close() throws BitstreamException {
        try {
            this.source.close();
        }
        catch (IOException ex) {
            throw this.newBitstreamException(258, ex);
        }
    }

    public Header readFrame() throws BitstreamException {
        Header result;
        block7: {
            result = null;
            try {
                result = this.readNextFrame();
                if (this.firstframe) {
                    result.parseVBR(this.frame_bytes);
                    this.firstframe = false;
                }
            }
            catch (BitstreamException ex) {
                if (ex.getErrorCode() == 261) {
                    try {
                        this.closeFrame();
                        result = this.readNextFrame();
                    }
                    catch (BitstreamException e) {
                        if (e.getErrorCode() != 260) {
                            throw this.newBitstreamException(e.getErrorCode(), e);
                        }
                        break block7;
                    }
                }
                if (ex.getErrorCode() == 260) break block7;
                throw this.newBitstreamException(ex.getErrorCode(), ex);
            }
        }
        return result;
    }

    private Header readNextFrame() throws BitstreamException {
        if (this.framesize == -1) {
            this.nextFrame();
        }
        return this.header;
    }

    private void nextFrame() throws BitstreamException {
        this.header.read_header(this, this.crc);
    }

    public void unreadFrame() throws BitstreamException {
        if (this.wordpointer == -1 && this.bitindex == -1 && this.framesize > 0) {
            try {
                this.source.unread(this.frame_bytes, 0, this.framesize);
            }
            catch (IOException ex) {
                throw this.newBitstreamException(258);
            }
        }
    }

    public void closeFrame() {
        this.framesize = -1;
        this.wordpointer = -1;
        this.bitindex = -1;
    }

    public boolean isSyncCurrentPosition(int syncmode) throws BitstreamException {
        int read = this.readBytes(this.syncbuf, 0, 4);
        int headerstring = this.syncbuf[0] << 24 & 0xFF000000 | this.syncbuf[1] << 16 & 0xFF0000 | this.syncbuf[2] << 8 & 0xFF00 | this.syncbuf[3] << 0 & 0xFF;
        try {
            this.source.unread(this.syncbuf, 0, read);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        boolean sync = false;
        switch (read) {
            case 0: {
                sync = true;
                break;
            }
            case 4: {
                sync = this.isSyncMark(headerstring, syncmode, this.syncword);
            }
        }
        return sync;
    }

    public int readBits(int n) {
        return this.get_bits(n);
    }

    public int readCheckedBits(int n) {
        return this.get_bits(n);
    }

    protected BitstreamException newBitstreamException(int errorcode) {
        return new BitstreamException(errorcode, null);
    }

    protected BitstreamException newBitstreamException(int errorcode, Throwable throwable) {
        return new BitstreamException(errorcode, throwable);
    }

    int syncHeader(byte syncmode) throws BitstreamException {
        boolean sync;
        int bytesRead = this.readBytes(this.syncbuf, 0, 3);
        if (bytesRead != 3) {
            throw this.newBitstreamException(260, null);
        }
        int headerstring = this.syncbuf[0] << 16 & 0xFF0000 | this.syncbuf[1] << 8 & 0xFF00 | this.syncbuf[2] << 0 & 0xFF;
        do {
            headerstring <<= 8;
            if (this.readBytes(this.syncbuf, 3, 1) == 1) continue;
            throw this.newBitstreamException(260, null);
        } while (!(sync = this.isSyncMark(headerstring |= this.syncbuf[3] & 0xFF, syncmode, this.syncword)));
        return headerstring;
    }

    public boolean isSyncMark(int headerstring, int syncmode, int word) {
        boolean sync = false;
        if (syncmode == INITIAL_SYNC) {
            sync = (headerstring & 0xFFE00000) == -2097152;
        } else {
            boolean bl = (headerstring & 0xFFF80C00) == word && (headerstring & 0xC0) == 192 == this.single_ch_mode ? true : (sync = false);
        }
        if (sync) {
            boolean bl = sync = (headerstring >>> 10 & 3) != 3;
        }
        if (sync) {
            boolean bl = sync = (headerstring >>> 17 & 3) != 0;
        }
        if (sync) {
            sync = (headerstring >>> 19 & 3) != 1;
        }
        return sync;
    }

    int read_frame_data(int bytesize) throws BitstreamException {
        int numread = 0;
        numread = this.readFully(this.frame_bytes, 0, bytesize);
        this.framesize = bytesize;
        this.wordpointer = -1;
        this.bitindex = -1;
        return numread;
    }

    void parse_frame() throws BitstreamException {
        int b = 0;
        byte[] byteread = this.frame_bytes;
        int bytesize = this.framesize;
        for (int k = 0; k < bytesize; k += 4) {
            boolean convert = false;
            byte b0 = 0;
            byte b1 = 0;
            byte b2 = 0;
            byte b3 = 0;
            b0 = byteread[k];
            if (k + 1 < bytesize) {
                b1 = byteread[k + 1];
            }
            if (k + 2 < bytesize) {
                b2 = byteread[k + 2];
            }
            if (k + 3 < bytesize) {
                b3 = byteread[k + 3];
            }
            this.framebuffer[b++] = b0 << 24 & 0xFF000000 | b1 << 16 & 0xFF0000 | b2 << 8 & 0xFF00 | b3 & 0xFF;
        }
        this.wordpointer = 0;
        this.bitindex = 0;
    }

    public int get_bits(int number_of_bits) {
        int returnvalue = 0;
        int sum = this.bitindex + number_of_bits;
        if (this.wordpointer < 0) {
            this.wordpointer = 0;
        }
        if (sum <= 32) {
            returnvalue = this.framebuffer[this.wordpointer] >>> 32 - sum & this.bitmask[number_of_bits];
            if ((this.bitindex += number_of_bits) == 32) {
                this.bitindex = 0;
                ++this.wordpointer;
            }
            return returnvalue;
        }
        int Right = this.framebuffer[this.wordpointer] & 0xFFFF;
        ++this.wordpointer;
        int Left = this.framebuffer[this.wordpointer] & 0xFFFF0000;
        returnvalue = Right << 16 & 0xFFFF0000 | Left >>> 16 & 0xFFFF;
        returnvalue >>>= 48 - sum;
        this.bitindex = sum - 32;
        return returnvalue &= this.bitmask[number_of_bits];
    }

    void set_syncword(int syncword0) {
        this.syncword = syncword0 & 0xFFFFFF3F;
        this.single_ch_mode = (syncword0 & 0xC0) == 192;
    }

    private int readFully(byte[] b, int offs, int len) throws BitstreamException {
        int nRead = 0;
        try {
            while (len > 0) {
                int bytesread = this.source.read(b, offs, len);
                if (bytesread == -1) {
                    while (len-- > 0) {
                        b[offs++] = 0;
                    }
                    break;
                }
                nRead += bytesread;
                offs += bytesread;
                len -= bytesread;
            }
        }
        catch (IOException ex) {
            throw this.newBitstreamException(258, ex);
        }
        return nRead;
    }

    private int readBytes(byte[] b, int offs, int len) throws BitstreamException {
        int totalBytesRead = 0;
        try {
            int bytesread;
            while (len > 0 && (bytesread = this.source.read(b, offs, len)) != -1) {
                totalBytesRead += bytesread;
                offs += bytesread;
                len -= bytesread;
            }
        }
        catch (IOException ex) {
            throw this.newBitstreamException(258, ex);
        }
        return totalBytesRead;
    }
}

