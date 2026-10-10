/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.BadHeaderException;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.CRC8;
import java.io.IOException;

public class Header {
    public int blockSize;
    public int sampleRate;
    public int channels;
    public int channelAssignment;
    public int bitsPerSample;
    public int frameNumber;
    public long sampleNumber;
    protected byte crc;

    public Header(BitInputStream is, byte[] headerWarmup, StreamInfo streamInfo) throws IOException, BadHeaderException {
        ByteData rawHeader;
        int sampleRateHint;
        int blocksizeHint;
        block53: {
            int asgnType;
            block52: {
                this.frameNumber = -1;
                this.sampleNumber = -1L;
                blocksizeHint = 0;
                sampleRateHint = 0;
                rawHeader = new ByteData(16);
                rawHeader.append(headerWarmup[0]);
                rawHeader.append(headerWarmup[1]);
                if ((rawHeader.getData(1) & 2) != 0) {
                    throw new BadHeaderException("Bad Magic Number: " + (rawHeader.getData(1) & 0xFF));
                }
                for (int i = 0; i < 2; ++i) {
                    if (is.peekRawUInt(8) == 255) {
                        throw new BadHeaderException("Found sync byte");
                    }
                    rawHeader.append((byte)is.readRawUInt(8));
                }
                int bsType = rawHeader.getData(2) >> 4 & 0xF;
                switch (bsType) {
                    case 0: {
                        throw new BadHeaderException("Unknown Block Size (0)");
                    }
                    case 1: {
                        this.blockSize = 192;
                        break;
                    }
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        this.blockSize = 576 << bsType - 2;
                        break;
                    }
                    case 6: 
                    case 7: {
                        blocksizeHint = bsType;
                        break;
                    }
                    case 8: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 14: 
                    case 15: {
                        this.blockSize = 256 << bsType - 8;
                        break;
                    }
                }
                int srType = rawHeader.getData(2) & 0xF;
                switch (srType) {
                    case 0: {
                        if (streamInfo == null) {
                            throw new BadHeaderException("Bad Sample Rate (0)");
                        }
                        this.sampleRate = streamInfo.getSampleRate();
                        break;
                    }
                    case 1: {
                        this.sampleRate = 88200;
                        break;
                    }
                    case 2: {
                        this.sampleRate = 176400;
                        break;
                    }
                    case 3: {
                        this.sampleRate = 192000;
                        break;
                    }
                    case 4: {
                        this.sampleRate = 8000;
                        break;
                    }
                    case 5: {
                        this.sampleRate = 16000;
                        break;
                    }
                    case 6: {
                        this.sampleRate = 22050;
                        break;
                    }
                    case 7: {
                        this.sampleRate = 24000;
                        break;
                    }
                    case 8: {
                        this.sampleRate = 32000;
                        break;
                    }
                    case 9: {
                        this.sampleRate = 44100;
                        break;
                    }
                    case 10: {
                        this.sampleRate = 48000;
                        break;
                    }
                    case 11: {
                        this.sampleRate = 96000;
                        break;
                    }
                    case 12: 
                    case 13: 
                    case 14: {
                        sampleRateHint = srType;
                        break;
                    }
                    case 15: {
                        throw new BadHeaderException("Bad Sample Rate (" + srType + ")");
                    }
                }
                asgnType = rawHeader.getData(3) >> 4 & 0xF;
                if ((asgnType & 8) == 0) break block52;
                this.channels = 2;
                switch (asgnType & 7) {
                    case 0: {
                        this.channelAssignment = 1;
                        break block53;
                    }
                    case 1: {
                        this.channelAssignment = 2;
                        break block53;
                    }
                    case 2: {
                        this.channelAssignment = 3;
                        break block53;
                    }
                    default: {
                        throw new BadHeaderException("Bad Channel Assignment (" + asgnType + ")");
                    }
                }
            }
            this.channels = asgnType + 1;
            this.channelAssignment = 0;
        }
        int bpsType = (rawHeader.getData(3) & 0xE) >> 1;
        switch (bpsType) {
            case 0: {
                if (streamInfo != null) {
                    this.bitsPerSample = streamInfo.getBitsPerSample();
                    break;
                }
                throw new BadHeaderException("Bad BPS (" + bpsType + ")");
            }
            case 1: {
                this.bitsPerSample = 8;
                break;
            }
            case 2: {
                this.bitsPerSample = 12;
                break;
            }
            case 4: {
                this.bitsPerSample = 16;
                break;
            }
            case 5: {
                this.bitsPerSample = 20;
                break;
            }
            case 6: {
                this.bitsPerSample = 24;
                break;
            }
            case 3: 
            case 7: {
                throw new BadHeaderException("Bad BPS (" + bpsType + ")");
            }
        }
        if ((rawHeader.getData(3) & 1) != 0) {
            throw new BadHeaderException("Bad Magic Number: " + (rawHeader.getData(3) & 1));
        }
        if ((rawHeader.getData(1) & 1) != 0 || streamInfo != null && streamInfo.getMinBlockSize() != streamInfo.getMaxBlockSize()) {
            this.sampleNumber = is.readUTF8Long(rawHeader);
            if (this.sampleNumber == -1L) {
                throw new BadHeaderException("Bad Sample Number");
            }
        } else {
            int lastFrameNumber = is.readUTF8Int(rawHeader);
            if (lastFrameNumber == -1) {
                throw new BadHeaderException("Bad Last Frame");
            }
            this.sampleNumber = (long)streamInfo.getMinBlockSize() * (long)lastFrameNumber;
        }
        if (blocksizeHint != 0) {
            int blockSizeCode = is.readRawUInt(8);
            rawHeader.append((byte)blockSizeCode);
            if (blocksizeHint == 7) {
                int blockSizeCode2 = is.readRawUInt(8);
                rawHeader.append((byte)blockSizeCode2);
                blockSizeCode = blockSizeCode << 8 | blockSizeCode2;
            }
            this.blockSize = blockSizeCode + 1;
        }
        if (sampleRateHint != 0) {
            int sampleRateCode = is.readRawUInt(8);
            rawHeader.append((byte)sampleRateCode);
            if (sampleRateHint != 12) {
                int sampleRateCode2 = is.readRawUInt(8);
                rawHeader.append((byte)sampleRateCode2);
                sampleRateCode = sampleRateCode << 8 | sampleRateCode2;
            }
            this.sampleRate = sampleRateHint == 12 ? sampleRateCode * 1000 : (sampleRateHint == 13 ? sampleRateCode : sampleRateCode * 10);
        }
        byte crc8 = (byte)is.readRawUInt(8);
        if (CRC8.calc(rawHeader.getData(), rawHeader.getLen()) != crc8) {
            throw new BadHeaderException("STREAM_DECODER_ERROR_STATUS_BAD_HEADER");
        }
    }

    public String toString() {
        return "FrameHeader: BlockSize=" + this.blockSize + " SampleRate=" + this.sampleRate + " Channels=" + this.channels + " ChannelAssignment=" + this.channelAssignment + " BPS=" + this.bitsPerSample + " SampleNumber=" + this.sampleNumber;
    }
}

