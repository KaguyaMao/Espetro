/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Frame;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import java.io.IOException;

public class PCMDecoder {
    private int channels;
    private int bps;
    private ByteData buf;

    public PCMDecoder(StreamInfo streamInfo) {
        this.channels = streamInfo.getChannels();
        this.bps = streamInfo.getBitsPerSample();
        this.buf = new ByteData(streamInfo.getMaxFrameSize());
    }

    public ByteData getFrame(Frame frame, ChannelData[] channelData) throws IOException {
        block18: {
            int wideSamples;
            boolean isUnsignedSamples;
            block20: {
                block19: {
                    isUnsignedSamples = this.bps <= 8;
                    wideSamples = frame.header.blockSize;
                    if (wideSamples <= 0) break block18;
                    if (this.bps != 8) break block19;
                    if (isUnsignedSamples) {
                        int sample = 0;
                        for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                            for (int channel = 0; channel < this.channels; ++channel) {
                                this.buf.append((byte)(channelData[channel].getOutput()[wideSample] + 128));
                            }
                        }
                    } else {
                        int sample = 0;
                        for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                            for (int channel = 0; channel < this.channels; ++channel) {
                                this.buf.append((byte)channelData[channel].getOutput()[wideSample]);
                            }
                        }
                    }
                    break block18;
                }
                if (this.bps != 16) break block20;
                if (isUnsignedSamples) {
                    int sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            short val = (short)(channelData[channel].getOutput()[wideSample] + 32768);
                            this.buf.append((byte)(val & 0xFF));
                            this.buf.append((byte)(val >> 8 & 0xFF));
                        }
                    }
                } else {
                    int sample = 0;
                    for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            short val = (short)channelData[channel].getOutput()[wideSample];
                            this.buf.append((byte)(val & 0xFF));
                            this.buf.append((byte)(val >> 8 & 0xFF));
                        }
                    }
                }
                break block18;
            }
            if (this.bps != 24) break block18;
            if (isUnsignedSamples) {
                int sample = 0;
                for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                    for (int channel = 0; channel < this.channels; ++channel) {
                        int val = channelData[channel].getOutput()[wideSample] + 0x800000;
                        this.buf.append((byte)(val & 0xFF));
                        this.buf.append((byte)(val >> 8 & 0xFF));
                        this.buf.append((byte)(val >> 16 & 0xFF));
                    }
                }
            } else {
                int sample = 0;
                for (int wideSample = 0; wideSample < wideSamples; ++wideSample) {
                    for (int channel = 0; channel < this.channels; ++channel) {
                        int val = channelData[channel].getOutput()[wideSample];
                        this.buf.append((byte)(val & 0xFF));
                        this.buf.append((byte)(val >> 8 & 0xFF));
                        this.buf.append((byte)(val >> 16 & 0xFF));
                    }
                }
            }
        }
        return this.buf;
    }
}

