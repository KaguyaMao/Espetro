/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.AudioUtils;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.FloatSampleBuffer;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.FloatSampleInput;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert.TAudioInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public abstract class TSynchronousFilteredAudioInputStream
extends TAudioInputStream
implements FloatSampleInput {
    private AudioInputStream originalStream;
    private FloatSampleInput originalStreamFloat;
    private AudioFormat originalFormat;
    private int originalFrameSize;
    private int newFrameSize;
    private boolean EOF = false;
    protected byte[] m_buffer = null;
    private boolean m_bConvertInPlace = false;
    private boolean m_enableFloatConversion = false;
    private byte[] m_floatByteBuffer = null;

    public TSynchronousFilteredAudioInputStream(AudioInputStream audioInputStream, AudioFormat newFormat) {
        super(audioInputStream, newFormat, audioInputStream.getFrameLength());
        this.originalStream = audioInputStream;
        this.originalFormat = audioInputStream.getFormat();
        this.originalFrameSize = this.originalFormat.getFrameSize() <= 0 ? 1 : this.originalFormat.getFrameSize();
        int n = this.newFrameSize = this.getFormat().getFrameSize() <= 0 ? 1 : this.getFormat().getFrameSize();
        if (this.originalStream instanceof FloatSampleInput) {
            this.originalStreamFloat = (FloatSampleInput)((Object)this.originalStream);
        }
        if (TDebug.TraceAudioConverter) {
            TDebug.out("TSynchronousFilteredAudioInputStream: original format =" + AudioUtils.format2ShortStr(this.originalFormat));
            TDebug.out("TSynchronousFilteredAudioInputStream: converted format=" + AudioUtils.format2ShortStr(this.getFormat()));
        }
        this.m_bConvertInPlace = false;
        this.m_enableFloatConversion = false;
    }

    protected boolean enableConvertInPlace() {
        if (this.newFrameSize >= this.originalFrameSize) {
            this.m_bConvertInPlace = true;
        }
        return this.m_bConvertInPlace;
    }

    protected void enableFloatConversion() {
        this.m_enableFloatConversion = true;
    }

    protected abstract int convert(byte[] var1, byte[] var2, int var3, int var4);

    protected void convertInPlace(byte[] buffer, int byteOffset, int frameCount) {
        throw new RuntimeException("illegal call to convertInPlace");
    }

    protected void convert(FloatSampleBuffer buffer, int offset, int count) {
        throw new RuntimeException("illegal call to convert(FloatSampleBuffer)");
    }

    public int read() throws IOException {
        if (this.newFrameSize != 1) {
            throw new IOException("frame size must be 1 to read a single byte");
        }
        byte[] temp = new byte[1];
        int result = this.read(temp);
        if (result == -1) {
            return -1;
        }
        if (result == 0) {
            return -1;
        }
        return temp[0] & 0xFF;
    }

    private void clearBuffer() {
        this.m_buffer = null;
        this.m_floatByteBuffer = null;
    }

    public AudioInputStream getOriginalStream() {
        return this.originalStream;
    }

    public AudioFormat getOriginalFormat() {
        return this.originalFormat;
    }

    public final int read(byte[] abData, int nOffset, int nLength) throws IOException {
        int readOffset;
        byte[] readBuffer;
        int nFrameLength = nLength / this.newFrameSize;
        int originalBytes = nFrameLength * this.originalFrameSize;
        if (TDebug.TraceAudioConverter) {
            TDebug.out("> TSynchronousFilteredAIS.read(buffer[" + abData.length + "], " + nOffset + " ," + nLength + " bytes ^=" + nFrameLength + " frames)");
        }
        int nFramesConverted = 0;
        if (this.m_bConvertInPlace) {
            readBuffer = abData;
            readOffset = nOffset;
        } else {
            if (this.m_buffer == null || this.m_buffer.length < originalBytes) {
                this.m_buffer = new byte[originalBytes];
            }
            readBuffer = this.m_buffer;
            readOffset = 0;
        }
        int nBytesRead = this.originalStream.read(readBuffer, readOffset, originalBytes);
        if (nBytesRead == -1) {
            this.clearBuffer();
            this.EOF = true;
            return -1;
        }
        int nFramesRead = nBytesRead / this.originalFrameSize;
        if (TDebug.TraceAudioConverter) {
            TDebug.out("original.read returned " + nBytesRead + " bytes ^=" + nFramesRead + " frames");
        }
        if (this.m_bConvertInPlace) {
            this.convertInPlace(abData, nOffset, nFramesRead);
            nFramesConverted = nFramesRead;
        } else {
            nFramesConverted = this.convert(this.m_buffer, abData, nOffset, nFramesRead);
        }
        if (TDebug.TraceAudioConverter) {
            TDebug.out("< converted " + nFramesConverted + " frames");
        }
        return nFramesConverted * this.newFrameSize;
    }

    public long skip(long nSkip) throws IOException {
        long skipFrames = nSkip / (long)this.newFrameSize;
        long originalSkippedBytes = this.originalStream.skip(skipFrames * (long)this.originalFrameSize);
        long skippedFrames = originalSkippedBytes / (long)this.originalFrameSize;
        return skippedFrames * (long)this.newFrameSize;
    }

    public int available() throws IOException {
        int origAvailFrames = this.originalStream.available() / this.originalFrameSize;
        return origAvailFrames * this.newFrameSize;
    }

    public void close() throws IOException {
        this.EOF = true;
        this.originalStream.close();
        this.clearBuffer();
    }

    public void mark(int readlimit) {
        int readLimitFrames = readlimit / this.newFrameSize;
        this.originalStream.mark(readLimitFrames * this.originalFrameSize);
    }

    public void reset() throws IOException {
        this.originalStream.reset();
    }

    public boolean markSupported() {
        return this.originalStream.markSupported();
    }

    public int getChannels() {
        return this.format.getChannels();
    }

    public float getSampleRate() {
        return this.format.getSampleRate();
    }

    public boolean isDone() {
        if (this.EOF) {
            return true;
        }
        if (this.originalStreamFloat != null) {
            return this.originalStreamFloat.isDone();
        }
        return false;
    }

    public void read(FloatSampleBuffer buffer, int offset, int sampleCount) {
        try {
            if (this.originalStreamFloat == null && this.m_enableFloatConversion) {
                int bytesRead;
                if (offset > 0 || sampleCount != buffer.getSampleCount()) {
                    throw new IllegalArgumentException("float reading with offset not supported");
                }
                int reqSize = sampleCount * this.originalFrameSize;
                if (this.m_floatByteBuffer == null || this.m_floatByteBuffer.length < reqSize) {
                    this.m_floatByteBuffer = new byte[reqSize];
                }
                if ((bytesRead = this.originalStream.read(this.m_floatByteBuffer, 0, reqSize)) <= 0) {
                    buffer.setSampleCount(0, false);
                    return;
                }
                buffer.initFromByteArray(this.m_floatByteBuffer, 0, bytesRead, this.originalFormat);
                this.convert(buffer, 0, buffer.getSampleCount());
            } else if (this.originalStreamFloat == null || !this.m_enableFloatConversion) {
                int bytesRead;
                if (offset > 0 || sampleCount != buffer.getSampleCount()) {
                    throw new IllegalArgumentException("float reading with offset not supported");
                }
                int reqSize = sampleCount * this.format.getFrameSize();
                if (this.m_floatByteBuffer == null || this.m_floatByteBuffer.length < reqSize) {
                    this.m_floatByteBuffer = new byte[reqSize];
                }
                if ((bytesRead = this.read(this.m_floatByteBuffer, 0, reqSize)) <= 0) {
                    buffer.setSampleCount(0, false);
                    return;
                }
                buffer.initFromByteArray(this.m_floatByteBuffer, 0, bytesRead, this.format);
            } else {
                this.originalStreamFloat.read(buffer, offset, sampleCount);
                if (offset + sampleCount > buffer.getSampleCount() && (sampleCount = buffer.getSampleCount() - offset) < 0) {
                    sampleCount = 0;
                }
                this.convert(buffer, offset, sampleCount);
            }
        }
        catch (IOException ioe) {
            if (TDebug.TraceAllExceptions) {
                ioe.printStackTrace();
            }
            buffer.setSampleCount(0, false);
        }
    }

    public void read(FloatSampleBuffer buffer) {
        this.read(buffer, 0, buffer.getSampleCount());
    }
}

