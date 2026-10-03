/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Frame;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.LittleEndianDataOutput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class WavWriter {
    private static final int MAX_BLOCK_SIZE = 65535;
    private long totalSamples;
    private int channels;
    private int bps;
    private int sampleRate;
    private byte[] s8buffer = new byte[2097120];
    private int samplesProcessed = 0;
    private int frameCounter = 0;
    private DataOutput os;
    private LittleEndianDataOutput osLE;

    public WavWriter(DataOutput os, StreamInfo streamInfo) {
        this.os = os;
        this.osLE = new LittleEndianDataOutput(os);
        this.totalSamples = streamInfo.getTotalSamples();
        this.channels = streamInfo.getChannels();
        this.bps = streamInfo.getBitsPerSample();
        this.sampleRate = streamInfo.getSampleRate();
    }

    public WavWriter(DataOutput os) {
        this.os = os;
        this.osLE = new LittleEndianDataOutput(os);
    }

    public WavWriter(OutputStream os, StreamInfo streamInfo) {
        this.os = new DataOutputStream(os);
        this.osLE = new LittleEndianDataOutput(this.os);
        this.totalSamples = streamInfo.getTotalSamples();
        this.channels = streamInfo.getChannels();
        this.bps = streamInfo.getBitsPerSample();
        this.sampleRate = streamInfo.getSampleRate();
    }

    public WavWriter(OutputStream os, Long totalSamples, Integer channels, Integer bps, Integer sampleRate) {
        this.os = new DataOutputStream(os);
        this.osLE = new LittleEndianDataOutput(this.os);
        this.totalSamples = totalSamples;
        this.channels = channels;
        this.bps = bps;
        this.sampleRate = sampleRate;
    }

    public WavWriter(OutputStream os) {
        this.os = new DataOutputStream(os);
        this.osLE = new LittleEndianDataOutput(this.os);
    }

    public void writeHeader() throws IOException {
        long dataSize = this.totalSamples * (long)this.channels * (long)((this.bps + 7) / 8);
        if (this.totalSamples == 0L && !(this.os instanceof RandomAccessFile)) {
            throw new IOException("Cannot seek in output stream");
        }
        this.os.write("RIFF".getBytes());
        this.osLE.writeInt((int)dataSize + 36);
        this.os.write("WAVEfmt ".getBytes());
        this.os.write(new byte[]{16, 0, 0, 0});
        this.os.write(new byte[]{1, 0});
        this.osLE.writeShort(this.channels);
        this.osLE.writeInt(this.sampleRate);
        this.osLE.writeInt(this.sampleRate * this.channels * ((this.bps + 7) / 8));
        this.osLE.writeShort(this.channels * ((this.bps + 7) / 8));
        this.osLE.writeShort(this.bps);
        this.os.write("data".getBytes());
        this.osLE.writeInt((int)dataSize);
    }

    public void writeHeader(StreamInfo streamInfo) throws IOException {
        this.totalSamples = streamInfo.getTotalSamples();
        this.channels = streamInfo.getChannels();
        this.bps = streamInfo.getBitsPerSample();
        this.sampleRate = streamInfo.getSampleRate();
        this.writeHeader();
    }

    public void writeFrame(Frame frame, ChannelData[] channelData) throws IOException {
        boolean isUnsignedSamples = this.bps <= 8;
        int wideSamples = frame.header.blockSize;
        if (wideSamples > 0) {
            this.samplesProcessed += wideSamples;
            ++this.frameCounter;
            if (this.bps == 8) {
                int sample;
                if (isUnsignedSamples) {
                    sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            this.s8buffer[sample++] = (byte)(channelData[channel].getOutput()[wideSample] + 128);
                        }
                    }
                } else {
                    sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            this.s8buffer[sample++] = (byte)channelData[channel].getOutput()[wideSample];
                        }
                    }
                }
                this.os.write(this.s8buffer, 0, sample);
            } else if (this.bps == 16) {
                int sample;
                if (isUnsignedSamples) {
                    sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            short val = (short)(channelData[channel].getOutput()[wideSample] + 32768);
                            this.s8buffer[sample++] = (byte)(val & 0xFF);
                            this.s8buffer[sample++] = (byte)(val >> 8 & 0xFF);
                        }
                    }
                } else {
                    sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            short val = (short)channelData[channel].getOutput()[wideSample];
                            this.s8buffer[sample++] = (byte)(val & 0xFF);
                            this.s8buffer[sample++] = (byte)(val >> 8 & 0xFF);
                        }
                    }
                }
                this.os.write(this.s8buffer, 0, sample);
            } else if (this.bps == 24) {
                int sample;
                if (isUnsignedSamples) {
                    sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            int val = channelData[channel].getOutput()[wideSample] + 0x800000;
                            this.s8buffer[sample++] = (byte)(val & 0xFF);
                            this.s8buffer[sample++] = (byte)(val >> 8 & 0xFF);
                            this.s8buffer[sample++] = (byte)(val >> 16 & 0xFF);
                        }
                    }
                } else {
                    sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            int val = channelData[channel].getOutput()[wideSample];
                            this.s8buffer[sample++] = (byte)(val & 0xFF);
                            this.s8buffer[sample++] = (byte)(val >> 8 & 0xFF);
                            this.s8buffer[sample++] = (byte)(val >> 16 & 0xFF);
                        }
                    }
                }
                this.os.write(this.s8buffer, 0, sample);
            }
        }
    }

    public void writePCM(ByteData space) throws IOException {
        this.os.write(space.getData(), 0, space.getLen());
    }
}

