/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.SampleBuffer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Decoder;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.AudioTrack;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Frame;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.AsynchronousAudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;

class MP4AudioInputStream
extends AsynchronousAudioInputStream {
    private final AudioTrack track;
    private final Decoder decoder;
    private final SampleBuffer sampleBuffer;
    static final String ERROR_MESSAGE_AAC_TRACK_NOT_FOUND = "movie does not contain any AAC track";

    MP4AudioInputStream(AudioTrack track, Decoder decoder, SampleBuffer sampleBuffer, InputStream in, AudioFormat format, long length) throws IOException {
        super(in, format, length);
        this.track = track;
        this.decoder = decoder;
        this.sampleBuffer = sampleBuffer;
    }

    @Override
    public void execute() {
        this.decodeFrame();
        if (this.buffer.isOpen()) {
            this.buffer.write(this.sampleBuffer.getData());
        }
    }

    private void decodeFrame() {
        if (!this.track.hasMoreFrames()) {
            this.buffer.close();
            return;
        }
        try {
            Frame frame = this.track.readNextFrame();
            if (frame == null) {
                this.buffer.close();
                return;
            }
            this.decoder.decodeFrame(frame.getData(), this.sampleBuffer);
        }
        catch (IOException e) {
            this.buffer.close();
        }
    }
}

