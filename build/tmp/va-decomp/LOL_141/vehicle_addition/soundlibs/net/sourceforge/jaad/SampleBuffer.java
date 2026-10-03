/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Receiver;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import javax.sound.sampled.AudioFormat;

public class SampleBuffer
implements Receiver {
    private int sampleRate;
    private int channels;
    private int bitsPerSample;
    private double length;
    private double bitrate;
    private byte[] data = new byte[0];
    private boolean bigEndian;

    public SampleBuffer() {
        this.sampleRate = 0;
        this.channels = 0;
        this.bitsPerSample = 0;
        this.bigEndian = true;
    }

    public SampleBuffer(AudioFormat af) {
        this.sampleRate = (int)af.getSampleRate();
        this.channels = af.getChannels();
        this.bitsPerSample = af.getSampleSizeInBits();
        this.bigEndian = af.isBigEndian();
    }

    public byte[] getData() {
        return this.data;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getChannels() {
        return this.channels;
    }

    public int getBitsPerSample() {
        return this.bitsPerSample;
    }

    public double getLength() {
        return this.length;
    }

    public double getBitrate() {
        return this.bitrate;
    }

    public boolean isBigEndian() {
        return this.bigEndian;
    }

    public void setBigEndian(boolean bigEndian) {
        if (bigEndian != this.bigEndian) {
            for (int i = 0; i < this.data.length; i += 2) {
                byte tmp = this.data[i];
                this.data[i] = this.data[i + 1];
                this.data[i + 1] = tmp;
            }
            this.bigEndian = bigEndian;
        }
    }

    public void setData(byte[] data, int sampleRate, int channels, int bitsPerSample, int bitsRead) {
        this.data = data;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitsPerSample = bitsPerSample;
        if (sampleRate == 0) {
            this.length = 0.0;
            this.bitrate = 0.0;
        } else {
            int bytesPerSample = bitsPerSample / 8;
            int samplesPerChannel = data.length / (bytesPerSample * channels);
            this.length = (double)samplesPerChannel / (double)sampleRate;
            this.bitrate = (double)(samplesPerChannel * bitsPerSample * channels) / this.length;
        }
    }

    @Override
    public void accept(List<float[]> samples, int sampleLength, int sampleRate) {
        this.sampleRate = sampleRate;
        this.channels = samples.size();
        this.bitsPerSample = 16;
        int bytes = samples.size() * 2 * sampleLength;
        if (this.data == null || this.data.length != bytes) {
            this.data = new byte[bytes];
        }
        this.length = (double)sampleLength / (double)sampleRate;
        this.bitrate = (double)sampleLength * (double)this.bitsPerSample * (double)this.channels / (double)bytes;
        ByteBuffer bb = ByteBuffer.wrap(this.data);
        bb.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        for (int is = 0; is < sampleLength; ++is) {
            for (float[] sample : samples) {
                int k = sample.length * is / sampleLength;
                float s = sample[k];
                int pulse = Math.round(s);
                pulse = Math.min(pulse, Short.MAX_VALUE);
                pulse = Math.max(pulse, Short.MIN_VALUE);
                bb.putShort((short)pulse);
            }
        }
    }
}

