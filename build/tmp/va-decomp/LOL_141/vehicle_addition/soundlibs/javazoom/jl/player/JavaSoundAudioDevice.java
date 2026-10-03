/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Decoder;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDeviceBase;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class JavaSoundAudioDevice
extends AudioDeviceBase {
    private SourceDataLine source = null;
    private AudioFormat fmt = null;
    private byte[] byteBuf = new byte[4096];

    protected void setAudioFormat(AudioFormat fmt0) {
        this.fmt = fmt0;
    }

    protected AudioFormat getAudioFormat() {
        if (this.fmt == null) {
            Decoder decoder = this.getDecoder();
            this.fmt = new AudioFormat(decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true, false);
        }
        return this.fmt;
    }

    protected DataLine.Info getSourceLineInfo() {
        AudioFormat fmt = this.getAudioFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
        return info;
    }

    public void open(AudioFormat fmt) throws JavaLayerException {
        if (!this.isOpen()) {
            this.setAudioFormat(fmt);
            this.openImpl();
            this.setOpen(true);
        }
    }

    protected void openImpl() throws JavaLayerException {
    }

    protected void createSource() throws JavaLayerException {
        Throwable t = null;
        try {
            Line line = this.createLine();
            if (line instanceof SourceDataLine) {
                this.source = (SourceDataLine)line;
                this.source.open(this.fmt);
                this.source.start();
            }
        }
        catch (RuntimeException ex) {
            t = ex;
        }
        catch (LinkageError ex) {
            t = ex;
        }
        catch (LineUnavailableException ex) {
            t = ex;
        }
        if (this.source == null) {
            throw new JavaLayerException("cannot obtain source audio line", t);
        }
    }

    protected Line createLine() throws LineUnavailableException {
        Line line = AudioSystem.getLine(this.getSourceLineInfo());
        return line;
    }

    public int millisecondsToBytes(AudioFormat fmt, int time) {
        return (int)((double)((float)time * (fmt.getSampleRate() * (float)fmt.getChannels() * (float)fmt.getSampleSizeInBits())) / 8000.0);
    }

    protected void closeImpl() {
        if (this.source != null) {
            this.source.close();
        }
    }

    protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
        if (this.source == null) {
            this.createSource();
        }
        byte[] b = this.toByteArray(samples, offs, len);
        this.source.write(b, 0, len * 2);
    }

    protected byte[] getByteArray(int length) {
        if (this.byteBuf.length < length) {
            this.byteBuf = new byte[length + 1024];
        }
        return this.byteBuf;
    }

    protected byte[] toByteArray(short[] samples, int offs, int len) {
        byte[] b = this.getByteArray(len * 2);
        int idx = 0;
        while (len-- > 0) {
            short s = samples[offs++];
            b[idx++] = (byte)s;
            b[idx++] = (byte)(s >>> 8);
        }
        return b;
    }

    protected void flushImpl() {
        if (this.source != null) {
            this.source.drain();
        }
    }

    public int getPosition() {
        int pos = 0;
        if (this.source != null) {
            pos = (int)(this.source.getMicrosecondPosition() / 1000L);
        }
        return pos;
    }

    public void test() throws JavaLayerException {
        try {
            this.open(new AudioFormat(22050.0f, 16, 1, true, false));
            short[] data = new short[2205];
            this.write(data, 0, data.length);
            this.flush();
            this.close();
        }
        catch (RuntimeException ex) {
            throw new JavaLayerException("Device test failed: " + ex);
        }
    }
}

