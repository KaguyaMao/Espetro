/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.convert;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Bitstream;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.BitstreamException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Decoder;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.DecoderException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Equalizer;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Header;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Obuffer;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.PropertiesContainer;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.IcyListener;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.TagParseEvent;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.TagParseListener;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.convert.TAsynchronousFilteredAudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class DecodedMpegAudioInputStream
extends TAsynchronousFilteredAudioInputStream
implements PropertiesContainer,
TagParseListener {
    private InputStream m_encodedStream;
    private Bitstream m_bitstream;
    private Decoder m_decoder;
    private Equalizer m_equalizer;
    private float[] m_equalizer_values;
    private Header m_header;
    private DMAISObuffer m_oBuffer;
    private long byteslength = -1L;
    private long currentByte = 0L;
    private int frameslength = -1;
    private long currentFrame = 0L;
    private int currentFramesize = 0;
    private int currentBitrate = -1;
    private long currentMicrosecond = 0L;
    private IcyListener shoutlst = null;
    private HashMap properties = null;

    public DecodedMpegAudioInputStream(AudioFormat outputFormat, AudioInputStream inputStream) {
        super(outputFormat, -1L);
        if (TDebug.TraceAudioConverter) {
            TDebug.out(">DecodedMpegAudioInputStream(AudioFormat outputFormat, AudioInputStream inputStream)");
        }
        try {
            this.byteslength = inputStream.available();
        }
        catch (IOException e) {
            TDebug.out("DecodedMpegAudioInputStream : Cannot run inputStream.available() : " + e.getMessage());
            this.byteslength = -1L;
        }
        this.m_encodedStream = inputStream;
        this.shoutlst = IcyListener.getInstance();
        this.shoutlst.reset();
        this.m_bitstream = new Bitstream(inputStream);
        this.m_decoder = new Decoder(null);
        this.m_equalizer = new Equalizer();
        this.m_equalizer_values = new float[32];
        for (int b = 0; b < this.m_equalizer.getBandCount(); ++b) {
            this.m_equalizer_values[b] = this.m_equalizer.getBand(b);
        }
        this.m_decoder.setEqualizer(this.m_equalizer);
        this.m_oBuffer = new DMAISObuffer(outputFormat.getChannels());
        this.m_decoder.setOutputBuffer(this.m_oBuffer);
        try {
            this.m_header = this.m_bitstream.readFrame();
            if (this.m_header != null && this.frameslength == -1 && this.byteslength > 0L) {
                this.frameslength = this.m_header.max_number_of_frames((int)this.byteslength);
            }
        }
        catch (BitstreamException e) {
            TDebug.out("DecodedMpegAudioInputStream : Cannot read first frame : " + e.getMessage());
            this.byteslength = -1L;
        }
        this.properties = new HashMap();
    }

    public Map properties() {
        this.properties.put("mp3.frame", new Long(this.currentFrame));
        this.properties.put("mp3.frame.bitrate", new Integer(this.currentBitrate));
        this.properties.put("mp3.frame.size.bytes", new Integer(this.currentFramesize));
        this.properties.put("mp3.position.byte", new Long(this.currentByte));
        this.properties.put("mp3.position.microseconds", new Long(this.currentMicrosecond));
        this.properties.put("mp3.equalizer", this.m_equalizer_values);
        if (this.shoutlst != null) {
            String surl = this.shoutlst.getStreamUrl();
            String stitle = this.shoutlst.getStreamTitle();
            if (stitle != null && stitle.trim().length() > 0) {
                this.properties.put("mp3.shoutcast.metadata.StreamTitle", stitle);
            }
            if (surl != null && surl.trim().length() > 0) {
                this.properties.put("mp3.shoutcast.metadata.StreamUrl", surl);
            }
        }
        return this.properties;
    }

    public void execute() {
        block11: {
            if (TDebug.TraceAudioConverter) {
                TDebug.out("execute() : begin");
            }
            try {
                Header header = null;
                header = this.m_header == null ? this.m_bitstream.readFrame() : this.m_header;
                if (TDebug.TraceAudioConverter) {
                    TDebug.out("execute() : header = " + header);
                }
                if (header == null) {
                    if (TDebug.TraceAudioConverter) {
                        TDebug.out("header is null (end of mpeg stream)");
                    }
                    this.getCircularBuffer().close();
                    return;
                }
                ++this.currentFrame;
                this.currentBitrate = header.bitrate_instant();
                this.currentFramesize = header.calculate_framesize();
                this.currentByte += (long)this.currentFramesize;
                this.currentMicrosecond = (long)((float)this.currentFrame * header.ms_per_frame() * 1000.0f);
                for (int b = 0; b < this.m_equalizer_values.length; ++b) {
                    this.m_equalizer.setBand(b, this.m_equalizer_values[b]);
                }
                this.m_decoder.setEqualizer(this.m_equalizer);
                Obuffer decoderOutput = this.m_decoder.decodeFrame(header, this.m_bitstream);
                this.m_bitstream.closeFrame();
                this.getCircularBuffer().write(this.m_oBuffer.getBuffer(), 0, this.m_oBuffer.getCurrentBufferSize());
                this.m_oBuffer.reset();
                if (this.m_header != null) {
                    this.m_header = null;
                }
            }
            catch (BitstreamException e) {
                if (TDebug.TraceAudioConverter) {
                    TDebug.out(e);
                }
            }
            catch (DecoderException e) {
                if (!TDebug.TraceAudioConverter) break block11;
                TDebug.out(e);
            }
        }
        if (TDebug.TraceAudioConverter) {
            TDebug.out("execute() : end");
        }
    }

    public long skip(long bytes) {
        if (this.byteslength > 0L && this.frameslength > 0) {
            float ratio = (float)bytes * 1.0f / (float)this.byteslength * 1.0f;
            long bytesread = this.skipFrames((long)(ratio * (float)this.frameslength));
            this.currentByte += bytesread;
            this.m_header = null;
            return bytesread;
        }
        return -1L;
    }

    public long skipFrames(long frames) {
        int bytesReads;
        int framesRead;
        block6: {
            if (TDebug.TraceAudioConverter) {
                TDebug.out("skip(long frames) : begin");
            }
            framesRead = 0;
            bytesReads = 0;
            try {
                int i = 0;
                while ((long)i < frames) {
                    Header header = this.m_bitstream.readFrame();
                    if (header != null) {
                        int fsize = header.calculate_framesize();
                        bytesReads += fsize;
                    }
                    this.m_bitstream.closeFrame();
                    ++framesRead;
                    ++i;
                }
            }
            catch (BitstreamException e) {
                if (!TDebug.TraceAudioConverter) break block6;
                TDebug.out(e);
            }
        }
        if (TDebug.TraceAudioConverter) {
            TDebug.out("skip(long frames) : end");
        }
        this.currentFrame += (long)framesRead;
        return bytesReads;
    }

    private boolean isBigEndian() {
        return this.getFormat().isBigEndian();
    }

    public void close() throws IOException {
        super.close();
        this.m_encodedStream.close();
    }

    public void tagParsed(TagParseEvent tpe) {
        System.out.println("TAG:" + tpe.getTag());
    }

    private class DMAISObuffer
    extends Obuffer {
        private int m_nChannels;
        private byte[] m_abBuffer;
        private int[] m_anBufferPointers;
        private boolean m_bIsBigEndian;

        public DMAISObuffer(int nChannels) {
            this.m_nChannels = nChannels;
            this.m_abBuffer = new byte[2304 * nChannels];
            this.m_anBufferPointers = new int[nChannels];
            this.reset();
            this.m_bIsBigEndian = DecodedMpegAudioInputStream.this.isBigEndian();
        }

        public void append(int nChannel, short sValue) {
            byte bSecondByte;
            byte bFirstByte;
            if (this.m_bIsBigEndian) {
                bFirstByte = (byte)(sValue >>> 8 & 0xFF);
                bSecondByte = (byte)(sValue & 0xFF);
            } else {
                bFirstByte = (byte)(sValue & 0xFF);
                bSecondByte = (byte)(sValue >>> 8 & 0xFF);
            }
            this.m_abBuffer[this.m_anBufferPointers[nChannel]] = bFirstByte;
            this.m_abBuffer[this.m_anBufferPointers[nChannel] + 1] = bSecondByte;
            int n = nChannel;
            this.m_anBufferPointers[n] = this.m_anBufferPointers[n] + this.m_nChannels * 2;
        }

        public void set_stop_flag() {
        }

        public void close() {
        }

        public void write_buffer(int nValue) {
        }

        public void clear_buffer() {
        }

        public byte[] getBuffer() {
            return this.m_abBuffer;
        }

        public int getCurrentBufferSize() {
            return this.m_anBufferPointers[0];
        }

        public void reset() {
            for (int i = 0; i < this.m_nChannels; ++i) {
                this.m_anBufferPointers[i] = i * 2;
            }
        }
    }
}

