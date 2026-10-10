/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.ts;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.SampleBuffer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Decoder;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.adts.ADTSDemultiplexer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.ts.TSToADTSInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TStoPCMInputStream
extends InputStream {
    private final ADTSDemultiplexer demux;
    private final Decoder decoder;
    private final SampleBuffer buf;
    private byte[] currentPcmData;
    private int pcmPosition = 0;
    private int pcmLimit = 0;

    public TStoPCMInputStream(InputStream in) throws IOException {
        this.demux = new ADTSDemultiplexer(new TSToADTSInputStream(in));
        this.decoder = Decoder.create(this.demux.getDecoderInfo());
        this.buf = new SampleBuffer();
        this.decodeNextFrame();
    }

    private boolean decodeNextFrame() throws IOException {
        try {
            byte[] aacFrame = this.demux.readNextFrame();
            this.decoder.decodeFrame(aacFrame, this.buf);
            this.currentPcmData = this.buf.getData();
            this.pcmLimit = this.currentPcmData.length;
            this.pcmPosition = 0;
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public int read() throws IOException {
        if (this.pcmPosition >= this.pcmLimit && !this.decodeNextFrame()) {
            return -1;
        }
        return this.currentPcmData[this.pcmPosition++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.pcmPosition >= this.pcmLimit && !this.decodeNextFrame()) {
            return -1;
        }
        int available = this.pcmLimit - this.pcmPosition;
        int toCopy = Math.min(available, len);
        System.arraycopy(this.currentPcmData, this.pcmPosition, b, off, toCopy);
        this.pcmPosition += toCopy;
        return toCopy;
    }

    public SampleBuffer getSampleBuffer() {
        return this.buf;
    }
}

