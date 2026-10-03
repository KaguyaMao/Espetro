/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

import LOL_141.vehicle_addition.soundlibs.org.jflac.ChannelData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.Constants;
import LOL_141.vehicle_addition.soundlibs.org.jflac.FrameDecodeException;
import LOL_141.vehicle_addition.soundlibs.org.jflac.FrameListener;
import LOL_141.vehicle_addition.soundlibs.org.jflac.FrameListeners;
import LOL_141.vehicle_addition.soundlibs.org.jflac.PCMProcessor;
import LOL_141.vehicle_addition.soundlibs.org.jflac.PCMProcessors;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.BadHeaderException;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.ChannelConstant;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.ChannelFixed;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.ChannelLPC;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.ChannelVerbatim;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Frame;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.RandomFileInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Application;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.CueSheet;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Padding;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Picture;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.SeekPoint;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.SeekTable;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Unknown;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.VorbisComment;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.CRC16;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FLACDecoder {
    private static final int FRAME_FOOTER_CRC_LEN = 16;
    private static final byte[] ID3V2_TAG = new byte[]{73, 68, 51};
    private BitInputStream bitStream;
    private ChannelData[] channelData = new ChannelData[8];
    private int outputCapacity = 0;
    private int outputChannels = 0;
    private long samplesDecoded = 0L;
    private StreamInfo streamInfo = null;
    private SeekTable seekTable = null;
    private Frame frame = new Frame();
    private byte[] headerWarmup = new byte[2];
    private int channels;
    private InputStream inputStream = null;
    private int badFrames;
    private boolean eof = false;
    private FrameListeners frameListeners = new FrameListeners();
    private PCMProcessors pcmProcessors = new PCMProcessors();

    public FLACDecoder(InputStream inputStream) {
        this.inputStream = inputStream;
        this.bitStream = new BitInputStream(inputStream);
        this.samplesDecoded = 0L;
    }

    public StreamInfo getStreamInfo() {
        return this.streamInfo;
    }

    public ChannelData[] getChannelData() {
        return this.channelData;
    }

    public BitInputStream getBitInputStream() {
        return this.bitStream;
    }

    public void addFrameListener(FrameListener listener) {
        this.frameListeners.addFrameListener(listener);
    }

    public void removeFrameListener(FrameListener listener) {
        this.frameListeners.removeFrameListener(listener);
    }

    public void addPCMProcessor(PCMProcessor processor) {
        this.pcmProcessors.addPCMProcessor(processor);
    }

    public void removePCMProcessor(PCMProcessor processor) {
        this.pcmProcessors.removePCMProcessor(processor);
    }

    private void callPCMProcessors(Frame frame) {
        ByteData bd = this.decodeFrame(frame, null);
        this.pcmProcessors.processPCM(bd);
    }

    public ByteData decodeFrame(Frame frame, ByteData pcmData) {
        block9: {
            block10: {
                block8: {
                    int byteSize = frame.header.blockSize * this.channels * ((this.streamInfo.getBitsPerSample() + 7) / 2);
                    if (pcmData == null || pcmData.getData().length < byteSize) {
                        pcmData = new ByteData(byteSize);
                    } else {
                        pcmData.setLen(0);
                    }
                    if (this.streamInfo.getBitsPerSample() != 8) break block8;
                    for (int i = 0; i < frame.header.blockSize; ++i) {
                        for (int channel = 0; channel < this.channels; ++channel) {
                            pcmData.append((byte)(this.channelData[channel].getOutput()[i] + 128));
                        }
                    }
                    break block9;
                }
                if (this.streamInfo.getBitsPerSample() != 16) break block10;
                for (int i = 0; i < frame.header.blockSize; ++i) {
                    for (int channel = 0; channel < this.channels; ++channel) {
                        short val = (short)this.channelData[channel].getOutput()[i];
                        pcmData.append((byte)(val & 0xFF));
                        pcmData.append((byte)(val >> 8 & 0xFF));
                    }
                }
                break block9;
            }
            if (this.streamInfo.getBitsPerSample() != 24) break block9;
            for (int i = 0; i < frame.header.blockSize; ++i) {
                for (int channel = 0; channel < this.channels; ++channel) {
                    int val = this.channelData[channel].getOutput()[i];
                    pcmData.append((byte)(val & 0xFF));
                    pcmData.append((byte)(val >> 8 & 0xFF));
                    pcmData.append((byte)(val >> 16 & 0xFF));
                }
            }
        }
        return pcmData;
    }

    public StreamInfo readStreamInfo() throws IOException {
        this.readStreamSync();
        Metadata metadata = this.readNextMetadata();
        if (!(metadata instanceof StreamInfo)) {
            throw new IOException("StreamInfo metadata block missing");
        }
        return (StreamInfo)metadata;
    }

    public Metadata[] readMetadata() throws IOException {
        Metadata metadata;
        this.readStreamSync();
        Vector<Metadata> metadataList = new Vector<Metadata>();
        do {
            metadata = this.readNextMetadata();
            metadataList.add(metadata);
        } while (!metadata.isLast());
        return metadataList.toArray(new Metadata[0]);
    }

    public Metadata[] readMetadata(StreamInfo streamInfo) throws IOException {
        Metadata metadata;
        if (streamInfo.isLast()) {
            return new Metadata[0];
        }
        Vector<Metadata> metadataList = new Vector<Metadata>();
        do {
            metadata = this.readNextMetadata();
            metadataList.add(metadata);
        } while (!metadata.isLast());
        return metadataList.toArray(new Metadata[0]);
    }

    public void decode() throws IOException {
        this.readMetadata();
        try {
            while (true) {
                this.findFrameSync();
                try {
                    this.readFrame();
                    this.frameListeners.processFrame(this.frame);
                    this.callPCMProcessors(this.frame);
                }
                catch (FrameDecodeException e) {
                    ++this.badFrames;
                }
            }
        }
        catch (EOFException e) {
            this.eof = true;
            return;
        }
    }

    public void decodeFrames() throws IOException {
        try {
            while (true) {
                this.findFrameSync();
                try {
                    this.readFrame();
                    this.frameListeners.processFrame(this.frame);
                    this.callPCMProcessors(this.frame);
                }
                catch (FrameDecodeException e) {
                    ++this.badFrames;
                }
            }
        }
        catch (EOFException e) {
            this.eof = true;
            return;
        }
    }

    public void decode(SeekPoint from, SeekPoint to) throws IOException {
        if (!(this.inputStream instanceof RandomFileInputStream)) {
            throw new IOException("Not a RandomFileInputStream: " + this.inputStream.getClass().getName());
        }
        ((RandomFileInputStream)this.inputStream).seek(from.getStreamOffset());
        this.bitStream.reset();
        this.samplesDecoded = from.getSampleNumber();
        try {
            do {
                this.findFrameSync();
                try {
                    this.readFrame();
                    this.frameListeners.processFrame(this.frame);
                    this.callPCMProcessors(this.frame);
                }
                catch (FrameDecodeException e) {
                    ++this.badFrames;
                }
            } while (to == null || this.samplesDecoded < to.getSampleNumber());
            return;
        }
        catch (EOFException e) {
            this.eof = true;
            return;
        }
    }

    public long seek(long samplesAbsolute) throws IOException {
        if (!(this.inputStream instanceof RandomFileInputStream)) {
            throw new IOException("Not a RandomFileInputStream: " + this.inputStream.getClass().getName());
        }
        ((RandomFileInputStream)this.inputStream).seek(0L);
        this.bitStream.reset();
        this.samplesDecoded = 0L;
        long seekDelta = samplesAbsolute - this.samplesDecoded;
        this.readMetadata();
        if (this.streamInfo == null) {
            throw new IOException("Could not obtain stream info required for seeking");
        }
        if (samplesAbsolute < 0L || samplesAbsolute >= this.streamInfo.getTotalSamples()) {
            throw new IllegalArgumentException("Invalid sample position for seek");
        }
        if (seekDelta == 0L) {
            return this.samplesDecoded;
        }
        int estimatedFrameSize = this.streamInfo.getMaxFrameSize();
        this.findFrameSync();
        long bytePositionCurrentFrame = this.bitStream.getTotalBytesRead() - 2;
        long samplePositionUpper = this.streamInfo.getTotalSamples();
        long bytePositionUpper = ((RandomFileInputStream)this.inputStream).getLength() - (long)estimatedFrameSize - bytePositionCurrentFrame;
        SeekPoint beforeSeekPosition = new SeekPoint(0L, 0L, estimatedFrameSize);
        SeekPoint afterSeekPosition = new SeekPoint(samplePositionUpper, bytePositionUpper, this.streamInfo.getMinBlockSize());
        if (this.seekTable != null) {
            for (int i = 0; i < this.seekTable.numberOfPoints(); ++i) {
                SeekPoint currentSeekPoint = this.seekTable.getSeekPoint(i);
                if (currentSeekPoint.getSampleNumber() < samplesAbsolute) {
                    if (currentSeekPoint.getSampleNumber() <= beforeSeekPosition.getSampleNumber() || samplesAbsolute >= (beforeSeekPosition = currentSeekPoint).getSampleNumber() + (long)beforeSeekPosition.getFrameSamples()) continue;
                    afterSeekPosition = currentSeekPoint;
                    break;
                }
                if (currentSeekPoint.getSampleNumber() > samplesAbsolute) {
                    if (currentSeekPoint.getSampleNumber() >= afterSeekPosition.getSampleNumber()) continue;
                    afterSeekPosition = currentSeekPoint;
                    continue;
                }
                if (currentSeekPoint.getSampleNumber() != samplesAbsolute) continue;
                beforeSeekPosition = currentSeekPoint;
                afterSeekPosition = currentSeekPoint;
                break;
            }
        }
        long bytePositionEstimate = bytePositionCurrentFrame + beforeSeekPosition.getStreamOffset();
        if (afterSeekPosition.getSampleNumber() > beforeSeekPosition.getSampleNumber()) {
            double percentBetweenSeekPositions = (double)(samplesAbsolute - beforeSeekPosition.getSampleNumber()) / (double)(afterSeekPosition.getSampleNumber() - beforeSeekPosition.getSampleNumber());
            long bytePositionBetweenSeekPositions = (long)((double)(afterSeekPosition.getStreamOffset() - beforeSeekPosition.getStreamOffset()) * percentBetweenSeekPositions) - (long)estimatedFrameSize;
            if (bytePositionBetweenSeekPositions > 0L) {
                bytePositionEstimate += bytePositionBetweenSeekPositions;
            }
        }
        ((RandomFileInputStream)this.inputStream).seek(bytePositionEstimate);
        this.bitStream.reset();
        int framesToSeekBack = 1;
        Map<Object, Object> seekBackwardsResults = new HashMap();
        while (seekDelta != 0L) {
            this.findFrameSync();
            bytePositionCurrentFrame = bytePositionEstimate + (long)this.bitStream.getTotalBytesRead() - 2L;
            try {
                this.readFrame();
            }
            catch (FrameDecodeException e) {
                ++this.badFrames;
                continue;
            }
            bytePositionEstimate += (long)this.bitStream.getTotalBytesRead();
            if (this.frame == null) {
                if (this.eof) {
                    seekBackwardsResults = this.seekBackwards(bytePositionCurrentFrame, framesToSeekBack, estimatedFrameSize);
                    bytePositionEstimate = (Long)seekBackwardsResults.get("bytePositionEstimate");
                    framesToSeekBack = (Integer)seekBackwardsResults.get("framesToSeekBack");
                    continue;
                }
                throw new IOException("Fatal seek data frame reading error");
            }
            this.samplesDecoded = this.frame.header.sampleNumber;
            seekDelta = samplesAbsolute - this.samplesDecoded;
            if (seekDelta < 0L) {
                seekBackwardsResults = this.seekBackwards(bytePositionCurrentFrame, framesToSeekBack, estimatedFrameSize);
                bytePositionEstimate = (Long)seekBackwardsResults.get("bytePositionEstimate");
                framesToSeekBack = (Integer)seekBackwardsResults.get("framesToSeekBack");
                continue;
            }
            if (seekDelta < (long)this.frame.header.blockSize) {
                ((RandomFileInputStream)this.inputStream).seek(bytePositionCurrentFrame);
                this.bitStream.reset();
                break;
            }
            if (seekDelta >= (long)this.frame.header.blockSize) {
                framesToSeekBack = 0;
                ((RandomFileInputStream)this.inputStream).seek(bytePositionEstimate);
                this.bitStream.reset();
                continue;
            }
            throw new IOException("Fatal seek logic error");
        }
        return this.samplesDecoded;
    }

    private Map<String, Object> seekBackwards(long bytePositionEstimate, int framesToSeekBack, long estimatedFrameSize) throws IOException {
        if (framesToSeekBack > 0) {
            if ((bytePositionEstimate -= (long)framesToSeekBack * estimatedFrameSize) < 0L) {
                bytePositionEstimate = 0L;
                framesToSeekBack = 0;
            } else {
                ++framesToSeekBack;
            }
        } else {
            throw new IOException("Fatal seek error: sample position not found");
        }
        ((RandomFileInputStream)this.inputStream).seek(bytePositionEstimate);
        this.bitStream.reset();
        HashMap<String, Object> returnVariables = new HashMap<String, Object>();
        returnVariables.put("bytePositionEstimate", bytePositionEstimate);
        returnVariables.put("framesToSeekBack", framesToSeekBack);
        return returnVariables;
    }

    public Frame readNextFrame() throws IOException {
        try {
            while (true) {
                this.findFrameSync();
                try {
                    this.readFrame();
                    return this.frame;
                }
                catch (FrameDecodeException e) {
                    ++this.badFrames;
                    continue;
                }
                break;
            }
        }
        catch (EOFException e) {
            this.eof = true;
            return null;
        }
    }

    public long getTotalBytesRead() {
        return this.bitStream.getTotalBytesRead();
    }

    private void allocateOutput(int size, int channels) {
        int i;
        if (size <= this.outputCapacity && channels <= this.outputChannels) {
            return;
        }
        for (i = 0; i < 8; ++i) {
            this.channelData[i] = null;
        }
        for (i = 0; i < channels; ++i) {
            this.channelData[i] = new ChannelData(size);
        }
        this.outputCapacity = size;
        this.outputChannels = channels;
    }

    private void readStreamSync() throws IOException {
        int id = 0;
        int i = 0;
        while (i < 4) {
            int x = this.bitStream.readRawUInt(8);
            if (x == Constants.STREAM_SYNC_STRING[i]) {
                ++i;
                id = 0;
                continue;
            }
            if (x == ID3V2_TAG[id]) {
                i = 0;
                if (++id != 3) continue;
                this.skipID3v2Tag();
                id = 0;
                continue;
            }
            throw new IOException("Could not find Stream Sync");
        }
    }

    public Metadata readNextMetadata() throws IOException {
        Metadata metadata = null;
        boolean isLast = this.bitStream.readRawUInt(1) != 0;
        int type = this.bitStream.readRawUInt(7);
        int length = this.bitStream.readRawUInt(24);
        if (type == 0) {
            metadata = this.streamInfo = new StreamInfo(this.bitStream, length, isLast);
            this.pcmProcessors.processStreamInfo((StreamInfo)metadata);
        } else if (type == 3) {
            this.seekTable = new SeekTable(this.bitStream, length, isLast);
            metadata = this.seekTable;
        } else {
            metadata = type == 2 ? new Application(this.bitStream, length, isLast) : (type == 1 ? new Padding(this.bitStream, length, isLast) : (type == 4 ? new VorbisComment(this.bitStream, length, isLast) : (type == 5 ? new CueSheet(this.bitStream, length, isLast) : (type == 6 ? new Picture(this.bitStream, length, isLast) : new Unknown(this.bitStream, length, isLast)))));
        }
        this.frameListeners.processMetadata(metadata);
        return metadata;
    }

    private void skipID3v2Tag() throws IOException {
        int verMajor = this.bitStream.readRawInt(8);
        int verMinor = this.bitStream.readRawInt(8);
        int flags = this.bitStream.readRawInt(8);
        int skip = 0;
        for (int i = 0; i < 4; ++i) {
            int x = this.bitStream.readRawUInt(8);
            skip <<= 7;
            skip |= x & 0x7F;
        }
        this.bitStream.readByteBlockAlignedNoCRC(null, skip);
    }

    private void findFrameSync() throws IOException {
        boolean first = true;
        if (this.streamInfo != null && this.streamInfo.getTotalSamples() > 0L && this.samplesDecoded >= this.streamInfo.getTotalSamples()) {
            throw new EOFException();
        }
        if (!this.bitStream.isConsumedByteAligned()) {
            this.bitStream.readRawUInt(this.bitStream.bitsLeftForByteAlignment());
        }
        try {
            while (true) {
                int x;
                if ((x = this.bitStream.readRawUInt(8)) == 255) {
                    this.headerWarmup[0] = (byte)x;
                    x = this.bitStream.peekRawUInt(8);
                    if (x >> 2 == 62) {
                        this.headerWarmup[1] = (byte)this.bitStream.readRawUInt(8);
                        return;
                    }
                }
                if (!first) continue;
                this.frameListeners.processError("FindSync LOST_SYNC: " + Integer.toHexString(x & 0xFF));
                first = false;
            }
        }
        catch (EOFException e) {
            if (!first) {
                this.frameListeners.processError("FindSync LOST_SYNC: Left over data in file");
            }
            return;
        }
    }

    public void readFrame() throws IOException, FrameDecodeException {
        int channel;
        boolean gotAFrame = false;
        short frameCRC = 0;
        frameCRC = CRC16.update(this.headerWarmup[0], frameCRC);
        frameCRC = CRC16.update(this.headerWarmup[1], frameCRC);
        this.bitStream.resetReadCRC16(frameCRC);
        try {
            this.frame.header = new Header(this.bitStream, this.headerWarmup, this.streamInfo);
        }
        catch (BadHeaderException e) {
            this.frameListeners.processError("Found bad header: " + e);
            throw new FrameDecodeException("Bad Frame Header: " + e);
        }
        this.allocateOutput(this.frame.header.blockSize, this.frame.header.channels);
        for (channel = 0; channel < this.frame.header.channels; ++channel) {
            int bps = this.frame.header.bitsPerSample;
            switch (this.frame.header.channelAssignment) {
                case 0: {
                    break;
                }
                case 1: {
                    if (channel != 1) break;
                    ++bps;
                    break;
                }
                case 2: {
                    if (channel != 0) break;
                    ++bps;
                    break;
                }
                case 3: {
                    if (channel != 1) break;
                    ++bps;
                    break;
                }
            }
            try {
                this.readSubframe(channel, bps);
                continue;
            }
            catch (IOException e) {
                this.frameListeners.processError("ReadSubframe: " + e);
                throw e;
            }
        }
        this.readZeroPadding();
        frameCRC = this.bitStream.getReadCRC16();
        this.frame.setCRC((short)this.bitStream.readRawUInt(16));
        if (frameCRC == this.frame.getCRC()) {
            switch (this.frame.header.channelAssignment) {
                case 0: {
                    break;
                }
                case 1: {
                    for (int i = 0; i < this.frame.header.blockSize; ++i) {
                        this.channelData[1].getOutput()[i] = this.channelData[0].getOutput()[i] - this.channelData[1].getOutput()[i];
                    }
                    break;
                }
                case 2: {
                    for (int i = 0; i < this.frame.header.blockSize; ++i) {
                        int[] nArray = this.channelData[0].getOutput();
                        int n = i;
                        nArray[n] = nArray[n] + this.channelData[1].getOutput()[i];
                    }
                    break;
                }
                case 3: {
                    for (int i = 0; i < this.frame.header.blockSize; ++i) {
                        int mid = this.channelData[0].getOutput()[i];
                        int side = this.channelData[1].getOutput()[i];
                        mid <<= 1;
                        this.channelData[0].getOutput()[i] = (mid |= side & 1) + side >> 1;
                        this.channelData[1].getOutput()[i] = mid - side >> 1;
                    }
                    break;
                }
            }
            gotAFrame = true;
        } else {
            this.frameListeners.processError("CRC Error: " + Integer.toHexString(frameCRC & 0xFFFF) + " vs " + Integer.toHexString(this.frame.getCRC() & 0xFFFF));
            for (channel = 0; channel < this.frame.header.channels; ++channel) {
                for (int j = 0; j < this.frame.header.blockSize; ++j) {
                    this.channelData[channel].getOutput()[j] = 0;
                }
            }
        }
        this.channels = this.frame.header.channels;
        this.samplesDecoded += (long)this.frame.header.blockSize;
    }

    private void readSubframe(int channel, int bps) throws IOException, FrameDecodeException {
        int x = this.bitStream.readRawUInt(8);
        boolean haveWastedBits = (x & 1) != 0;
        x &= 0xFE;
        int wastedBits = 0;
        if (haveWastedBits) {
            wastedBits = this.bitStream.readUnaryUnsigned() + 1;
            bps -= wastedBits;
        }
        if ((x & 0x80) != 0) {
            this.frameListeners.processError("ReadSubframe LOST_SYNC: " + Integer.toHexString(x & 0xFF));
            throw new FrameDecodeException("ReadSubframe LOST_SYNC: " + Integer.toHexString(x & 0xFF));
        }
        if (x == 0) {
            this.frame.subframes[channel] = new ChannelConstant(this.bitStream, this.frame.header, this.channelData[channel], bps, wastedBits);
        } else if (x == 2) {
            this.frame.subframes[channel] = new ChannelVerbatim(this.bitStream, this.frame.header, this.channelData[channel], bps, wastedBits);
        } else {
            if (x < 16) {
                throw new FrameDecodeException("ReadSubframe Bad Subframe Type: " + Integer.toHexString(x & 0xFF));
            }
            if (x <= 24) {
                this.frame.subframes[channel] = new ChannelFixed(this.bitStream, this.frame.header, this.channelData[channel], bps, wastedBits, x >> 1 & 7);
            } else {
                if (x < 64) {
                    throw new FrameDecodeException("ReadSubframe Bad Subframe Type: " + Integer.toHexString(x & 0xFF));
                }
                this.frame.subframes[channel] = new ChannelLPC(this.bitStream, this.frame.header, this.channelData[channel], bps, wastedBits, (x >> 1 & 0x1F) + 1);
            }
        }
        if (haveWastedBits) {
            x = this.frame.subframes[channel].getWastedBits();
            int i = 0;
            while (i < this.frame.header.blockSize) {
                int[] nArray = this.channelData[channel].getOutput();
                int n = i++;
                nArray[n] = nArray[n] << x;
            }
        }
    }

    private void readZeroPadding() throws IOException, FrameDecodeException {
        int zero;
        if (!this.bitStream.isConsumedByteAligned() && (zero = this.bitStream.readRawUInt(this.bitStream.bitsLeftForByteAlignment())) != 0) {
            this.frameListeners.processError("ZeroPaddingError: " + Integer.toHexString(zero));
            throw new FrameDecodeException("ZeroPaddingError: " + Integer.toHexString(zero));
        }
    }

    public long getSamplesDecoded() {
        return this.samplesDecoded;
    }

    public int getBadFrames() {
        return this.badFrames;
    }

    public boolean isEOF() {
        return this.eof;
    }
}

