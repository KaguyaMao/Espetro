/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import LOL_141.vehicle_addition.soundlibs.org.jflac.FLACDecoder;
import LOL_141.vehicle_addition.soundlibs.org.jflac.PCMProcessor;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Frame;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi.RingedAudioInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;

public class Flac2PcmAudioInputStream
extends RingedAudioInputStream
implements PCMProcessor {
    private FLACDecoder decoder;
    private ByteData pcmData;
    private StreamInfo streamInfo;
    private Metadata[] metaData;

    public Flac2PcmAudioInputStream(InputStream in, AudioFormat format, long length) {
        this(in, format, length, 2048);
    }

    public Flac2PcmAudioInputStream(InputStream in, AudioFormat format, long length, int size) {
        super(in, format, length, size);
    }

    @Override
    protected void fill() throws IOException {
        if (this.decoder == null) {
            this.initDecoder();
        }
        if (this.decoder.isEOF()) {
            this.buffer.setEOF(true);
        } else {
            Frame frame = this.decoder.readNextFrame();
            if (frame != null) {
                this.pcmData = this.decoder.decodeFrame(frame, this.pcmData);
                this.processPCM(this.pcmData);
            }
        }
    }

    protected void initDecoder() throws IOException {
        this.decoder = new FLACDecoder(this.in);
        this.decoder.addPCMProcessor(this);
        this.metaData = this.decoder.readMetadata();
    }

    @Override
    public void processStreamInfo(StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    @Override
    public void processPCM(ByteData pcm) {
        this.buffer.resize(pcm.getLen() * 2);
        this.buffer.put(pcm.getData(), 0, pcm.getLen());
    }

    public StreamInfo getStreamInfo() {
        return this.streamInfo;
    }

    public Metadata[] getMetaData() {
        return this.metaData;
    }
}

