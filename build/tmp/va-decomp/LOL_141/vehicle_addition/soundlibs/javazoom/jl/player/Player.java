/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Bitstream;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.BitstreamException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Decoder;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Header;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.SampleBuffer;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDevice;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.FactoryRegistry;
import java.io.InputStream;

public class Player {
    private int frame = 0;
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;
    private boolean closed = false;
    private boolean complete = false;
    private int lastPosition = 0;

    public Player(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    public Player(InputStream stream, AudioDevice device) throws JavaLayerException {
        this.bitstream = new Bitstream(stream);
        this.decoder = new Decoder();
        if (device != null) {
            this.audio = device;
        } else {
            FactoryRegistry r = FactoryRegistry.systemRegistry();
            this.audio = r.createAudioDevice();
        }
        this.audio.open(this.decoder);
    }

    public void play() throws JavaLayerException {
        this.play(Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean play(int frames) throws JavaLayerException {
        AudioDevice out;
        boolean ret = true;
        while (frames-- > 0 && ret) {
            ret = this.decodeFrame();
        }
        if (!ret && (out = this.audio) != null) {
            out.flush();
            Player player = this;
            synchronized (player) {
                this.complete = !this.closed;
                this.close();
            }
        }
        return ret;
    }

    public synchronized void close() {
        AudioDevice out = this.audio;
        if (out != null) {
            this.closed = true;
            this.audio = null;
            out.close();
            this.lastPosition = out.getPosition();
            try {
                this.bitstream.close();
            }
            catch (BitstreamException bitstreamException) {
                // empty catch block
            }
        }
    }

    public synchronized boolean isComplete() {
        return this.complete;
    }

    public int getPosition() {
        int position = this.lastPosition;
        AudioDevice out = this.audio;
        if (out != null) {
            position = out.getPosition();
        }
        return position;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice out = this.audio;
            if (out == null) {
                return false;
            }
            Header h = this.bitstream.readFrame();
            if (h == null) {
                return false;
            }
            SampleBuffer output = (SampleBuffer)this.decoder.decodeFrame(h, this.bitstream);
            Player player = this;
            synchronized (player) {
                out = this.audio;
                if (out != null) {
                    out.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }
            this.bitstream.closeFrame();
        }
        catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }
}

