/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.SampleBuffer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Decoder;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.adts.ADTSDemultiplexer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.AsynchronousAudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;

class AACAudioInputStream
extends AsynchronousAudioInputStream {
    private final ADTSDemultiplexer adts;
    private final Decoder decoder;
    private final SampleBuffer sampleBuffer;

    AACAudioInputStream(ADTSDemultiplexer adts, Decoder decoder, SampleBuffer sampleBuffer, InputStream in, AudioFormat format, long length) throws IOException {
        super(in, format, length);
        this.adts = adts;
        this.decoder = decoder;
        this.sampleBuffer = sampleBuffer;
    }

    @Override
    public void execute() {
        try {
            this.decoder.decodeFrame(this.adts.readNextFrame(), this.sampleBuffer);
            this.buffer.write(this.sampleBuffer.getData());
        }
        catch (IOException e) {
            this.buffer.close();
        }
    }
}

